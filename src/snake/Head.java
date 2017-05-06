package snake;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import snake.utilities.Object;




/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-28, 21:19.
 * gmalinowski@protonmail.com
 */
public class Head extends Object {
    private Point2D border = null;

    public Head(Node object, int x, int y) {
        super(object, x, y);
    }

    void setBorder(Point2D borderSize) {
        border = borderSize;
    }

    Point2D getBorder() {
        return border;
    }

    @Override
    public void move(double xOffSet, double yOffSet) {
        if (border != null) {
            double x = object.getTranslateX() + xOffSet;
            double y = object.getTranslateY() + yOffSet;
            if (x < 0) setCoordinates(border.getX() + xOffSet, object.getTranslateY());
            else if (x >= border.getX()) setCoordinates(0, object.getTranslateY());
            else if (y < 0) setCoordinates(object.getTranslateX(), border.getY() + yOffSet);
            else if (y >= border.getY()) setCoordinates(object.getTranslateX(), 0);
            else super.move(xOffSet, yOffSet);
        } else {
            super.move(xOffSet, yOffSet);
        }
    }
}