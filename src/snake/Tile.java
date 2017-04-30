package snake;

import javafx.scene.Node;
import javafx.scene.paint.Color;
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

    public Color getColor() {
        return color;
    }
}
