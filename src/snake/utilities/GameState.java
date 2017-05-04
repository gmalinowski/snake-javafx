package snake.utilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * snake-javafx
 * Created by Grzegorz Malinowski on 2017-05-03, 18:17.
 * gmalinowski@protonmail.com
 */
public class GameState {
    private double hx, hy, fx, fy;
    private int tailLength, points, foodTime, fps, bestScore;
    private double [] rgb;
    private Direction direction;
    private double [] tx, ty;
    private boolean collision;

    private GameState(double hx, double hy, double fx, double fy, double[] rgb, int tailLength, int points, int bestScore, int foodTime, Direction direction, double [] tx, double [] ty, boolean collision, int fps) {
        this.hx = hx;
        this.hy = hy;
        this.fx = fx;
        this.fy = fy;
        this.rgb = rgb;
        this.foodTime = foodTime;
        this.tailLength = tailLength;
        this.points = points;
        this.bestScore = bestScore;
        this.tx = tx;
        this.ty = ty;
        this.direction = direction;
        this.collision = collision;
        this.fps = fps;
    }
    public GameState() {

    }

    public static void save(String fileSrc,double hx, double hy, double fx, double fy, double[] rgb, int tailLength, int points, int bestScore, int foodTime, Direction direction, double [] tx, double [] ty, boolean collision, int fps) {
        GameState gameState = new GameState(hx, hy, fx, fy, rgb, tailLength, points, bestScore, foodTime, direction, tx, ty, collision, fps);

        ObjectMapper mapper = new ObjectMapper();
////        JEZELI NIE MA GETEROW I SETEROW TO TRZEBA USTAWIC ↓↓↓
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            mapper.writeValue(new File(fileSrc), gameState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] getRgb() {
        return rgb;
    }

    public void setRgb(double[] rgb) {
        this.rgb = rgb;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public static GameState read(String fileSrc) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        return mapper.readValue(new File(fileSrc), GameState.class);
//            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gameStat);

    }

    public double[] getTx() {
        return tx;
    }

    public void setTx(double[] tx) {
        this.tx = tx;
    }

    public double[] getTy() {
        return ty;
    }

    public void setTy(double[] ty) {
        this.ty = ty;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getHx() {
        return hx;
    }

    public void setHx(double hx) {
        this.hx = hx;
    }

    public double getHy() {
        return hy;
    }

    public void setHy(double hy) {
        this.hy = hy;
    }

    public double getFx() {
        return fx;
    }

    public void setFx(double fx) {
        this.fx = fx;
    }

    public double getFy() {
        return fy;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    public int getTailLength() {
        return tailLength;
    }

    public void setTailLength(int tailLength) {
        this.tailLength = tailLength;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getFoodTime() {
        return foodTime;
    }

    public void setFoodTime(int foodTime) {
        this.foodTime = foodTime;
    }
}
