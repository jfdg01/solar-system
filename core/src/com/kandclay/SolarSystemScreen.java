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
import com.badlogic.gdx.scenes.scene2d.Stage;
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

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
        this.assetManager = new AssetManager();
        celestialBodies = new Array<CelestialBodyActor>();

        loadAssets();
    }

    private void loadAssets() {
        backgroundPath = "sprites/static/backgroundSimple.png";

        assetManager.load(backgroundPath, Texture.class);
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

        BackgroundActor backgroundActor = new BackgroundActor(backgroundTexture, worldCamera);

        backgroundActor.setSize(worldViewport.getWorldWidth(), worldViewport.getWorldHeight());

        backgroundActor.setPosition(0, 0);

        worldStage.addActor(backgroundActor);
    }


    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cameraController);
        multiplexer.addProcessor(worldStage);
        // Add other input processors as needed
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createSolarSystem() {
        CelestialBodyActor sun = createSun();

        CelestialBodyActor mercury = createPlanet(MERCURY_DISTANCE_TO_SUN_PIXELS, MERCURY_RADIUS_PIXELS, MERCURY_ORBIT_SPEED, "mercury");
        MERCURY = celestialBodies.indexOf(mercury, true);
        CelestialBodyActor venus = createPlanet(VENUS_DISTANCE_TO_SUN_PIXELS, VENUS_RADIUS_PIXELS, VENUS_ORBIT_SPEED, "venus");
        VENUS = celestialBodies.indexOf(venus, true);
        CelestialBodyActor earth = createPlanet(EARTH_DISTANCE_TO_SUN_PIXELS, EARTH_RADIUS_PIXELS, EARTH_ORBIT_SPEED, "earth");
        EARTH = celestialBodies.indexOf(earth, true);
        // Moon orbiting Earth
        CelestialBodyActor moon = createPlanet(MOON_DISTANCE_TO_EARTH_PIXELS, MOON_RADIUS_PIXELS, MOON_ORBIT_SPEED, "moon", earth);
        MOON = celestialBodies.indexOf(moon, true);
        CelestialBodyActor mars = createPlanet(MARS_DISTANCE_TO_SUN_PIXELS, MARS_RADIUS_PIXELS, MARS_ORBIT_SPEED, "mars");
        MARS = celestialBodies.indexOf(mars, true);
        CelestialBodyActor jupiter = createPlanet(JUPITER_DISTANCE_TO_SUN_PIXELS, JUPITER_RADIUS_PIXELS, JUPITER_ORBIT_SPEED, "jupiter");
        JUPITER = celestialBodies.indexOf(jupiter, true);
        CelestialBodyActor saturn = createPlanet(SATURN_DISTANCE_TO_SUN_PIXELS, SATURN_RADIUS_PIXELS, SATURN_ORBIT_SPEED, "saturn", sun);
        SATURN = celestialBodies.indexOf(saturn, true);
        CelestialBodyActor uranus = createPlanet(URANUS_DISTANCE_TO_SUN_PIXELS, URANUS_RADIUS_PIXELS, URANUS_ORBIT_SPEED, "uranus");
        URANUS = celestialBodies.indexOf(uranus, true);
        CelestialBodyActor neptune = createPlanet(NEPTUNE_DISTANCE_TO_SUN_PIXELS, NEPTUNE_RADIUS_PIXELS, NEPTUNE_ORBIT_SPEED, "neptune");
        NEPTUNE = celestialBodies.indexOf(neptune, true);
    }


    private CelestialBodyActor createSun() {
        float sunRadius = SUN_RADIUS_PIXELS;
        Animation<TextureRegion> sunAnimation = createAnimationFromAssetManager("sun");

        // Calculate the sun's position to be at the center of the screen
        float sunX = (Gdx.graphics.getWidth() / 2f) - sunRadius; // Center the sun on the screen
        float sunY = (Gdx.graphics.getHeight() / 2f) - sunRadius; // Center the sun on the screen

        CelestialBodyActor sun = new CelestialBodyActor("sun", sunRadius, sunAnimation, null, 0, 0);

        sun.setPosition(sunX, sunY);
        worldStage.addActor(sun);

        celestialBodies.add(sun);
        SUN = celestialBodies.indexOf(sun, true);

        return sun;
    }

    // Reduce visual clutter
    private CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
                                            float orbitSpeed, String texturePathSuffix) {
        return createPlanet(distanceToOrbitedBody, radiusPixels, orbitSpeed, texturePathSuffix, celestialBodies.get(SUN));
    }

    private CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
                                            float orbitSpeed, String texturePathSuffix, CelestialBodyActor orbitedBody) {

        float orbitedBodyX = orbitedBody.getX() + orbitedBody.getWidth() / 2; // Center X of the orbited body
        float orbitedBodyY = orbitedBody.getY() + orbitedBody.getHeight() / 2; // Center Y of the orbited body

        // For simplicity, initially place the planet right of its orbited body. Adjust as needed for actual orbit.
        float planetX = orbitedBodyX + distanceToOrbitedBody;
        float planetY = orbitedBodyY - radiusPixels; // Adjust if you want the initial position differently

        Animation<TextureRegion> animation = createAnimationFromAssetManager(texturePathSuffix);

        // Create the planet CelestialBody without needing Box2D physics parameters
        CelestialBodyActor planet = new CelestialBodyActor(texturePathSuffix, radiusPixels, animation, orbitedBody, distanceToOrbitedBody, orbitSpeed);

        // Set the planet's position
        planet.setPosition(planetX, planetY);

        // Add the planet to the stage and celestial bodies list
        worldStage.addActor(planet);
        celestialBodies.add(planet);

        return planet;
    }

    private Animation<TextureRegion> createAnimationFromAssetManager(String regionName) {
        String name = "sprites/anim/" + regionName + ".png";
        Texture texture = assetManager.get(name, Texture.class);

        // Assuming each frame is of equal size, calculate the width and height of each frame
        int frameWidth = texture.getWidth() / ANIMATION_NUM_COLS;
        int frameHeight = texture.getHeight() / ANIMATION_NUM_ROWS;

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[ANIMATION_NUM_ROWS * ANIMATION_NUM_COLS];

        int index = 0;
        for (int i = 0; i < ANIMATION_NUM_ROWS; i++) {
            for (int j = 0; j < ANIMATION_NUM_COLS; j++) {
                animationFrames[index++] = tmpFrames[i][j];
            }
        }

        return new Animation<TextureRegion>(FRAME_DURATION, animationFrames);
    }

    @Override
    public void render(float delta) {

        clearScreen();

        updateWorldStage(delta);
        cameraController.update(delta);

        worldStage.draw();
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    private void updateWorldStage(float delta) {
        worldCamera.update();
        worldStage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);

        Actor backgroundActor = worldStage.getActors().first();
        if (backgroundActor instanceof BackgroundActor) {
            backgroundActor.setSize(worldViewport.getWorldWidth(), worldViewport.getWorldHeight());
        }
    }

    @Override
    public void dispose() {
        worldStage.dispose();
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