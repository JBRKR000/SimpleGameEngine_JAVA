package org.engine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
       Window window = new Window(1920, 1080, "NpEx Engine");
       window.init();
       window.gameLoop();
       window.cleanup();
    }
}