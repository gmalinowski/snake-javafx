package snake.utilities;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
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
        snake.setHeadBorder(new Point2D(scene.getWidth(), scene.getHeight()));

        if (msgON) {
            System.out.println("Window width: " + scene.getWidth());
            System.out.println("Window height: " + scene.getWidth());
        }
    }



}
