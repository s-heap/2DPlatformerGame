package Sheapy;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Input extends GLFWKeyCallback {

	public static boolean[] keys = new boolean[65536];

	public void invoke(long window, int key, int scancode, int action, int mods) {
		// Checks if the key in "key" is being pressed or repeated. by making
		// sure it isn't released.
		keys[key] = action != GLFW.GLFW_RELEASE;
	}
	
	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}

	
}
