package imageSegmentation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestMain {

	public TestMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		File imageFile = new File("src/imageSegmentation/harewood.jpg");
		BufferedImage imageOut, image = ImageIO.read(imageFile);
		double[] hist = generateHistogram(image);
		double optK;
		
		double total = 0;
		for(double x : hist) {
			total += x;
			System.out.print(x + ", ");
		}
		
		optK = retrieveK(hist);
		System.out.println("\n" + total + "\n" + optK);
		
		imageOut = thresholdImage(ImageIO.read(imageFile), optK);
		
		//save output image
		try {
			File outputFile = new File("src/imageSegmentation/" + imageFile.getName().substring(0, imageFile.getName().lastIndexOf('.')) + "_segmented.jpg");
			ImageIO.write(imageOut, "jpg", outputFile);
		}catch(IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public static double[] generateHistogram(BufferedImage image) throws IOException {
		double[] histogramArray = new double[256];
        double pixelValue = (double)1/(image.getHeight()*image.getWidth());
        
        for(int h = 0; h < image.getHeight(); h++)
        {
            for(int w = 0; w < image.getWidth(); w++)
            {
            	int power = image.getRaster().getSample(w, h, 0);
                histogramArray[power] += pixelValue;
            }
        }
        
        return histogramArray;
	} //end generateHistogram()
	
	public static double retrieveK(double[] histogram) {
		double optK = 0, maxBCV = Double.MIN_VALUE, bcv;
		
		//find the maximum between-class variance for K
		for(int k = 0; k<histogram.length; k++) {
			bcv = betweenClassVariance(histogram, k);
			
			if(bcv > maxBCV) {
				optK = k;
				maxBCV = bcv;
			}
		}
		
		return optK;
	}
	
	public static double betweenClassVariance(double[] histogram, double k) {
		double lowMean = 0, hiMean = 0, classProb = 0, bcv;
		
		//calculate class probability
		for(int i = 0; i < k; i++) {
			classProb += histogram[i];
		}
		
		//calculate average intensity of dark and light classes
		for(int i = 0; i < k; i++) {
			lowMean += i*histogram[i];
		}
		for(int i = (int)k; i < histogram.length; i++) {
			hiMean += i*histogram[i];
		}
		
		//calculate the average power of the dark class
		lowMean /= classProb;
		//calculate the average power of the light class
		hiMean /= 1-classProb;
		
		//calculate between-class variance
		bcv = Math.pow((lowMean - hiMean), 2);
		bcv *= classProb*(1-classProb);
		
		return bcv;
	}
	
	public static BufferedImage thresholdImage(BufferedImage input, double k) {
		int height = input.getHeight();
		int width = input.getWidth();
		BufferedImage output = new BufferedImage(width, height, input.getType());
		
		for(int h = 0; h < height; h++)
        {
            for(int w = 0; w < width; w++)
            {
            	int power = input.getRaster().getSample(w, h, 0);
                if(power < k) {
                	output.setRGB(w, h, 0);
                }else {
                	output.setRGB(w, h, -1);
                }
            }
        }
		
		return output;
	}
	
	//TODO test method
	public static BufferedImage meanFilter(BufferedImage input, int filterRadius) {
		//apply a mean filter to smooth the image
		int width = input.getWidth(), height = input.getHeight();
		BufferedImage output = new BufferedImage(width, height, input.getType());
		
		//iterate through the image
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				
				int total=0;
				//for each pixel, iterate through the filter
				for(int fy = y-filterRadius;fy <= y+filterRadius; fy++) {
					for(int fx = x-filterRadius;fx <= x+filterRadius; fx++) {
						//use mirror padding
						int corX = Math.abs(fx);
						int corY = Math.abs(fy);
						if(corX >= width) {
							corX = (width-1)*2 - corX;
						}
						if(corY >= height) {
							corY = (height-1)*2 - corY;
						}
						
						total += input.getRaster().getSample(corX, corY, 0);
					}
				}
				
				//get the average intensity
				total /= Math.pow((filterRadius*2)+1,2);
				//convert to RGB integer & set colour
				String binary = Integer.toBinaryString(total).substring(24);
				output.setRGB(x, y, Integer.parseInt("11111111" + binary.repeat(3), 2));
			}
		}
		
		return output;
	}
}
