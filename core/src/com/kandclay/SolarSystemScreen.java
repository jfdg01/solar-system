// SolarSystemScreen.java
package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game;
    private final AssetManager assetManager;
    private CameraController cameraController;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private final Array<CelestialBodyActor> celestialBodies;
    private Group planetGroup;
    private Skin skin;
    private SolarSystemUI solarSystemUI;
    private CelestialBodyFactory celestialBodyFactory;

    public SolarSystemScreen(SolarSystemGame game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
        celestialBodies = new Array<>();
        planetGroup = new Group();
        celestialBodyFactory = new CelestialBodyFactory(assetManager, planetGroup, celestialBodies);

        loadAssets();
    }

    private void loadAssets() {
        String backgroundPath = "sprites/static/backgroundSimple.png";
        String skinPath = "skin/default/skin/uiskin.json";

        assetManager.load(backgroundPath, Texture.class);
        // SolarSystemScreen.java (continued)
        assetManager.load(skinPath, Skin.class);
        assetManager.load("sprites/anim/earth.png", Texture.class);
        assetManager.load("sprites/anim/saturn.png", Texture.class);
        assetManager.load("sprites/anim/moon.png", Texture.class);
        assetManager.load("sprites/anim/sun.png", Texture.class);
        assetManager.load("sprites/anim/uranus.png", Texture.class);
        assetManager.load("sprites/anim/venus.png", Texture.class);
        assetManager.load("sprites/anim/jupiter.png", Texture.class);
        assetManager.load("sprites/anim/mars.png", Texture.class);
        assetManager.load("sprites/anim/neptune.png", Texture.class);
        assetManager.load("sprites/anim/mercury.png", Texture.class);
        assetManager.load("sprites/static/ice.png", Texture.class);

        assetManager.finishLoading();
    }

    @Override
    public void show() {
        initializeCameraAndViewport();
        initializeStage();
        initializeBackground();
        skin = assetManager.get("skin/default/skin/uiskin.json", Skin.class);
        solarSystemUI = new SolarSystemUI(skin, celestialBodies);
        solarSystemUI.initializeUI();

        setupInputProcessors();
        createSolarSystem();
    }

    private void initializeCameraAndViewport() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(Constants.Camera.VIEWPORT_WIDTH_PIXELS_INIT, Constants.Camera.VIEWPORT_HEIGHT_PIXELS_INIT, worldCamera);
        worldViewport.setScaling(Scaling.contain);
        cameraController = new CameraController(worldCamera, Constants.Camera.MOVE_SPEED, Constants.Camera.ZOOM_IN_FACTOR, Constants.Camera.ZOOM_OUT_FACTOR, this.getCelestialBodies());
    }

    private void initializeStage() {
        worldStage = new Stage(worldViewport);
    }

    private void initializeBackground() {
        Texture backgroundTexture = assetManager.get("sprites/static/backgroundSimple.png", Texture.class);

        Background backgroundActor = new Background(backgroundTexture, worldCamera);

        backgroundActor.setSize(worldViewport.getWorldWidth(), worldViewport.getWorldHeight());
        backgroundActor.setPosition(0, 0);
        backgroundActor.toBack();

        worldStage.addActor(backgroundActor);
    }

    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cameraController);
        multiplexer.addProcessor(worldStage);
        multiplexer.addProcessor(solarSystemUI.getUiStage());
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createSolarSystem() {
        CelestialBodyActor sun = celestialBodyFactory.createSun();

        CelestialBodyActor mercury = celestialBodyFactory.createPlanet(Constants.Distance.MERCURY_TO_SUN_PIXELS, Constants.Radius.MERCURY_PIXELS, Constants.OrbitSpeed.MERCURY, "mercury");
        Constants.CelestialBody.MERCURY.ordinal();
        CelestialBodyActor venus = celestialBodyFactory.createPlanet(Constants.Distance.VENUS_TO_SUN_PIXELS, Constants.Radius.VENUS_PIXELS, Constants.OrbitSpeed.VENUS, "venus");
        Constants.CelestialBody.VENUS.ordinal();
        CelestialBodyActor earth = celestialBodyFactory.createPlanet(Constants.Distance.EARTH_TO_SUN_PIXELS, Constants.Radius.EARTH_PIXELS, Constants.OrbitSpeed.EARTH, "earth");
        Constants.CelestialBody.EARTH.ordinal();
        CelestialBodyActor moon = celestialBodyFactory.createPlanet(Constants.Distance.MOON_TO_EARTH_PIXELS, Constants.Radius.MOON_PIXELS, Constants.OrbitSpeed.MOON, "moon", earth);
        Constants.CelestialBody.MOON.ordinal();
        CelestialBodyActor mars = celestialBodyFactory.createPlanet(Constants.Distance.MARS_TO_SUN_PIXELS, Constants.Radius.MARS_PIXELS, Constants.OrbitSpeed.MARS, "mars");
        Constants.CelestialBody.MARS.ordinal();
        CelestialBodyActor jupiter = celestialBodyFactory.createPlanet(Constants.Distance.JUPITER_TO_SUN_PIXELS, Constants.Radius.JUPITER_PIXELS, Constants.OrbitSpeed.JUPITER, "jupiter");
        Constants.CelestialBody.JUPITER.ordinal();
        CelestialBodyActor saturn = celestialBodyFactory.createPlanet(Constants.Distance.SATURN_TO_SUN_PIXELS, Constants.Radius.SATURN_PIXELS, Constants.OrbitSpeed.SATURN, "saturn", sun);
        Constants.CelestialBody.SATURN.ordinal();
        CelestialBodyActor uranus = celestialBodyFactory.createPlanet(Constants.Distance.URANUS_TO_SUN_PIXELS, Constants.Radius.URANUS_PIXELS, Constants.OrbitSpeed.URANUS, "uranus");
        Constants.CelestialBody.URANUS.ordinal();
        CelestialBodyActor neptune = celestialBodyFactory.createPlanet(Constants.Distance.NEPTUNE_TO_SUN_PIXELS, Constants.Radius.NEPTUNE_PIXELS, Constants.OrbitSpeed.NEPTUNE, "neptune");
        Constants.CelestialBody.NEPTUNE.ordinal();

        worldStage.addActor(planetGroup);
    }

    @Override
    public void render(float delta) {
        clearScreen();

        solarSystemUI.getUiStage().act(delta);
        updateWorldStage(delta);
        cameraController.update(delta);

        worldStage.draw();
        solarSystemUI.getUiStage().draw();
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void updateWorldStage(float delta) {
        worldStage.act(delta);
        worldCamera.update();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
        solarSystemUI.update(width, height, true);

        Actor backgroundActor = worldStage.getActors().first();
        if (backgroundActor instanceof Background) {
            backgroundActor.setSize(worldViewport.getWorldWidth(), worldViewport.getWorldHeight());
        }
    }

    @Override
    public void dispose() {
        worldStage.dispose();
        solarSystemUI.dispose();
        assetManager.dispose();
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
}