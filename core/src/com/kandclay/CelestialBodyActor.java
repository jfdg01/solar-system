package com.kandclay;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Objects;

public class CelestialBodyActor extends Actor {

    public String name;
    private float radius;
    private Animation<TextureRegion> animation;
    private float stateTime = 0;
    private CelestialBodyActor orbitedBody;
    private float distanceToOrbitedBody;
    private float orbitSpeed;
    private float orbitSpeedRadiansPerSecond;
    private boolean isCircularOrbit = true;
    private float currentOrbitAngleRadians = 0f; // Tracks the current angle in radians
    private float axisRatio = 1f; // Current axis ratio
    private float targetAxisRatio = 1f; // Target axis ratio for interpolation
    private float interpolationSpeed = 0.01f; // Speed of interpolation
    private Group parent;

    public CelestialBodyActor(String name, float radius, Animation<TextureRegion> animation,
                              CelestialBodyActor orbitedBody, float distanceToOrbitedBody, float orbitSpeed) {
        this.name = name;
        this.radius = radius;
        this.animation = animation;
        this.orbitedBody = orbitedBody;
        this.distanceToOrbitedBody = distanceToOrbitedBody;
        this.orbitSpeed = orbitSpeed;
        this.orbitSpeedRadiansPerSecond = MathUtils.degreesToRadians * orbitSpeed;
        this.parent = this.getParent();

        setSize(radius * 2, radius * 2); // Assumes the texture's width and height represent the full diameter.
        addClickListener();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (Math.abs(targetAxisRatio - axisRatio) > 0.01f) {
            axisRatio += (targetAxisRatio - axisRatio) * interpolationSpeed;
        }

        updateOrbit(delta, axisRatio);
    }

    public void switchOrbitType() {

        if (isCircularOrbit) {
            targetAxisRatio = 0.05f; // For an elliptical orbit
        } else {
            targetAxisRatio = 1f; // For a circular orbit
        }

        isCircularOrbit = !isCircularOrbit;
    }

    private void updateOrbit(float delta, float axisRatio) {

        System.out.println("Name: " + name + ", zi: " + getZIndex());

        if (Objects.equals(name, "sun")) {
            return;
        }

        // Update the current angle based on the orbit speed and elapsed time
        currentOrbitAngleRadians += orbitSpeedRadiansPerSecond * delta;

        // Ensure the angle wraps correctly by keeping it within 0 to 2*PI radians
        if (currentOrbitAngleRadians >= MathUtils.PI2) {
            currentOrbitAngleRadians %= MathUtils.PI2;
        }

        // Calculate new position using the current orbit angle
        float centerX = orbitedBody.getX() + orbitedBody.getWidth() / 2;
        float centerY = orbitedBody.getY() + orbitedBody.getHeight() / 2;

        // Semi-major axis and semi-minor axis
        float a = distanceToOrbitedBody; // Maximum distance to orbited body
        float b = distanceToOrbitedBody * axisRatio; // axisRatio of the maximum distance

        float newX = centerX + a * MathUtils.cos(currentOrbitAngleRadians) - getWidth() / 2;
        float newY = centerY + b * MathUtils.sin(currentOrbitAngleRadians) - getHeight() / 2;

        // Update the position of the celestial body
        setPosition(newX, newY);

        // Adjust the scale based on the current orbit angle
        float scale = 2 - (0.5f + (1 + MathUtils.sin(currentOrbitAngleRadians)) / 2); // Subtract the current scale from 2 to invert the scaling effect
        setScale(scale);

        // If in the second half of the orbit, set the Z position to be in front
        if (currentOrbitAngleRadians > MathUtils.PI) {
            // Set the Z position to be the first
            toFront();
        } else {
            // Set the Z position to be the last
            toBack();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private void addClickListener() {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Handle click event. This could be used to select the celestial body or display information about it.
                System.out.println(name + " clicked!");
            }
        });
    }

    public void setTargetAxisRatio(float targetAxisRatio) {
        this.targetAxisRatio = targetAxisRatio;
    }
}

