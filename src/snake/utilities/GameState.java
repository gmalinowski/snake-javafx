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
    private int tailLength, fps;
    private byte[] bestScore, points, foodTime;
    private byte [][] rgb;
    private Direction direction;
    private double [] tx, ty;
    private double ww, wh;
    private boolean collision, fullScreen, sound;

    private GameState(double hx, double hy, double fx, double fy, byte[][] rgb,
                      int tailLength, byte[] points, byte[] bestScore, byte[] foodTime, Direction direction,
                      double [] tx, double [] ty, boolean collision, int fps, double ww, double wh, boolean fullScreen, boolean sound) {
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
        this.ww = ww;
        this.wh = wh;
        this.fullScreen = fullScreen;
        this.sound = sound;
    }
    public GameState() {

    }

    public static void save(String fileSrc, double hx, double hy, double fx, double fy, byte[][] rgb,
                            int tailLength, byte[] points, byte[] bestScore, byte[] foodTime, Direction direction,
                            double [] tx, double [] ty, boolean collision, int fps, double ww, double wh, boolean fullScreen, boolean sound) {
        GameState gameState = new GameState(hx, hy, fx, fy, rgb, tailLength, points, bestScore,
                foodTime, direction, tx, ty, collision, fps, ww, wh, fullScreen, sound);

        ObjectMapper mapper = new ObjectMapper();
////        JEZELI NIE MA GETEROW I SETEROW TO TRZEBA USTAWIC ↓↓↓
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            mapper.writeValue(new File(fileSrc), gameState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public double getWw() {
        return ww;
    }

    public void setWw(double ww) {
        this.ww = ww;
    }

    public double getWh() {
        return wh;
    }

    public void setWh(double wh) {
        this.wh = wh;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public byte[][] getRgb() {
        return rgb;
    }

    public void setRgb(byte[][] rgb) {
        this.rgb = rgb;
    }

    public byte[] getBestScore() {
        return bestScore;
    }

    public void setBestScore(byte[] bestScore) {
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

    public byte[] getPoints() {
        return points;
    }

    public void setPoints(byte[] points) {
        this.points = points;
    }

    public byte[] getFoodTime() {
        return foodTime;
    }

    public void setFoodTime(byte[] foodTime) {
        this.foodTime = foodTime;
    }
}
