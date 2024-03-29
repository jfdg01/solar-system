package com.kandclay;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.kandclay.Constants.*;

public class CameraController extends InputAdapter {
    private final OrthographicCamera camera;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private final float moveSpeed;
    private final float zoomInFactor;
    private final float zoomOutFactor;
    private final Array<CelestialBodyActor> celestialBodies;
    private CelestialBodyActor targetBody = null;
    private int currentBodyIndex = -1;

    public CameraController(OrthographicCamera camera, float moveSpeed, float zoomInFactor, float zoomOutFactor, Array<CelestialBodyActor> celestialBodies) {
        this.camera = camera;
        this.moveSpeed = moveSpeed;
        this.zoomInFactor = zoomInFactor;
        this.zoomOutFactor = zoomOutFactor;
        this.celestialBodies = celestialBodies;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                upPressed = true;
                setTargetBody(null);
                break;
            case Keys.A:
                leftPressed = true;
                setTargetBody(null);
                break;
            case Keys.S:
                downPressed = true;
                setTargetBody(null);
                break;
            case Keys.D:
                rightPressed = true;
                setTargetBody(null);
                break;
            case Keys.H:
            case Keys.UP:
                camera.zoom *= zoomInFactor;
                break;
            case Keys.J:
            case Keys.DOWN:
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
                setTargetBody(celestialBodies.get(SUN)); // Track Sun
                break;
            case Keys.NUM_2:
                setTargetBody(celestialBodies.get(EARTH)); // Track Earth
                break;
            case Keys.NUM_3:
                setTargetBody(celestialBodies.get(MOON)); // Track Moon
                break;
            case Keys.NUM_4:
                setTargetBody(celestialBodies.get(SATURN)); // Track Saturn
                break;
            case Keys.NUM_5:
                setTargetBody(null); // Free mode
                break;
            case Keys.LEFT:
                selectPreviousBody();
                break;
            case Keys.RIGHT:
                selectNextBody();
                break;
        }
        return false;
    }

    public void setTargetBody(CelestialBodyActor targetBody) {
        this.targetBody = targetBody;
    }

    private void selectNextBody() {
        if (celestialBodies.isEmpty()) return;

        currentBodyIndex = (currentBodyIndex + 1) % celestialBodies.size;
        setTargetBody(celestialBodies.get(currentBodyIndex));
    }

    private void selectPreviousBody() {
        if (celestialBodies.isEmpty()) return;

        currentBodyIndex = (currentBodyIndex - 1 + celestialBodies.size) % celestialBodies.size;
        setTargetBody(celestialBodies.get(currentBodyIndex));
    }

    public void update(float deltaTime) {
        if (targetBody != null) {
            float targetX = targetBody.getX() + targetBody.getWidth() / 2; // Center X of the target body
            float targetY = targetBody.getY() + targetBody.getHeight() / 2; // Center Y of the target body

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
