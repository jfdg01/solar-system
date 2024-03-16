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

        // Orbit logic could be implemented here if desired, affecting the position.
        updateOrbit(delta);
    }

    private void updateOrbit(float delta) {
        // Implement logic to update the position of the celestial body based on its orbit
        // This is a placeholder for custom orbit logic.
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


    public float getOrbitSpeed() {
        return orbitSpeed;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setOrbitSpeed(float orbitSpeed) {
        this.orbitSpeed = orbitSpeed;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }
}

