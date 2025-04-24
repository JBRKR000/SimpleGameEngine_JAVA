package org.engine;
import org.engine.graphic.ExampleObjects.Triangle;
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
    private Triangle triangle2;
    private Triangle triangle3;


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


        triangle = new Triangle(1.5f,0,0, .5f,.5f,.5f);
        triangle.init();

        triangle2 = new Triangle(-1.5f,0,0, .5f,.5f,.5f);
        triangle2.init();

        triangle3 = new Triangle(0,0,0, .5f,.5f,.5f);
        triangle3.init();


    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    /// This method is called every frame
    public void update() {
        glfwPollEvents();

        triangle.render();
        triangle2.render();
        triangle3.render();

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
