package org.engine.graphic.ExampleObjects;

import org.engine.shaders.ShaderProgram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Square {

    private ShaderProgram shaderProgram;
    private int vao;

    public void init() throws IOException {
        createShaders();
        createSquare();
    }

    private void createShaders() throws IOException {
        shaderProgram = new ShaderProgram(
                "src/main/java/org/engine/shaders/vertex_shader.glsl",
                "src/main/java/org/engine/shaders/fragment_shader.glsl"
        );
        shaderProgram.use();
    }
    private void createSquare() {
        float[] vertices = {
                -0.5f, -0.5f, 0.0f,         1.0f, 0.1f, 0.0f, //KOLORY
                0.5f, -0.5f, 0.0f,          0.1f, 1.0f, 0.0f,
                0.5f,  0.5f, 0.0f,          0.0f,  1.0f, 1.0f,
                -0.5f,  0.5f, 0.0f,         1.0f, 1.0f, 1.0f
        };

        vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES * (3 + 3), 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES * (3 + 3), Float.BYTES * 3);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    public void render() {
        glUseProgram(shaderProgram.getProgramId());
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glBindVertexArray(0);
    }
}
