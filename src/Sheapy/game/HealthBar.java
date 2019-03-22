package Sheapy.game;

import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class HealthBar extends HUDEntity {

	private final static float initialWidth = 150f;
	private final static float initialHeight = 30f;
	private final static Vector3f basePosition = new Vector3f(1000 - initialWidth/2 - 5, 1000 - initialHeight/2 - 5);

	public HealthBar() {
		super(initialWidth, initialHeight);
		entityTexture = new Texture("res/HealthBar/100%.png");
		position = basePosition;
	}

	// The specific texture needed to display the player's proportional helath is updated.
	public void update(float scale, Vector3f screenCentre, int health) {		
		String textureString;
		int percentageHealth = 100 * health/Constants.playerMaxHealth;
		int roundedPercentageHealth = percentageHealth/25;
		textureString = "res/HealthBar/" + (25*roundedPercentageHealth) + "%.png";
		entityTexture = new Texture(textureString);
		
	}

}
