package snake;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import snake.utilities.Object;

/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-29, 19:45.
 * gmalinowski@protonmail.com
 */
class Food extends Object {
    private Rectangle food = ((Rectangle) super.getNode());

    Food(int foodSize, Color foodColor, double initPosX, double initPosY) {
        super(new Rectangle(foodSize, foodSize, foodColor), initPosX, initPosY);
    }

    void newFoodPosition(double x, double y) {

    }

    void newRandomFoodPosition(Point2D borderSize) {
        double x = Math.random() * (borderSize.getX() - food.getWidth());
        double y = Math.random() * (borderSize.getY() - food.getHeight());

        food.setTranslateY(x);
        food.setTranslateY(y);
    }

    Pane addToScene(Pane pane) {
        pane.getChildren().add(food);

        return pane;
    }
}
