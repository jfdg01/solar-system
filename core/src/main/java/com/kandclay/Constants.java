package com.kandclay;

public class Constants {
    public static final float LERP_FACTOR = 1f;

    public static class Camera {
        public static final float MOVE_SPEED = 400f;
        public static final float ZOOM_IN_FACTOR = 0.9f;
        public static final float ZOOM_OUT_FACTOR = 1.1f;
        public static final int VIEWPORT_WIDTH_PIXELS_INIT = 400;
        public static final int VIEWPORT_HEIGHT_PIXELS_INIT = 300;
    }

    public static class Animation {
        public static final int NUM_ROWS = 28;
        public static final int NUM_COLS = 28;
        public static final float FRAME_DURATION = 5f / 100f;
    }

    public static class Speed {
        public static final float BASE = 5f;
    }

    public static class Scale {
        public static final float SUN = 2f;
        public static final float SATURN = 3f;
    }

    public static class Radius {
        public static final float SUN_PIXELS = Scale.SUN * 200;
        public static final float MERCURY_PIXELS = 8;
        public static final float VENUS_PIXELS = 33;
        public static final float EARTH_PIXELS = 34;
        public static final float MARS_PIXELS = 17;
        public static final float JUPITER_PIXELS = 100;
        public static final float SATURN_PIXELS = Scale.SATURN * 95;
        public static final float URANUS_PIXELS = 72;
        public static final float NEPTUNE_PIXELS = 71;
        public static final float MOON_PIXELS = 6;
    }

    public static class OrbitSpeed {
        public static final float MERCURY = Speed.BASE * 4f;
        public static final float VENUS = Speed.BASE * 3.5f;
        public static final float EARTH = Speed.BASE * 2f;
        public static final float MARS = Speed.BASE * 1.8f;
        public static final float JUPITER = Speed.BASE * 1.3f;
        public static final float SATURN = Speed.BASE * 1.5f;
        public static final float URANUS = Speed.BASE * 1.2f;
        public static final float NEPTUNE = Speed.BASE * 1.1f;
        public static final float MOON = Speed.BASE * 3f;
    }

    public static class Distance {
        public static final float FACTOR = 1.4f;
        public static final float MERCURY_TO_SUN_PIXELS = 200 * FACTOR;
        public static final float VENUS_TO_SUN_PIXELS = 413 * FACTOR;
        public static final float EARTH_TO_SUN_PIXELS = 633 * FACTOR;
        public static final float MARS_TO_SUN_PIXELS = 862 * FACTOR;
        public static final float JUPITER_TO_SUN_PIXELS = 1117 * FACTOR;
        public static final float SATURN_TO_SUN_PIXELS = 1385 * FACTOR;
        public static final float URANUS_TO_SUN_PIXELS = 1668 * FACTOR;
        public static final float NEPTUNE_TO_SUN_PIXELS = 1960 * FACTOR;
        public static final float MOON_TO_EARTH_PIXELS = 100 * FACTOR;
    }

    public enum CelestialBody {
        SUN,
        MERCURY,
        VENUS,
        EARTH,
        MOON,
        MARS,
        JUPITER,
        SATURN,
        URANUS,
        NEPTUNE
    }

    public static class UI {
        public static final float BUTTON_WIDTH = 150f;
        public static final float BUTTON_HEIGHT = 100f;
        public static final float BUTTON_PADDING = 10f;
        public static final float SLIDER_WIDTH = 300f;
        public static final float SLIDER_HEIGHT = 40f;
        public static final float SLIDER_MARGIN_TOP = 50f;
        public static final float SLIDER_MARGIN_BOTTOM = 60f;
        public static final float SLIDER_X_OFFSET = 150f;
        public static final float BUTTON_X_OFFSET = 10f;
        public static final float PADDING = 20f;
    }
}
