package snake;

import javafx.geometry.Point2D;
import javafx.scene.Node;


/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-28, 17:30.
 * gmalinowski@protonmail.com
 */
public class Object {
    protected Node object;

    public Object(Node object, double x, double y) {
        this.object = object;
        object.setTranslateX(x);
        object.setTranslateY(y);
    }

    public void move(int xOffSet, int yOffSet) {
        object.setTranslateX(object.getTranslateX() + xOffSet);
        object.setTranslateY(object.getTranslateY() + yOffSet);
    }

    public void setCoordinates(Point2D point) {
        object.setTranslateX(point.getX());
        object.setTranslateY(point.getY());
    }

    public Node getNode() {
        return object;
    }

    public boolean isColliding(Object other) {
        return object.getBoundsInParent().intersects(other.getNode().getBoundsInParent());
    }

}
