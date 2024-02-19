package com.kandclay;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SolarSystemGame extends Game {
	// SpriteBatch can be shared across Screens, if necessary
	SpriteBatch batch;

	@Override
	public void create() {
		batch = new SpriteBatch();
		// Set the screen to your SolarSystemScreen
		this.setScreen(new SolarSystemScreen(this));
	}

	@Override
	public void dispose() {
		if (batch != null) {
			batch.dispose();
		}
	}
}
