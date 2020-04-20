
package mapgen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author lukak
 */
public class MapGen extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        boolean add = scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());

        stage.setScene(scene); //showing the window
        stage.setTitle("MapGen - Perlin Noise Generator"); //setting the title
        stage.getIcons().add( //setting the icon
                new Image(
                        MapGen.class.getResourceAsStream("icon.png")));
        stage.show();

        //setting the max and min heighs so that the window cant be streched
        stage.setMaxHeight(stage.getHeight()); 
        stage.setMaxWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
