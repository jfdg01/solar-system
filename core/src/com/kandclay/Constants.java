package com.kandclay;

public class Constants {
    public static final float ZOOM_IN_FACTOR = 0.9f;
    public static final float ZOOM_OUT_FACTOR = 1.1f;
    public static final int PIXELS_TO_METERS = 50;
    public static final int PHYSICS_TIME_STEP = 240;
    public static final int PHYSICS_VELOCITY_ITERATIONS = 6;
    public static final int PHYSICS_POSITION_ITERATIONS = 2;
    public static final float VIEWPORT_WIDTH_PIXELS_INIT = 400;
    public static final float VIEWPORT_HEIGHT_PIXELS_INIT = 300;
    public static final float CAMERA_MOVE_SPEED = 400f;
    public static final float LERP_FACTOR = 8f / 100f;

    // Animation
    public static final int ANIMATION_NUM_ROWS = 32;
    public static final int ANIMATION_NUM_COLS = 32;
    public static final float FRAME_DURATION = 3f / 100f;

    // Radius in pixels
    public static final float SUN_RADIUS_PIXELS = 100;
    public static final float MERCURY_RADIUS_PIXELS = 8; // Smaller than Earth
    public static final float VENUS_RADIUS_PIXELS = 15; // Slightly smaller than Earth
    public static final float EARTH_RADIUS_PIXELS = 16; // Original value
    public static final float MARS_RADIUS_PIXELS = 12; // Smaller than Earth
    public static final float JUPITER_RADIUS_PIXELS = 90; // Larger than Saturn, smaller than the Sun
    public static final float SATURN_RADIUS_PIXELS = 80; // Adjusted to fit between Jupiter and Uranus
    public static final float URANUS_RADIUS_PIXELS = 60; // Adjusted to fit between Saturn and Neptune
    public static final float NEPTUNE_RADIUS_PIXELS = 58; // Slightly smaller than Uranus
    public static final float MOON_RADIUS_PIXELS = 6; // Relative to Earth

    // Orbit speed
    public static final float MERCURY_ORBIT_SPEED = 4f; // Faster than Earth
    public static final float VENUS_ORBIT_SPEED = 3.5f; // Slightly faster than Earth
    public static final float EARTH_ORBIT_SPEED = 2f; // Original value
    public static final float MARS_ORBIT_SPEED = 1.8f; // Slightly slower than Earth
    public static final float JUPITER_ORBIT_SPEED = 1.3f; // Between Saturn and Uranus
    public static final float SATURN_ORBIT_SPEED = 1.5f; // Original value
    public static final float URANUS_ORBIT_SPEED = 1.2f; // Slower, further than Saturn
    public static final float NEPTUNE_ORBIT_SPEED = 1.1f; // The slowest, further than Uranus
    public static final float MOON_ORBIT_SPEED = 3f; // Relative to Earth

    // Distance to another celestial body in pixels
    public static final float MERCURY_DISTANCE_TO_SUN_PIXELS = 100; // Closest to the Sun
    public static final float VENUS_DISTANCE_TO_SUN_PIXELS = 200; // Closer than Earth
    public static final float EARTH_DISTANCE_TO_SUN_PIXELS = 400; // Original value
    public static final float MARS_DISTANCE_TO_SUN_PIXELS = 600; // Slightly further than Earth
    public static final float JUPITER_DISTANCE_TO_SUN_PIXELS = 800; // Between Mars and Saturn
    public static final float SATURN_DISTANCE_TO_SUN_PIXELS = 1200; // Original value
    public static final float URANUS_DISTANCE_TO_SUN_PIXELS = 1600; // Adjusted for order
    public static final float NEPTUNE_DISTANCE_TO_SUN_PIXELS = 2000; // The farthest listed
    public static final float MOON_DISTANCE_TO_EARTH_PIXELS = 150; // Relative to Earth

    // Enum

    public static int SUN = 0;
    public static int MERCURY = 1;
    public static int VENUS = 2;
    public static int EARTH = 3;
    public static int MOON = 9;
    public static int MARS = 4;
    public static int JUPITER = 5;
    public static int SATURN = 6;
    public static int URANUS = 7;
    public static int NEPTUNE = 8;

}
