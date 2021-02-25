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
		// TODO Auto-generated method stub
		generateHistogram();
		
	}
	
	public static void generateHistogram() throws IOException {
		int[] histogramArray = new int[256];

        File imageFile = new File("src/imageSegmentation/image1.jpg");
        BufferedImage image = ImageIO.read(imageFile);

        for(int h = 0; h < image.getHeight(); h++)
        {
            for(int w = 0; w < image.getWidth(); w++)
            {
                Color c = new Color(image.getRGB(w, h));
                histogramArray[c.getRed()]++;
            }
        }
        
        for(int i = 0; i<histogramArray.length; i++) {
        	System.out.println(histogramArray[i]);
        }
	} //end generateHistogram()

}