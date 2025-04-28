package org.engine.graphic.ExampleObjects;

import org.engine.scene.Camera;
import org.engine.shaders.ShaderProgram;
import org.engine.utils.TextureLoader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Cube {
    private int vao;
    private int textureID;
    private ShaderProgram shaderProgram;

    private float x, y, z; // Position
    private float scaleX, scaleY, scaleZ; // Scale

    public Cube(float x, float y, float z, float scaleX, float scaleY, float scaleZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public void init() throws IOException {
        createShaders();
        createCube();
        loadTexture("src/main/resources/stone_texture.jpg");
    }

    private void createShaders() throws IOException {
        shaderProgram = new ShaderProgram(
                "src/main/java/org/engine/shaders/triangle/vertex_shader.glsl",
                "src/main/java/org/engine/shaders/triangle/fragment_shader.glsl"
        );
        shaderProgram.use();
    }

    private void createCube() {
        float[] vertices = {
            // Front face
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,   1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,    0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,     0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
    
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,     0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,    1.0f, 1.0f, 0.0f,  0.0f, 0.0f,
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,   1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
    
            // Back face
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
    
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,    1.0f, 1.0f, 0.0f,  0.0f, 0.0f,
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
    
            // Left face
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,  1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,   0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
    
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   1.0f, 1.0f, 0.0f,  0.0f, 0.0f,
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,  1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
    
            // Right face
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,    1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
    
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,     1.0f, 1.0f, 0.0f,  0.0f, 0.0f,
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,    1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
    
            // Top face
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,    1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,     0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
    
            (0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   1.0f, 1.0f, 0.0f,  0.0f, 0.0f,
            (-0.5f + x) * scaleX, (0.5f + y) * scaleY, (0.5f + z) * scaleZ,    1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
    
            // Bottom face
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,  1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,   0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
    
            (0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,    0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (0.5f + z) * scaleZ,   1.0f, 1.0f, 0.0f,  0.0f, 0.0f,
            (-0.5f + x) * scaleX, (-0.5f + y) * scaleY, (-0.5f + z) * scaleZ,  1.0f, 0.0f, 0.0f,  0.0f, 1.0f
        };
    

        vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Texture coordinate attribute
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void loadTexture(String filePath) {
        textureID = TextureLoader.loadTexture(filePath);
    }

    public void render(Camera camera, Matrix4f projection) {
        shaderProgram.use();
        shaderProgram.setUniform("view", camera.getViewMatrix());
        shaderProgram.setUniform("projection", projection);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 36); // 36 vertices for a cube (6 faces * 2 triangles * 3 vertices)
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        shaderProgram.cleanup();
    }
}