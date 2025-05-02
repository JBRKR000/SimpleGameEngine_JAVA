package org.engine;

public class Main {
    static {
        System.load(System.getProperty("user.dir") + "/libs/imgui-java64.dll");
    }
    public static void main(String[] args) throws Exception {
       Window window = new Window(1920, 1080, "NpEx Engine");
       window.init();
       window.gameLoop();
       window.cleanup();
    }
}