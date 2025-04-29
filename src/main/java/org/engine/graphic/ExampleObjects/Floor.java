package org.engine.graphic.ExampleObjects;

import org.engine.scene.Camera;
import org.engine.shaders.ShaderProgram;
import org.engine.utils.TextureLoader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Floor {
    private int vao;
    private int textureID;
    private ShaderProgram shaderProgram;

    private float x, y, z; // Position
    private float width, height; // Size

    public Floor(float x, float y, float z, float width, float height) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
    }

    public void init() throws IOException {
        createShaders();
        createFloor();
        loadTexture("src/main/resources/cube_1m.png");
    }

    private void createShaders() throws IOException {
        shaderProgram = new ShaderProgram(
                "src/main/java/org/engine/shaders/floor/vertex_shader.glsl",
                "src/main/java/org/engine/shaders/floor/fragment_shader.glsl"
        );
        shaderProgram.use();
    }

    private void createFloor() {
        float[] vertices = {
            // Positions                   // Texture Coords
            (x - width) , y, (z - height),   0.0f, 1.0f, // bottom left
            (x + width) , y, (z - height),   1.0f, 1.0f, // bottom right
            (x + width) , y, (z + height),   1.0f, 0.0f, // top right

            (x + width) , y, (z + height),   1.0f, 0.0f, // top right
            (x - width) , y, (z + height),   0.0f, 0.0f, // top left
            (x - width) , y, (z - height),   0.0f, 1.0f  // bottom left
        };

        vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Texture coordinate attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void loadTexture(String filePath) {
    textureID = TextureLoader.loadTexture(filePath);

    glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL11.GL_TEXTURE_2D, 0);
}


    public void render(Camera camera, Matrix4f projection) {
        shaderProgram.use();
        shaderProgram.setUniform("view", camera.getViewMatrix());
        shaderProgram.setUniform("projection", projection);
    
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        shaderProgram.setUniform("textureSampler", 0);
    
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6); // Floor
        glBindVertexArray(0);
    }
    

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteTextures(textureID);
        shaderProgram.cleanup();
    }
}
