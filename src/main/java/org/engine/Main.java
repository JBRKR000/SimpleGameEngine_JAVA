package org.engine;

public class Main {
    public static void main(String[] args) throws Exception {
       Window window = new Window(800, 600, "NpEx Engine");
       window.init();
       window.gameLoop();
       window.cleanup();
    }
}