package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kandclay.actors.CelestialBodyActor;
import com.kandclay.controllers.CameraController;
import com.kandclay.screens.MenuScreen;

import java.util.Objects;

import static com.kandclay.Constants.UI.*;

public class SolarSystemUI {
    private final Skin skin;
    private final Array<CelestialBodyActor> celestialBodies;
    private final Main game;
    private Slider orbitAngleSlider;
    private Slider speedSlider;
    private Label orbitAngleLabel;
    private Label speedLabel;
    private TextButton modeButton;
    private TextButton nextButton;
    private TextButton prevButton;
    private TextButton toggleDirectionButton;
    private TextButton mainMenuButton;
    private Stage uiStage;
    private ScreenViewport uiViewport;
    private boolean enlargeMode = true;
    private CameraController cameraController;
    private WidgetGroup uiGroup;
    private Window infoWindow;
    private Label infoLabel;

    public SolarSystemUI(Skin skin, Array<CelestialBodyActor> celestialBodies, CameraController cameraController, Main game) {
        this.skin = skin;
        this.celestialBodies = celestialBodies;
        this.cameraController = cameraController;
        this.game = game;
    }

    public void initializeUI() {
        uiViewport = new ScreenViewport();
        uiStage = new Stage(uiViewport);
        uiGroup = new WidgetGroup();
        uiStage.addActor(uiGroup);

        initializeModeButton();
        initializeSliders();
        initializeNavigationButtons();
        initializeToggleDirectionButton();
        initializeMainMenuButton();
        initializeInfoWindow();

        addStageClickListener();
        updatePositions();
    }

    private void initializeInfoWindow() {
        infoWindow = new Window("Planet Info", skin);
        infoLabel = new Label("", skin);
        infoWindow.add(infoLabel).row();
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infoWindow.setVisible(false);
            }
        });
        infoWindow.add(closeButton);
        infoWindow.pack();
        infoWindow.setVisible(false);
        uiStage.addActor(infoWindow);
    }

    public void showInfoWindow(float screenX, float screenY, String info) {
        infoLabel.setText(info);
        infoWindow.pack();
        infoWindow.setPosition(screenX, screenY);
        infoWindow.setVisible(true);
    }

    public void hideInfoWindow() {
        infoWindow.setVisible(false);
    }

    private void addStageClickListener() {
        uiStage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector2 stageCoords = new Vector2(x, y);
                String info = "Clicked at: " + stageCoords.x + ", " + stageCoords.y; // Example info
                showInfoWindow(stageCoords.x, stageCoords.y, info);
            }
        });
    }

    private void initializeSliders() {
        orbitAngleSlider = createOrbitAngleSlider();
        speedSlider = createSpeedSlider();

        orbitAngleLabel = new Label("Orbital Plane Angle", skin);
        speedLabel = new Label("Orbital Speed", skin);

        uiGroup.addActor(orbitAngleLabel);
        uiGroup.addActor(orbitAngleSlider);
        uiGroup.addActor(speedLabel);
        uiGroup.addActor(speedSlider);
    }

    private Slider createOrbitAngleSlider() {
        final Slider slider = new Slider(0, 1, 0.01f, false, skin);
        slider.setValue(1f);
        slider.setSize(SLIDER_WIDTH, SLIDER_HEIGHT);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float targetEllipseAxisRatio = slider.getValue();
                for (CelestialBodyActor body : celestialBodies) {
                    body.setTargetEllipseAxisRatio(targetEllipseAxisRatio);
                }
            }
        });
        return slider;
    }

    private Slider createSpeedSlider() {
        final Slider slider = new Slider(0, 10, 0.1f, false, skin);
        slider.setValue(1f);
        slider.setSize(SLIDER_WIDTH, SLIDER_HEIGHT);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float speed = slider.getValue();
                for (CelestialBodyActor body : celestialBodies) {
                    if (!Objects.equals(body.getName(), "sun")) {
                        body.setOrbitSpeed(body.getOrbitSpeed() * speed);
                    }
                }
            }
        });
        return slider;
    }

    private void initializeModeButton() {
        modeButton = new TextButton("Switch to Shrink Mode", skin);
        modeButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        modeButton.getLabel().setWrap(true);
        modeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                enlargeMode = !enlargeMode;
                modeButton.setText(enlargeMode ? "Switch to Shrink Mode" : "Switch to Enlarge Mode");
                for (CelestialBodyActor body : celestialBodies) {
                    body.setEnlargeMode(enlargeMode);
                }
            }
        });
        uiGroup.addActor(modeButton);
    }

    private void initializeNavigationButtons() {
        float buttonWidth = BUTTON_WIDTH / 2;

        prevButton = new TextButton("Previous", skin);
        prevButton.setSize(buttonWidth, BUTTON_HEIGHT);
        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cameraController.selectPreviousBody();
            }
        });

        nextButton = new TextButton("Next", skin);
        nextButton.setSize(buttonWidth, BUTTON_HEIGHT);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cameraController.selectNextBody();
            }
        });

        uiGroup.addActor(prevButton);
        uiGroup.addActor(nextButton);
    }

    private void initializeToggleDirectionButton() {
        toggleDirectionButton = new TextButton("Toggle Orbital Direction", skin);
        toggleDirectionButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        toggleDirectionButton.getLabel().setWrap(true);
        toggleDirectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (CelestialBodyActor body : celestialBodies) {
                    body.toggleOrbitDirection();
                }
            }
        });
        uiGroup.addActor(toggleDirectionButton);
    }

    private void initializeMainMenuButton() {
        mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        mainMenuButton.getLabel().setWrap(true);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        uiGroup.addActor(mainMenuButton);
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public void dispose() {
        uiStage.dispose();
    }

    public void update(int width, int height, boolean centerCamera) {
        uiViewport.update(width, height, centerCamera);
        updatePositions();
    }

    private void updatePositions() {
        modeButton.setPosition(Constants.UI.PADDING, uiViewport.getWorldHeight() - BUTTON_HEIGHT - Constants.UI.PADDING);

        orbitAngleLabel.setPosition(modeButton.getX() + modeButton.getWidth() + Constants.UI.PADDING, modeButton.getY() + modeButton.getHeight() - orbitAngleLabel.getHeight());
        orbitAngleSlider.setPosition(orbitAngleLabel.getX(), orbitAngleLabel.getY() - orbitAngleLabel.getHeight() - Constants.UI.PADDING);

        speedLabel.setPosition(orbitAngleLabel.getX(), orbitAngleSlider.getY() - orbitAngleSlider.getHeight() - Constants.UI.PADDING);
        speedSlider.setPosition(speedLabel.getX(), speedLabel.getY() - speedLabel.getHeight() - Constants.UI.PADDING);

        float buttonWidth = BUTTON_WIDTH / 2;
        prevButton.setPosition(modeButton.getX(), modeButton.getY() - BUTTON_HEIGHT - Constants.UI.PADDING);
        nextButton.setPosition(prevButton.getX() + buttonWidth, prevButton.getY());

        toggleDirectionButton.setPosition(prevButton.getX(), prevButton.getY() - BUTTON_HEIGHT - Constants.UI.PADDING);
        mainMenuButton.setPosition(toggleDirectionButton.getX(), toggleDirectionButton.getY() - BUTTON_HEIGHT - Constants.UI.PADDING);
    }
}
