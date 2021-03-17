package imageSegmentation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
 
public class testMainFX extends Application {
 
    @Override
    public void start(Stage stage) throws IOException {
    	File imageFile = new File("src/imageSegmentation/smile.jpg");
		double[] hist = generateHistogram(imageFile);
    	
        stage.setTitle("Histogram");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0,1,1);
        final BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);
        
        for(int i = 0; i<hist.length; i++) {
			System.out.print(hist[i] + ", ");
			XYChart.Series tempSeries = new XYChart.Series();
			tempSeries.getData().add(new XYChart.Data(i+"", hist[i]));
			bc.getData().add(tempSeries);
		}
        
        bc.setLegendVisible(false);
        Scene scene  = new Scene(bc,800,600);
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
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
}