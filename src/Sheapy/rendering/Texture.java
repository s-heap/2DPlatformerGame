package Sheapy.rendering;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import Sheapy.utils.BufferUtilities;

public class Texture {

	// 2 integers are made to store the texture's dimensions.
	private int width, height;
	// An integer is made to store the texture id.
	private int textureID;

	// In it's constructor it calls load to set up the texture.
	public Texture(String path) {
		int[] pixelArray = loadImage(path);
		// We now have an array of pixels but beforeopenGL we need to change it's format through some byte shifting.
		int[] glPixelArray = rearrangeColourLayout(pixelArray);
		// Our final texture that we'll output is made.
		int tex = glGenTextures();
		// We bind the texture to activate it for use.
		glBindTexture(GL_TEXTURE_2D, tex);
		// To fine tune we set some parameter by deactivating anti-aliasing to avoid image blurring at high resolutions.
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtilities.createIntegerBuffer(glPixelArray));
		// Finally we unbind the texture so we don't accidently use it in the future.
		glBindTexture(GL_TEXTURE_2D, 0);
		textureID = tex;
	}

	private int[] loadImage(String filePath) {
		// Creates an array of pixels to feed to OpenGL.
		int[] pixels = null;
		try {
			// A buffered image is created and with the image's file path it's pixel data is loaded herein.
			BufferedImage image = ImageIO.read(new FileInputStream(filePath));
			// The width and height of the image are loaded into width and height respectively.
			width = image.getWidth();
			height = image.getHeight();
			// A one dimensional array is used to store the pixels as if they were in a two dimensional array. (Similar to how we did this with matrices.)
			// This array will store the integer colour of each pixel once we fill it.
			pixels = new int[width * height];
			// The RGB values of each pixel is taken from the BufferedImage
			// "image" and stored in pixels.
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pixels;
	}

	private int[] rearrangeColourLayout(int[] pixelsArray) {
		// We loaded the colours in as "A R G B". OpenGL requires "R G B A".
		// First we make a new array to store the newly formatted array.
		int[] output = new int[width * height];
		// A for loop is used to loop through each pixel in our array.
		for (int i = 0; i < width * height; i++) {
			// Now we extract each channel. A, R, G and B.
			// We use the bitwise AND operator to shift the bits so they are reordered.
			// Since the alpha values need to be effectively shifted by 6 hex digits (Each represents a 4 bit binary number.) we need to therefore move it by 24 bits.
			int a = ((pixelsArray[i] >> 24) & 0xff);
			int r = ((pixelsArray[i] >> 16) & 0xff);
			int g = ((pixelsArray[i] >> 8) & 0xff);
			int b = ((pixelsArray[i] >> 0) & 0xff);
			// Finally we give the specific pixel "i" it's re-formatted value. r is at the front, g is 8 bits behind. b is another 8 bits behind and a is at the back.
			output[i] = a << 24 | b << 16 | g << 8 | r;
		}
		return output;
	}

	public void attach() {
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	public void detach() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

}
