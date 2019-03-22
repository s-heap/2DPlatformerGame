package Sheapy.menu;

import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class Button extends MenuEntity {

	private static float buttonWidth = 100;
	private static float buttonHeight = 100;
	private String texturePath;

	public Button(Vector3f startPosition, String tPath) {
		super(buttonWidth, buttonHeight, 0.5f);
		position = new Vector3f(startPosition);
		entityTexture = new Texture(tPath);
		texturePath = tPath;
	}

	// Checks if the mouse position given collides with the menu button.
	public boolean checkIfOver(Vector3f mousePos) {
		if (mousePos.x > position.x - 50 && mousePos.x < position.x + 50) {
			if (mousePos.y > position.y - 50 && mousePos.y < position.y + 50) {
				return true;
			}
		}
		return false;
	}
	
	// Returns the file path of the texture of the button.
	public String getTPath() {
		return texturePath;
	}
	
	protected void setTpath(String tPath) {
		entityTexture = new Texture(tPath);
	}

}
