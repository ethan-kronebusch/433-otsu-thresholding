package imageSegmentation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Scene1Controller {

    @FXML
    private Button selectImageButton;

    @FXML
    private TextField filePathTextField;

    @FXML
    private Button browseButton;
    
    @FXML
    private Pane imageDisplayPane;
    
    
    private String filePath = null;
    private File imageFile;
    double[] hist;

    @FXML
    void browseButtonAction(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Resource File");
    	imageFile = fileChooser.showOpenDialog(imageDisplayPane.getScene().getWindow());
    	filePathTextField.setText(imageFile.toString());

    }

    @FXML
    void selectImageButtonAction(ActionEvent event) throws IOException {
    	filePath = filePathTextField.getText();
    	
    	if(!filePath.isBlank()) {
    		imageFile = new File(filePath);
    		
    		hist = generateHistogram(imageFile);
    		displayHistogram(hist);
    		
    		imageDisplayPane.getChildren().add(new ImageView(imageFile.toURI().toURL().toExternalForm()));
    	}
    	else {
    		filePathTextField.setPromptText("Please select a file!!");
    	}

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
	} //end generateHistogram
    
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

}
