package org.engine;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.engine.graphic.ExampleObjects.Cube;
import org.engine.graphic.ExampleObjects.Floor;
import org.engine.graphic.ExampleObjects.Triangle;
import org.engine.scene.Camera;
import org.engine.scene.Crosshair;
import org.engine.utils.ImGuiHandler;
import org.engine.utils.ImGuiLogHandler;
import org.engine.utils.MapLoader;
import org.engine.utils.ThreadMenager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import imgui.ImGui;



public class Window {
    private final int width;
    private final int height;
    private final String windowTitle;
    private Long window;
    private final int MAX_FPS = 500;
    private boolean isFullscreen = false;
    private int windowedWidth, windowedHeight;
    private int windowedPosX, windowedPosY;
    private Logger logger = Logger.getLogger(Window.class.getName());

    // MOUSE MOVEMENT
    private double lastMouseX, lastMouseY;
    private double mouseX, mouseY;

    // TRIANGLE TEST OBJECT
    private Triangle triangle;
    private Triangle triangle2;
    private Triangle triangle3;
    private Cube cube;
    private Floor floor;
    private List<MapLoader.MapObject> mapObjects;
    private MapLoader mapLoader;
    private MapLoader.MapData mapData;

    // CAMERA
    private Camera camera;
    private float deltaTime;
    private float lastFrame;
    private Matrix4f projection;

    //CROSSHAIR
    private Crosshair crosshair;

    // THREAD MANAGER
    ThreadMenager threadManager = new ThreadMenager();

    public Window(int width, int height, String windowTitle) {
        this.width = width;
        this.height = height;
        this.windowTitle = windowTitle;
        this.mouseX = width / 2.0;
        this.mouseY = height / 2.0;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void init() throws IOException {
        camera = new Camera(new Vector3f(0.0f, 0.0f, 3.0f));
        lastFrame = 0.0f;
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new ImGuiLogHandler());
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
    
        try {
            window = glfwCreateWindow(width, height, windowTitle, 0, 0);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to create the GLFW window", e);
        }
    
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);
        glfwFocusWindow(window);
        GL.createCapabilities();
        projection = new Matrix4f().perspective(
                (float) Math.toRadians(45.0f),
                (float) width / height,
                1.0f,
                100.0f
        );
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
    
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseX = xpos;
                mouseY = ypos;
            }
        });
    
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    
        initObjects(threadManager); // Extracted initialization logic
        ImGuiHandler.initImGui(window); // Extracted ImGui initialization logic
    }
    public void update() {
        glfwPollEvents();
        float currentFrame = (float) glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;
        
        if(!ImGuiHandler.isConsoleEnabled()){
            inputHandler();
            // Mouse handling
            double deltaX = mouseX - lastMouseX;
            double deltaY = lastMouseY - mouseY; // Invert Y-axis
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            camera.processMouseMovement((float) deltaX, (float) deltaY);
        }
        
        threadManager.updateMainThreadQueue();
        render();
        glfwSwapBuffers(window);
    }

    private void initObjects(ThreadMenager threadManager) throws IOException {
        threadManager.submitToWorker(() -> {
            try {
                MapLoader loader = new MapLoader();
                MapLoader.MapData data = loader.loadMap("src/main/resources/map.txt");
                threadManager.submitToMain(() -> {
                    try {
                        float startTime = (float) glfwGetTime();
                        this.mapLoader = loader;
                        this.mapData = data;
                        this.mapLoader.initGLResources(mapData.objects);
                        logger.warning("Map loaded with " + mapData.objects.size() + " objects.");
                        logger.info("Map loading took " + (int)(glfwGetTime() - startTime) + " seconds.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        crosshair = new Crosshair();
        crosshair.init();
    }
    
    
    

    private void render() {
        mapLoader.render(camera, projection, mapData.objects);
        glDisable(GL_DEPTH_TEST);
        crosshair.render();
        glEnable(GL_DEPTH_TEST);
        ImGuiHandler.renderImGui(camera, window, threadManager); // Extracted ImGui rendering logic
    }
    


    public void cleanup() {
        mapLoader.cleanup(mapData.objects);
        crosshair.cleanup();
        threadManager.shutdown(); // Shutdown thread manager
        ImGuiHandler.cleanup(); // Extracted ImGui cleanup logic
        ImGui.destroyContext();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void gameLoop() {
        while (!shouldClose()) {
            float startTime = (float) glfwGetTime();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            update();

            float frameTime = (float) glfwGetTime() - startTime;
            float targetFrameTime = 1.0f / MAX_FPS;

            if (frameTime < targetFrameTime) {
                try {
                    Thread.sleep((long) ((targetFrameTime - frameTime) * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void inputHandler(){
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.processKeyboard("FORWARD", deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.processKeyboard("BACKWARD", deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.processKeyboard("LEFT", deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.processKeyboard("RIGHT", deltaTime);
        }
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
        if(glfwGetKey(window, GLFW_KEY_F12) == GLFW_PRESS) {
            toggleFullscreen();
        }
    }

    public void toggleFullscreen() {
    if (!isFullscreen) {
        windowedWidth = width;
        windowedHeight = height;
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode videoMode = glfwGetVideoMode(monitor);

        glfwGetWindowPos(window, new int[1], new int[1]);
        glfwSetWindowMonitor(window, monitor, 0, 0, videoMode.width(), videoMode.height(), GLFW_DONT_CARE);
        isFullscreen = true;
    } else {
        glfwSetWindowMonitor(window, 0, windowedPosX, windowedPosY, windowedWidth, windowedHeight, GLFW_DONT_CARE);
        isFullscreen = false;
    }
    }
    

}