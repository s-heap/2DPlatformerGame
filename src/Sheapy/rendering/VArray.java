package Sheapy.rendering;

import static org.lwjgl.opengl. GL11. *;
import static org.lwjgl.opengl. GL15. *;
import static org.lwjgl.opengl. GL20. *;
import static org.lwjgl.opengl. GL30. *;

import Sheapy.utils.BufferUtilities;

// A vertex array is just an array of vertices that we send to the shader for the graphic's card to use.
public class VArray {
	// A few integer values are made: A Vertex Array Object and an Index Buffer Object.
	private int vao, ibo;

	private int indexCount;
	
	// The constructor for this class is made to effectively connect the vertices, indices and texture coordinates needed to make a vertex array together.
	public VArray(float[] vertices, byte[] indices, float[] textureCoordinates) {
		vao = glGenVertexArrays();
		
		glBindVertexArray(vao);
		
		updateVertexBufferObject(vertices);
		
		updateTextureBufferObject(textureCoordinates);
		
		updateIndexBufferObject(indices);
		
		// To make sure we make no mistakes we will merely unbind all the buffers now we have generated them.
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void updateAttributeList(int attributeLocation,int dimensions, float[] verticesArray) {
		// We make a buffer for the Vertex Buffer Object and proceed to bind it so we can change it. (This effectively selects the buffer we are going to change.)
		glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
		
		glBufferData(GL_ARRAY_BUFFER, BufferUtilities.createFloatBuffer(verticesArray), GL_STATIC_DRAW);
		// The location of the vertex attributes that will be supplied to the shader is established.
		// It is given a size of 3 (For X, Y and Z). Floats are being used hence GL_FLOAT. It is not normalised.
		glVertexAttribPointer(attributeLocation, dimensions, GL_FLOAT, false, 0, 0);
		// Finally the vertex attribute array is enabled.
		glEnableVertexAttribArray(attributeLocation);
	}
	
	public void updateTextureBufferObject(float[] textureCoordinateArray) {
		updateAttributeList(Shader.TEXTURE_COORD_INPUT, 2, textureCoordinateArray);
	}
	
	public void updateVertexBufferObject(float[] verticesArray) {
		updateAttributeList(Shader.VERTEX_POSITION_INPUT, 3, verticesArray);
	}
	
	public void updateIndexBufferObject(byte[] indicesArray) {
		// Count is established as the amount of values in "indices".
		indexCount = indicesArray.length;
		ibo = glGenBuffers();
		// Unlike the others it uses an Element array buffer. 
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		// 
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtilities.createByteBuffer(indicesArray), GL_STATIC_DRAW);
	}
	
	// This subroutine handles the binding of the vertex array before actually drawing the triangles needed on screen.
	public void render() {
		glBindVertexArray(vao);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		// glDrawElements is used to draw count amount of vertices (Usually 6, to make 2 triangles to form a rectangle) onto the screen to actually render everything seen in the game.
		glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_BYTE, 0);
	}	
}
