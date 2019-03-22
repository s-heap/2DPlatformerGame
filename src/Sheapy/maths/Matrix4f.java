package Sheapy.maths;

import java.nio.FloatBuffer;

import Sheapy.utils.BufferUtilities;

public class Matrix4f {
	
	public static final float SIZE = 4 * 4;
	public float[] elements = new float[4 * 4];

	// The matrix constructor fills in all the null elements with 0 to avoid issues with newly creates matrices.
	public Matrix4f() {
		for (int i = 0; i < SIZE; i++) {
			elements[i] = 0.0f;
		}
	}
	
	// Used in almost all matrix creations the identity matrix is made with a diagonal strip of 3 ones from the top left of the matrix.
	// 1 0 0 0
	// 0 1 0 0
	// 0 0 1 0
	// 0 0 0 0
	public static Matrix4f identity() {
		Matrix4f output = new Matrix4f();
		for (int x = 0; x < 4; x++) {
			output.elements[x + x * 4] = 1.0f;
		}
		return output;
	}
	
	// Returns an orthographic matrix used to define the projection matrix.
	public static Matrix4f orthographic(float leftBoundary, float rightBoundary, float bottomBoundary, float topBoundary, float nearBoundary, float farBoundary) {
		Matrix4f output = identity();
		
		output.elements[0] = 2.0f / (rightBoundary - leftBoundary);
		output.elements[5] = 2.0f / (topBoundary - bottomBoundary);
		output.elements[10] = 2.0f / (nearBoundary - farBoundary);
		
		output.elements[12] = (leftBoundary + rightBoundary) / (leftBoundary - rightBoundary);
		output.elements[13] = (bottomBoundary + topBoundary) / (bottomBoundary - topBoundary);
		output.elements[14] = (farBoundary + nearBoundary) / (farBoundary - nearBoundary);
		
		return output;
	}
	
	// Simply assigns a position vector to a matrix.
	public static Matrix4f translate(Vector3f vector) {
		Matrix4f output = identity();
		
		output.elements[12] = vector.x;
		output.elements[13] = vector.y;
		output.elements[14] = vector.z;
		
		return output;
	}
	
	// Returns a rotation matrix that is used to create a model matrix for each rotating entity.
	public static Matrix4f rotate(float angle) {
		Matrix4f output = identity();
		float rAngle = (float) Math.toRadians(angle);
		float cos = (float) Math.cos(rAngle);
		float sin = (float) Math.sin(rAngle);
		
		output.elements[0] = cos;
		output.elements[1] = sin;
		
		output.elements[4] = -sin;
		output.elements[5] = cos;
		
		return output;
	}
	
	// A function that returns the matrix multiplied by an input matrix.
	public Matrix4f multiply(Matrix4f matrix) {
		Matrix4f output = new Matrix4f();
		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				float sum = 0.0f;
				for (int e = 0; e < 4; e++) {
					sum += this.elements[x + e * 4] * matrix.elements[e + y * 4];
				}
				output.elements[x + y * 4] = sum;
			}
		}		
		return output;
	}
	
	// Converts the elements array into a float buffer for use in a uniform variable so that matrices can be easily sent to the shaders.
	public FloatBuffer toFloatBuffer() {
		return BufferUtilities.createFloatBuffer(elements);
	}
	
	public  void print() {
		for (float element : elements) {
			System.out.println(element);
		}
	}
}
