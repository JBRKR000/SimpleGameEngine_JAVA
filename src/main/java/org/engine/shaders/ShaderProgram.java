package org.engine.shaders;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int programId;

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) throws IOException {
        String vertexShaderSource = Files.readString(Path.of(vertexShaderPath));
        String fragmentShaderSource = Files.readString(Path.of(fragmentShaderPath));

        int vertexShader = createShader(vertexShaderSource, GL_VERTEX_SHADER);
        int fragmentShader = createShader(fragmentShaderSource, GL_FRAGMENT_SHADER);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error linking shader program: " + glGetProgramInfoLog(programId));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private int createShader(String shaderSource, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error compiling shader: " + glGetShaderInfoLog(shaderId));
        }

        return shaderId;
    }

    public void use() {
        glUseProgram(programId);
    }

    public void cleanup() {
        glDeleteProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }
    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            int location = glGetUniformLocation(programId, name);
            glUniformMatrix4fv(location, false, fb);
        }
    }
    public void setUniform(String name, int value) {
    int location = glGetUniformLocation(programId, name);
    if (location != -1) {
        glUniform1i(location, value);
    }
}


}