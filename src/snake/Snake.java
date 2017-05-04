package snake;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import snake.utilities.Direction;
import snake.utilities.Object;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-28, 21:14.
 * gmalinowski@protonmail.com
 */
public class Snake {
    private Head head;
    private Direction currentDirection;
    private int xOffSet, yOffSet;
    private List<Tile> tail = new ArrayList<>();

    private int tileSize;
    private Color tailColor;
    private boolean setMoveLock = true;
    String [] haedImgs = new String[4];

    Snake(int xOffSet, int yOffSet, int headSize, int headPosX, int headPosY, int tileSize, int tailLength, Color headColor, Color tailColor, Point2D border, Direction currentDirection) {
        head = new Head(new Rectangle(headSize, headSize, headColor), headPosX, headPosY);
        head.getNode().setId("head");
        this.currentDirection = currentDirection;
        this.xOffSet = xOffSet;
        this.yOffSet = yOffSet;
        haedImgs[0] = "snake/img/head/Square/snakeU.png";
        haedImgs[1] = "snake/img/head/Square/snakeR.png";
        haedImgs[2] = "snake/img/head/Square/snakeD.png";
        haedImgs[3] = "snake/img/head/Square/snakeL.png";
        setImgAsHead();
        if (border != null) head.setBorder(border);

        this.tileSize = tileSize;
        this.tailColor = tailColor;
        for (int i = 0; i < tailLength; i++) {
            tail.add(new Tile(new Rectangle(tileSize, tileSize, tailColor), headPosX, ((headPosY+ tileSize) + i * tileSize), tailColor));
        }

    }

    Snake(int headSize, int headPosX, int headPosY, int tileSize, int tailLength, Color headColor, Color tailColor, Point2D border, Direction currentDirection) {
        this(0, -headSize, headSize, headPosX, headPosY, tileSize, tailLength, headColor, tailColor, border, currentDirection);
    }

    public int getTileSize() {
        return tileSize;
    }

    void setImgAsHead() {
        int i = 0;
        switch (currentDirection) {
            case RIGHT: i = 1;
            break;
            case DOWN: i = 2;
            break;
            case LEFT: i = 3;
            break;
        }
        ((Rectangle) head.getNode()).setFill(new ImagePattern(
                new Image(haedImgs[i]), 0, 0, 1, 1, true
        ));
    }


    Pane addToScene(Pane pane) {
        pane.getChildren().add(head.getNode());

        for (int i = 0; i < tail.size(); i++) {
            pane.getChildren().add(tail.get(i).getNode());
        }

        return pane;
    }

    private boolean canMove(Direction direction) {
        if (currentDirection == direction) return false;
        if (currentDirection == Direction.UP && direction == Direction.DOWN) return false;
        if (currentDirection == Direction.RIGHT && direction == Direction.LEFT) return false;
        if (currentDirection == Direction.DOWN && direction == Direction.UP) return false;

        return !(currentDirection == Direction.LEFT && direction == Direction.RIGHT);
    }

    private Point2D translateOffset(int offSet, Direction direction) {
        int x = offSet, y = offSet;
        switch (direction) {
            case UP:
                y *= -1;
                x = 0;
                break;
            case RIGHT:
                y = 0;
                break;
            case DOWN:
                x = 0;
                break;
            case LEFT:
                x *= -1;
                y = 0;
                break;
        }
        return new Point2D(x, y);
    }


    boolean setMove(int offSet, Direction direction) {

        if (canMove(direction) && setMoveLock) {
            setMoveLock = false;
            this.currentDirection = direction;
            setImgAsHead();
            this.xOffSet = ((int) translateOffset(offSet, direction).getX());
            this.yOffSet = ((int) translateOffset(offSet, direction).getY());
            return true;
        }

        return false;
    }

    void setDirection(Direction direction) {
        this.currentDirection = direction;
        setImgAsHead();
        int offSet = Math.max(Math.abs(yOffSet), Math.abs(xOffSet));

        Point2D offset2 = translateOffset(offSet, direction);

        this.xOffSet = ((int) offset2.getX());
        this.yOffSet = ((int) offset2.getY());


    }

    boolean setMove(Direction direction) {
        int offSet = Math.max(Math.abs(yOffSet), Math.abs(xOffSet));
        return setMove(offSet, direction);
    }

    void move() {
        Point2D lastPos = new Point2D(head.getNode().getTranslateX(), head.getNode().getTranslateY());
        Point2D lastPos2;
        head.move(xOffSet, yOffSet);
        setMoveLock = true;
        for (int i = 0; i < tail.size(); i++) {
            lastPos2 = new Point2D(tail.get(i).getNode().getTranslateX(), tail.get(i).getNode().getTranslateY());
            tail.get(i).setCoordinates(lastPos);
            i++;
            if (i >= tail.size()) break;
            lastPos = new Point2D(tail.get(i).getNode().getTranslateX(), tail.get(i).getNode().getTranslateY());
            tail.get(i).setCoordinates(lastPos2);
        }
    }

    Direction getCurrentDirection() {
        return currentDirection;
    }

    double [] getTailXs() {
        double [] x = new double[tail.size()];

        for (int i = 0; i < tail.size(); i++)
            x[i] = tail.get(i).getNode().getTranslateX();

        return x;
    }

    double [] getTailYs() {
        double [] y = new double[tail.size()];

        for (int i = 0; i < tail.size(); i++)
            y[i] = tail.get(i).getNode().getTranslateY();

        return y;
    }

    void setTilePos(double x, double y, int tileIndex) {
        tail.get(tileIndex).getNode().setTranslateX(x);
        tail.get(tileIndex).getNode().setTranslateY(y);
    }

    Tile generateTile(boolean clearTail) {
        double rand = Math.random();

        if (rand < 0.33) return generateTile(tailColor.darker(), clearTail);
        else if (rand < 0.66) return generateTile(tailColor, clearTail);
        else return generateTile(tailColor.brighter(), clearTail);
    }

    Tile generateTile(Color color, boolean clearTail) {
        if (clearTail) tail = new ArrayList<>();

        Rectangle last;
        if (tail.size() > 0)
            last = (Rectangle) tail.get(tail.size() - 1).getNode();
        else
            last = ((Rectangle) head.getNode());


        Rectangle newRect = new Rectangle(tileSize, tileSize, color);
        Tile tile = new Tile(newRect, last.getTranslateX(), last.getTranslateY(), color);
        tail.add(tile);

        return tile;
    }

    public void setHeadBorder(Point2D point) {
        head.setBorder(point);
    }

    public Point2D getHeadBorder() {
        return head.getBorder();
    }

    boolean isColliding(Object other) {
        return tail.stream().anyMatch(tile -> tile.isColliding(other)) ||
                head.isColliding(other);
    }

    boolean isHeadCollidingWithTail() {
        if (tail.size() < 4) return false;
        return tail.stream().anyMatch(tile -> tile.isColliding(head));
    }

    int size() {
        return 1 + tail.size();
    }

    Color getTailColor() {
        return tailColor;
    }

    Point2D getHeadPosition() {
        Point2D headPosition = new Point2D(head.getNode().getTranslateX(), head.getNode().getTranslateY());

        return headPosition;
    }

    void setHeadPosition(double x, double y) {
        head.getNode().setTranslateX(x);
        head.getNode().setTranslateY(y);
    }

    int getTailSize() {
        return tail.size();
    }


}
