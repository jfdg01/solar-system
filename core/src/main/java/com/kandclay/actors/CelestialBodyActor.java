package com.kandclay.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Logger;

import java.util.Objects;

public class CelestialBodyActor extends Actor {

    public String name;
    private float radius;
    private float touchRadius;
    private final Animation<TextureRegion> animation;
    private float stateTime = 0;
    private final CelestialBodyActor orbitedBody;
    private final float distanceToOrbitedBody;
    private final float orbitSpeed;
    private float orbitSpeedRadiansPerSecond;
    private float currentOrbitAngleRadians = 0f;
    private float ellipseAxisRatio = 1f;
    private float targetEllipseAxisRatio = 1f;
    private final Circle touchableArea;
    private static final Logger logger = new Logger(CelestialBodyActor.class.getName(), Logger.DEBUG);
    private boolean enlargeMode = true;
    private boolean clockwiseDirection = true;


    public CelestialBodyActor(String name, float radius, float touchRadius, Animation<TextureRegion> animation,
                              CelestialBodyActor orbitedBody, float distanceToOrbitedBody, float orbitSpeed) {
        this.name = name;
        this.radius = radius;
        this.touchRadius = touchRadius;
        this.animation = animation;
        this.orbitedBody = orbitedBody;
        this.distanceToOrbitedBody = distanceToOrbitedBody;
        this.orbitSpeed = orbitSpeed;
        this.orbitSpeedRadiansPerSecond = MathUtils.degreesToRadians * orbitSpeed;

        setSize(radius * 2, radius * 2);
        setOrigin(getWidth() / 2, getHeight() / 2);  // Set the origin to the center

        // Initialize the touchable area as a circle
        touchableArea = new Circle(getX() + getWidth() / 2, getY() + getHeight() / 2, touchRadius);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        updateEllipseAxisRatio(delta);
        updateOrbitPosition(delta);

        // Update the position and radius of the touchable area
        float scale = getScaleX();  // Assuming uniform scaling
        touchableArea.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
        touchableArea.setRadius(touchRadius * scale);
    }

    private void updateEllipseAxisRatio(float delta) {
        if (Math.abs(targetEllipseAxisRatio - ellipseAxisRatio) > 0.01f) {
            float axisRatioInterpolationSpeed = 0.01f;
            ellipseAxisRatio += (targetEllipseAxisRatio - ellipseAxisRatio) * axisRatioInterpolationSpeed;
        }
    }

    private void updateOrbitPosition(float delta) {
        if (Objects.equals(name, "sun")) {
            return;
        }

        if (clockwiseDirection) {
            currentOrbitAngleRadians += orbitSpeedRadiansPerSecond * delta;
        } else {
            currentOrbitAngleRadians -= orbitSpeedRadiansPerSecond * delta;
        }
        currentOrbitAngleRadians %= MathUtils.PI2;

        float centerX = orbitedBody.getX() + orbitedBody.getWidth() / 2;
        float centerY = orbitedBody.getY() + orbitedBody.getHeight() / 2;

        float semiMinorAxis = distanceToOrbitedBody * ellipseAxisRatio;

        float newOrbitX = centerX + distanceToOrbitedBody * MathUtils.cos(currentOrbitAngleRadians) - getWidth() / 2;
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public void addClickListener() {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.info(name + " clicked!");
                if (enlargeMode) {
                    augmentSize();
                } else {
                    reduceSize();
                }
            }
        });
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (!isTouchable() || !isVisible()) return null;

        Vector2 touchPoint = new Vector2(x, y);
        Vector2 center = new Vector2(getWidth() / 2, getHeight() / 2);

        if (touchableArea.contains(getX() + x, getY() + y)) {
            return this;
        } else {
            return null;
        }
    }

    public boolean isHit(float x, float y) {
        return touchableArea.contains(x, y);
    }

    private void augmentSize() {
        float scaleFactor = 1.15f;
        float newRadius = radius * scaleFactor;
        float newTouchRadius = touchRadius * scaleFactor;
        float deltaRadius = newRadius - radius;

        setSize(newRadius * 2, newRadius * 2);
        setOrigin(getWidth() / 2, getHeight() / 2);  // Update the origin after resizing
        moveBy(-deltaRadius, -deltaRadius);  // Adjust position to keep the planet centered
        radius = newRadius;  // Update the radius to the new value

        // Adjust the touchable area
        touchableArea.setRadius(newTouchRadius);
        touchRadius = newTouchRadius;
    }

    private void reduceSize() {
        float scaleFactor = 0.85f;
        float newRadius = radius * scaleFactor;
        float newTouchRadius = touchRadius * scaleFactor;
        float deltaRadius = radius - newRadius;

        setSize(newRadius * 2, newRadius * 2);
        setOrigin(getWidth() / 2, getHeight() / 2);  // Update the origin after resizing
        moveBy(deltaRadius, deltaRadius);  // Adjust position to keep the planet centered
        radius = newRadius;  // Update the radius to the new value

        // Adjust the touchable area
        touchableArea.setRadius(newTouchRadius);
        touchRadius = newTouchRadius;
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

    public float getOrbitSpeed() {
        return orbitSpeed;
    }

    public void setOrbitSpeed(float orbitSpeed) {
        this.orbitSpeedRadiansPerSecond = MathUtils.degreesToRadians * orbitSpeed;
    }

    public void setEnlargeMode(boolean enlargeMode) {
        this.enlargeMode = enlargeMode;
    }

    public void toggleOrbitDirection() {
        clockwiseDirection = !clockwiseDirection;
    }

    public String getRadius() {
        return String.valueOf(radius);
    }

    @Override
    public String getName() {
        return name;
    }
}
