package com.kandclay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import static com.kandclay.Constants.*;
import static com.kandclay.Constants.FRAME_DURATION;

public class CelestialBodyFactory {
    private final AssetManager assetManager;
    private final Group planetGroup;
    private final Array<CelestialBodyActor> celestialBodies;

    public CelestialBodyFactory(AssetManager assetManager, Group planetGroup, Array<CelestialBodyActor> celestialBodies) {
        this.assetManager = assetManager;
        this.planetGroup = planetGroup;
        this.celestialBodies = celestialBodies;
    }

    public CelestialBodyActor createSun() {
        float sunRadius = SUN_RADIUS_PIXELS;
        Animation<TextureRegion> sunAnimation = createAnimationFromAssetManager("sun");

        // Calculate the sun's position to be at the center of the screen
        float sunX = (Gdx.graphics.getWidth() / 2f) - sunRadius; // Center the sun on the screen
        float sunY = (Gdx.graphics.getHeight() / 2f) - sunRadius; // Center the sun on the screen

        CelestialBodyActor sun = new CelestialBodyActor("sun", sunRadius, sunAnimation, null, 0, 0);

        sun.setPosition(sunX, sunY);
        planetGroup.addActor(sun);
        sun.setZIndex(2);
        celestialBodies.add(sun);
        SUN = celestialBodies.indexOf(sun, true);

        return sun;
    }

    public CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
                                            float orbitSpeed, String texturePathSuffix) {
        return createPlanet(distanceToOrbitedBody, radiusPixels, orbitSpeed, texturePathSuffix, celestialBodies.get(SUN));
    }

    public CelestialBodyActor createPlanet(float distanceToOrbitedBody, float radiusPixels,
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
        planetGroup.addActor(planet);
        celestialBodies.add(planet);

        return planet;
    }

    public Animation<TextureRegion> createAnimationFromAssetManager(String regionName) {
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
}
