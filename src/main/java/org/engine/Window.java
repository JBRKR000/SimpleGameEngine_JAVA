package org.engine;
import org.engine.graphic.ExampleObjects.Triangle;
import org.engine.graphic.ExampleObjects.Square;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Window {
    private final int width;
    private final int height;
    private final String windowTitle;
    private Long window;

    // RIANGLE TEST OBJECT
    private Triangle triangle;
    private Square square;


    public Window(int width, int height, String windowTitle) {
        this.width = width;
        this.height = height;
        this.windowTitle = windowTitle;
    }

    public void init() throws IOException {
        if(!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
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
        GL.createCapabilities();
        glClearColor(0.1f, 0.1f, 0.15f, 1.0f);

        triangle = new Triangle();
        triangle.init();

        square = new Square();
        square.init();

    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    /// This method is called every frame
    public void update() {
        glfwPollEvents();



        glfwSwapBuffers(window);

    }

    public void cleanup() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void gameLoop() {
        while (!shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            update();
        }
    }

}
