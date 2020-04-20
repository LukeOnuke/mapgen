
package mapgen;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import java.util.Random;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javax.imageio.ImageIO;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/*import umontreal.iro.lecuyer.probdistmulti.MultiNormalDist;*/
/**
 *
 * @author Lukeonuke
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private ImageView picFrame;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Slider scrollbar;
    @FXML
    private TextField nRMTF;
    @FXML
    private RadioButton sNFRadioButton;
    @FXML
    private Slider scrollbar1;
    @FXML
    private TextField sNFTF;
    @FXML
    private TextField nPoints;
    @FXML
    private RadioButton iCRB;
    @FXML
    private AnchorPane ap;

    private BufferedImage bImg;

    //Generation of image
    @FXML
    public void handleGenImgButAction() {
        progressBar.setProgress(0.0);

        int imgSize = 512;
        int nPointsI = 20;
        try {
            nPointsI = Integer.parseInt(nPoints.getText());
        } catch (Exception e) {
            System.out.println(e);
        }

        //String imagePath = "C:/Users/lukak/Desktop/TestImage.jpg";
        BufferedImage image = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLUE);
        //array dotpoz x, y, x, y
        int[] dotpos = new int[nPointsI * 2];
        int[] colorIndexAr = new int[100];
        Color color = new Color(255, 255, 255, 255);
        int rgb = color.getRGB();

        int numofelements = dotpos.length; //get the num of elements of the dotpos array
        colorIndexAr = new int[numofelements / 2];
        Random rand = new Random();
        for (int i = 0; i < numofelements; i += 2) {
            dotpos[i] = rand.nextInt(512);
            dotpos[i + 1] = rand.nextInt(512);
        }

        for (int x = 0; x < imgSize; x++) {
            for (int y = 0; y < imgSize; y++) {
                image.setRGB(x, y, rgb);

                int colorIndex = 1;

                //getting the colorindexes
                for (int i = 0; i < numofelements; i += 2) {
                    int n = i;
                    if (n != 0) {
                        n = i / 2;
                    }
                    colorIndexAr[n] = (int) Math.round(Math.sqrt(Math.pow(x - dotpos[i], 2) + Math.pow(y - dotpos[i + 1], 2)));
                }

                colorIndex = colorIndexAr[0];

                //seting the actual color index based on wich one is the closest
                for (int colorIndexCandidate : colorIndexAr) {
                    if (colorIndexCandidate < colorIndex) {
                        colorIndex = colorIndexCandidate;
                    }
                }

                double noiseRadiusMultiplyer = 1.0;
                try {
                    noiseRadiusMultiplyer = scrollbar.getValue();
                } catch (Exception e) {

                }

                colorIndex = (int) Math.round(colorIndex * noiseRadiusMultiplyer);

                colorIndex = Math.max(0, Math.min(255, colorIndex));

                image.setRGB(x, y, new Color(colorIndex, colorIndex, colorIndex, 255).getRGB());
                progressBar.setProgress(progressBar.getProgress() + 0.000381469726562);
            }
        }

        boolean blur = sNFRadioButton.isSelected();
        if (blur) {
            progressBar.setProgress(0.0); //setting the progressbar
            float blurf = 1f; //default valiue

            try {
                blurf = (float) scrollbar1.getValue();
            } catch (Exception e) {

            }

            if (blurf < 1) {
                blurf = 1;
            }
            progressBar.setProgress(20.0); //setting the progressbar
            Kernel kernel = new Kernel(3, 3,
                    new float[]{
                        0.5f / blurf, 0.5f / blurf, 0.5f / blurf,
                        0.5f / blurf, 0.5f / blurf, 0.5f / blurf,
                        0.5f / blurf, 0.5f / blurf, 0.5f / blurf});

            BufferedImageOp op = new ConvolveOp(kernel);
            progressBar.setProgress(25.0 + progressBar.getProgress()); //setting the progressbar
            image = op.filter(image, null);
            progressBar.setProgress(100.0); //setting the progressbar
        }

        if (iCRB.isSelected()) {
            for (int x = 0; x < imgSize; x++) {
                for (int y = 0; y < imgSize; y++) {
                    int rgba = image.getRGB(x, y);
                    Color col = new Color(rgba, true);
                    col = new Color(255 - col.getRed(),
                            255 - col.getGreen(),
                            255 - col.getBlue());
                    image.setRGB(x, y, col.getRGB());
                }
            }
        }
        //Set the image
        bImg = image;
        picFrame.setImage(convertToFxImage(image));
    }

    @FXML
    public void sliderScrolEv() {
        nRMTF.setText(Double.toString(scrollbar.getValue()));
    }

    @FXML
    public void sliderMsREv() {
        nRMTF.setText(Double.toString(scrollbar.getValue()));
    }

    @FXML
    public void nRMTFAction() {
        try {
            if (nRMTF.getText() != null) {
                scrollbar.setValue(Double.parseDouble(nRMTF.getText()));
            }
        } catch (Exception e) {
        }
    }

    @FXML
    public void sliderScrolEv1() {
        sNFTF.setText(Double.toString(scrollbar1.getValue()));
    }

    @FXML
    public void sliderMsREv1() {
        sNFTF.setText(Double.toString(scrollbar1.getValue()));
    }

    @FXML
    public void sNFTFAction() {
        try {
            if (sNFTF.getText() != null) {
                scrollbar1.setValue(Double.parseDouble(sNFTF.getText()));
            }
        } catch (Exception e) {
        }
    }

    @FXML
    public void saveFile() { //menu item interface for save and save as
        Window mainStage = ap.getScene().getWindow(); //get ze window

        FileChooser fileChooser = new FileChooser(); //Filechooser the class that has the file chooser
        fileChooser.setTitle("Save"); //Title of prompt
        fileChooser.getExtensionFilters().addAll(                                       //add filter
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", ".bmp"),  //Filters
                new ExtensionFilter("All Files", "*.*"));                               //Filters
        File selectedFile = fileChooser.showSaveDialog(mainStage); //get the file
        if (selectedFile != null) { //if something is selected then
            System.out.println(selectedFile.getAbsoluteFile()); //debug
            System.out.println(selectedFile.getAbsoluteFile().getParent()); //debug
            Image img = SwingFXUtils.toFXImage(bImg, null); //convert to Image
            saveToFile(img, selectedFile); //save the Image
        }

    }

    @FXML
    public void exit() { // menu item interface for close or exit
        System.exit(0); //Close progam
    }

    @FXML
    public void about() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("About.fxml")); //load fxml
        Parent root1 = null; //new parrent
        try {
            root1 = (Parent) fxmlLoader.load(); //load
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage stage = new Stage(); //new stage
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("About"); //title
        stage.setScene(new Scene(root1)); //scene
        

        stage.show(); //show window

        stage.setMaxHeight(stage.getHeight()); //set the max ammounts so that its no strech
        stage.setMaxWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight()); //set the max ammounts so that its no strech
        stage.setMinWidth(stage.getWidth());
    }

    //Copy pasted from StackOverflow
    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }

    /**
     * A simple image save protocol
     *
     * @param image The image you want to save
     * @param file The file where you want to save it
     */
    public static void saveToFile(Image image, File file) {
        File outputFile = file; //file

        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null); //Convert to bufferedimage
        try {
            ImageIO.write(bImage, getFileExtension(outputFile).toUpperCase(), outputFile.getAbsoluteFile()); //actualy save
            System.out.println("Saveing file ex: " + getFileExtension(outputFile).toUpperCase() + " to: " + outputFile.getAbsoluteFile() + " with name " + outputFile.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".") + 1;
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scrollbar.setMax(1);
        scrollbar.setMin(0.0);
        scrollbar.setValue(0.5);

        scrollbar1.setMax(50);
        scrollbar1.setMin(1.0);
        scrollbar1.setValue(6);
    }

}
