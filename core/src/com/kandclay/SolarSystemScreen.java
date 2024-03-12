package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

    private Animation<TextureRegion> createAnimation(Texture texture, int frameWidth, int frameHeight, float frameDuration) {
        TextureRegion[][] tmpFrames = TextureRegion.split(texture, frameWidth, frameHeight);
        int rows = tmpFrames.length;
        int columns = tmpFrames[0].length;
        TextureRegion[] frames = new TextureRegion[rows * columns];

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                frames[index++] = tmpFrames[i][j];
            }
        }

        return new Animation<>(frameDuration, frames);
    }

    private void createSolarSystem() {
        Vector2 sunPosition = new Vector2(Gdx.graphics.getWidth() / 2f / PIXELS_TO_METERS, Gdx.graphics.getHeight() / 2f / PIXELS_TO_METERS);
        sun = new CelestialBody(world, BodyDef.BodyType.StaticBody, SUN_RADIUS_PIXELS / PIXELS_TO_METERS, 0, sunPosition, 1f, spriteBatch);
        sun.setTexture(new Texture(Gdx.files.internal("sprites/Baren.png")));
        sun.setAnimationTexture(new Texture(Gdx.files.internal("sprites/sunAnim.png")));
        sun.setAnimation(createAnimation(sun.getAnimationTexture(), 200, 200, 0.1f));

        Vector2 earthPosition = sun.getBody().getPosition().cpy().add(EARTH_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, 0);
        earth = new CelestialBody(world, BodyDef.BodyType.DynamicBody, EARTH_RADIUS_PIXELS / PIXELS_TO_METERS, EARTH_ORBIT_SPEED, earthPosition, 1f, spriteBatch);
        earth.setTexture(new Texture(Gdx.files.internal("sprites/Baren.png")));
        earth.setAnimationTexture(new Texture(Gdx.files.internal("sprites/earthAnim.png")));
        earth.setAnimation(createAnimation(earth.getAnimationTexture(), 100, 100, 0.1f));

        Vector2 icePlanetPosition = sun.getBody().getPosition().cpy().add(ICE_DISTANCE_TO_SUN_PIXELS / PIXELS_TO_METERS, 0);
        icePlanet = new CelestialBody(world, BodyDef.BodyType.DynamicBody, ICE_RADIUS_PIXELS / PIXELS_TO_METERS, ICE_ORBIT_SPEED, icePlanetPosition, 1f, spriteBatch);
        icePlanet.setTexture(new Texture(Gdx.files.internal("sprites/Ice.png")));

        Vector2 moonPosition = earth.getBody().getPosition().cpy().add(MOON_DISTANCE_TO_EARTH_PIXELS / PIXELS_TO_METERS, 0);
        moon = new CelestialBody(world, BodyDef.BodyType.DynamicBody, MOON_RADIUS_PIXELS / PIXELS_TO_METERS, MOON_ORBIT_SPEED, moonPosition, 1f, spriteBatch);
        moon.setTexture(new Texture(Gdx.files.internal("sprites/Baren.png")));
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

    private void drawBody(CelestialBody body, float scalePixels) {
        if (body.getAnimation() != null) {
            TextureRegion currentFrame = body.getAnimation().getKeyFrame(stateTime, true);
            body.setRegion(currentFrame);
        }

        Vector2 position = body.body.getPosition();
        TextureRegion region = body.getRegion();
        float scale = scalePixels > 0 ? (scalePixels * 2) / region.getRegionWidth() : 1;
        float screenX = position.x * Constants.PIXELS_TO_METERS - region.getRegionWidth() / 2f;
        float screenY = position.y * Constants.PIXELS_TO_METERS - region.getRegionHeight() / 2f;
        float rotation = MathUtils.radiansToDegrees * body.body.getAngle();

        spriteBatch.draw(region, screenX, screenY, region.getRegionWidth() / 2f, region.getRegionHeight() / 2f, region.getRegionWidth(), region.getRegionHeight(), scale, scale, rotation);
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
        backgroundTexture.dispose();
        sun.dispose();
        earth.dispose();
        icePlanet.dispose();
        moon.dispose();
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