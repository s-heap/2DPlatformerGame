package Sheapy.game;

import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Shader;
import Sheapy.rendering.Texture;
import Sheapy.rendering.VArray;

public class GameEntity {
	private VArray vArray;

	// The coordinates referenced in the following two arrays are in terms of 0,0 being the top left.
	// They store the texture coordinates need for the texture to be drawn normally and flipped horizontally respectively.
	final private static float[] right = new float[] {
			// 0, 1 refers to the bottom left.
			0, 1,
			// 0, 0 refers to the top left.
			0, 0,
			// 1, 0 refers to the top right.
			1, 0,
			// 1, 1 refers to the bottom right.
			1, 1 };

	final private static float[] left = new float[] {
			// 0, 1 refers to the bottom right.
			1, 1,
			// 0, 0 refers to the top right.
			1, 0,
			// 1, 0 refers to the top left.
			0, 0,
			// 1, 1 refers to the bottom left.
			0, 1 };

	protected Vector3f position = new Vector3f();
	protected Texture entityTexture;
	protected float width;
	protected float height;
	protected float renderDepth;

	// THe constructor creates all the arrays needed to create the entities vArray.
	public GameEntity(Float w, Float h, Float z) {
		width = w;
		height = h;
		renderDepth = z;

		// The indices array defines the position of all the coordinates of the two triangles needed to create the square object being rendered.
		byte[] indices = new byte[] {
				// The first triangle uses 0, 1 and 2. In other words the bottom
				// left, the top left and the top right.
				0, 1, 2,
				// The second triangle uses 2, 3 and 0. In other words the top
				// right, the bottom right and the bottom left.
				2, 3, 0 };

		// A vertex array is created with all the needed data to be rendered.
		vArray = new VArray(createVertices(), indices, right);
	}

	// All 4 vertices are defined for the 4 corners of the object to be rendered, relative to the centre of the object.
	private float[] createVertices() {
		float halfWidth = width / 2;
		float halfHeight = height / 2;
		return new float[] {
				// The bottom left corner is made.
				-halfWidth, -halfHeight, renderDepth,
				// The top left corner is made.
				-halfWidth, halfHeight, renderDepth,
				// The top right corner is made.
				halfWidth, halfHeight, renderDepth,
				// The bottom right corner is made.
				halfWidth, -halfHeight, renderDepth };
	}

	public void render() {
		render(Matrix4f.translate(position));
	}

	protected void render(Matrix4f output) {
		entityTexture.attach();
		Shader.WORLDSHADER.setUniformMatrix("ml_matrix", output);
		vArray.render();
		entityTexture.detach();
	}

	// Used to update the texture coordinates used by the vArray by sending through fresh texture coordinates.
	protected void flipLeft() {
		vArray.updateTextureBufferObject(left);
	}

	protected void flipRight() {
		vArray.updateTextureBufferObject(right);
	}
}