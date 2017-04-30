package snake;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import snake.utilities.Object;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-28, 21:14.
 * gmalinowski@protonmail.com
 */
public class Snake {
    private Head head;
    private List<Tile> tail = new ArrayList<>();

    private int tileSize;
    private Color tailColor;

    Snake(int headSize, int headPosX, int headPosY, int tileSize, int tailLength, Color headColor, Color tailColor, Point2D border) {
        head = new Head(new Rectangle(headSize, headSize, headColor), headPosX, headPosY);
        if (border != null) head.setBorder(border);

        this.tileSize = tileSize;
        this.tailColor = tailColor;
        for (int i = 0; i < tailLength; i++) {
            tail.add(new Tile(new Rectangle(tileSize, tileSize, tailColor), headPosX, ((headPosY+ tileSize) + i * tileSize), tailColor));
        }

    }

    Pane addToScene(Pane pane) {
        pane.getChildren().add(head.getNode());

        for (int i = 0; i < tail.size(); i++) {
            pane.getChildren().add(tail.get(i).getNode());
        }

        return pane;
    }

    void move(int xOffSet, int yOffSet) {
        Point2D lastPos = new Point2D(head.getNode().getTranslateX(), head.getNode().getTranslateY());
        Point2D lastPos2;
        head.move(xOffSet, yOffSet);
        for (int i = 0; i < tail.size(); i++) {
            lastPos2 = new Point2D(tail.get(i).getNode().getTranslateX(), tail.get(i).getNode().getTranslateY());
            tail.get(i).setCoordinates(lastPos);
            i++;
            if (i >= tail.size()) break;
            lastPos = new Point2D(tail.get(i).getNode().getTranslateX(), tail.get(i).getNode().getTranslateY());
            tail.get(i).setCoordinates(lastPos2);
        }
    }

    Tile generateTile() {
        return generateTile(tailColor);
    }

    Tile generateTile(Color color) {
        Rectangle last = (Rectangle) tail.get(tail.size() - 1).getNode();
        Rectangle newRect = new Rectangle(tileSize, tileSize, color);
        Tile tile = new Tile(newRect, last.getTranslateX(), last.getTranslateY(), color);
        tail.add(tile);

        return tile;
    }

    public void setHeadBorder(Point2D point) {
        head.setBorder(point);
    }

    boolean isColliding(Object other) {
        return tail.stream().anyMatch(tile -> tile.isColliding(other)) ||
                head.isColliding(other);
    }

    boolean isHeadCollidingWithTail() {
        return tail.stream().anyMatch(tile -> tile.isColliding(head));
    }

    int size() {
        return 1 + tail.size();
    }

    Color getTailColor() {
        return tail.get(0).getColor();
    }


    //TODO czy head is cooliding with tail
}
