package Sheapy.game;

import Sheapy.maths.Vector2f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class Character extends GameEntity {
	final protected float jumpStrength;

	protected Vector2f velocity = new Vector2f();

	public Character(Float w, Float h, Float z, Float jumpHeight, String tPath) {
		super(w, h, z);
		jumpStrength = (float) Math.sqrt(-2 * Constants.worldAcceleration * jumpHeight); // u = (v^2 - 2as)^0.5

		entityTexture = new Texture(tPath);
	}

	// Used to call the individual world collision checks based off the character's movement by comparing it to the backgrounds given as input.
	public Vector3f worldCollide(Vector3f move, Background[] backgrounds) {
		Vector3f xCheck = new Vector3f(position);
		xCheck.x = move.x;
		xCheck = floorCollision(xCheck, position, backgrounds);
		Vector3f yCheck = new Vector3f(xCheck);
		yCheck.y = move.y;
		return floorCollision(yCheck, xCheck, backgrounds);
	}

	// Finds out whether the character is in the air, on the ground or with their head on the bottom of a block.
	public int grounded(Vector3f position, Background[] backgrounds) {
		float playerLeft = position.x - width/2;
		float playerRight = position.x + width/2;
		for (Background background : backgrounds) {
			if (background.solid == true) {
				float bgLeft = background.getCoord().x - (background.getSize() / 2);
				float bgRight = background.getCoord().x + (background.getSize() / 2);
				if (position.y == background.getCoord().y + background.getSize() / 2 + height/2) {
					if (playerRight > bgLeft && playerLeft < bgRight) {
						return 1;
					}
				}
				if (position.y == background.getCoord().y - background.getSize() / 2 - height/2) {
					if (playerRight > bgLeft && playerLeft < bgRight) {
						return 2;
					}
				}
			}
		}
		return 0;
	}

	// Checks and adjusts any collision a character's movement might ilicit with a background tile.
	public Vector3f floorCollision(Vector3f predicted, Vector3f original, Background[] backgrounds) {
		float characterLeft = predicted.x - width/2;
		float characterRight = predicted.x + width/2;
		float characterTop = predicted.y + height/2;
		float characterBottom = predicted.y - height/2;
		for (Background background : backgrounds) {
			if (background.solid == true) {
				float bgLeft = background.getCoord().x - (background.getSize() / 2);
				float bgRight = background.getCoord().x + (background.getSize() / 2);
				float bgTop = background.getCoord().y + (background.getSize() / 2);
				float bgBottom = background.getCoord().y - (background.getSize() / 2);
				// If the player's right is past the background's left but their left isn't past the background's right. They are in the right range.
				if (characterRight > bgLeft && characterLeft < bgRight) {
					// If the player's top is past the background's bottom but their bottom isn't past the background's top. They are in the right range.
					if (characterTop > bgBottom && characterBottom < bgTop) {
						// If going right.
						if (predicted.x > original.x) {
							predicted.x = background.getCoord().x - (background.getSize() / 2) - width/2;
						}
						// If going left.
						else if (predicted.x < original.x) {
							predicted.x = background.getCoord().x + (background.getSize() / 2) + width/2;
						}
						// If going up.
						if (predicted.y > original.y) {
							predicted.y = background.getCoord().y - (background.getSize() / 2) - height/2;
						}
						// If going down.
						else if (predicted.y < original.y) {
							predicted.y = background.getCoord().y + (background.getSize() / 2) + height/2;
						}

					}
				}
			}
		}
		return predicted;
	}
	
	// An entity collider used to simple check if the character has collided with another entity.
	public boolean collide(GameEntity entity) {
		float eLeft = entity.position.x - entity.width/2;
		float eRight = entity.position.x + entity.width/2;
		float eTop = entity.position.y + entity.height/2;
		float eBottom = entity.position.y - entity.height/2;
		float Left = position.x - width/2;
		float Right = position.x + width/2;
		float Top = position.y + height/2;
		float Bottom = position.y + height/2;

		if (Right > eLeft && Left < eRight) {
			if (Top > eBottom && Bottom < eTop) {
				return true;
			}
		}
		return false;
	}

	// A method to apply the current world gravity to the character's vertical velocity.
	public void fall() {
		velocity.y += Constants.worldAcceleration;
	}

	// A method to reset a character's y velocity.
	public void reset() {
		velocity.y = 0;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

}
