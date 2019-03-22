package Sheapy.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtilities {
	
	private BufferUtilities() {}
	
	// Creates a java.nio ByteBuffer from a byte array input, ready for use in OpenGl.
	public static ByteBuffer createByteBuffer(byte[] input) {
		ByteBuffer output = ByteBuffer.allocateDirect(input.length);
		output.order(ByteOrder.nativeOrder());
		output.put(input).flip();
		return output;
	}
	
	// Creates a java.nio ByteBuffer from a byte array input, ready for use in OpenGl.
	public static FloatBuffer createFloatBuffer(float[] input) {
		ByteBuffer tempBuffer = ByteBuffer.allocateDirect(input.length * 4);
		FloatBuffer output = tempBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();
		output.put(input).flip();
		return output;
	}
	
	// Creates a java.nio ByteBuffer from a byte array input, ready for use in OpenGl.
	public static IntBuffer createIntegerBuffer(int[] input) {
		ByteBuffer tempBuffer = ByteBuffer.allocateDirect(input.length * 4);
		IntBuffer output = tempBuffer.order(ByteOrder.nativeOrder()).asIntBuffer();
		output.put(input).flip();
		return output;
	}
}
