package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.kandclay.actors.CelestialBodyActor;
import com.kandclay.controllers.CameraController;

public class SolarSystemInputHandler extends InputAdapter {

    private final CameraController cameraController;
    private final SolarSystemUI solarSystemUI;
    private final Array<CelestialBodyActor> celestialBodies;

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public SolarSystemInputHandler(CameraController cameraController, Array<CelestialBodyActor> celestialBodies, SolarSystemUI solarSystemUI) {
        this.cameraController = cameraController;
        this.solarSystemUI = solarSystemUI;
        this.celestialBodies = celestialBodies;
    }

    public Array<CelestialBodyActor> getCelestialBodies() {
        return cameraController.getCelestialBodies();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                upPressed = true;
                cameraController.setTargetBody(null);
                break;
            case Keys.A:
                leftPressed = true;
                cameraController.setTargetBody(null);
                break;
            case Keys.S:
                downPressed = true;
                cameraController.setTargetBody(null);
                break;
            case Keys.D:
                rightPressed = true;
                cameraController.setTargetBody(null);
                break;
            case Keys.H:
            case Keys.UP:
                cameraController.zoomIn();
                break;
            case Keys.J:
            case Keys.DOWN:
                cameraController.zoomOut();
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
                cameraController.setTargetBody(null);
                break;
            case Keys.LEFT:
                cameraController.selectPreviousBody();
                break;
            case Keys.RIGHT:
                cameraController.selectNextBody();
                break;
        }
        if (num >= 0) {
            cameraController.setTargetBody(cameraController.getCelestialBodies().get(num));
        }

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY > 0) {
            cameraController.zoomOut();
        } else if (amountY < 0) {
            cameraController.zoomIn();
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cameraController.handleTouchDown(screenX, screenY, pointer);
        return false;
    }

    private String getCelestialBodyInfo(CelestialBodyActor body) {
        // Construct and return information about the celestial body
        return "Name: " + body.getName() + "\n" +
            "Radius: " + body.getRadius() + "\n" +
            "Orbit Speed: " + body.getOrbitSpeed();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        cameraController.handleTouchDragged(screenX, screenY, pointer);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        cameraController.handleTouchUp(screenX, screenY, pointer);
        return false;
    }

    public void update(float deltaTime) {
        cameraController.update(deltaTime, upPressed, downPressed, leftPressed, rightPressed);
    }

    public CameraController getCameraController() {
        return cameraController;
    }
}
