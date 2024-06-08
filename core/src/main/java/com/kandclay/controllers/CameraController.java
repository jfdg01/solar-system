package com.kandclay.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kandclay.actors.CelestialBodyActor;
import com.kandclay.managers.CameraManager;

import static com.kandclay.Constants.*;

public class CameraController extends InputAdapter {
    private final CameraManager cameraManager;
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
    private Vector2 lastTouch = new Vector2();
    private Vector2 initialTouch1 = new Vector2();
    private Vector2 initialTouch2 = new Vector2();
    private boolean isZooming = false;
    private float initialDistance = 0;
    private Vector2 lastMidPoint = new Vector2();

    public CameraController(CameraManager cameraManager, float moveSpeed, float zoomInFactor, float zoomOutFactor, Array<CelestialBodyActor> celestialBodies) {
        this.cameraManager = cameraManager;
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
                cameraManager.zoomCamera(zoomInFactor);
                break;
            case Keys.J:
            case Keys.DOWN:
                cameraManager.zoomCamera(zoomOutFactor);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        int num = -1;

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
                num = 0;
                break;
            case Keys.NUM_2:
                num = 1;
                break;
            case Keys.NUM_3:
                num = 2;
                break;
            case Keys.NUM_4:
                num = 3;
                break;
            case Keys.NUM_5:
                setTargetBody(null);
                break;
            case Keys.LEFT:
                selectPreviousBody();
                break;
            case Keys.RIGHT:
                selectNextBody();
                break;
        }
        if (num >= 0) {
            setTargetBody(celestialBodies.get(num));
        }

        return false;
    }

    public void setTargetBody(CelestialBodyActor targetBody) {
        this.targetBody = targetBody;
    }

    public void selectNextBody() {
        if (celestialBodies.isEmpty()) return;
        currentBodyIndex = (currentBodyIndex + 1) % celestialBodies.size;
        setTargetBody(celestialBodies.get(currentBodyIndex));
    }

    public void selectPreviousBody() {
        if (celestialBodies.isEmpty()) return;
        currentBodyIndex = (currentBodyIndex - 1 + celestialBodies.size) % celestialBodies.size;
        setTargetBody(celestialBodies.get(currentBodyIndex));
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY > 0) {
            cameraManager.zoomCamera(zoomOutFactor);
        } else if (amountY < 0) {
            cameraManager.zoomCamera(zoomInFactor);
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) {
            initialTouch1.set(screenX, screenY);
            lastTouch.set(screenX, screenY);
        } else if (pointer == 1) {
            initialTouch2.set(screenX, screenY);
            initialDistance = initialTouch1.dst(initialTouch2);
            isZooming = true;
            lastMidPoint.set((initialTouch1.x + initialTouch2.x) / 2, (initialTouch1.y + initialTouch2.y) / 2);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // If a drag is detected remove the planet lock
        if (targetBody != null) setTargetBody(null);

        if (isZooming && pointer <= 1) {
            if (pointer == 0) {
                initialTouch1.set(screenX, screenY);
            } else if (pointer == 1) {
                initialTouch2.set(screenX, screenY);
            }
            float currentDistance = initialTouch1.dst(initialTouch2);
            if (initialDistance != 0) {
                float zoomFactor = initialDistance / currentDistance;
                cameraManager.zoomCamera(zoomFactor);
            }
            initialDistance = currentDistance;
            Vector2 currentMidPoint = new Vector2((initialTouch1.x + initialTouch2.x) / 2, (initialTouch1.y + initialTouch2.y) / 2);
            float deltaX = currentMidPoint.x - lastMidPoint.x;
            float deltaY = currentMidPoint.y - lastMidPoint.y;
            cameraManager.translateCamera(-deltaX * cameraManager.getCamera().zoom, deltaY * cameraManager.getCamera().zoom);
            lastMidPoint.set(currentMidPoint);
        } else if (!isZooming && pointer == 0) {
            float deltaX = screenX - lastTouch.x;
            float deltaY = screenY - lastTouch.y;
            cameraManager.translateCamera(-deltaX * cameraManager.getCamera().zoom, deltaY * cameraManager.getCamera().zoom);
            lastTouch.set(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == 1) {
            isZooming = false;
            lastTouch.set(initialTouch1.x, initialTouch1.y);
        } else if (pointer == 0 && !isZooming) {
            lastTouch.set(screenX, screenY);
        }
        return false;
    }

    public void update(float deltaTime) {
        if (targetBody != null) {
            float targetX = targetBody.getX() + targetBody.getWidth() / 2;
            float targetY = targetBody.getY() + targetBody.getHeight() / 2;
            cameraManager.getCamera().position.x += (targetX - cameraManager.getCamera().position.x) * LERP_FACTOR;
            cameraManager.getCamera().position.y += (targetY - cameraManager.getCamera().position.y) * LERP_FACTOR;
        } else {
            if (upPressed) {
                cameraManager.translateCamera(0, moveSpeed * cameraManager.getCamera().zoom * deltaTime);
            }
            if (downPressed) {
                cameraManager.translateCamera(0, -moveSpeed * cameraManager.getCamera().zoom * deltaTime);
            }
            if (leftPressed) {
                cameraManager.translateCamera(-moveSpeed * cameraManager.getCamera().zoom * deltaTime, 0);
            }
            if (rightPressed) {
                cameraManager.translateCamera(moveSpeed * cameraManager.getCamera().zoom * deltaTime, 0);
            }
        }
        cameraManager.updateCamera();
    }
}
