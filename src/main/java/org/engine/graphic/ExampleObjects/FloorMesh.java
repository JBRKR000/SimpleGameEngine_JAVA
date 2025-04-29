package org.engine.graphic.ExampleObjects;

import org.engine.scene.Camera;
import org.engine.shaders.ShaderProgram;
import org.engine.utils.TextureLoader;
import org.joml.Matrix4f;

import java.io.IOException;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class FloorMesh {
    private int vao;
    private int vbo;
    private int ebo;
    private int textureID;
    private ShaderProgram shaderProgram;
    private int tileCountX, tileCountZ;
    private float offsetX, offsetZ; // Dodane przesunięcia

    public FloorMesh(int tileCountX, int tileCountZ, float offsetX, float offsetZ) {
        this.tileCountX = tileCountX;
        this.tileCountZ = tileCountZ;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
    }

    public void init() throws IOException {
        createShaders();
        createMesh();
        loadTexture("src/main/resources/cube_1m_floor.png"); // <- Twoja tekstura
    }

    private void createShaders() throws IOException {
        shaderProgram = new ShaderProgram(
                "src/main/java/org/engine/shaders/floor/vertex_shader.glsl",
                "src/main/java/org/engine/shaders/floor/fragment_shader.glsl"
        );
        shaderProgram.use();
    }

    private void createMesh() {
        float tileSize = 1.0f;
        float heightY = -0.01f; // Małe obniżenie podłogi
        int vertexCountX = tileCountX + 1;
        int vertexCountZ = tileCountZ + 1;

        float[] vertices = new float[vertexCountX * vertexCountZ * 5]; // 5 floats per vertex (x, y, z, u, v)
        int[] indices = new int[tileCountX * tileCountZ * 6]; // 6 indices per quad

        int v = 0;
        for (int x = 0; x < vertexCountX; x++) {
            for (int z = 0; z < vertexCountZ; z++) {
                vertices[v++] = (x * tileSize) + offsetX;     // posX z przesunięciem
                vertices[v++] = heightY;                     // posY
                vertices[v++] = (z * tileSize) + offsetZ;     // posZ z przesunięciem
                vertices[v++] = (float) x / tileCountX;       // u (dla powtarzania tekstury)
                vertices[v++] = (float) z / tileCountZ;       // v
            }
        }

        int idx = 0;
        for (int x = 0; x < tileCountX; x++) {
            for (int z = 0; z < tileCountZ; z++) {
                int topLeft = (x * vertexCountZ) + z;
                int topRight = topLeft + 1;
                int bottomLeft = ((x + 1) * vertexCountZ) + z;
                int bottomRight = bottomLeft + 1;

                indices[idx++] = topLeft;
                indices[idx++] = bottomLeft;
                indices[idx++] = topRight;

                indices[idx++] = topRight;
                indices[idx++] = bottomLeft;
                indices[idx++] = bottomRight;
            }
        }

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Pozycje
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Tekstury UV
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void loadTexture(String filePath) {
        textureID = TextureLoader.loadTexture(filePath);
    }

    public void render(Camera camera, Matrix4f projection) {
        shaderProgram.use();
        shaderProgram.setUniform("view", camera.getViewMatrix());
        shaderProgram.setUniform("projection", projection);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        shaderProgram.setUniform("textureSampler", 0);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, tileCountX * tileCountZ * 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteTextures(textureID);
        shaderProgram.cleanup();
    }
}
