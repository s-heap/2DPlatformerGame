package Sheapy.game;

import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class WorldButton extends GameEntity {
	private final static float initialWidth = 100.0f;
	private final static float initialHeight = 17.0f;
	private float rot = 0f;
	private boolean isValid = true;
	private boolean isPressed = false;

	public WorldButton(float x, float y) {
		super(initialWidth, initialHeight, 0.3f);
		position.x = x;
		position.y = y;
		entityTexture = new Texture("res/WorldButtonOpen.png");
	}

	// Checks for nearby walls and alters the buttons rotation so it mounts itself on a wall. If not ir makes the button invalid.
	public void setRotation(Background[] backgrounds) {
		if (backgrounds.length == 9) {
			if (backgrounds[1].solid) {
				rot = 90;
				position.x -= (50 - initialHeight/2);
				float temp = height;
				height = width;
				width = temp;
			} else if (backgrounds[7].solid) {
				rot = 270;
				position.x += (50 - initialHeight/2);
				float temp = height;
				height = width;
				width = temp;
			} else if (backgrounds[5].solid) {
				rot = 0;
				position.y += (50 - initialHeight/2);
			} else if (backgrounds[3].solid) {
				rot = 180;
				position.y -= (50 - initialHeight/2);
			} else {
				System.out.println("Invalid button location: No wall to mount to.");
				isValid = false;
			}
		} else {
			System.out.println("Invalid button location: Button can not be on the edge of the map.");
			isValid = false;
		}
	}

	public void render(Vector3f pPos) {
		render(Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
	}

	public boolean checkValid() {
		return isValid;
	}

	public float getRot() {
		return rot;
	}

	public void press() {
		isPressed = true;
		entityTexture = new Texture("res/WorldButtonClosed.png");
	}

	public boolean isPressed() {
		return isPressed;
	}

}
