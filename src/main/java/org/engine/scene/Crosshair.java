package org.engine.scene;

import org.engine.shaders.ShaderProgram;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Crosshair {

    private int vao;
    private int vbo;
    private ShaderProgram shaderProgram;

    // Parametry celownika
    private final float lineLength = 0.05f; // Długość ramion
    private final float gap = 0.02f;       // Odstęp od środka

    public void init() throws IOException {
        createShaders();
        createCrosshair();
    }

    private void createShaders() throws IOException {
        shaderProgram = new ShaderProgram(
                "src/main/java/org/engine/shaders/crosshair/vertex_shader.glsl",
                "src/main/java/org/engine/shaders/crosshair/fragment_shader.glsl"
        );
        shaderProgram.use();
    }

    private void createCrosshair() {
        float[] vertices = {
                //left
                -gap - lineLength, 0.0f, 0.0f,
                -gap, 0.0f, 0.0f,
                //right
                gap, 0.0f, 0.0f,
                gap + lineLength, 0.0f, 0.0f,
                //up
                0.0f, gap + lineLength, 0.0f,
                0.0f, gap, 0.0f,
                //down
                0.0f, -gap, 0.0f,
                0.0f, -gap - lineLength, 0.0f
        };

        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {
        shaderProgram.use();

        glBindVertexArray(vao);
        glDrawArrays(GL_LINES, 0, 8); // 8 wierzchołków (4 linie)
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        shaderProgram.cleanup();
    }
}