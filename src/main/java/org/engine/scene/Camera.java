package org.engine.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Vector3f front;
    private final Vector3f up;
    private float yaw;
    private float pitch;

    public Camera(Vector3f position) {
        this.position = position;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);
        this.yaw = -90.0f; // Początkowy kierunek (w prawo)
        this.pitch = 0.0f;
    }

    public Matrix4f getViewMatrix() {
        Vector3f center = new Vector3f();
        position.add(front, center);
        return new Matrix4f().lookAt(position, center, up);
    }

    public void processMouseMovement(float deltaX, float deltaY) {
        float sensitivity = 0.1f;
        yaw += deltaX * sensitivity;
        pitch += deltaY * sensitivity;

        // Ograniczenie kąta pitch
        pitch = Math.max(-89.0f, Math.min(89.0f, pitch));

        // Aktualizacja wektora front
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.normalize();
    }

    public void processKeyboard(String direction, float deltaTime) {
        float speed = 2.5f * deltaTime;
        if (direction.equals("FORWARD")) {
            position.add(new Vector3f(front).mul(speed));
        }
        if (direction.equals("BACKWARD")) {
            position.sub(new Vector3f(front).mul(speed));
        }
        if (direction.equals("LEFT")) {
            position.sub(new Vector3f(front).cross(up, new Vector3f()).normalize().mul(speed));
        }
        if (direction.equals("RIGHT")) {
            position.add(new Vector3f(front).cross(up, new Vector3f()).normalize().mul(speed));
        }
    }
    public Vector3f getPosition() {
        return position;
    }

}