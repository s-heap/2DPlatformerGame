package Sheapy.rendering;

import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector2f;
import Sheapy.maths.Vector3f;
import Sheapy.utils.FileUtilities;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;

public class Shader {

	// Attribute locations are made. They are like uniform variables because they are given to the shader program. However they are set every single vertex.
	// They store vertex data which is different for each vertex being rendered.
	public static final int VERTEX_POSITION_INPUT = 0;
	public static final int TEXTURE_COORD_INPUT = 1;

	// A value used to check if the shader is currently activated, ready for use.
	private boolean active;

	// These are the two static shader's my program makes used of.
	// WORLDSHADER implements lighting and a view matrix whilst STATICSHADER is simpler and doesn't.
	public static Shader WORLDSHADER, STATICSHADER;

	private final int ID;
	// A map is created so that the program only needs to find the location once.
	// It has a name (String) and a location (Integer) and is called "locationCache"
	private HashMap<String, Integer> locationCache = new HashMap<String, Integer>();

	// Sets up a constructor which establishes ID upon creation.
	public Shader(String vertPath, String fragPath) {
		String vert = FileUtilities.loadAsString(vertPath);
		String frag = FileUtilities.loadAsString(fragPath);
		// Creates a new program object.
		int program = glCreateProgram();
		// The memory location ID for a vertex and fragment shader respectively is created.
		int vertID = glCreateShader(GL_VERTEX_SHADER);
		int fragID = glCreateShader(GL_FRAGMENT_SHADER);
		
		attachShader(program, vertID, vert);
		attachShader(program, fragID, frag);
		
		// Links ands Validates the program object.
		glLinkProgram(program);
		glValidateProgram(program);
		// After linking everything to the program we can delete the shaders we have made. So they don't get in the way.
		glDeleteShader(vertID);
		glDeleteShader(fragID);

		// Returns program for use when create() is called.
		ID = program;

	}

	// Connects the shader ID to it's specific shader with the shader program.
	private void attachShader(int program, int ID, String shaderAsString) {
		glShaderSource(ID, shaderAsString);
		if (!hasCompiled(ID)) {
			System.err.println("Failed to compile vertex shader!");
			System.exit(1);
		}
		glAttachShader(program, ID);
	}
	
	// Checks to make sure the shader is properly compiled.
	private boolean hasCompiled(int ID) {
		glCompileShader(ID);
		if (glGetShaderi(ID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(ID));
			return false;
		}
		return true;
	}

	// The load all subroutine is used to initialise all the needed shaders.
	public static void loadShaders() {
		WORLDSHADER = new Shader("shaders/WorldShader.vert", "shaders/WorldShader.frag");
		STATICSHADER = new Shader("shaders/StaticShader.vert", "shaders/StaticShader.frag");
	}

	// Get's the location of a specified uniform variable to get data to the shader.
	public int getUniformValue(String name) {
		// A check is made to see if the "locationCache" map has the location needed. If found it can skip the finding process and return the location stored in the map.
		if (locationCache.containsKey(name)) {
			return locationCache.get(name);
		}
		// If the location is not in the map we made the location must be found.
		int result = glGetUniformLocation(ID, name);
		if (result == -1) {
			System.err.println("Could not find uniform variable '" + name + "'!");
		} else {
			// Presuming a successful location has been found it is stored to the cache for future use.
			locationCache.put(name, result);
		}
		return result;
	}

	// A variety of uniform variable setting subroutines which combine getting the correct memory locations from the variable's name aswell as changing some of the formatting so they function properly.
	public void setUniformInt(String name, int value) {
		activate();
		glUniform1i(getUniformValue(name), value);
	}

	public void setUniformFloat(String name, float value) {
		activate();
		glUniform1f(getUniformValue(name), value);
	}

	public void setUniformFloatArray(String name, float[] array) {
		activate();
		glUniform1fv(getUniformValue(name), array);
	}

	public void setUniformVec2(String name, Vector2f vec) {
		activate();
		glUniform2f(getUniformValue(name), vec.x, vec.y);
	}

	public void setUniformVec2Array(String name, Vector2f[] vecArray) {
		activate();
		int count = 0;
		float[] outputArray = new float[vecArray.length * 2];
		for (Vector2f vector : vecArray) {
			outputArray[count++] = vector.x;
			outputArray[count++] = vector.y;
		}
		glUniform2fv(getUniformValue(name), outputArray);
	}

	public void setUniformVec3(String name, Vector3f vec) {
		activate();
		glUniform3f(getUniformValue(name), vec.x, vec.y, vec.z);
	}

	public void setUniformMatrix(String name, Matrix4f matrix) {
		activate();
		glUniformMatrix4fv(getUniformValue(name), false, matrix.toFloatBuffer());
	}

	// Activate the shader for future use.
	public void activate() {
		if (!active) {
			glUseProgram(ID);
			active = true;
		}
	}

	// Deactivate the shader.
	public void deactivate() {
		glUseProgram(0);
		active = false;
	}

}
