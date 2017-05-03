package snake;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import snake.utilities.Object;

import java.util.Random;

/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-29, 19:45.
 * gmalinowski@protonmail.com
 */
public class Food extends Object {
    private final int pricef;
    private int price;


    private Rectangle food = ((Rectangle) super.getNode());

    Food(int foodSize, Color foodColor, double initPosX, double initPosY, int price) {
        super(new Rectangle(foodSize, foodSize, foodColor), initPosX, initPosY);
        this.price = price;
        this.pricef = price;
    }

    void setImageAsFood(String imageSrc) {
        food.setFill(new ImagePattern(
                new Image(imageSrc), 0, 0, 1, 1, true
        ));
    }

    void resetPrice() {
        price = pricef;
    }

    int getPrice() {
        return price;
    }

    void setPrice(int price) {
        this.price = price;
    }

    void setFoodPosition(double x, double y) {
        food.setTranslateX(x);
        food.setTranslateY(y);
    }

    void newRandomFoodPosition(Point2D borderSize) {
        Random generator = new Random();
        double x = generator.nextInt(10000) * food.getWidth();
        x %= (borderSize.getX() - food.getWidth());

        double y = generator.nextInt(10000) * food.getHeight();
        y %= (borderSize.getY() - food.getHeight());

        food.setTranslateX(x);
        food.setTranslateY(y);
    }

    Pane addToScene(Pane pane) {
        pane.getChildren().add(food);

        return pane;
    }
}
