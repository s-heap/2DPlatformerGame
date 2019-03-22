package Sheapy.game;

import org.lwjgl.glfw.GLFW;

import Sheapy.Input;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class Player extends Character {
	private final static float initialWidth = 50.0f;
	private final static float initialHeight = 50.0f;
	private static String tPath = "res/PlayerTextures/archerStill.png";
	private int hitCount = 0;
	private Arm arm;
	private boolean isWalk1 = true;
	private int walkTimer = 0;

	public Player() {
		super(initialWidth, initialHeight, 0.95f, Constants.playerJumpHeight, tPath);
		arm = new Arm((float) (width * 0.7), (float) (height * 0.7), 1f);
	}

	// Everything regarding the player's action during a single update is applied in this method, from movement to collisions.
	public void update(Background[] backgrounds, SpikeBoy[] spikeBoys, WorldButton[] buttons, float angle, float cooldown) {
		
		if (angle < 360 && angle > 180) {
			flipLeft();
		} else if (angle > 0 && angle < 180) {
			flipRight();
		}

		int grounded = grounded(position, backgrounds);
		switch (grounded) {
			case 0:
				fall();
				entityTexture = new Texture("res/PlayerTextures/archerAirtime.png");
			break;
			case 1:
				reset();
				if (Input.keys[GLFW.GLFW_KEY_SPACE] || Input.keys[GLFW.GLFW_KEY_W]) {
					velocity.y = jumpStrength;
				}
				if (velocity.x != 0) {
					if (walkTimer > 10) {
						if (isWalk1) {
							entityTexture = new Texture("res/PlayerTextures/archerWalk2.png");
							isWalk1 = !isWalk1;
						} else {
							entityTexture = new Texture("res/PlayerTextures/archerWalk1.png");
							isWalk1 = !isWalk1;
						}
						walkTimer = 0;
					} else {
						walkTimer++;
					}
				} else {
					entityTexture = new Texture("res/PlayerTextures/archerStill.png");
				}
			break;
			case 2:
				reset();
				fall();
				entityTexture = new Texture("res/PlayerTextures/archerAirtime.png");
			break;
		}

		for (SpikeBoy spikeBoy : spikeBoys) {
			if (!spikeBoy.isDestroyed()) {
				if (collide(spikeBoy)) {
					spikeBoy.explode();
					hitCount += 1;
				}
			}
		}
		
		for (WorldButton button : buttons) {
			if (collide(button)) {
				button.press();
			}
		}

		position = worldCollide(movement(grounded), backgrounds);
		arm.update(angle, position, cooldown);
	}

	public void render() {
		super.render();
		arm.render();
	}

	// The movement of the player for this update is determined from the user's keyboard input.
	public Vector3f movement(int grounded) {
		if (Input.keys[GLFW.GLFW_KEY_A] && velocity.x >= -Constants.maxPlayerSpeed) {
			if (grounded == 0) {
				velocity.x -= 1;
			} else {
				velocity.x = -Constants.maxPlayerSpeed;
			}
		} else if (Input.keys[GLFW.GLFW_KEY_D] && velocity.x <= Constants.maxPlayerSpeed) {
			if (grounded == 0) {
				velocity.x += 1;
			} else {
				velocity.x = Constants.maxPlayerSpeed;
			}
		} else {
			if (grounded == 0) {
				velocity.x *= 0.95;
			} else {
				velocity.x = 0;
			}
		}
		return new Vector3f(position.x + velocity.x, position.y + velocity.y, position.z);
	}

	public void hasBeenHit() {
		hitCount++;
	}

	public int getHealth() {
		return Constants.playerMaxHealth - hitCount;
	}
}