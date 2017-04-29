package snake;

import javafx.geometry.Point2D;
import javafx.scene.Node;


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

    @Override
    public void move(int xOffSet, int yOffSet) {
        if (border != null) {
            double x = object.getTranslateX() + xOffSet;
            double y = object.getTranslateY() + yOffSet;
            if (x < 0) setCoordinates(new Point2D(border.getX() - xOffSet, object.getTranslateY()));
            else if (x > border.getX()) setCoordinates(new Point2D(0, object.getTranslateY()));
            else if (y < 0) setCoordinates(new Point2D(object.getTranslateX(), border.getY() - yOffSet));
            else if (y > border.getY()) setCoordinates(new Point2D(object.getTranslateX(), 0));
            else super.move(xOffSet, yOffSet);
        } else {
            super.move(xOffSet, yOffSet);
        }
    }
}