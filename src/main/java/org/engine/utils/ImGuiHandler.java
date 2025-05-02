package org.engine.utils;

import org.engine.scene.Camera;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImString;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

import java.util.ArrayList;
import java.util.List;

public class ImGuiHandler {

    private static ImGuiImplGlfw imguiGlfw;
    private static ImGuiImplGl3 imguiGl3;
    private static float deltaTime;
    private static long lastFrameTime = System.nanoTime();
    private static boolean consoleEnabled = false;

    private static final List<String> consoleLogs = new ArrayList<>();
    private static final int MAX_CONSOLE_LOGS = 100;
    private static ImString commandInput = new ImString(256);

    public ImGuiHandler() {
        imguiGlfw = new ImGuiImplGlfw();
        imguiGl3 = new ImGuiImplGl3();
    }

    public static void initImGui(long window) {
        imguiGlfw = new ImGuiImplGlfw();
        imguiGl3 = new ImGuiImplGl3();

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);

        imguiGlfw.init(window, true);
        imguiGl3.init("#version 330 core");

        addConsoleLog("[Console] Initialized.");
    }

    public static void renderImGui(Camera camera, long window) {
        imguiGlfw.newFrame();
        ImGui.newFrame();

        long currentFrameTime = System.nanoTime();
        deltaTime = (float) (currentFrameTime - lastFrameTime) / 1_000_000_000.0f;
        lastFrameTime = currentFrameTime;

        renderDebugWindow(camera);

        if (ImGui.isKeyPressed(290)) { // F1 to toggle console
            consoleEnabled = !consoleEnabled;
        }

        if (consoleEnabled) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            renderConsoleWindow();
        }else {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }

        ImGui.render();
        imguiGl3.renderDrawData(ImGui.getDrawData());
    }

    private static void renderDebugWindow(Camera camera) {
        Runtime runtime = Runtime.getRuntime();

        ImGui.begin("NpEx Engine Debug Window");
        ImGui.text("Engine Version: 0.1.0");
        ImGui.text("Memory Usage: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");
        ImGui.text("Total Memory: " + runtime.totalMemory() / (1024 * 1024) + " MB");
        ImGui.text("Free Memory: " + runtime.freeMemory() / (1024 * 1024) + " MB");
        ImGui.text("FPS: " + (1.0f / deltaTime));
        ImGui.text("Delta Time: " + deltaTime);
        ImGui.text("Mouse Position: (" + ImGui.getMousePosX() + ", " + ImGui.getMousePosY() + ")");
        ImGui.text("Camera Position: (" + camera.getPosition().x + ", " + camera.getPosition().y + ", " + camera.getPosition().z + ")");
        ImGui.end();
    }

    private static void renderConsoleWindow() {
        final int windowFlags = ImGuiWindowFlags.NoDecoration |
                                ImGuiWindowFlags.AlwaysAutoResize |
                                ImGuiWindowFlags.NoMove |
                                ImGuiWindowFlags.NoSavedSettings |
                                ImGuiWindowFlags.NoFocusOnAppearing;

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY() * 0.5f);

        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.1f, 0.1f, 0.1f, 0.9f); // Ciemne tło
        ImGui.begin("Console", windowFlags);

        for (String line : consoleLogs) {
            if (line.contains("[Error]")) {
                ImGui.textColored(1, 0.2f, 0.2f, 1, line);
            } else if (line.contains("[Info]")) {
                ImGui.textColored(0.2f, 1, 0.2f, 1, line);
            } else if (line.contains("[Warning]")) {
                ImGui.textColored(1, 1, 0.2f, 1, line);
            } else {
                ImGui.text(line);
            }
        }

        // Input field for commands
        ImGui.separator();
        ImGui.text(">");
        ImGui.sameLine();
        ImGui.pushItemWidth(-1);
        if (ImGui.inputText("##consoleInput", commandInput, ImGuiInputTextFlags.EnterReturnsTrue)) {
            String command = commandInput.get();
            addConsoleLog("[Command] " + command);
            handleCommand(command);
            commandInput.set(""); // clear
        }
        

        ImGui.popItemWidth();
        ImGui.end();
        ImGui.popStyleColor();
    }

    private static void addConsoleLog(String log) {
        if (consoleLogs.size() >= MAX_CONSOLE_LOGS) {
            consoleLogs.remove(0);
        }
        consoleLogs.add(log);
    }

    private static void handleCommand(String command) {
        // Przykładowa obsługa polecenia
        if (command.equalsIgnoreCase("clear")) {
            consoleLogs.clear();
            addConsoleLog("[Console] Cleared.");
        } else {
            addConsoleLog("[Info] Unknown command: " + command);
        }
    }

    public static void cleanup() {
        imguiGl3.dispose();
        imguiGlfw.dispose();
    }
}
