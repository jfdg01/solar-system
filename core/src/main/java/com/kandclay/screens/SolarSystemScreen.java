package com.kandclay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.kandclay.Constants;
import com.kandclay.Main;
import com.kandclay.SolarSystemUI;
import com.kandclay.actors.BackgroundActor;
import com.kandclay.actors.CelestialBodyActor;
import com.kandclay.controllers.CameraController;
import com.kandclay.managers.CelestialBodyFactory;
import com.kandclay.managers.StageManager;
import com.kandclay.managers.CameraManager;
import com.kandclay.SolarSystemInputHandler;

public class SolarSystemScreen implements Screen {
    private final Main game;
    private CameraController cameraController;
    private SolarSystemInputHandler inputHandler;
    private final StageManager stageManager;
    private SolarSystemUI solarSystemUI;
    private final Group planetGroup;
    private final Array<CelestialBodyActor> celestialBodies;
    private final CelestialBodyFactory celestialBodyFactory;
    private final CameraManager cameraManager;

    public SolarSystemScreen(Main game) {
        this.game = game;
        this.celestialBodies = new Array<CelestialBodyActor>();
        this.planetGroup = new Group();

        this.cameraManager = new CameraManager(Constants.Camera.VIEWPORT_WIDTH_PIXELS_INIT, Constants.Camera.VIEWPORT_HEIGHT_PIXELS_INIT);
        this.celestialBodyFactory = new CelestialBodyFactory(game.assetManager, planetGroup, celestialBodies);
        this.stageManager = new StageManager(cameraManager);
    }

    @Override
    public void show() {
        initializeCameraController();
        initializeInputHandler();
        stageManager.initializeBackground(game.assetManager.get("sprites/static/backgroundSimple.png", Texture.class), cameraManager.getViewport().getWorldWidth(), cameraManager.getViewport().getWorldHeight());
        solarSystemUI = new SolarSystemUI(game.assetManager.get("skin/default/skin/uiskin.json", Skin.class), celestialBodies, cameraController, inputHandler, game);
        solarSystemUI.initializeUI();

        setupInputProcessors();
        createSolarSystem();
        toggleOrbitDirections();
    }

    private void initializeCameraController() {
        cameraController = new CameraController(cameraManager, Constants.Camera.MOVE_SPEED, Constants.Camera.ZOOM_IN_FACTOR, Constants.Camera.ZOOM_OUT_FACTOR, this.getCelestialBodies());
    }

    private void initializeInputHandler() {
        inputHandler = new SolarSystemInputHandler(cameraController, celestialBodies, solarSystemUI);
    }

    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(solarSystemUI.getUiStage());
        multiplexer.addProcessor(inputHandler);
        multiplexer.addProcessor(stageManager.getWorldStage());
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createSolarSystem() {
        CelestialBodyActor sun = celestialBodyFactory.createSun();
        celestialBodyFactory.createPlanet(Constants.Distance.MERCURY_TO_SUN_PIXELS, Constants.Radius.MERCURY_PIXELS, Constants.OrbitSpeed.MERCURY, "mercury");
        celestialBodyFactory.createPlanet(Constants.Distance.VENUS_TO_SUN_PIXELS, Constants.Radius.VENUS_PIXELS, Constants.OrbitSpeed.VENUS, "venus");
        celestialBodyFactory.createPlanet(Constants.Distance.EARTH_TO_SUN_PIXELS, Constants.Radius.EARTH_PIXELS, Constants.OrbitSpeed.EARTH, "earth");
        celestialBodyFactory.createPlanet(Constants.Distance.MOON_TO_EARTH_PIXELS, Constants.Radius.MOON_PIXELS, 1, Constants.OrbitSpeed.MOON, "moon", celestialBodies.get(Constants.CelestialBody.EARTH.ordinal())); //Orbited body
        celestialBodyFactory.createPlanet(Constants.Distance.MARS_TO_SUN_PIXELS, Constants.Radius.MARS_PIXELS, Constants.OrbitSpeed.MARS, "mars");
        celestialBodyFactory.createPlanet(Constants.Distance.JUPITER_TO_SUN_PIXELS, Constants.Radius.JUPITER_PIXELS, Constants.OrbitSpeed.JUPITER, "jupiter");
        celestialBodyFactory.createPlanet(Constants.Distance.SATURN_TO_SUN_PIXELS, Constants.Radius.SATURN_PIXELS, 3, Constants.OrbitSpeed.SATURN, "saturn", celestialBodies.get(Constants.CelestialBody.SUN.ordinal()));
        celestialBodyFactory.createPlanet(Constants.Distance.URANUS_TO_SUN_PIXELS, Constants.Radius.URANUS_PIXELS, Constants.OrbitSpeed.URANUS, "uranus");
        celestialBodyFactory.createPlanet(Constants.Distance.NEPTUNE_TO_SUN_PIXELS, Constants.Radius.NEPTUNE_PIXELS, Constants.OrbitSpeed.NEPTUNE, "neptune");
        stageManager.getWorldStage().addActor(planetGroup);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        solarSystemUI.getUiStage().act(delta);
        stageManager.update(delta);
        inputHandler.update(delta);
        stageManager.getWorldStage().draw();
        solarSystemUI.getUiStage().draw();
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.update(width, height);
        solarSystemUI.update(width, height, true);
        Actor backgroundActor = stageManager.getWorldStage().getActors().first();
        if (backgroundActor instanceof BackgroundActor) {
            backgroundActor.setSize(cameraManager.getViewport().getWorldWidth(), cameraManager.getViewport().getWorldHeight());
        } else {
            throw new IllegalStateException("First actor in world stage should be Background");
        }
    }

    @Override
    public void dispose() {
        stageManager.getWorldStage().dispose();
        solarSystemUI.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    public Array<CelestialBodyActor> getCelestialBodies() {
        return celestialBodies;
    }

    public void toggleOrbitDirections() {
        if (game.clockwiseDirection) {
            for (CelestialBodyActor body : celestialBodies) {
                body.toggleOrbitDirection();
            }
        }

    }
}
