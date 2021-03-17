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
		File imageFile = new File("src/imageSegmentation/swordcandle.png");
		double[] hist = generateHistogram(imageFile);
		
		double total = 0;
		for(double x : hist) {
			total += x;
			System.out.print(x + ", ");
		}
		
		System.out.println("\n" + total + "\n" + retrieveK(hist));
	}
	
	public static double[] generateHistogram(File imageFile) throws IOException {
		double[] histogramArray = new double[256];
        BufferedImage image = ImageIO.read(imageFile);
        double pixelValue = (double)1/(image.getHeight()*image.getWidth());
        
        for(int h = 0; h < image.getHeight(); h++)
        {
            for(int w = 0; w < image.getWidth(); w++)
            {
                Color c = new Color(image.getRGB(w, h));
                histogramArray[c.getRed()] += pixelValue;
            }
        }
        
        return histogramArray;
	} //end generateHistogram()
	
	public static double retrieveK(double[] histogram) {
		double globalMean = 0, optK = 0, maxBCV = Double.MIN_VALUE, bcv;
		
		//calculate global mean from histogram
		for(int i = 0; i<histogram.length; i++) {
			globalMean += i*histogram[i];
		}
		
		//find the maximum between-class variance for K
		for(int k = 0; k<histogram.length; k++) {
			bcv = betweenClassVariance(histogram, k, globalMean);
			if(bcv > maxBCV) {
				optK = k;
				maxBCV = bcv;
			}
		}
		
		return optK;
	}
	
	public static double betweenClassVariance(double[] histogram, double k, double globalMean) {
		double cumMean = 0, classProb = 0, bcv;
		
		//calculate class probability and cumulative mean
		for(int i = 0; i <= k; i++) {
			classProb += histogram[i];
		}
		for(int i = 0; i <= k; i++) {
			cumMean += i*histogram[i];
		}
		
		//calculate the between-class variance
		bcv = Math.pow(globalMean*classProb-cumMean, 2);
		bcv /= classProb*(1-classProb);
		
		return bcv;
	}
}
