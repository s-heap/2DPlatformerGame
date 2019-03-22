package Sheapy.game;

import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Texture;

public class Arm extends GameEntity{
	float rot = 0f;

	public Arm(Float w, Float h, Float z) {
		super(w, h, z);
		entityTexture = new Texture("res/ArmTextures/ArmDrawn.png");
	}
	
	// The srm's texture and rotation is updated.
	public void update(float angle, Vector3f location, float cooldown) {
		rot = 360 - angle;
		position = new Vector3f(location.x, location.y + 5, location.z);
		if (cooldown == 0) {
			entityTexture = new Texture("res/ArmTextures/ArmDrawn.png");
		} else {
			entityTexture = new Texture("res/ArmTextures/ArmLoose.png");
		}
	}

	// The arm entity needs rotation so it is applied here and sent to the render method in entity.
	public void render() {
		render(Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
	}
	
}
