package snake;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import snake.utilities.Object;

/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-28, 21:48.
 * gmalinowski@protonmail.com
 */
class Tile extends Object {
    private Color color;
    Tile(Node object, double x, double y, Color color) {
        super(object, x, y);
        this.color = color;
    }

    Color getColor() {
        return color;
    }

    void setColor(Color color) {
        ((Rectangle) object).setFill(color);
    }

    void setRandomeColor(Color color) {
        double rand = Math.random();

        if (rand < 0.33) ((Rectangle) object).setFill(color);
        else if (rand < 0.66) ((Rectangle) object).setFill(color.brighter());
        else ((Rectangle) object).setFill(color.darker());
    }
}
