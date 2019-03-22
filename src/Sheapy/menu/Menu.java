package Sheapy.menu;

import java.io.File;

import Sheapy.game.Constants;
import Sheapy.maths.Matrix4f;
import Sheapy.maths.Vector3f;
import Sheapy.rendering.Shader;

public class Menu {
	private String filepath;
	private boolean optionSelected = false;
	private boolean isQuitGame = false;
	private Button[] phase0Buttons = new Button[0];
	private Button[] phase1Buttons = new Button[0];
	private Button[] phase2Buttons = new Button[0];
	private final float leftSide = 0, rightSide = 1000, topSide = 1000, bottomSide = 0;
	private int clickCooldown = 0;

	// 0 = home menu
	// 1 = level select
	// 2 = settings
	private int menuPhase = 0;

	public Menu() {
		File folder = new File("res/Maps");
		File[] listOfFiles = folder.listFiles();

		phase0Buttons = addButton(phase0Buttons, new Vector3f(500, 600), "res/Buttons/levelSelect.png");
		phase0Buttons = addButton(phase0Buttons, new Vector3f(500, 400), "res/Buttons/settings.png");
		phase0Buttons = addButton(phase0Buttons, new Vector3f(500, 200), "res/Buttons/quit.png");

		phase1Buttons = addButton(phase1Buttons, new Vector3f(945, 55), "res/Buttons/back.png");
		float xPos = 55;
		float yPos = 945;
		for (File file : listOfFiles) {
			if (!file.isHidden()) {
				phase1Buttons = addButton(phase1Buttons, new Vector3f(xPos, yPos), "res/Maps/" + file.getName());
				yPos -= 105;
				if (yPos < 100) {
					xPos += 105;
					yPos = 945;
				}
			}
		}

		phase2Buttons = addButton(phase2Buttons, new Vector3f(945, 55), "res/Buttons/back.png");
		if (Constants.isLightingOn) {
			phase2Buttons = addButton(phase2Buttons, new Vector3f(555, 500), "res/Buttons/lightingOn.png");
		} else {
			phase2Buttons = addButton(phase2Buttons, new Vector3f(555, 500), "res/Buttons/lightingOff.png");
		}
		if (Constants.isHardMode) {
			phase2Buttons = addButton(phase2Buttons, new Vector3f(445, 500), "res/Buttons/hardModeOn.png");
		} else {
			phase2Buttons = addButton(phase2Buttons, new Vector3f(445, 500), "res/Buttons/hardModeOff.png");
		}

	}

	public void update(float xPos, float yPos, Boolean mouseClick) {
		updateProjectionMatrix();

		xPos *= 500;
		yPos *= 500;
		xPos += 500;
		yPos += 500;

		// System.out.println(xPos + " " + yPos);
		if (clickCooldown > 0) {
			clickCooldown--;
		} else if (mouseClick) {
			switch (menuPhase) {
				case 0:
					if (phase0Buttons[0].checkIfOver(new Vector3f(xPos, yPos))) {
						menuPhase = 1;
					} else if (phase0Buttons[1].checkIfOver(new Vector3f(xPos, yPos))) {
						menuPhase = 2;
					} else if (phase0Buttons[2].checkIfOver(new Vector3f(xPos, yPos))) {
						optionSelected = true;
						isQuitGame = true;
					}
				break;
				case 1:
					if (phase1Buttons[0].checkIfOver(new Vector3f(xPos, yPos))) {
						menuPhase = 0;
					} else {
						for (Button button : phase1Buttons) {
							if (button.checkIfOver(new Vector3f(xPos, yPos))) {
								filepath = button.getTPath();
								optionSelected = true;
							}
						}
					}
				break;
				case 2:
					if (phase2Buttons[0].checkIfOver(new Vector3f(xPos, yPos))) {
						menuPhase = 0;
					} else if (phase2Buttons[1].checkIfOver(new Vector3f(xPos, yPos))) {
						if (Constants.isLightingOn) {
							phase2Buttons[1].setTpath("res/Buttons/lightingOff.png");
							Constants.isLightingOn = false;
						} else {
							phase2Buttons[1].setTpath("res/Buttons/lightingOn.png");
							Constants.isLightingOn = true;
						}
					} else if (phase2Buttons[2].checkIfOver(new Vector3f(xPos, yPos))) {
						if (Constants.isHardMode) {
							phase2Buttons[2].setTpath("res/Buttons/hardModeOff.png");
							Constants.isHardMode = false;
						} else {
							phase2Buttons[2].setTpath("res/Buttons/hardModeOn.png");
							Constants.isHardMode = true;
						}
					}
				break;
			}
			clickCooldown = 30;
		}
	}

	public void render() {
		Shader.STATICSHADER.activate();

		switch (menuPhase) {
			case 0:
				for (Button button : phase0Buttons) {
					button.render();
				}
			break;
			case 1:
				for (Button button : phase1Buttons) {
					button.render();
				}
			break;
			case 2:
				for (Button button : phase2Buttons) {
					button.render();
				}
			break;
		}

		Shader.STATICSHADER.deactivate();
	}

	public String getOutcome() {
		return filepath;
	}

	public boolean isGameToClose() {
		return isQuitGame;
	}

	public boolean isMenuComplete() {
		return optionSelected;
	}

	// Handles adding a new button to the button array.
	private Button[] addButton(Button[] buttons, Vector3f pos, String tPath) {
		Button[] temp = new Button[buttons.length + 1];
		for (int p = 0; p < buttons.length; p++) {
			temp[p] = buttons[p];
		}
		temp[temp.length - 1] = new Button(pos, tPath);
		return temp;
	}

	private void updateProjectionMatrix() {
		// A new orthographic matrix is made called "pr_matrix" (For "Projection Matrix") which has values corresponding to the screen size.
		Matrix4f pr_matrix = Matrix4f.orthographic(leftSide, rightSide, bottomSide, topSide, -1.0f, 1.0f);
		// Sets the active texture to 1 which corresponds to when "setUniform1i" is called.

		// Sets a uniform variable to be given to the shader "player" when run. The uniform variable is of type Matrix4f and is called "pr_matrix".
		Shader.STATICSHADER.setUniformMatrix("pr_matrix", pr_matrix);
	}
}
