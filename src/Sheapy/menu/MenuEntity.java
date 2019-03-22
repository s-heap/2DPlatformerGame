package Sheapy.menu;

import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Shader;
import Sheapy.rendering.Texture;
import Sheapy.rendering.VArray;

public class MenuEntity {
	protected Vector3f position = new Vector3f();
	protected VArray vArray;
	protected Texture entityTexture;
	float[] vertices;
	byte[] indices;
	float[] tcs;
			
	
	public MenuEntity(Float w, Float h, Float z) {
		float halfWidth = w/2;
		float halfHeight = h/2;
		vertices = new float[] {
				// All 4 vertices are made for the 4 corners needed. At the
				// moment this will create a sprite covering half the screen.
				// The coordinates are made as x,y and z.
				// The bottom left corner is made.
				-halfWidth, -halfHeight, z,
				// The top left corner is made.
				-halfWidth, halfHeight, z,
				// The top right corner is made.
				halfWidth, halfHeight, z,
				// The bottom right corner is made.
				halfWidth, -halfHeight, z
		};

		indices = new byte[] {
				// This declares the vertices that will need to be used to
				// render each of the two triangles.
				// The first triangle uses 0, 1 and 2. In other words the bottom
				// left, the top left and the top right.
				0, 1, 2,
				// The second triangle uses 2, 3 and 0. In other words the top
				// right, the bottom right and the bottom left.
				2, 3, 0
		};

		tcs = new float[] {
				// Finally the texture coordinates are defined as the four
				// corners of the texture.
				// These coordinates are in terms of 0,0 being the top left.
				// 0, 1 refers to the bottom left.
				0, 1,
				// 0, 0 refers to the top left.
				0, 0,
				// 1, 0 refers to the top right.
				1, 0,
				// 1, 1 refers to the bottom right.
				1, 1
		};
		
		// A vertex array is filled with all the needed vertices to be rendered.
		vArray = new VArray(vertices, indices, tcs);
	}
	
	public void render() {
		entityTexture.attach();
		Shader.STATICSHADER.activate();
		Matrix4f output = Matrix4f.translate(position);
		Shader.STATICSHADER.setUniformMatrix("ml_matrix", output);
		vArray.render();
		Shader.STATICSHADER.deactivate();
		entityTexture.detach();
	}
}
