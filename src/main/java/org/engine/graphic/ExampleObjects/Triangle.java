package org.engine.graphic.ExampleObjects;

import org.engine.shaders.ShaderProgram;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Triangle {
    private int vao;
    private ShaderProgram shaderProgram;

    public void init() throws IOException {
        createShaders();
        createTriangle();
    }

    private void createShaders() throws IOException {
        shaderProgram = new ShaderProgram(
                "src/main/java/org/engine/shaders/vertex_shader.glsl",
                "src/main/java/org/engine/shaders/fragment_shader.glsl"
        );
        shaderProgram.use();
    }

    private void createTriangle() {
        float[] vertices = {
                -0.5f, -0.5f, 0.0f,         1.0f, 0.1f, 0.0f, //KOLORY
                0.5f, -0.5f, 0.0f,          0.1f, 1.0f, 0.0f,
                0.0f,  0.5f, 0.0f,          0.0f,  0.1f, 1.0f
        };

        vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


        // Współrzędne wierzchołków
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0); // Współrzędne wierzchołków

        // Kolory
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1); // Kolory

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {
        glUseProgram(shaderProgram.getProgramId());
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glBindVertexArray(0);
    }
}