package Sheapy.game;

import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class Turret extends GameEntity {
	private static float initialWidth = 90;
	private static float initialHeight = 90;
	private float rot = 0f;
	private float angle = 0f;
	private int maxCooldown = 0;
	private int cooldown = 0;
	private Background[] obstaclesInRange = new Background[0];
	final private int extraShots;
	final private int extraBurst;
	private int burstCounter = 0;
	private Boolean burstTrigger = false;
	
	// Sets up the turret in question and determines it's constants from the 
	public Turret(float x, float y, int turretType) {
		super(initialWidth, initialHeight, 0.9f);
		position.x = x;
		position.y = y;
		switch (turretType) {
			case 0:
				entityTexture = new Texture("res/TurretTextures/Turret1.png");
				extraShots = Constants.rifleTurretExtraShotCount;
				maxCooldown = Constants.rifleTurretFireCooldown;
				extraBurst = Constants.rifleTurretExtraBurstCount;
			break;
			case 1:
				entityTexture = new Texture("res/TurretTextures/Turret2.png");
				extraShots = Constants.shotgunTurretExtraShotCount;
				maxCooldown = Constants.shotgunTurretFireCooldown;
				extraBurst = Constants.shotgunTurretExtraBurstCount;
			break;
			case 2:
				entityTexture = new Texture("res/TurretTextures/Turret3.png");
				extraShots = Constants.burstRifleTurretExtraShotCount;
				maxCooldown = Constants.burstRifleTurretFireCooldown;
				extraBurst = Constants.burstRifleTurretExtraBurstCount;
			break;
			default:
				entityTexture = new Texture("res/TurretTextures/Turret1.png");
				extraShots = 0;
				maxCooldown = 60;
				extraBurst = 0;
			break;
		}
		burstCounter = extraBurst;
	}

	// Retrieves the obstacles within range which will be used to check if a projectile being fired will hit an obstacle.
	public void setObstacles(Background[] backgrounds) {
		for (Background backgroundTile : backgrounds) {
			if (backgroundTile.solid) {
				float distanceFromTurret = (float) Math.sqrt(Math.pow((backgroundTile.getCoord().x - position.x), 2) + Math.pow((backgroundTile.getCoord().y - position.y), 2));
				if (distanceFromTurret < Constants.turretAimRange && distanceFromTurret != 0) {
					Background[] temp = new Background[obstaclesInRange.length + 1];
					for (int p = 0; p < temp.length - 1; p++) {
						temp[p] = obstaclesInRange[p];
					}
					temp[temp.length - 1] = backgroundTile;
					obstaclesInRange = temp;
				}
			}
		}
	}

	// Handles all the changes a turret makes each update. Includes managing firing, aiming and rotation.
	public void update(Vector3f playerPos) {				
		if (cooldown > 0) {
			cooldown--;
		}
		float xDifference = playerPos.x - position.x;
		float yDifference = playerPos.y - position.y;

		if (Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2)) < Constants.turretAimRange) {
			
			if (burstTrigger) {
				burstCounter = extraBurst;
				burstTrigger = false;
			}
			if (cooldown == 0) {
				if (aim(xDifference, yDifference)) {
					
					fire();
					rot = 360 - angle;

					for (int x = 1; x < extraShots + 1; x++) {
						angle += x * 2 * ((x % 2) * 2 - 1);
						fire();
					}
					
					if (burstCounter > 0) {
						burstCounter--;
						cooldown = 5;
					} else {
						cooldown = maxCooldown - (5 * extraBurst);
						burstTrigger = true;
					}
				}
			}
		}
	}

	// Determines if the turret can hit the player and if so the angle it should fire at.
	public boolean aim(float changeInX, float changeInY) {

		Vector3f displacement = new Vector3f(changeInX, changeInY, 0f);
		float power = Projectile.getPower();
		float acceleration = Constants.worldAcceleration;
		float timeToHit = 0f;
		
//		// Experimental.
//		timeToHit = ComplexAimer.aim(displacement, pVelocity);
//		//System.out.println(timeToHit);
//		if (!Float.isNaN(timeToHit)) {
//			Vector3f velocity = new Vector3f((displacement.x + pVelocity.x*timeToHit) / timeToHit, (float) (((displacement.y + pVelocity.y*timeToHit) - 0.5 * acceleration * Math.pow(timeToHit, 2)) / timeToHit), 0f);
//			if (isPathClear(timeToHit, velocity, acceleration, pVelocity)) {
//				calculateAngle(velocity);
//				return true;
//			}
//		}
		
		
		
		// Short path
		timeToHit = (float) (Math.sqrt(2) * Math.sqrt(((acceleration * displacement.y) + Math.pow(power, 2) - Math.sqrt((-Math.pow(acceleration, 2) * Math.pow(displacement.x, 2)) + (2 * acceleration * Math.pow(power, 2) * displacement.y) + Math.pow(power, 4))) / Math.pow(acceleration, 2)));
		if (!Float.isNaN(timeToHit)) {
			Vector3f velocity = new Vector3f(displacement.x / timeToHit, (float) ((displacement.y - 0.5 * acceleration * Math.pow(timeToHit, 2)) / timeToHit), 0f);
			if (isPathClear(timeToHit, velocity, acceleration)) {
				calculateAngle(velocity);
				return true;
			}
		}

		// Long path
		timeToHit = (float) (Math.sqrt(2) * Math.sqrt(((acceleration * displacement.y) + Math.pow(power, 2) + Math.sqrt((-Math.pow(acceleration, 2) * Math.pow(displacement.x, 2)) + (2 * acceleration * Math.pow(power, 2) * displacement.y) + Math.pow(power, 4))) / Math.pow(acceleration, 2)));
		if (!Float.isNaN(timeToHit)) {
			Vector3f velocity = new Vector3f(displacement.x / timeToHit, (float) ((displacement.y - 0.5 * acceleration * Math.pow(timeToHit, 2)) / timeToHit), 0f);
			if (isPathClear(timeToHit, velocity, acceleration)) {
				calculateAngle(velocity);
				return true;
			}
		}
		
		
		return false;
	}

	// Checks if the projectile fired will collide with any obstacles at intervals in it's path.
	public boolean isPathClear(float time, Vector3f velocity, float acceleration/*, Vector3f pVelocity*/) {
		for (float timeInstance = 0; timeInstance < time; timeInstance += time / Constants.turretObstacleDetectionInstances) {
			float predictedXLocation = position.x + (timeInstance * velocity.x);
			float predictedYLocation = position.y + (float) ((timeInstance * velocity.y) + (0.5 * acceleration * Math.pow(timeInstance, 2)));
//			float predictedXLocation = (position.x + pVelocity.x*time) + (timeInstance * velocity.x);
//			float predictedYLocation = (position.y + pVelocity.y*time) + (float) ((timeInstance * velocity.y) + (0.5 * acceleration * Math.pow(timeInstance, 2)));
			if (isCollisionDetected(predictedXLocation, predictedYLocation)) {
				return false;
			}
		}
		return true;
	}

	// Determines the angle that will need to be fired at based on the x and y velocities calculated.
	public void calculateAngle(Vector3f velocity) {
		if (velocity.x > 0 && velocity.y > 0) {
			angle = (float) (Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		} else if (velocity.x > 0 && velocity.y < 0) {
			angle = (float) (90 + 90 - Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		} else if (velocity.x < 0 && velocity.y < 0) {
			angle = (float) (180 + Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		} else if (velocity.x < 0 && velocity.y > 0) {
			angle = (float) (270 + 90 - Math.toDegrees(Math.atan(Math.abs(velocity.x / velocity.y))));
		}
	}

	// Checks a collision at a specific time instance.
	public boolean isCollisionDetected(float x, float y) {
		for (Background backgroundTile : obstaclesInRange) {
			float bgLeft = backgroundTile.getCoord().x - 50;
			float bgRight = backgroundTile.getCoord().x + 50;
			float bgTop = backgroundTile.getCoord().y + 50;
			float bgBottom = backgroundTile.getCoord().y - 50;
			if (x > bgLeft && x < bgRight) {
				if (y > bgBottom && y < bgTop) {
					return true;
				}
			}
		}
		return false;
	}

	// Triggers the static game method to fire a projectile and increase the projectiles array.
	public void fire() {
		Game.fireProjectile(new Vector3f(position), angle, true);
	}

	public void render(Vector3f pPos) {
		render(Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
	}

}
