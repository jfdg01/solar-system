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
    private TextButton speed05xButton;
    private TextButton speed1xButton;
    private TextButton speed2xButton;
    private TextButton speed5xButton;
    private TextButton speed10xButton;
    private Stage uiStage;
    private ScreenViewport uiViewport;
    private Array<CelestialBodyActor> celestialBodies;

    public SolarSystemUI(Skin skin, Array<CelestialBodyActor> celestialBodies) {
        this.skin = skin;
        this.celestialBodies = celestialBodies;
        initializeUI();
    }

    public void initializeUI() {
        uiViewport = new ScreenViewport();
        uiStage = new Stage(uiViewport);
        initializeOrbitalPlaneSlider();
        initializeSpeedSlider();
        initializeSpeedButtons();
    }

    private void initializeOrbitalPlaneSlider() {
        orbitAngleSlider = new Slider(0, 1, 0.01f, false, skin);
        orbitAngleSlider.setValue(1f);
        // Height seems to be not taken into account
        orbitAngleSlider.setSize(200, 20);
        // Position the slider at the top left corner of the screen
        orbitAngleSlider.setPosition(0, Gdx.graphics.getHeight() - orbitAngleSlider.getHeight());
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
    }

    private void initializeSpeedSlider() {
        speedSlider = new Slider(0, 10, 0.1f, false, skin);
        speedSlider.setValue(1f);
        speedSlider.setSize(200, 20);
        speedSlider.setPosition(0, orbitAngleSlider.getY() - speedSlider.getHeight() - 10); // 10 is the gap between the two sliders
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
        speed05xButton = new TextButton("0.5x", skin);
        speed1xButton = new TextButton("1x", skin);
        speed2xButton = new TextButton("2x", skin);
        speed5xButton = new TextButton("5x", skin);
        speed10xButton = new TextButton("10x", skin);

        TextButton[] buttons = {speed05xButton, speed1xButton, speed2xButton, speed5xButton, speed10xButton};
        float[] speeds = {0.5f, 1f, 2f, 5f, 10f};

        for (int i = 0; i < buttons.length; i++) {
            TextButton button = buttons[i];
            float speed = speeds[i];

            button.setSize(100, 30);
            button.setPosition(i * (button.getWidth() + 10), 0); // 10 is the gap between the buttons

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
    }
}