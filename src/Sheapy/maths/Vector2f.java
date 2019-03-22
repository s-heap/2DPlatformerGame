package Sheapy.maths;

public class Vector2f {
	
	public float x, y;

	public Vector2f() {
		x = 0.0f;
		y = 0.0f;
	}

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f(Vector2f vec) {
		this(vec.x, vec.y);
	}
	
	public Vector2f(Vector3f vec) {
		this(vec.x, vec.y);
	}
}
