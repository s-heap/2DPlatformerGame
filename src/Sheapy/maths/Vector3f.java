package Sheapy.maths;

public class Vector3f {

	public float x, y, z;

	public Vector3f() {
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f(float x, float y) {
		this.x = x;
		this.y = y;
		this.z = 0.0f;
	}
	
	public Vector3f(Vector3f vec) {
		this(vec.x, vec.y, vec.z);
	}
	
	// Returns a vector with a negative x and y value. Used for creating the view matrix.
	public Vector3f toMinus() {
		return new Vector3f(-x, -y);
	}
}
