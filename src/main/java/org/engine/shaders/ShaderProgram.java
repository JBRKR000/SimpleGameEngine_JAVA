package org.engine.shaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}