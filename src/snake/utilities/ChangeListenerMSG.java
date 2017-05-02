package snake.utilities;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.Stage;
import snake.Snake;

/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-29, 19:21.
 * gmalinowski@protonmail.com
 */
public class ChangeListenerMSG implements ChangeListener<Number> {
    private Snake snake;
    private Scene scene;
    private boolean msgON;

    public ChangeListenerMSG(Snake snake, Scene scene, boolean msgON) {
        this.snake = snake;
        this.scene = scene;
        this.msgON = msgON;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double width, height;
        int tmp;
        tmp = ((int) scene.getWidth()) / snake.getTileSize();
        width = tmp * snake.getTileSize();

        tmp = ((int) scene.getHeight()) / snake.getTileSize();
        height = tmp * snake.getTileSize();


        snake.setHeadBorder(new Point2D(width, height));

//        scene.setWidth(width);
//        scene.setHeight(height);

        if (msgON) {
            System.out.println("Window width: " + scene.getWidth() + " border: " + width);
            System.out.println("Window height: " + scene.getHeight() + " border: " + height);
        }
    }



}
