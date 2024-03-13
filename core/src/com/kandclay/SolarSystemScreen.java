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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.kandclay.Constants.*;

public class SolarSystemScreen implements Screen {

    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private final SolarSystemGame game;
    private final AssetManager assetManager;
    private CameraController cameraController;
    private World world;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private Batch spriteBatch;
    private float stateTime = 0;
    private final Array<CelestialBody> celestialBodies;
    String backgroundPath;

    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
        this.assetManager = new AssetManager();
        celestialBodies = new Array<CelestialBody>();

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

        assetManager.finishLoading();
    }

    @Override
    public void show() {
        initializeWorld();
        initializeCameraAndViewport();
        initializeStage();
        setupInputProcessors();
        createSolarSystem();
    }

    private void initializeWorld() {
        world = new World(new Vector2(0, 0), true);
    }

    private void initializeCameraAndViewport() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(VIEWPORT_WIDTH_PIXELS_INIT, VIEWPORT_HEIGHT_PIXELS_INIT, worldCamera);
        worldViewport.setScaling(Scaling.contain);
        cameraController = new CameraController(worldCamera, worldViewport, CAMERA_MOVE_SPEED, ZOOM_IN_FACTOR, ZOOM_OUT_FACTOR, this.getCelestialBodies());
    }

    private void initializeStage() {
        worldStage = new Stage(worldViewport);
        spriteBatch = worldStage.getBatch();
    }

    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cameraController);
        // Add other input processors as needed
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createSolarSystem() {

        CelestialBody sun = createSun();

        CelestialBody mercury = createPlanet(SUN, MERCURY_DISTANCE_TO_SUN_PIXELS, MERCURY_RADIUS_PIXELS, MERCURY_ORBIT_SPEED, "mercury", sun);
        MERCURY = celestialBodies.indexOf(mercury, true);
        CelestialBody venus = createPlanet(SUN, VENUS_DISTANCE_TO_SUN_PIXELS, VENUS_RADIUS_PIXELS, VENUS_ORBIT_SPEED, "venus", sun);
        VENUS = celestialBodies.indexOf(venus, true);
        CelestialBody earth = createPlanet(SUN, EARTH_DISTANCE_TO_SUN_PIXELS, EARTH_RADIUS_PIXELS, EARTH_ORBIT_SPEED, "earth", sun);
        EARTH = celestialBodies.indexOf(earth, true);
        CelestialBody moon = createPlanet(EARTH, MOON_DISTANCE_TO_EARTH_PIXELS, MOON_RADIUS_PIXELS, MOON_ORBIT_SPEED, "moon", earth);
        MOON = celestialBodies.indexOf(moon, true);
        CelestialBody mars = createPlanet(SUN, MARS_DISTANCE_TO_SUN_PIXELS, MARS_RADIUS_PIXELS, MARS_ORBIT_SPEED, "mars", sun);
        MARS = celestialBodies.indexOf(mars, true);
        CelestialBody jupiter = createPlanet(SUN, JUPITER_DISTANCE_TO_SUN_PIXELS, JUPITER_RADIUS_PIXELS, JUPITER_ORBIT_SPEED, "jupiter", sun);
        JUPITER = celestialBodies.indexOf(jupiter, true);
        CelestialBody saturn = createPlanet(SUN, SATURN_DISTANCE_TO_SUN_PIXELS, SATURN_RADIUS_PIXELS, SATURN_ORBIT_SPEED, "saturn", sun);
        SATURN = celestialBodies.indexOf(saturn, true);
        CelestialBody uranus = createPlanet(SUN, URANUS_DISTANCE_TO_SUN_PIXELS, URANUS_RADIUS_PIXELS, URANUS_ORBIT_SPEED, "uranus", sun);
        URANUS = celestialBodies.indexOf(uranus, true);
        CelestialBody neptune = createPlanet(SUN, NEPTUNE_DISTANCE_TO_SUN_PIXELS, NEPTUNE_RADIUS_PIXELS, NEPTUNE_ORBIT_SPEED, "neptune", sun);
        NEPTUNE = celestialBodies.indexOf(neptune, true);
    }

    private CelestialBody createSun() {
        Vector2 sunPosition = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        float sunRadius = SUN_RADIUS_PIXELS;
        Animation<TextureRegion> sunAnimation = createAnimationFromAssetManager("sun");
        CelestialBody sun = new CelestialBody(world, "sun", BodyDef.BodyType.StaticBody, sunRadius, 0, sunPosition, spriteBatch, sunAnimation, null, 0);
        celestialBodies.add(sun);
        SUN = celestialBodies.indexOf(sun, true);
        return sun;
    }

    private CelestialBody createPlanet(int orbitedBodyIndex, float distanceToOrbitedBody, float radiusPixels,
                                       float orbitSpeed, String texturePathSuffix, CelestialBody orbitedBody) {
        float planetPositionX = celestialBodies.get(orbitedBodyIndex).getBody().getPosition().x;
        float planetPositionY = celestialBodies.get(orbitedBodyIndex).getBody().getPosition().y;
        float planetRadius = celestialBodies.get(orbitedBodyIndex).getRadius();

        Vector2 position = new Vector2(planetPositionX + distanceToOrbitedBody + planetRadius, planetPositionY);
        float radius = radiusPixels;

        Animation<TextureRegion> animation = createAnimationFromAssetManager(texturePathSuffix);
        CelestialBody celestialBody = new CelestialBody(world, texturePathSuffix, BodyDef.BodyType.DynamicBody,
                radius, orbitSpeed, position, spriteBatch, animation, orbitedBody, distanceToOrbitedBody);
        celestialBody.addClickListener();

        celestialBodies.add(celestialBody);

        return celestialBody;
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

    public void manageOrbits() {
        for (int i = 0; i < celestialBodies.size; i++) {
            CelestialBody body = celestialBodies.get(i);
            body.updateOrbit();
        }
    }

    private void drawSolarSystem() {
        for (int i = 0; i < celestialBodies.size; i++) {
            CelestialBody body = celestialBodies.get(i);

            if (body == celestialBodies.get(SUN))
                body.draw(stateTime, 2f);
            else if (body == celestialBodies.get(SATURN))
                body.draw(stateTime, 3f);
            else
                body.draw(stateTime);
        }
    }

    private void drawBackground() {
        Texture backgroundTexture = assetManager.get(backgroundPath, Texture.class);
        float backgroundWidth = backgroundTexture.getWidth();
        float backgroundHeight = backgroundTexture.getHeight();

        // Calculate visible area considering zoom
        float visibleWidth = worldViewport.getWorldWidth() * worldCamera.zoom;
        float visibleHeight = worldViewport.getWorldHeight() * worldCamera.zoom;

        // Calculate the offset to align the background center with the camera center
        float startX = worldCamera.position.x - (backgroundWidth / 2);
        float startY = worldCamera.position.y - (backgroundHeight / 2);

        // Calculate the required number of tiles to cover the visible area, including extra for zooming out
        int tilesX = (int) Math.ceil(visibleWidth / backgroundWidth) + 2; // Adding 2 for padding
        int tilesY = (int) Math.ceil(visibleHeight / backgroundHeight) + 2; // Adding 2 for padding

        // Adjust for initial layer to be centered
        startX -= (tilesX / 2f) * backgroundWidth;
        startY -= (tilesY / 2f) * backgroundHeight;

        // Draw the background tiles
        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                spriteBatch.draw(backgroundTexture, startX + (x * backgroundWidth), startY + (y * backgroundHeight));
            }
        }
    }


    @Override
    public void render(float delta) {
        stateTime += delta;
        manageOrbits();
        clearScreen();
        stepWorld();

        spriteBatch.begin();
        drawBackground();
        drawSolarSystem();
        spriteBatch.end();

        updateWorldStage(delta);
        update(delta);
        debugRenderer.render(world, worldCamera.combined);
    }

    public void update(float deltaTime) {
        cameraController.update(deltaTime);
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void stepWorld() {
        world.step(1f / PHYSICS_TIME_STEP, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
    }

    private void updateWorldStage(float delta) {
        worldCamera.update();
        worldStage.act(delta);
        worldStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
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

    public Array<CelestialBody> getCelestialBodies() {
        return celestialBodies;
    }
}