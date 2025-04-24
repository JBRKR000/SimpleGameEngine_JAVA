package org.engine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
       Window window = new Window(800, 600, "My Game");
       window.init();
       window.gameLoop();
       window.cleanup();
    }
}