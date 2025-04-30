package org.engine.utils;

import org.engine.graphic.ExampleObjects.Cube;
import org.engine.graphic.ExampleObjects.FloorMesh;
import org.engine.scene.Camera;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    private FloorMesh floorMesh;

    public static class MapObject {
        private final Object object;

        public MapObject(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

    public static class MapData {
        public List<MapObject> objects;
        public int width;
        public int height;

        public MapData(List<MapObject> objects, int width, int height) {
            this.objects = objects;
            this.width = width;
            this.height = height;
        }
    }

    public MapData loadMap(String filePath) throws IOException {
        List<MapObject> objects = new ArrayList<>();
        int width = 0;
        int height = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int y = 0;
            while ((line = reader.readLine()) != null) {
                line = line.replace(" ", "");
                width = Math.max(width, line.length());
                for (int x = 0; x < line.length(); x++) {
                    char c = line.charAt(x);
                    if (c == 'X') {
                        Cube cube = new Cube(x + 0.5f, 0.5f, -y - 0.5f, 1.0f, 1.0f, 1.0f);
                        cube.init();
                        cube.loadTexture("src/main/resources/cube_1m.png");
                        objects.add(new MapObject(cube));
                    }
                }
                y++;
            }
            height = y;
        }
        floorMesh = new FloorMesh(20, 15, 0f, -15f);
        floorMesh.init();

        return new MapData(objects, width, height);
    }
    public void render(Camera camera, Matrix4f projection, List<MapObject> mapObjects) {
        floorMesh.render(camera, projection);
        for (MapObject mapObject : mapObjects) {
            if (mapObject.getObject() instanceof Cube) {
                ((Cube) mapObject.getObject()).render(camera, projection);
            }
        }
    }
    public void cleanup(List<MapObject> mapObjects) {
        floorMesh.cleanup();
        for (MapObject mapObject : mapObjects) {
            if (mapObject.getObject() instanceof Cube) {
                ((Cube) mapObject.getObject()).cleanup();
            }
        }
    }
}