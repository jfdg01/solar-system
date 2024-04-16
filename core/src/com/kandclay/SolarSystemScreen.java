package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.kandclay.Constants.*;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game;
    private final AssetManager assetManager;
    private CameraController cameraController;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private Batch spriteBatch;
    private final Array<CelestialBodyActor> celestialBodies;
    String backgroundPath;
    private Group planetGroup;
    private Skin skin;
    private SolarSystemUI solarSystemUI;
    private String skinPath;
    private CelestialBodyFactory celestialBodyFactory;


    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
        this.assetManager = new AssetManager();
        celestialBodies = new Array<>();
        planetGroup = new Group();
        celestialBodyFactory = new CelestialBodyFactory(assetManager, planetGroup, celestialBodies);

        loadAssets();
    }

    private void loadAssets() {
        backgroundPath = "sprites/static/backgroundSimple.png";
        skinPath = "skin/default/skin/uiskin.json";

        assetManager.load(backgroundPath, Texture.class);
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
        skin = assetManager.get(skinPath);
        solarSystemUI = new SolarSystemUI(skin, celestialBodies);
        solarSystemUI.initializeUI();

        setupInputProcessors();
        createSolarSystem();
    }

    private void initializeCameraAndViewport() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(VIEWPORT_WIDTH_PIXELS_INIT, VIEWPORT_HEIGHT_PIXELS_INIT, worldCamera);
        worldViewport.setScaling(Scaling.contain);
        cameraController = new CameraController(worldCamera, CAMERA_MOVE_SPEED, ZOOM_IN_FACTOR, ZOOM_OUT_FACTOR, this.getCelestialBodies());
    }

    private void initializeStage() {
        worldStage = new Stage(worldViewport);
        spriteBatch = worldStage.getBatch();
    }

    private void initializeBackground() {
        Texture backgroundTexture = assetManager.get(backgroundPath, Texture.class);

        Background backgroundActor = new Background(backgroundTexture, worldCamera);

        backgroundActor.setSize(worldViewport.getWorldWidth(), worldViewport.getWorldHeight());

        backgroundActor.setPosition(0, 0);

        // Set lowest Zindex
        backgroundActor.toBack();

        worldStage.addActor(backgroundActor);
    }

    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cameraController);
        multiplexer.addProcessor(worldStage);
        multiplexer.addProcessor(solarSystemUI.getUiStage());
        // Add other input processors as needed
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createSolarSystem() {
        CelestialBodyActor sun = celestialBodyFactory.createSun();

        CelestialBodyActor mercury = celestialBodyFactory.createPlanet(MERCURY_DISTANCE_TO_SUN_PIXELS, MERCURY_RADIUS_PIXELS, MERCURY_ORBIT_SPEED, "mercury");
        MERCURY = celestialBodies.indexOf(mercury, true);
        CelestialBodyActor venus = celestialBodyFactory.createPlanet(VENUS_DISTANCE_TO_SUN_PIXELS, VENUS_RADIUS_PIXELS, VENUS_ORBIT_SPEED, "venus");
        VENUS = celestialBodies.indexOf(venus, true);
        CelestialBodyActor earth = celestialBodyFactory.createPlanet(EARTH_DISTANCE_TO_SUN_PIXELS, EARTH_RADIUS_PIXELS, EARTH_ORBIT_SPEED, "earth");
        EARTH = celestialBodies.indexOf(earth, true);
        // Moon orbiting Earth
        CelestialBodyActor moon = celestialBodyFactory.createPlanet(MOON_DISTANCE_TO_EARTH_PIXELS, MOON_RADIUS_PIXELS, MOON_ORBIT_SPEED, "moon", earth);
        MOON = celestialBodies.indexOf(moon, true);
        CelestialBodyActor mars = celestialBodyFactory.createPlanet(MARS_DISTANCE_TO_SUN_PIXELS, MARS_RADIUS_PIXELS, MARS_ORBIT_SPEED, "mars");
        MARS = celestialBodies.indexOf(mars, true);
        CelestialBodyActor jupiter = celestialBodyFactory.createPlanet(JUPITER_DISTANCE_TO_SUN_PIXELS, JUPITER_RADIUS_PIXELS, JUPITER_ORBIT_SPEED, "jupiter");
        JUPITER = celestialBodies.indexOf(jupiter, true);
        CelestialBodyActor saturn = celestialBodyFactory.createPlanet(SATURN_DISTANCE_TO_SUN_PIXELS, SATURN_RADIUS_PIXELS, SATURN_ORBIT_SPEED, "saturn", sun);
        SATURN = celestialBodies.indexOf(saturn, true);
        CelestialBodyActor uranus = celestialBodyFactory.createPlanet(URANUS_DISTANCE_TO_SUN_PIXELS, URANUS_RADIUS_PIXELS, URANUS_ORBIT_SPEED, "uranus");
        URANUS = celestialBodies.indexOf(uranus, true);
        CelestialBodyActor neptune = celestialBodyFactory.createPlanet(NEPTUNE_DISTANCE_TO_SUN_PIXELS, NEPTUNE_RADIUS_PIXELS, NEPTUNE_ORBIT_SPEED, "neptune");
        NEPTUNE = celestialBodies.indexOf(neptune, true);

        worldStage.addActor(planetGroup);
    }

    @Override
    public void render(float delta) {

        clearScreen();

        solarSystemUI.getUiStage().act(delta);
        updateWorldStage(delta);
        cameraController.update(delta);

        worldStage.draw(); // First draw the world stage
        solarSystemUI.getUiStage().draw(); // Then draw the UI on top
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