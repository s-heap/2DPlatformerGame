package Sheapy.game;

// A class containing all the constants needed in the game.
public class Constants {
	// Player Constants:
	static final float maxPlayerSpeed = 10.0f;
	static final float playerJumpHeight = 300.0f;
	static final int playerFireCooldown = 60;
	static final int playerMaxHealth = 8;

	// Turret Constants:
	static final float turretAimRange = 1500.0f;
	static final int turretObstacleDetectionInstances = 100;
	// Rifle Turret Constants:
	static final int rifleTurretFireCooldown = 60;
	static final int rifleTurretExtraShotCount = 0;
	static final int rifleTurretExtraBurstCount = 0;
	// Shotgun Turret Constants:
	static final int shotgunTurretFireCooldown = 180;
	static final int shotgunTurretExtraShotCount = 4;
	static final int shotgunTurretExtraBurstCount = 0;
	// Burst Rifle Turret Constants:
	static final int burstRifleTurretFireCooldown = 120;
	static final int burstRifleTurretExtraShotCount = 0;
	static final int burstRifleTurretExtraBurstCount = 2;

	// SpikeBoy constants:
	static final float maxSpikeBoySpeed = 10.0f;
	static final float spikeBoyJumpHeight = 400.0f;
	
	// Projectile Constants:
	static final float projectilePower = 25.0f;

	// World Constant:
	static final float worldAcceleration = -1.0f;
	static final float initialScale = 700f;
	
	// Game settings:
	public static boolean isLightingOn = true;
	public static boolean isHardMode = false;
}
