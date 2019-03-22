package Sheapy.maths;

public class MapReference {
	
	public int x, y;

	public MapReference() {
		x = 0;
		y = 0;
	}

	public MapReference(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public MapReference(MapReference ref) {
		this(ref.x, ref.y);
	}
}
