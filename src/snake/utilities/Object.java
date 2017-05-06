package snake.utilities;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;


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


    public void move(double xOffSet, double yOffSet) {
        object.setTranslateX(object.getTranslateX() + xOffSet);
        object.setTranslateY(object.getTranslateY() + yOffSet);
    }

    public void setCoordinates(double x, double y) {
        object.setTranslateX(x);
        object.setTranslateY(y);
    }

    public Node getNode() {
        return object;
    }


    public boolean isColliding(Object other) {
        return object.getTranslateX() == other.getNode().getTranslateX() && object.getTranslateY() == other.getNode().getTranslateY();
//        return object.getBoundsInParent().intersects(other.getNode().getBoundsInParent());
    }

}
