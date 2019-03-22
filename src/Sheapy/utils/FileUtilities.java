package Sheapy.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtilities {

	private FileUtilities() {}

	public static String loadAsString(String filePath) {
		// Takes the file and reads it as a string. StringBuilder is used so that the string can be read line by line more easily.
		StringBuilder output = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String buffer = "";
			boolean validString = true;
			// Makes a while loop which ends if we are the end of the file. Ergo "null".
			while (validString) {
				buffer = reader.readLine();
				if (buffer != null) {
					output.append(buffer + "\n");
				} else {
					validString = false;
				}
			}
			reader.close();
			// Checks to see if a certain error is found and prints the stack if this occurs.
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Turns the string builder back into a string to be returned.
		return output.toString();
	}
}
