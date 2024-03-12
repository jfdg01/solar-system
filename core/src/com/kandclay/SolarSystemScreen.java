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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import static com.kandclay.Constants.*;

public class SolarSystemScreen implements Screen {
    private final SolarSystemGame game;
    private AssetManager assetManager;
    private TextureAtlas atlas;
    private World world;
    private Stage worldStage;
    private ExtendViewport worldViewport;
    private OrthographicCamera worldCamera;
    private Batch spriteBatch;
    private Texture backgroundTexture;
    private float stateTime = 0;
    private CelestialBody sun, earth, icePlanet, moon;


    public SolarSystemScreen(SolarSystemGame game) {
        this.game = game;
        this.assetManager = new AssetManager();
        loadAssets();
    }

    private void loadAssets() {
        assetManager.load("sprites/atlas/solarSystemAssets.atlas", TextureAtlas.class);
        assetManager.finishLoading();

        atlas = assetManager.get("sprites/atlas/solarSystemAssets.atlas", TextureAtlas.class);
    }

    @Override
    public void show() {
        initializeWorld();
        initializeCameraAndViewport();
        initializeStage();
        setupInputProcessors();
        createSolarSystem();
        backgroundTexture = new Texture(Gdx.files.internal("sprites/background.png"));

    }

    private void initializeWorld() {
        world = new World(new Vector2(0, 0), true);
    }

    private void initializeCameraAndViewport() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(VIEWPORT_WIDTH_PIXELS_INIT, VIEWPORT_HEIGHT_PIXELS_INIT, worldCamera);
        worldViewport.setScaling(Scaling.contain);
    }

    private void initializeStage() {
        worldStage = new Stage(worldViewport);
        spriteBatch = worldStage.getBatch();
    }

    private void setupInputProcessors() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.H) {
                    zoomIn();
                    return true;
                } else if (keycode == Keys.J) {
                    zoomOut();
                    return true;
                }
                return false;
            }
        });
        multiplexer.addProcessor(worldStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createSolarSystem() {
        Vector2 sunPosition = new Vector2(Gdx.graphics.getWidth() / 2f / PIXELS_TO_METERS, Gdx.graphics.getHeight() / 2f / PIXELS_TO_METERS);
        float sunRadius = SUN_RADIUS_PIXELS / PIXELS_TO_METERS;
        sun = createCelestialBody(world, BodyDef.BodyType.StaticBody, sunRadius, 0, sunPosition, "sun", "sunAnim", FRAME_DURATION, 200, 200);

        Vector2 earthPosition = new Vector2(sunPosition.x + EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, sunPosition.y);
        float earthRadius = EARTH_RADIUS_PIXELS / PIXELS_TO_METERS;
        earth = createCelestialBody(world, BodyDef.BodyType.DynamicBody, earthRadius, EARTH_ORBIT_SPEED, earthPosition, "terran", "earthAnim", FRAME_DURATION, 100, 100);

        Vector2 icePosition = new Vector2(sunPosition.x + ICE_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, sunPosition.y);
        float iceRadius = ICE_RADIUS_PIXELS / PIXELS_TO_METERS;
        icePlanet = createCelestialBody(world, BodyDef.BodyType.DynamicBody, iceRadius, ICE_ORBIT_SPEED, icePosition, "ice", null, FRAME_DURATION, 100, 100);

        Vector2 moonPosition = new Vector2(earthPosition.x + MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS, earthPosition.y);
        float moonRadius = MOON_RADIUS_PIXELS / PIXELS_TO_METERS;
        moon = createCelestialBody(world, BodyDef.BodyType.DynamicBody, moonRadius, MOON_ORBIT_SPEED, moonPosition, "baren", null, FRAME_DURATION, 100, 100);
    }

    private Animation<TextureRegion> createAnimationFromAtlas(TextureAtlas atlas, String regionName,
                                                              float frameDuration, int tileWidht, int tileHeight) {

        TextureRegion[][] tmpFrames = atlas.findRegion(regionName).split(tileWidht, tileHeight);
        TextureRegion[] animationFrames = new TextureRegion[ANIMATION_NUM_ROWS * ANIMATION_NUM_COLS];

        int index = 0;
        for (int i = 0; i < ANIMATION_NUM_ROWS; i++) {
            for (int j = 0; j < ANIMATION_NUM_COLS; j++) {
                animationFrames[index++] = tmpFrames[i][j];
            }
        }

        return new Animation<>(frameDuration, animationFrames);
    }


    private CelestialBody createCelestialBody(World world, BodyDef.BodyType bodyType, float radius, float orbitSpeed,
                                              Vector2 position, String regionName, String animationPrefix,
                                              float frameDuration, int tileWidth, int tileHeight) {
        TextureRegion staticRegion = null;
        Animation<TextureRegion> animation = (animationPrefix != null && !atlas.findRegions(animationPrefix).isEmpty())
                ? createAnimationFromAtlas(atlas, animationPrefix, frameDuration, tileWidth, tileHeight)
                : null;

        if (animation == null && regionName != null) {
            staticRegion = atlas.findRegion(regionName);
        }

        return new CelestialBody(world, bodyType, radius, orbitSpeed, position, 1f, spriteBatch, staticRegion, animation);
    }

    public void manageOrbits() {
        adjustOrbitVelocity(moon, earth, MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS);
        adjustOrbitVelocity(earth, sun, EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS);
        adjustOrbitVelocity(icePlanet, sun, ICE_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS);
    }

    public void adjustOrbitVelocity(CelestialBody orbitingBody, CelestialBody centralBody, float maxOrbitDistance) {
        Vector2 centerToBody = orbitingBody.body.getPosition().sub(centralBody.body.getPosition());
        float currentDistance = centerToBody.len();

        // Calculate the tangent velocity for the orbiting body
        Vector2 tangentVelocity = new Vector2(-centerToBody.y, centerToBody.x).nor().scl(orbitingBody.getOrbitSpeed());

        // If the current distance exceeds the maximum allowed distance, adjust the velocity to bring the orbiting body closer
        if (currentDistance > maxOrbitDistance) {
            // Calculate a correction vector that points towards the central body
            Vector2 correctionVector = centerToBody.scl(-1).nor().scl(orbitingBody.getOrbitSpeed() * 0.5f); // Apply a gentle pull towards the central body
            tangentVelocity.add(correctionVector);
        }

        // Adjust the orbiting body's velocity, considering the central body's movement as well
        Vector2 adjustedVelocity = tangentVelocity.add(centralBody.body.getLinearVelocity());
        orbitingBody.body.setLinearVelocity(adjustedVelocity.x, adjustedVelocity.y);
    }

    private void drawSolarSystem() {
        drawSun();
        drawEarth();
        drawIce();
        drawMoon();
    }

    private void drawBackground() {
        // Calculate the visible area taking into account zoom and ensuring the background covers the entire screen.
        float visibleWidth = worldViewport.getWorldWidth() * worldCamera.zoom;
        float visibleHeight = worldViewport.getWorldHeight() * worldCamera.zoom;

        // Calculate the offset based on the camera's position to make the background appear stationary.
        // This adjustment makes the background's starting point align with the world's coordinate system.
        float startX = (worldCamera.position.x - visibleWidth / 2) - (worldCamera.position.x % backgroundTexture.getWidth());
        float startY = (worldCamera.position.y - visibleHeight / 2) - (worldCamera.position.y % backgroundTexture.getHeight());

        for (float x = startX; x < startX + visibleWidth; x += backgroundTexture.getWidth()) {
            for (float y = startY; y < startY + visibleHeight; y += backgroundTexture.getHeight()) {
                spriteBatch.draw(backgroundTexture, x, y);
            }
        }
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        manageOrbits();
        clearScreen();
        stepWorld(delta);

        spriteBatch.begin();
        drawBackground();
        drawSolarSystem();
        spriteBatch.end();

        updateWorldStage(delta);
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void stepWorld(float delta) {
        world.step(1f / PHYSICS_TIME_STEP, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
    }

    private void drawSun() {
        //drawBody(sun, SUN_RADIUS_PIXELS * 2);
        sun.draw(stateTime);
    }

    private void drawEarth() {
        // drawBody(earth, EARTH_RADIUS_PIXELS);
        earth.draw(stateTime);
    }

    private void drawIce() {
        //drawBody(icePlanet, ICE_RADIUS_PIXELS);
        icePlanet.draw(stateTime);
    }

    private void drawMoon() {
        //drawBody(moon, MOON_RADIUS_PIXELS);
        moon.draw(stateTime);
    }

    private void updateWorldStage(float delta) {
        worldCamera.update();
        worldStage.act(delta);
        worldStage.draw();
    }

    public void zoomIn() {
        worldCamera.zoom *= ZOOM_IN_FACTOR;
    }

    public void zoomOut() {
        worldCamera.zoom *= ZOOM_OUT_FACTOR;
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
    }

    @Override
    public void dispose() {
        worldStage.dispose();
        assetManager.dispose();
        atlas.dispose();
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
}