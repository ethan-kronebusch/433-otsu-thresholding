package imageSegmentation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Scene1Controller {

    @FXML
    private Button selectImageButton;

    @FXML
    private TextField filePathTextField;

    @FXML
    private Button browseButton;
    
    @FXML
    private ImageView inputImageView;
    
    @FXML
    private ImageView outputImageView;

    @FXML
    private Button inputHistButton;
    
    
    private String filePath = null;
    private File imageFile;
    private BufferedImage inputImage, outputImage;
    private double[] inputHist;
    private double optK;

    @FXML
    void browseButtonAction(ActionEvent event) {
    	try {
    		FileChooser fileChooser = new FileChooser();
        	fileChooser.setTitle("Open Resource File");
        	fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        	
        	imageFile = fileChooser.showOpenDialog(null);
        	filePathTextField.setText(imageFile.toString());
    	}
    	catch(NullPointerException e) {
    		System.out.println(e.toString());
    		System.out.println("No file was selected");
    	}
    }

    @FXML
    void selectImageButtonAction(ActionEvent event) throws IOException {
    	filePath = filePathTextField.getText();
    	filePathTextField.setText("");
    	
    	if(!filePath.isBlank()) {
    		try {
    			imageFile = new File(filePath);
        		inputImage = ImageIO.read(imageFile);
        		
        		inputHist = generateHistogram(inputImage);
        		
        		optK = retrieveK(inputHist);
        		
        		outputImage = thresholdImage(inputImage, optK);
        		
        		//FileInputStream inputstream = new FileInputStream(filePath);
        		//Image image = new Image(inputstream);
        		
        		Image imageIn = SwingFXUtils.toFXImage(inputImage, null);
        		Image imageOut = SwingFXUtils.toFXImage(outputImage, null);
        		
        		inputImageView.setImage(imageIn);
        		inputImageView.setPreserveRatio(true);
        		
        		outputImageView.setImage(imageOut);
        		outputImageView.setPreserveRatio(true);
        		
        		inputHistButton.setOpacity(1);
        		inputHistButton.setDisable(false);
    		}
    		catch(NullPointerException e) {
    			System.out.println("Incorrect File Type!");
    		}
    		catch(IIOException e) {
    			System.out.println("Incorrect Filepath!");
    		}
    		catch(Exception e) {
    			System.out.println(e.toString());
    		}
    		
    	}
    	else {
    		filePathTextField.setPromptText("Please select a file!!");
    	}

    }
    
    @FXML
    void selectInputHistButtonAction(ActionEvent event) {
    	displayHistogram(inputHist);
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
    
    public void displayHistogram(double[] histogramArray) {
    	Stage stage = new Stage();
    	final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0,1,1);
        final BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);
        
        for(int i = 0; i<histogramArray.length; i++) {
			System.out.print(histogramArray[i] + ", ");
			XYChart.Series tempSeries = new XYChart.Series();
			tempSeries.getData().add(new XYChart.Data(i+"", histogramArray[i]));
			bc.getData().add(tempSeries);
		}
        
        bc.setLegendVisible(false);
        Scene scene  = new Scene(bc,800,600);
        stage.setScene(scene);
        stage.show(); 
    } //end displayHistogram
    
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

}
