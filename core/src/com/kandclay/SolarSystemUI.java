// SolarSystemUI.java
package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Objects;

public class SolarSystemUI {
    private Skin skin;
    private Slider orbitAngleSlider;
    private Slider speedSlider;
    private TextButton[] speedButtons;
    private Stage uiStage;
    private ScreenViewport uiViewport;
    private Array<CelestialBodyActor> celestialBodies;

    public SolarSystemUI(Skin skin, Array<CelestialBodyActor> celestialBodies) {
        this.skin = skin;
        this.celestialBodies = celestialBodies;
    }

    public void initializeUI() {
        uiViewport = new ScreenViewport();
        uiStage = new Stage(uiViewport);
        initializeSliders();
        initializeSpeedButtons();
    }

    private void initializeSliders() {
        orbitAngleSlider = new Slider(0, 1, 0.01f, false, skin);
        orbitAngleSlider.setValue(1f);
        orbitAngleSlider.setSize(200, 20);
        orbitAngleSlider.setPosition(uiViewport.getWorldWidth() - orbitAngleSlider.getWidth(), uiViewport.getWorldHeight() - orbitAngleSlider.getHeight());
        orbitAngleSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float targetEllipseAxisRatio = orbitAngleSlider.getValue();
                for (CelestialBodyActor body : celestialBodies) {
                    body.setTargetEllipseAxisRatio(targetEllipseAxisRatio);
                }
            }
        });

        uiStage.addActor(orbitAngleSlider);

        speedSlider = new Slider(0, 10, 0.1f, false, skin);
        speedSlider.setValue(1f);
        speedSlider.setSize(200, 20);
        speedSlider.setPosition(uiViewport.getWorldWidth() - speedSlider.getWidth(), orbitAngleSlider.getY() - speedSlider.getHeight() - 10);
        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float speed = speedSlider.getValue();
                for (CelestialBodyActor body : celestialBodies) {
                    if (!Objects.equals(body.getName(), "sun")) {
                        body.setOrbitSpeed(body.getBaseOrbitSpeed() * speed);
                    }
                }
            }
        });
        uiStage.addActor(speedSlider);
    }

    private void initializeSpeedButtons() {
        float[] speeds = {0.5f, 1f, 2f, 5f, 10f};
        speedButtons = new TextButton[speeds.length];

        for (int i = 0; i < speeds.length; i++) {
            TextButton button = new TextButton(String.valueOf(speeds[i]), skin);
            speedButtons[i] = button;

            button.setSize(100, 30);
            button.setPosition(i * (button.getWidth() + 10), 0);

            float speed = speeds[i];
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    for (CelestialBodyActor body : celestialBodies) {
                        if (!Objects.equals(body.getName(), "sun")) {
                            body.setOrbitSpeed(body.getBaseOrbitSpeed() * speed);
                        }
                    }
                }
            });

            uiStage.addActor(button);
        }
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public void dispose() {
        uiStage.dispose();
    }

    public void update(int width, int height, boolean b) {
        uiViewport.update(width, height, b);
        orbitAngleSlider.setPosition(uiViewport.getWorldWidth() - orbitAngleSlider.getWidth(), uiViewport.getWorldHeight() - orbitAngleSlider.getHeight());
        speedSlider.setPosition(uiViewport.getWorldWidth() - speedSlider.getWidth(), orbitAngleSlider.getY() - speedSlider.getHeight() - 10);
    }
}