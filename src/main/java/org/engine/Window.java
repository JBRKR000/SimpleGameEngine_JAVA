package org.engine;

import org.engine.graphic.ExampleObjects.Cube;
import org.engine.graphic.ExampleObjects.Floor;
import org.engine.graphic.ExampleObjects.FloorMesh;
import org.engine.graphic.ExampleObjects.Triangle;
import org.engine.scene.Camera;
import org.engine.scene.Crosshair;
import org.engine.utils.MapLoader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.opengl.GL;
import java.io.IOException;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;


public class Window {
    private final int width;
    private final int height;
    private final String windowTitle;
    private Long window;
    private final int MAX_FPS = 500;

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
    FloorMesh floorMesh;

    // CAMERA
    private Camera camera;
    private float deltaTime;
    private float lastFrame;
    private Matrix4f projection;

    //CROSSHAIR
    private Crosshair crosshair;


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
    
        initObjects(); // Extracted initialization logic
    }
    public void update() {
        glfwPollEvents();
        float currentFrame = (float) glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;
        inputHandler();
    
        // Mouse handling
        double deltaX = mouseX - lastMouseX;
        double deltaY = lastMouseY - mouseY; // Invert Y-axis
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    
        camera.processMouseMovement((float) deltaX, (float) deltaY);
    
        render(); // Extracted rendering logic
        printFPS();
        glfwSwapBuffers(window);
    }


    private void initObjects() throws IOException {
        MapLoader mapLoader = new MapLoader();
        MapLoader.MapData mapData = mapLoader.loadMap("src/main/resources/map.txt");
    
        floorMesh = new FloorMesh(20, 15, 0f, -15f);
        floorMesh.init();

        mapObjects = mapData.objects;
        crosshair = new Crosshair();
        crosshair.init();
    }

    private void render() {
        floorMesh.render(camera, projection);
        for (MapLoader.MapObject mapObject : mapObjects) {
            if (mapObject.getObject() instanceof Cube) {
                ((Cube) mapObject.getObject()).render(camera, projection);
            }
        }
        glDisable(GL_DEPTH_TEST);
        crosshair.render();
        glEnable(GL_DEPTH_TEST);
    }
    


    public void cleanup() {
        floorMesh.cleanup();
        for (MapLoader.MapObject mapObject : mapObjects) {
            if (mapObject.getObject() instanceof Cube) {
                ((Cube) mapObject.getObject()).cleanup();
            }
        }
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
    }


    private void printFPS() {
        float fps = 1.0f / deltaTime;
        System.out.println("FPS: " + fps);
    }
}