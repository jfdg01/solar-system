package com.kandclay;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.kandclay.Constants.LERP_FACTOR;

public class CameraController extends InputAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private final float moveSpeed;
    private final float zoomInFactor;
    private final float zoomOutFactor;
    private final SolarSystemScreen solarSystemScreen;
    private CelestialBody targetBody = null;

    public CameraController(OrthographicCamera camera, Viewport viewport, float moveSpeed, float zoomInFactor, float zoomOutFactor, SolarSystemScreen solarSystemScreen) {
        this.camera = camera;
        this.viewport = viewport;
        this.moveSpeed = moveSpeed;
        this.zoomInFactor = zoomInFactor;
        this.zoomOutFactor = zoomOutFactor;
        this.solarSystemScreen = solarSystemScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                upPressed = true;
                break;
            case Keys.A:
                leftPressed = true;
                break;
            case Keys.S:
                downPressed = true;
                break;
            case Keys.D:
                rightPressed = true;
                break;
            case Keys.H:
                camera.zoom *= zoomInFactor;
                break;
            case Keys.J:
                camera.zoom *= zoomOutFactor;
                break;
        }
        return false; // Let other input processors handle the input if not consumed here
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.W:
                upPressed = false;
                break;
            case Keys.A:
                leftPressed = false;
                break;
            case Keys.S:
                downPressed = false;
                break;
            case Keys.D:
                rightPressed = false;
                break;
            case Keys.NUM_1:
                setTargetBody(null); // Free mode
                break;
            case Keys.NUM_2:
                setTargetBody(solarSystemScreen.getEarth()); // Track Earth
                break;
            case Keys.NUM_3:
                setTargetBody(solarSystemScreen.getSaturn()); // Track Saturn
                break;
            case Keys.NUM_4:
                setTargetBody(solarSystemScreen.getSun()); // Track Sun
                break;
            case Keys.NUM_5:
                setTargetBody(solarSystemScreen.getMoon()); // Track Moon
                break;
        }
        return false;
    }

    public void setTargetBody(CelestialBody targetBody) {
        this.targetBody = targetBody;
    }

    public void update(float deltaTime) {
        if (targetBody != null) {
            Vector2 targetWorldPos = targetBody.getBody().getPosition();

            float targetX = targetWorldPos.x * Constants.PIXELS_TO_METERS;
            float targetY = targetWorldPos.y * Constants.PIXELS_TO_METERS;

            camera.position.x += (targetX - camera.position.x) * LERP_FACTOR;
            camera.position.y += (targetY - camera.position.y) * LERP_FACTOR;
        } else {
            // Handle manual camera movement
            if (upPressed) {
                camera.translate(0, moveSpeed * camera.zoom * deltaTime);
            }
            if (downPressed) {
                camera.translate(0, -moveSpeed * camera.zoom * deltaTime);
            }
            if (leftPressed) {
                camera.translate(-moveSpeed * camera.zoom * deltaTime, 0);
            }
            if (rightPressed) {
                camera.translate(moveSpeed * camera.zoom * deltaTime, 0);
            }
        }
        camera.update();
    }
}
