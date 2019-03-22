package Sheapy.game;

import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class Background extends GameEntity{
	private static float size = 100;
	public boolean solid;
	
	// The background is created and it's texture and valid for 'solid' is determined by it's type.
	public Background(Vector3f Coord, int type) {
		super(size, size, 0f);
		position = Coord;
		// The jpeg image with file path specified is loaded into the "background" texture.
		if (type == 1 || type == 4 || type == 5 || type == 6) {
			entityTexture = new Texture("res/TerrainTextures/brick.png");
			solid = true;
		} else if (type == 2) {
			entityTexture = new Texture("res/TerrainTextures/entryPortal.png");
			solid = false;
		} else if (type == 3) {
			entityTexture = new Texture("res/TerrainTextures/exitPortal.png");
			solid = false;
		} else if (type == 9) {
			entityTexture = new Texture("res/TerrainTextures/DisappearingWall.png");
			solid = true;
		} else {
			entityTexture = new Texture("res/TerrainTextures/sky.png");
			solid = false;
		}
		
	}
	
	public float getSize() {
		return size;
	}
	
	public Vector3f getCoord() {
		return position;
	}
	
	// Used for all the disappearing walls in the game it stops the tile from being solid and changes it's texture.
	public void disappear() {
		entityTexture = new Texture("res/TerrainTextures/sky.png");
		solid = false;
	}
}
