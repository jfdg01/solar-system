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
    private final float baseOrbitSpeed;
    private float orbitSpeed;
    private float orbitSpeedRadiansPerSecond;
    private boolean isCircularOrbit = true;
    private float currentOrbitAngleRadians = 0f;
    private float ellipseAxisRatio = 1f;
    private float targetEllipseAxisRatio = 1f;
    private float axisRatioInterpolationSpeed = 0.01f;
    private Group parent;

    public CelestialBodyActor(String name, float radius, Animation<TextureRegion> animation,
                              CelestialBodyActor orbitedBody, float distanceToOrbitedBody, float orbitSpeed) {
        this.name = name;
        this.radius = radius;
        this.animation = animation;
        this.orbitedBody = orbitedBody;
        this.distanceToOrbitedBody = distanceToOrbitedBody;
        this.orbitSpeed = orbitSpeed;
        this.baseOrbitSpeed = orbitSpeed;
        this.orbitSpeedRadiansPerSecond = MathUtils.degreesToRadians * orbitSpeed;
        this.parent = this.getParent();

        setSize(radius * 2, radius * 2);
        addClickListener();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        updateEllipseAxisRatio(delta);
        updateOrbitPosition(delta);
    }

    private void updateEllipseAxisRatio(float delta) {
        if (Math.abs(targetEllipseAxisRatio - ellipseAxisRatio) > 0.01f) {
            ellipseAxisRatio += (targetEllipseAxisRatio - ellipseAxisRatio) * axisRatioInterpolationSpeed;
        }
    }

    private void updateOrbitPosition(float delta) {
        if (Objects.equals(name, "sun")) {
            return;
        }

        currentOrbitAngleRadians += orbitSpeedRadiansPerSecond * delta;
        currentOrbitAngleRadians %= MathUtils.PI2;

        float centerX = orbitedBody.getX() + orbitedBody.getWidth() / 2;
        float centerY = orbitedBody.getY() + orbitedBody.getHeight() / 2;

        float semiMajorAxis = distanceToOrbitedBody;
        float semiMinorAxis = distanceToOrbitedBody * ellipseAxisRatio;

        float newOrbitX = centerX + semiMajorAxis * MathUtils.cos(currentOrbitAngleRadians) - getWidth() / 2;
        float newOrbitY = centerY + semiMinorAxis * MathUtils.sin(currentOrbitAngleRadians) - getHeight() / 2;

        setPosition(newOrbitX, newOrbitY);

        float scale = 2 - (0.5f + (1 + MathUtils.sin(currentOrbitAngleRadians)) / 2);
        setScale(scale);

        if (currentOrbitAngleRadians > MathUtils.PI) {
            toFront();
        } else {
            toBack();
        }
    }

    public void toggleOrbitShape() {
        isCircularOrbit = !isCircularOrbit;
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
                System.out.println(name + " clicked!");
            }
        });
    }

    public void setTargetEllipseAxisRatio(float targetEllipseAxisRatio) {
        this.targetEllipseAxisRatio = targetEllipseAxisRatio;
    }

    public float getCurrentOrbitAngleRadians() {
        return this.currentOrbitAngleRadians;
    }

    public void setCurrentOrbitAngleRadians(float targetOrbitAngle) {
        this.currentOrbitAngleRadians = targetOrbitAngle;
    }

    public float getBaseOrbitSpeed() {
        return baseOrbitSpeed;
    }

    public void setOrbitSpeed(float orbitSpeed) {
        this.orbitSpeedRadiansPerSecond = MathUtils.degreesToRadians * orbitSpeed;
    }
}