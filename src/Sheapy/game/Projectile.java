package Sheapy.game;

import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector2f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class Projectile extends GameEntity {

	private final static float initialWidth = 10.0f;
	private final static float initialHeight = 30.0f;
	private static String tPath = "res/arrow.png";
	private Vector2f velocity = new Vector2f();
	public boolean active = true;
	protected float rot = 0f;
	private Vector3f startPos;
	private boolean isEvilProjectile;

	public Projectile(Vector3f coord, float bearing, Vector2f baseSpeed, boolean type) {
		super(initialWidth, initialHeight, 1f);
		entityTexture = new Texture(tPath);
		position = coord;
		startPos = new Vector3f(coord);

		float radBearing = (float) Math.toRadians(bearing);
		velocity.x = (float) Math.sin(radBearing) * getPower() + baseSpeed.x;
		velocity.y = (float) Math.cos(radBearing) * getPower() + baseSpeed.y;

		isEvilProjectile = type;
	}

	// Updates everything needed with the projectile in an update. Including all the collision checks needed and their designated response. As well as rotation based on it's new velocity.
	public void update(Player player, SpikeBoy[] spikeBoys, Background[] backgrounds, WorldButton[] buttons) {

		if (velocity.x > 0 && velocity.y > 0) {
			rot = (float) (Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		} else if (velocity.x > 0 && velocity.y < 0) {
			rot = (float) (90 + 90 - Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		} else if (velocity.x < 0 && velocity.y < 0) {
			rot = (float) (180 + Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		} else if (velocity.x < 0 && velocity.y > 0) {
			rot = (float) (270 + 90 - Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		}
		rot = 360 - rot;

		position.x += velocity.x;
		position.y += velocity.y;
		velocity.y += Constants.worldAcceleration;

		for (WorldButton button : buttons) {
			if (!button.isPressed()) {
				if (collide(button)) {
					active = false;
					button.press();
				}
			}
		}

		if (collide(player) && isEvilProjectile) {
			player.hasBeenHit();
			active = false;
		}

		for (SpikeBoy spikeBoy : spikeBoys) {
			if (!spikeBoy.isDestroyed()) {
				if (collide(spikeBoy)) {
					active = false;
					if (!isEvilProjectile) {
						spikeBoy.hit();
					}
				}
			}
		}

		for (Background background : backgrounds) {
			if (background.solid == true && (background.getCoord().x != startPos.x || background.getCoord().y != startPos.y)) {
				if (collide(background)) {
					active = false;
				}
			}
		}
	}

	public void render(Vector3f pPos) {
		render(Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
	}

	// A simple collision checker which checks if a specific entity should collide with the projectile. Takes simple rotation into account.
	public boolean collide(GameEntity entity) {
		float eLeft = entity.position.x - entity.width / 2;
		float eRight = entity.position.x + entity.width / 2;
		float eTop = entity.position.y + entity.height / 2;
		float eBottom = entity.position.y - entity.height / 2;
		float Left = position.x - width / 2;
		float Right = position.x + width / 2;
		float Top = position.y + height / 2;
		float Bottom = position.y + height / 2;
		if ((rot > 45 && rot < 135) || (rot > 225 && rot < 315)) {
			Left = position.x - height / 2;
			Right = position.x + height / 2;
			Top = position.y + width / 2;
			Bottom = position.y - width / 2;
		}

		if (Right > eLeft && Left < eRight) {
			if (Top > eBottom && Bottom < eTop) {
				return true;
			}
		}
		return false;
	}

	public static float getPower() {
		return Constants.projectilePower;
	}
}
