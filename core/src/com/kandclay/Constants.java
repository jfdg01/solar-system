package com.kandclay;

public class Constants {
    public static final float ZOOM_IN_FACTOR = 0.9f;
    public static final float ZOOM_OUT_FACTOR = 1.1f;
    public static final float VIEWPORT_WIDTH_PIXELS_INIT = 400;
    public static final float VIEWPORT_HEIGHT_PIXELS_INIT = 300;
    public static final float CAMERA_MOVE_SPEED = 400f;
    public static final float LERP_FACTOR = 8f / 100f;

    // Animation
    public static final int ANIMATION_NUM_ROWS = 28;
    public static final int ANIMATION_NUM_COLS = 28;
    public static final float FRAME_DURATION = 5f / 100f;
    public static final float SPEED = 5f;
    public static final float SUN_SCALE = 2f;
    public static final float SATURN_SCALE = 3f;

    // Radius in pixels
    public static final float SUN_RADIUS_PIXELS = SUN_SCALE * 200;
    public static final float MERCURY_RADIUS_PIXELS = 8;
    public static final float VENUS_RADIUS_PIXELS = 33;
    public static final float EARTH_RADIUS_PIXELS = 34;
    public static final float MARS_RADIUS_PIXELS = 17;
    public static final float JUPITER_RADIUS_PIXELS = 100;
    public static final float SATURN_RADIUS_PIXELS = SATURN_SCALE * 95;
    public static final float URANUS_RADIUS_PIXELS = 72;
    public static final float NEPTUNE_RADIUS_PIXELS = 71;
    public static final float MOON_RADIUS_PIXELS = 6;

    // Orbit speed
    public static final float MERCURY_ORBIT_SPEED = SPEED * 4f;
    public static final float VENUS_ORBIT_SPEED = SPEED * 3.5f;
    public static final float EARTH_ORBIT_SPEED = SPEED * 2f;
    public static final float MARS_ORBIT_SPEED = SPEED * 1.8f;
    public static final float JUPITER_ORBIT_SPEED = SPEED * 1.3f;
    public static final float SATURN_ORBIT_SPEED = SPEED * 1.5f;
    public static final float URANUS_ORBIT_SPEED = SPEED * 1.2f;
    public static final float NEPTUNE_ORBIT_SPEED = SPEED * 1.1f;
    public static final float MOON_ORBIT_SPEED = SPEED * 3f;

    // Distance to another celestial body in pixels
    public static final float FACTOR = 1.4f;
    public static final float MERCURY_DISTANCE_TO_SUN_PIXELS = 200 * FACTOR;
    public static final float VENUS_DISTANCE_TO_SUN_PIXELS = 413 * FACTOR;
    public static final float EARTH_DISTANCE_TO_SUN_PIXELS = 633 * FACTOR;
    public static final float MARS_DISTANCE_TO_SUN_PIXELS = 862 * FACTOR;
    public static final float JUPITER_DISTANCE_TO_SUN_PIXELS = 1117 * FACTOR;
    public static final float SATURN_DISTANCE_TO_SUN_PIXELS = 1385 * FACTOR;
    public static final float URANUS_DISTANCE_TO_SUN_PIXELS = 1668 * FACTOR;
    public static final float NEPTUNE_DISTANCE_TO_SUN_PIXELS = 1960 * FACTOR;
    public static final float MOON_DISTANCE_TO_EARTH_PIXELS = 100 * FACTOR;

    // Enum
    public static int SUN = 0;
    public static int MERCURY = 1;
    public static int VENUS = 2;
    public static int EARTH = 3;
    public static int MOON = 4;
    public static int MARS = 5;
    public static int JUPITER = 6;
    public static int SATURN = 7;
    public static int URANUS = 8;
    public static int NEPTUNE = 9;

}
