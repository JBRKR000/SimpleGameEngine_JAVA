package org.engine.graphic.ExampleObjects;

import org.engine.scene.Camera;
import org.engine.shaders.ShaderProgram;
import org.engine.utils.TextureLoader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Triangle {
    private int textureID;
    private int vao;
    private ShaderProgram shaderProgram;
    float x,y,z;
    float scaleX, scaleY, scaleZ;


    public Triangle(float x, float y, float z, float scaleX, float scaleY, float scaleZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public void init() throws IOException {
        createShaders();
        createTriangle();
    }
    public void loadTexture(String filePath) {
        textureID = TextureLoader.loadTexture(filePath);
    }
    private void createShaders() throws IOException {
        shaderProgram = new ShaderProgram(
                "src/main/java/org/engine/shaders/triangle/vertex_shader.glsl",
                "src/main/java/org/engine/shaders/triangle/fragment_shader.glsl"
        );
        shaderProgram.use();
    }

    private void createTriangle() {
        float[] vertices = {
                // Współrzędne wierzchołków   // Kolory       // Współrzędne tekstury
                (0.0f + x) * scaleX, (0.5f + y) * scaleY, (0.0f + z) * scaleZ,     1.0f, 0.0f, 0.0f,  0.25f, .25f,
                (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.0f + z) * scaleZ,   0.0f, 1.0f, 0.0f,  0.0f, 1.0f,
                (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.0f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  0.5f, 1.0f
        };

        vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Współrzędne wierzchołków
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Kolory
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Współrzędne tekstury
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    public void render(Camera camera, Matrix4f projection) {
        shaderProgram.use();
        uploadUniforms(camera, projection);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glBindVertexArray(0);
    }
    private void uploadUniforms(Camera camera, Matrix4f projection) {
        shaderProgram.setUniform("view", camera.getViewMatrix());
        shaderProgram.setUniform("projection", projection);
    }

}