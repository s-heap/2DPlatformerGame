package Sheapy;

//Using static and ".*" at the end means we import EVERYTHING in GLFW.
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import Sheapy.game.Game;
import Sheapy.menu.Menu;
import Sheapy.rendering.Shader;

public class Main {

	// Screen dimensions are determined as variables.
	private int width = 1000;
	private int height = 1000;


	// A boolean value to determine if the program should close.
	private boolean running = false;

	//A boolean variable to determine what state the entire should be in, menu or game.
	private boolean isMenu = true;

	// Creates a type long which stores the data for the window we will be using to display the game.
	private long window;
	
	
	private Game game;
	private Menu menu;

	// the start subroutine is called in main(String[] args) to start the program.
	public void start() {
		// Sets running to true so we know the game is supposed to be running.
		running = true;

		run();
	}

	// Sets up the window for use in the game.
	private void initialiseWindow() {
		if (!glfwInit()) {
			running = false;
		}

		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		// The window is initialised with a width, height and the monitor to use.
		window = GLFW.glfwCreateWindow(width, height, "Simon's Game", NULL, NULL);

		// Sets the video mode of the game to that of the primary monitor.
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Sets the position of the top left of the window when it is spawned on screen.
		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		glfwSetKeyCallback(window, new Input());

		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		GL.createCapabilities();

		// Enables depth test which checks the z coordinate before rendering so that certain
		// objects are rendered on top of each other (You wouldn't want the background written over the top of the player.).
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE1);
		// All of the needed shader's are loaded in for use in rendering.
		Shader.loadShaders();
		
		// Sets a uniform variable of 1 integer to be given to the shader "PLAYER".
		Shader.WORLDSHADER.setUniformInt("tex", 1);
		Shader.STATICSHADER.setUniformInt("tex", 1);

		menu = new Menu();
	}

	public void run() {
		initialiseWindow();
		// The current time in nanoseconds is stored in last time.
		long lastTime = System.nanoTime();
		// Delta is made. It will store the time since we last ran.
		double timePassed = 0.0;
		// The time of 1 second / 60 is found in nano seconds. If we refresh at this rate we will get 60 fps.
		double timePeriod = 1000000000.0 / 60.0;
		// Updates and frames are made to store the amount of times the game has updated and rendered respectively. Whilst timer marks the time passed since last the fps was outputted.
		int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();

		// Checks that the game is in running mode.
		while (running) {
			// "currentTime" is made and stores the time current time.
			long currentTime = System.nanoTime();
			// The time passed since the last update is found.
			timePassed += currentTime - lastTime;
			// lastTime is updated.
			lastTime = currentTime;
			// If the time passed is equal to the time period an update occurs and timePassed resets.
			if (timePassed >= timePeriod) {

				// Calls "update" to update everything that needs to be changed since the last frame was rendered. Then resets Time passed and increments update.
				update();
				timePassed -= timePeriod;
				updates++;
			}

			// Calls "render" to draw everything necessary on screen.
			render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer = System.currentTimeMillis();
				System.out.println(updates + " Updates Per Second | " + frames + " Frames Per Second ");
				updates = 0;
				frames = 0;
			}

			// Checks if the user is trying to close the window. And prevents further updating and rendering by setting running to false.
			if (glfwWindowShouldClose(window)) {
				running = false;
			}
		}
	}

	// Update's everything that needs to be changed since the last frame was rendered.
	private void update() {
		boolean mouseClick = (glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GL_TRUE);

		// All pending events are processed?
		glfwPollEvents();

		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, x, y);
		float xPos = (float) x.get(0) - width / 2;
		float yPos = (float) y.get(0) - height / 2;
		xPos /= width / 2;
		yPos /= -height / 2;

		// An if statement is used to check if the program is in the game state or menu state.
		// If after being updated the state is found to be completed it is swapped and a new instance of the opposite state is created.
		if (isMenu) {
			menu.update(xPos, yPos, mouseClick);
			if (menu.isMenuComplete()) {
				if (menu.isGameToClose()) {
					running = false;
				} else {
					game = new Game(menu.getOutcome());
					isMenu = false;
					System.out.println("Game starting");
				}
			}
		} else { 
			game.update(xPos, yPos, mouseClick);
			if (game.isGameComplete()) {
				menu = new Menu();
				isMenu = true;
				System.out.println("Menu starting");
			}
		}
	}

	// Draws everything necessary on screen.
	private void render() {
		// Clears every pixel in the window so it is ready to be manipulated.
		glClearColor(0, 0, 0, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		if (isMenu) {
			menu.render();
		} else {
			game.render();
		}

		glfwSwapBuffers(window);

	}

	public static void main(String[] args) {
		new Main().start();
	}

}