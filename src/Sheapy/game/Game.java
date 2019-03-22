package Sheapy.game;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;

import Sheapy.Input;
import Sheapy.maths.MapReference;
import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector2f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Shader;

public class Game {
	// Boolean value used to check if the game should end or not.
	private boolean isLevelFinished = false;

	// Two values to specify the width and height of the virtual world in tiles.
	private int tileWidth;
	private int tileHeight;

	// A list of the4 background tiles that make up the virtual world.
	private Background[] backgrounds;
	// A list collection of lists and singular instantiated entities. These store all the entities and objects within the virtual world
	private SpikeBoy[] spikeBoys = new SpikeBoy[0];
	private Turret[] turrets = new Turret[0];
	private static Projectile[] projectiles = new Projectile[0];
	private WorldButton[] buttons = new WorldButton[0];
	private Background[] disappearingWalls = new Background[0];
	private Vector3f[] exitPortalLocations = new Vector3f[0];
	private Player player = new Player();
	private HealthBar healthBar;
	
	// A vector used to keep track of the centre of the screen. This is used in the updating of the view matrix.
	private Vector3f screenCentre = new Vector3f();
	// A value used to save the file path for the map used to load the specific level being used.
	private String filepath;
	
	// A boolean value to check whether all buttons have been pressed and all disappearing walls should vanish.
	private boolean hasTriggerBeenCarriedOut = false;

	// An vector storing the user's cursor position in the virtual world. Calculated each update it serve little purpose but could be used if more code were to be added.
	private Vector3f mouseWorldPos = new Vector3f();
	
	// A float that stores half the current width/height of the screen. Each update the projection matrix is updated based on this value so by increasing or decreasing it the camera's zoom can be manipulated.
	private float scale = Constants.initialScale;

	// A float to hold the current angle between the player and the cursor.
	private float angle = 0f;

	// An integer to hold the fire cooldown of the player to prevent a constant stream of projectiles being fired.
	private int cooldown = 60;

	// An array which has the level's image file loaded into it upon being pixel analysed. This is used to create the all the starting positions and entities in the level.
	public static int level[];

	// The constructor sets up the game world by loading in the map and creating all the necessary entities in the world from this map.
	public Game(String path) {
		filepath = path;
		level = loadMap();
		backgrounds = new Background[tileWidth * tileHeight];
		for (int i = 0; i < tileWidth; i++) {
			for (int j = 0; j < tileHeight; j++) {
				backgrounds[i + j * tileWidth] = new Background(new Vector3f(50 + 100 * i, 50 + 100 * j, 0.0f), level[i + j * tileWidth]);
				switch (level[i + j * tileWidth]) {
					case 2:
						player.position = new Vector3f(50 + 100 * i, 50 + 100 * j);
					break;
					case 3:
						Vector3f[] exitTemp = new Vector3f[exitPortalLocations.length + 1];
						for (int p = 0; p <= exitPortalLocations.length - 1; p++) {
							exitTemp[p] = exitPortalLocations[p];
						}
						exitTemp[exitTemp.length - 1] = new Vector3f(50 + 100 * i, 50 + 100 * j);
						exitPortalLocations = exitTemp;
					break;
					case 4:
					case 5:
					case 6:
						int turretType = 0;
						switch (level[i + j * tileWidth]) {
							case 4:
								turretType = 0;
							break;
							case 5:
								turretType = 1;
							break;
							case 6:
								turretType = 2;
							break;
						}
						Turret[] turretTemp = new Turret[turrets.length + 1];
						for (int p = 0; p <= turrets.length - 1; p++) {
							turretTemp[p] = turrets[p];
						}
						turretTemp[turretTemp.length - 1] = new Turret(50 + 100 * i, 50 + 100 * j, turretType);
						turrets = turretTemp;
					break;
					case 7:
						SpikeBoy[] enemyTemp = new SpikeBoy[spikeBoys.length + 1];
						for (int p = 0; p <= spikeBoys.length - 1; p++) {
							enemyTemp[p] = spikeBoys[p];
						}
						enemyTemp[enemyTemp.length - 1] = new SpikeBoy(50 + 100 * i, 50 + 100 * j);
						spikeBoys = enemyTemp;
					break;
					case 8:
						WorldButton[] buttonTemp = new WorldButton[buttons.length + 1];
						for (int p = 0; p <= buttons.length - 1; p++) {
							buttonTemp[p] = buttons[p];
						}
						buttonTemp[buttonTemp.length - 1] = new WorldButton(50 + 100 * i, 50 + 100 * j);
						buttons = buttonTemp;
					break;
					case 9:
						Background[] backgroundTemp = new Background[disappearingWalls.length + 1];
						for (int p = 0; p <= disappearingWalls.length - 1; p++) {
							backgroundTemp[p] = disappearingWalls[p];
						}
						backgroundTemp[backgroundTemp.length - 1] = backgrounds[i + j * tileWidth];
						disappearingWalls = backgroundTemp;
					break;
				}
			}
		}
		for (Turret turret : turrets) {
			turret.setObstacles(getSurroundingBackgrounds(turret.position, (int) Math.ceil(Constants.turretAimRange / 100)));
		}

		for (WorldButton button : buttons) {
			button.setRotation(getSurroundingBackgrounds(button.position));
		}
		int newLength = buttons.length;
		for (WorldButton button : buttons) {
			if (!button.checkValid()) {
				newLength--;
			}
		}
		WorldButton[] temp = new WorldButton[newLength];
		for (int x = 0, y = 0; x < buttons.length; x++) {
			if (buttons[x].checkValid()) {
				temp[y++] = buttons[x];
			}
		}
		buttons = temp;

		updateRenderValues();

		healthBar = new HealthBar();
		
		if (exitPortalLocations.length == 0) {
			System.out.println("This level is missing an exit portal and therefore cannot be created.");
			isLevelFinished = true;
		} else if (player.position.x == 0 && player.position.y == 0) {
			System.out.println("This level is missing an entrance portal and therefore cannot be created.");
			isLevelFinished = true;
		}
	}

	// Used to trigger the rendering of all the various entities in the game so the user can see them on the screen.
	public void render() {
		Shader.WORLDSHADER.activate();

		for (Background background : backgrounds/* getSurroundingBackgrounds(screenCentre, (int) Math.ceil(scale / 100)) */) {
			background.render();
		}

		player.render();

		for (Projectile arrow : projectiles) {
			if (arrow.active) {
				arrow.render(player.position);
			}
		}
		for (SpikeBoy spikeBoy : spikeBoys) {
			if (!spikeBoy.isDestroyed()) {
				spikeBoy.render();
			}
		}
		for (Turret turret : turrets) {
			turret.render(player.position);
		}
		for (WorldButton button : buttons) {
			button.render(player.position);
		}
		Shader.WORLDSHADER.deactivate();

		if (!Constants.isHardMode) {
			Shader.STATICSHADER.activate();
			healthBar.render();
			Shader.STATICSHADER.deactivate();
		}
	}

	// Called once per update in the main class this subroutine triggers updates in all the other instantiated objects aswell as taking care of user input whilst also updating the render values.
	public void update(float x, float y, boolean mouseClick) {
		for (Vector3f exitPortal : exitPortalLocations) {
			if (player.position.x > exitPortal.x - 50 && player.position.x < exitPortal.x + 50) {
				if (player.position.y > exitPortal.y - 50 && player.position.y < exitPortal.y + 50) {
					isLevelFinished = true;
				}
			}
		}

		if (Input.keys[GLFW.GLFW_KEY_ESCAPE]) {
			isLevelFinished = true;
		}

		int pressedCount = 0;
		for (WorldButton button : buttons) {
			if (button.isPressed()) {
				pressedCount++;
			}
		}
		if (pressedCount == buttons.length && !hasTriggerBeenCarriedOut) {
			for (Background wallPanel : disappearingWalls) {
				wallPanel.disappear();
			}
			hasTriggerBeenCarriedOut = true;
		}

		processMousePosition(x, y);

		player.update(getSurroundingBackgrounds(player.position), spikeBoys, buttons, angle, cooldown);

		for (SpikeBoy spikeBoy : spikeBoys) {
			if (!spikeBoy.isDestroyed()) {
				spikeBoy.update(getSurroundingBackgrounds(spikeBoy.position));
			}
		}
		for (Turret turret : turrets) {
			turret.update(player.position);
		}

		for (Projectile arrow : projectiles) {
			if (arrow.active) {

				arrow.update(player, spikeBoys, getSurroundingBackgrounds(arrow.position), buttons);
			}
		}

		if (Input.keys[GLFW.GLFW_KEY_K] && tileHeight * 100 >= 2 * scale && tileWidth * 100 >= 2 * scale) {
			scale += 25f;
			if (tileHeight * 100 <= 2 * scale) {
				scale = (tileHeight * 100) / 2;
			} else if (tileWidth * 100 <= 2 * scale) {
				scale = (tileWidth * 100) / 2;
			}

		} else if (Input.keys[GLFW.GLFW_KEY_L] && 100 <= scale) {
			scale -= 25f;
			if (100 >= scale) {
				scale = 100;
			}
		}

		if (mouseClick && cooldown == 0) {
			fireProjectile(player.position, angle, player.velocity, false);
			cooldown = Constants.playerFireCooldown;
		}

		cleanProjectileArray();

		if (cooldown > 0) {
			cooldown--;
		}

		updateRenderValues();

		if (player.getHealth() < 1) {
			isLevelFinished = true;
		} else {
			healthBar.update(scale, screenCentre, player.getHealth());
		}
	}

	//  Called in the constructor this function loads in the level's specific image file and analysis each pixel before changing each one to a more manageable number and returning it for use in setting up the level.
	private int[] loadMap() {
		int[] pixels = null;
		int width = 0, height = 0;
		// Load in colour ID into the pixel array.
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(filepath));

			width = image.getWidth();
			height = image.getHeight();

			pixels = new int[width * height];

			image.getRGB(0, 0, width, height, pixels, 0, width);
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		// Edit the pixel array to use more managable numbers.
		for (int i = 0; i < width * height; i++) {

			switch (pixels[i]) {
				case -1:
					pixels[i] = 0;
				break;
				case -16777216:
					pixels[i] = 1;
				break;
				case -14503604:
					pixels[i] = 2;
				break;
				case -1237980:
					pixels[i] = 3;
				break;
				case -16735512:
					pixels[i] = 4;
				break;
				case -6694422:
					pixels[i] = 5;
				break;
				case -12629812:
					pixels[i] = 6;
				break;
				case -32985:
					pixels[i] = 7;
				break;
				case -20791:
					pixels[i] = 8;
				break;
				case -6075996:
					pixels[i] = 9;
				break;
				default:
					System.out.println("New image pixel id detected. Code is: " + pixels[i]);
				break;
			}
		}
		tileWidth = width;
		tileHeight = height;
		// Flip the array vertically.
		int[] flippedPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				flippedPixels[x + y * width] = pixels[x + (height - 1 - y) * width];
			}
		}
		return flippedPixels;
	}

	private void processMousePosition(float mouseWindowX, float mouseWindowY) {
		// Mouse value scaled to accommodate for the current level of zoom.
		mouseWindowX *= scale;
		mouseWindowY *= scale;
		Vector3f screenCentre = new Vector3f();
		screenCentre.x = player.getX();
		screenCentre.y = player.getY();

		if (player.getX() <= scale) {
			screenCentre.x = scale;
		}
		if (player.getX() >= 100 * tileWidth - scale) {
			screenCentre.x = 100 * tileWidth - scale;
		}
		if (player.getY() <= scale) {
			screenCentre.y = scale;
		}
		if (player.getY() >= 100 * tileHeight - scale) {
			screenCentre.y = 100 * tileHeight - scale;
		}

		mouseWorldPos.x = screenCentre.x + mouseWindowX;
		mouseWorldPos.y = screenCentre.y + mouseWindowY;


		float changeInX = mouseWorldPos.x - player.getX(), changeInY = mouseWorldPos.y - player.getY();

		if (changeInX > 0 && changeInY > 0) {
			angle = (float) (Math.toDegrees(Math.atan(Math.abs(changeInX / changeInY))));
		} else if (changeInX > 0 && changeInY < 0) {
			angle = (float) (90 + 90 - Math.toDegrees(Math.atan(Math.abs(changeInX / changeInY))));
		} else if (changeInX < 0 && changeInY < 0) {
			angle = (float) (180 + Math.toDegrees(Math.atan(Math.abs(changeInX / changeInY))));
		} else if (changeInX < 0 && changeInY > 0) {
			angle = (float) (270 + 90 - Math.toDegrees(Math.atan(Math.abs(changeInX / changeInY))));
		}
	}

	// A simple subroutine to trigger the updating of the projection and view matrix. Along with the light instances uniform array.
	private void updateRenderValues() {
		updateViewMatrix();
		updateProjectionMatrix();
		updateLightingArray();
	}

	// Updates the view matrix by sending through a uniform variable to the shader.
	private void updateViewMatrix() {
		if (player.getX() <= scale) {
			screenCentre.x = scale;
		} else if (player.getX() >= 100 * tileWidth - scale) {
			screenCentre.x = 100 * tileWidth - scale;
		} else {
			screenCentre.x = player.getX();
		}

		if (player.getY() <= scale) {
			screenCentre.y = scale;
		} else if (player.getY() >= 100 * tileHeight - scale) {
			screenCentre.y = 100 * tileHeight - scale;
		} else {
			screenCentre.y = player.getY();
		}
		Matrix4f vw_matrix = Matrix4f.translate(screenCentre.toMinus());
		Shader.WORLDSHADER.setUniformMatrix("vw_matrix", vw_matrix);
	}

	// Updates the light sources being rendered by sending through a uniform array to the shader.
	private void updateLightingArray() {
		Vector2f[] temp = new Vector2f[2 + exitPortalLocations.length];
		temp[0] = new Vector2f(player.position.x, player.position.y);
		temp[1] = new Vector2f(mouseWorldPos.x, mouseWorldPos.y);
		for (int i = 0; i < exitPortalLocations.length; i++) {
			temp[2 + i] = new Vector2f(exitPortalLocations[i]);
			;
		}
		Shader.WORLDSHADER.setUniformVec2Array("lightPos", temp);
		if (Constants.isLightingOn) {
			Shader.WORLDSHADER.setUniformInt("lightNo", temp.length);
		} else {
			Shader.WORLDSHADER.setUniformInt("lightNo", 0);
		}

	}

	// Updates the projection matrix by sending through a uniform variable to the shader.
	private void updateProjectionMatrix() {
		// A new orthographic matrix is made called "pr_matrix" (For "Projection Matrix") which has values corresponding to the screen size.
		Matrix4f pr_matrix = Matrix4f.orthographic(-scale, scale, -scale, scale, -1.0f, 1.0f);

		// Sets a uniform variable to be given to the shader "player" when run. The uniform variable is of type Matrix4f and is called "pr_matrix".
		Shader.WORLDSHADER.setUniformMatrix("pr_matrix", pr_matrix);
	}

	// A subroutine used to eliminate destroyed projectiles from the array
	public void cleanProjectileArray() {
		int newLength = projectiles.length;
		for (Projectile projectile : projectiles) {
			if (!projectile.active) {
				newLength--;
			}
		}
		Projectile[] temp = new Projectile[newLength];
		for (int x = 0, y = 0; x < projectiles.length; x++) {
			if (projectiles[x].active) {
				temp[y++] = projectiles[x];
			}
		}
		projectiles = temp;
	}

	// Increases the size of the projectiles array by one and instantiates a new projectile therein in this new location.
	public static void fireProjectile(Vector3f position, float angle, Vector2f baseSpeed, boolean isEvil) {
		Projectile[] temp = new Projectile[projectiles.length + 1];
		for (int p = 0; p < projectiles.length; p++) {
			temp[p] = projectiles[p];
		}
		temp[temp.length - 1] = new Projectile(position, angle, baseSpeed, isEvil);
		projectiles = temp;
	}

	// Returns the 3 by 3 grid of background tiles surrounding a position in the virtual world. Used to retrieve background tiles needed in checking for collisions on objects. Prevents needing to compare against every background tile on the map.
	public Background[] getSurroundingBackgrounds(Vector3f position, int radius) {
		int totalBlocks = 2 * radius + 1;
		totalBlocks *= totalBlocks;
		MapReference ref = getMapRef(position);
		Background[] surroundingBackgrounds = new Background[totalBlocks];
		int count = 0;
		for (int xRef = ref.x - radius; xRef <= ref.x + radius; xRef++) {
			for (int yRef = ref.y - radius; yRef <= ref.y + radius; yRef++) {
				if (xRef >= 0 && xRef < tileWidth && yRef >= 0 && yRef < tileHeight) {
					surroundingBackgrounds[count++] = backgrounds[xRef + yRef * tileWidth];
				}
			}
		}
		if (count < totalBlocks) {
			Background[] temp = new Background[count];
			for (int x = 0, y = 0; x < surroundingBackgrounds.length; x++) {
				if (surroundingBackgrounds[x] != null) {
					temp[y++] = surroundingBackgrounds[x];
				}
			}
			surroundingBackgrounds = temp;
		}
		return surroundingBackgrounds;
	}
	public Background[] getSurroundingBackgrounds(Vector3f position) {
		return getSurroundingBackgrounds(position, 1);
	}

	// A subroutine used so that any class can trigger a the firing of a projectile.
	public static void fireProjectile(Vector3f position, float angle, boolean isEvil) {
		fireProjectile(position, angle, new Vector2f(), isEvil);
	}

	public boolean isGameComplete() {
		return isLevelFinished;
	}
	
	// A function which finds the grid location of the map for a certain position. Used when finding surrounding background tiles.
	public MapReference getMapRef(Vector3f pos) {
		return new MapReference((int) ((pos.x - (pos.x % 100)) / 100), (int) ((pos.y - (pos.y % 100)) / 100));
	}

}
