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
        private final String texturePath;

        public MapObject(Object object, String texturePath) {
            this.object = object;
            this.texturePath = texturePath;
        }

        public Object getObject() {
            return object;
        }

        public String getTexturePath() {
            return texturePath;
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

    // Wczytywanie danych z pliku, bez OpenGL
    public MapData loadMap(String filePath) throws IOException {
        List<MapObject> objects = new ArrayList<>();
        int width = 0;
        int height = 0;
        String texturePath = "src/main/resources/cube_1m.png";

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
                        MapObject obj = new MapObject(cube, texturePath);
                        objects.add(obj);
                    }
                }
                y++;
            }
            height = y;
        }

        // FloorMesh tworzymy tu jako obiekt danych — init() dopiero w głównym wątku
        floorMesh = new FloorMesh(20, 15, 0f, -15f);

        return new MapData(objects, width, height);
    }

    // Inicjalizacja zasobów OpenGL — tylko w głównym wątku
    public void initGLResources(List<MapObject> objects) throws IOException {
        for (MapObject obj : objects) {
            if (obj.getObject() instanceof Cube) {
                Cube cube = (Cube) obj.getObject();
                cube.init(); // tworzy VAO/VBO — tylko w GL context
                int textureId = TextureLoader.loadTexture(obj.getTexturePath());
                cube.loadTexture(textureId);
            }
        }

        if (floorMesh != null) {
            floorMesh.init();
        }
    }

    // Renderowanie mapy
    public void render(Camera camera, Matrix4f projection, List<MapObject> mapObjects) {
        if (floorMesh != null) {
            floorMesh.render(camera, projection);
        }

        for (MapObject mapObject : mapObjects) {
            if (mapObject.getObject() instanceof Cube) {
                ((Cube) mapObject.getObject()).render(camera, projection);
            }
        }
    }

    // Zwolnienie zasobów OpenGL
    public void cleanup(List<MapObject> mapObjects) {
        if (floorMesh != null) {
            floorMesh.cleanup();
        }

        for (MapObject mapObject : mapObjects) {
            if (mapObject.getObject() instanceof Cube) {
                ((Cube) mapObject.getObject()).cleanup();
            }
        }
    }
}
