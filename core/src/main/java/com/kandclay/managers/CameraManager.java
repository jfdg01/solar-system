package com.kandclay.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class CameraManager {
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    public CameraManager(float width, float height) {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(width, height, camera);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public ExtendViewport getViewport() {
        return viewport;
    }

    public void update(int width, int height) {
        viewport.update(width, height);
    }

    public void updateCamera() {
        camera.update();
    }

    public void translateCamera(float x, float y) {
        camera.translate(x, y);
    }

    public void zoomCamera(float zoomFactor) {
        camera.zoom *= zoomFactor;
    }
}
