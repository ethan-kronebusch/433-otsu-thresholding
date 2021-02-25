package imageSegmentation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestMain {

	public TestMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		File imageFile = new File("src/imageSegmentation/image1.jpg");
		int[] hist = generateHistogram(imageFile);
		
		for(int x : hist) {
			System.out.println(x);
		}
	}
	
	public static int[] generateHistogram(File imageFile) throws IOException {
		int[] histogramArray = new int[256];
        BufferedImage image = ImageIO.read(imageFile);

        for(int h = 0; h < image.getHeight(); h++)
        {
            for(int w = 0; w < image.getWidth(); w++)
            {
                Color c = new Color(image.getRGB(w, h));
                histogramArray[c.getRed()]++;
            }
        }
        
        return histogramArray;
	} //end generateHistogram()

}
