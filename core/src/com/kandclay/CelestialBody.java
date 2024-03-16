package com.kandclay;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CelestialBody extends Actor {

    public String name;
    private float radius;
    private Animation<TextureRegion> animation;
    private float stateTime = 0;
    private CelestialBody orbitedBody;
    private float distanceToOrbitedBody;
    private float orbitSpeed;
    private float currentOrbitAngleRadians = 0f; // Tracks the current angle in radians

    public CelestialBody(String name, float radius, Animation<TextureRegion> animation,
                         CelestialBody orbitedBody, float distanceToOrbitedBody, float orbitSpeed) {
        this.name = name;
        this.radius = radius;
        this.animation = animation;
        this.orbitedBody = orbitedBody;
        this.distanceToOrbitedBody = distanceToOrbitedBody;
        this.orbitSpeed = orbitSpeed;

        setSize(radius * 2, radius * 2); // Assumes the texture's width and height represent the full diameter.
        addClickListener();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        updateOrbit(delta);
    }

    private void updateOrbit(float delta) {
        if (orbitedBody != null) {
            // Assuming orbitSpeed is in degrees per second, convert it to radians per second first
            float orbitSpeedRadiansPerSecond = MathUtils.degreesToRadians * orbitSpeed;

            // Update the current angle based on the orbit speed and elapsed time
            currentOrbitAngleRadians += orbitSpeedRadiansPerSecond * delta;

            // Ensure the angle wraps correctly by keeping it within 0 to 2*PI radians
            currentOrbitAngleRadians %= MathUtils.PI2;

            // Calculate new position using the current orbit angle
            float centerX = orbitedBody.getX() + orbitedBody.getWidth() / 2;
            float centerY = orbitedBody.getY() + orbitedBody.getHeight() / 2;

            float newX = centerX + MathUtils.cos(currentOrbitAngleRadians) * distanceToOrbitedBody - getWidth() / 2;
            float newY = centerY + MathUtils.sin(currentOrbitAngleRadians) * distanceToOrbitedBody - getHeight() / 2;

            // Update the position of the celestial body
            setPosition(newX, newY);
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
}

