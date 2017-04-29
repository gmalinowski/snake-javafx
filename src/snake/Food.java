package snake;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import snake.utilities.Object;

/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-29, 19:45.
 * gmalinowski@protonmail.com
 */
public class Food extends Object {

    public Food(Node object, double x, double y) {
        super(object, x, y);
    }

    public void randFoodCoord(double x, double y) {

    }

    Pane addToScene(Pane pane) {
        pane.getChildren().add(head.getNode());

        for (int i = 0; i < tail.size(); i++) {
            pane.getChildren().add(tail.get(i).getNode());
        }

        return pane;
    }
}
