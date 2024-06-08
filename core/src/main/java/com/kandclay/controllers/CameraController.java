package com.kandclay.controllers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kandclay.actors.CelestialBodyActor;
import com.kandclay.managers.CameraManager;

import static com.kandclay.Constants.LERP_FACTOR;

public class CameraController {
    private final CameraManager cameraManager;
    private final float moveSpeed;
    private final float zoomInFactor;
    private final float zoomOutFactor;
    private final Array<CelestialBodyActor> celestialBodies;
    private CelestialBodyActor targetBody = null;
    private int currentBodyIndex = -1;
    private final Vector2 lastTouch = new Vector2();
    private final Vector2 initialTouch1 = new Vector2();
    private final Vector2 initialTouch2 = new Vector2();
    private boolean isZooming = false;
    private float initialDistance = 0;
    private final Vector2 lastMidPoint = new Vector2();

    public CameraController(CameraManager cameraManager, float moveSpeed, float zoomInFactor, float zoomOutFactor, Array<CelestialBodyActor> celestialBodies) {
        this.cameraManager = cameraManager;
        this.moveSpeed = moveSpeed;
        this.zoomInFactor = zoomInFactor;
        this.zoomOutFactor = zoomOutFactor;
        this.celestialBodies = celestialBodies;
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

    public void zoomIn() {
        cameraManager.zoomCamera(zoomInFactor);
    }

    public void zoomOut() {
        cameraManager.zoomCamera(zoomOutFactor);
    }

    public void moveCamera(float deltaX, float deltaY, float deltaTime) {
        cameraManager.translateCamera(deltaX * moveSpeed * cameraManager.getCamera().zoom * deltaTime, deltaY * moveSpeed * cameraManager.getCamera().zoom * deltaTime);
    }

    public void handleTouchDown(int screenX, int screenY, int pointer) {
        if (pointer == 0) {
            initialTouch1.set(screenX, screenY);
            lastTouch.set(screenX, screenY);
        } else if (pointer == 1) {
            initialTouch2.set(screenX, screenY);
            initialDistance = initialTouch1.dst(initialTouch2);
            isZooming = true;
            lastMidPoint.set((initialTouch1.x + initialTouch2.x) / 2, (initialTouch1.y + initialTouch2.y) / 2);
        }
    }

    public void handleTouchDragged(int screenX, int screenY, int pointer) {
        if (targetBody != null) {
            targetBody = null;
        }
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
    }

    public void handleTouchUp(int screenX, int screenY, int pointer) {
        if (pointer == 1) {
            isZooming = false;
            lastTouch.set(initialTouch1.x, initialTouch1.y);
        } else if (pointer == 0 && !isZooming) {
            lastTouch.set(screenX, screenY);
        }
    }

    public void update(float deltaTime, boolean upPressed, boolean downPressed, boolean leftPressed, boolean rightPressed) {
        if (targetBody != null) {
            float targetX = targetBody.getX() + targetBody.getWidth() / 2;
            float targetY = targetBody.getY() + targetBody.getHeight() / 2;
            cameraManager.getCamera().position.x += (targetX - cameraManager.getCamera().position.x) * LERP_FACTOR;
            cameraManager.getCamera().position.y += (targetY - cameraManager.getCamera().position.y) * LERP_FACTOR;
        } else {
            if (upPressed) {
                moveCamera(0, 1, deltaTime);
            }
            if (downPressed) {
                moveCamera(0, -1, deltaTime);
            }
            if (leftPressed) {
                moveCamera(-1, 0, deltaTime);
            }
            if (rightPressed) {
                moveCamera(1, 0, deltaTime);
            }
        }
        cameraManager.updateCamera();
    }

    public Array<CelestialBodyActor> getCelestialBodies() {
        return celestialBodies;
    }

    public Camera getCamera() {
        return cameraManager.getCamera();
    }
}
