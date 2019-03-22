package Sheapy.game;

import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class SpikeBoy extends Character {
	private static float width = 75.0f;
	private static float height = 75.0f;
	private static String tPath = "res/SpikeBoyTextures/SpikeBoy.png";
	private boolean isRight = true;
	private int decisionCooldown = 0;
	private final int decisionMaxCooldown = 15;
	private boolean destroyed = false;
	private boolean anger = false;

	public SpikeBoy(float x, float y) {
		super(width, height, 0.9f, Constants.spikeBoyJumpHeight, tPath);
		position.x = x;
		position.y = y;
	}

	// Triggers all spikeBoy updates necessary for one update.
	public void update(Background[] backgrounds) {
		if (!anger) {
			entityTexture = new Texture("res/SpikeBoyTextures/SpikeBoy.png");
		} else {
			entityTexture = new Texture("res/SpikeBoyTextures/AngrySpikeBoy.png");
		}
		
		switch (grounded(position, backgrounds)) {
			case 0:
				fall();
			break;
			case 1:
				reset();
				if (decisionCooldown == 0) {
					if ((int) (Math.random() * 2) == 0) {
						isRight = true;
					} else {
						isRight = false;
					}
					if ((int) (Math.random() * 2) == 0) {
						velocity.y = jumpStrength;
					}
					decisionCooldown = decisionMaxCooldown;
				} else {
					decisionCooldown--;
				}
			break;
			case 2:
				reset();
				fall();
			break;
		}

		if (velocity.x > 0) {
			flipRight();
		} else if (velocity.x < 0) {
			flipLeft();
		}

		position = worldCollide(movement(), backgrounds);
	}

	// Determines the movement of the spikeBoy from the randomly manipulated class variables.
	public Vector3f movement() {
		if (isRight) {
			if (!anger) {
				velocity.x = Constants.maxSpikeBoySpeed;
			} else {
				velocity.x = 2 * Constants.maxSpikeBoySpeed;
			}
		} else {
			if (!anger) {
				velocity.x = -Constants.maxSpikeBoySpeed;
			} else {
				velocity.x = -2 * Constants.maxSpikeBoySpeed;
			}
		}

		return new Vector3f(position.x + velocity.x, position.y + velocity.y, position.z);
	}

	public void explode() {
		destroyed = true;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void hit() {
		if (!anger) {
			anger = true;
		} else {
			explode();
		}
	}
}
