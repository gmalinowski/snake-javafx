package snake;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import snake.utilities.ChangeListenerMSG;
import snake.utilities.Direction;
import snake.utilities.GameState;

import java.io.*;
import java.lang.*;
import java.util.Random;

import static snake.utilities.Direction.*;


/**
 * snakeFx
 * Created by Grzegorz Malinowski on 2017-04-28, 03:29.
 * gmalinowski@protonmail.com
 */
public class SnakeApp extends Application {
    private int width, height, elementsSize, bestScore, points, foodSpeedBonus, setFps, frameCounter;
    private Pane root;
    private Stage stage;
    private Point2D snakeBorder;
    private Snake snake;
    private Food food;
    private String gameStateSrc;
    private HBox labelsBox;
    private Label pointsLbl, bestScoreLbl, foodTimeLbl, speedBonusLbl, helpLbl;
    private Timeline foodTimeline, newFoodTimelineBlink, pointsTimelineBlink;
    private boolean scoreBlinking, fullScreen, soundON;

    public SnakeApp() {
        this.width = 1280;
        this.height = 720;
        this.elementsSize = 40;

        this.snakeBorder = new Point2D(width, height);

        this.snake = new Snake(elementsSize, 320, 320 , elementsSize,
                0, Color.YELLOW, Color.GREEN, snakeBorder, Direction.UP); // IF LAST ARGUMENT (BORDER) == NULL THEN SNAKE CAN GO OVER THE WINDOW;

        this.food = new Food(elementsSize, Color.ORANGE, 0, 0, 15);

        this.setFps = 15;// Frame per second (60fps / setFps -> 60fps/10 == 6 fps)
        this.frameCounter = 0;
        this.gameStateSrc = "snake.json";
        this.labelsBox = new HBox();
        this.bestScore = 0;
        this.points = 0;
        this.foodSpeedBonus = 61 - setFps;
        this.pointsLbl = new Label("" + Integer.toString(points));
        this.bestScoreLbl =  new Label("/" + Integer.toString(bestScore));
        this.foodTimeLbl = new Label(Integer.toString(food.getPrice()));
        this.speedBonusLbl = new Label("Price: " + Integer.toString(foodSpeedBonus) + " + ");
        this.helpLbl = new Label();
        this.foodTimeline = new Timeline();
        this.newFoodTimelineBlink = new Timeline();
        this.pointsTimelineBlink = new Timeline();
        this.scoreBlinking = true;
        this.fullScreen = false;
        this.soundON = true;

        this.labelsBox.setId("labelsBox");
        this.pointsLbl.setId("pointsLbl");
        this.bestScoreLbl.setId("bestScoreLbl");
        this.foodTimeLbl.setId("foodTimeLbl");
        this.speedBonusLbl.setId("speedBonusLbl");
        this.helpLbl.setId("helpLbl");

        this.helpLbl.setText(
                        "H_____HELP\n" +
                        "Esc___Exit\n" +
                        "R_____Reset\n" +
                        "S_____Sound on/off\n" +
                        "F11___Full screen\n" +
                        "Space_+ speed/price\n" +
                        "Shift__- speed/price\n\n" +
                        "Change tail color:\n" +
                        "1      Green\n" +
                        "2      YellowGreen (3000 - pts to unlock)\n" +
                        "3      Orange (6000)\n" +
                        "4      Blue (12000)\n"+
                        "5      Black (18000)\n" +
                        "6      Random (30000)"
        );
    }

    private void newSnake() {
        root = snake.removeFromScene(root);
        this.snake = new Snake(elementsSize, 320, 320 , elementsSize,
                0, Color.YELLOW, Color.GREEN, snakeBorder, Direction.UP); // IF LAST ARGUMENT (BORDER) == NULL THEN SNAKE CAN GO OVER THE WINDOW;
        root = snake.addToScene(root);
    }

    private void newFood() {
        root.getChildren().remove(food.getNode());
        this.food = new Food(elementsSize, Color.ORANGE, 0, 0, 15);
        food.newRandomFoodPosition(snakeBorder);
        food.newRandomFoodImg();
        root = food.addToScene(root);
    }

    ////////////////////////////////////////////////////////////////////////////////////////    CREATE PANE - SCENE
    private Scene createScene() {

        root = new Pane();
        root.setPrefSize(width, height);
        root.getChildren().addAll(labelsBox);

        HBox.setMargin(speedBonusLbl, new Insets(0, 0, 0, 25));
        HBox.setMargin(helpLbl, new Insets(0, 5, 0, 50));
        labelsBox.getChildren().addAll(pointsLbl, bestScoreLbl, speedBonusLbl, foodTimeLbl, helpLbl);

        root.getStylesheets().add(getClass().getResource("/snake/style.css").toExternalForm());
        food.newRandomFoodImg();
        food.newRandomFoodPosition(snakeBorder);
        root = food.addToScene(root);
        root = snake.addToScene(root);

    /////////////////////////////////////////////////////////////////// ANIMATION
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frameCounter++;
                if (frameCounter == setFps){
                    frameCounter = 0;
                    snake.move();
                    foodCollision();
                    tailCollision();
                }
            }
        };
        animationTimer.start();

        return new Scene(root);
    }
///////////////////////////////////////////////////
    //////////////////  ANIMATION FUNCTIONS
    private void foodCollision() {
        if (snake.isColliding(food)) {
            points += foodSpeedBonus + food.getPrice();
            pointsLbl.setText("" + Integer.toString(points));
            playSound(soundON);
                addTailTile(false);

                generateFood();

                pointsCountDown(true);

                newFoodTimelineBlink = new Timeline(
                        new KeyFrame(Duration.seconds(0.25), evt -> food.getNode().setVisible(false)),
                        new KeyFrame(Duration.seconds(0.5), evt -> food.getNode().setVisible(true))
                );
                newFoodTimelineBlink.setCycleCount(1);
                newFoodTimelineBlink.play();
        }
    }

    private void generateFood() {
        int newFoodCounter = 0;
        while (snake.isColliding(food)) {
            newFoodCounter++;
            food.newRandomFoodPosition(new Point2D(snake.getHeadBorder().getX(), snake.getHeadBorder().getY()));
            if (newFoodCounter > 100000) {
                System.err.println("Couldn't generate new food tile.");
                Platform.exit();
            }
        }
        food.newRandomFoodImg();
    }

    private void pointsCountDown(boolean reset) {
        foodTimeline.stop();
        if (reset)
        food.resetPrice();

        foodTimeLbl.setText(Integer.toString(food.getPrice()));
        foodTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0)),
                new KeyFrame(Duration.seconds(1), event -> {
                    food.setPrice(food.getPrice() - 1);
                    foodTimeLbl.setText(Integer.toString(food.getPrice()));
                }
                ));
        foodTimeline.setCycleCount(food.getPrice());
        foodTimeline.play();
    }

    private void addTailTile(boolean clearTail) {
        double rand = Math.random();
        if (rand < 0.33)
            root.getChildren().add(snake.generateTile(snake.getTailColor().brighter(), clearTail).getNode());
        else if (rand < 0.66)
            root.getChildren().add(snake.generateTile(clearTail).getNode());
        else
            root.getChildren().add(snake.generateTile(snake.getTailColor().darker(), clearTail).getNode());
    }

    private void tailCollision() {
        if (snake.isHeadCollidingWithTail()) {
            if (points > bestScore)
                saveGameState();

            cleanUP();
        }
    }

    private void pointsLblBlinking() {
            if (points > bestScore && scoreBlinking) {
                scoreBlinking = false;

                    pointsTimelineBlink = new Timeline(
                            new KeyFrame(Duration.seconds(0.4), evt -> pointsLbl.setVisible(false)),
                            new KeyFrame(Duration.seconds(0.6), evt -> pointsLbl.setVisible(true))
                    );

                    pointsTimelineBlink.setCycleCount(Animation.INDEFINITE);
                    pointsTimelineBlink.play();
            }

    }

    private void playSound(boolean on) {
        if (on)
            food.playSound(null);


        if ((points >= 3000 && bestScore < 3000) ||
                (points >= 6000 && bestScore < 6000) ||
                (points >= 12000 && bestScore < 12000) ||
                (points >= 18000 && bestScore < 18000) ||
                (points >= 30000 && bestScore < 30000)
                ) {
            food.playSound("/snake/sound/triumphal.wav");
        }
        pointsLblBlinking();
        pointsToBest();
    }

    private void pointsToBest() {
        if (points > bestScore) bestScore = points;
    }

    private void changeSpeed(int i) {
        if (i > 0 && i < 61) {
            setFps = i;
            foodSpeedBonus = 61 -i;
            speedBonusLbl.setText("Price: " + foodSpeedBonus + " + ");
            frameCounter = 0;
        }
    }

    private void cleanUP() {
        try {
            loadLastGame(true);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        if (points > bestScore) {
            bestScore = points;
            bestScoreLbl.setText("/" + bestScore);
        }

        points = 0;
        pointsLbl.setText("" + points);

        foodTimeLbl.setText(15 + "");
        foodTimeline.stop();

//        generateFood();
        if (newFoodTimelineBlink != null)
            newFoodTimelineBlink.stop();
            food.getNode().setVisible(true);
        if ((pointsTimelineBlink != null)) {
            pointsTimelineBlink.stop();
            pointsLbl.setVisible(true);
        }
        scoreBlinking = true;

        changeSpeed(15);

        newSnake();
        newFood();
    }

    private void switchHelp(int hide) {
        switch (hide) {
            case 0: labelsBox.getChildren().remove(helpLbl);
            break;
            case 1: labelsBox.getChildren().add(helpLbl);
            break;
            default:
                if (labelsBox.getChildren().contains(helpLbl))
                    labelsBox.getChildren().remove(helpLbl);
                else
                    labelsBox.getChildren().add(helpLbl);
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////    CONTROL - on key pressed
    private void setOnKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        if (KeyCode.RIGHT == code) snake.setMove(RIGHT);
        else if (KeyCode.DOWN == code) snake.setMove(DOWN);
        else if (KeyCode.LEFT == code) snake.setMove(LEFT);
        else if (KeyCode.UP == code) snake.setMove(UP);


        switch (code) {
            case H:
                switchHelp(2);
                break;
            case R: cleanUP();
            break;
            case ESCAPE: Platform.exit();
            break;
            case SHIFT: changeSpeed(setFps + 1);
            break;
            case SPACE: changeSpeed(setFps - 1);
            break;
            case F11:
                if (fullScreen) {
                    fullScreen = false;
                    stage.setFullScreen(false);
                } else {
                    fullScreen = true;
                    stage.setFullScreen(true);
                }
                break;
            case S:
                soundON = !soundON;
                break;
            case DIGIT1:
                snake.changeTailColor(Color.GREEN);
                break;
            case DIGIT2:
                if (bestScore >= 3000)
                snake.changeTailColor(Color.GREENYELLOW.darker());
                break;
            case DIGIT3:
                if (bestScore >= 6000)
                snake.changeTailColor(Color.DARKORANGE.darker());
                break;
            case DIGIT4:
                if (bestScore >= 12000)
                snake.changeTailColor(Color.CADETBLUE);
                break;
            case DIGIT5:
                if (bestScore >= 18000)
                snake.changeTailColor(Color.BLACK.brighter().brighter());
                break;
            case DIGIT6:
                if (bestScore >= 30000) {
                    Random gen = new Random();
                    int r = gen.nextInt(256);
                    System.out.println(r);
                    int g = gen.nextInt(256);
                    int b = gen.nextInt(256);

                    snake.changeTailColor(Color.rgb(r, g, b));
                }
                break;
        }

    }

///////////////////////////////////////////////////////////////////////////////////////////////////     LOAD GAME STATE
    private void loadLastGame(boolean onlyBest) {
        try {
            GameState gs = GameState.read(gameStateSrc);
            bestScore = gs.getBestScore();
            bestScoreLbl.setText("/" + gs.getBestScore());
            if (onlyBest) return;

            if (!gs.isCollision()) {
                soundON = gs.isSound();
                switchHelp(0);
                fullScreen = gs.isFullScreen();
                stage.setFullScreen(gs.isFullScreen());

                snake.changeTailColor(Color.color(gs.getRgb()[0], gs.getRgb()[1], gs.getRgb()[2]));

                changeSpeed(gs.getFps());

                snake.setHeadPosition(gs.getHx(), gs.getHy());
                snake.setDirection(gs.getDirection());

                if (gs.getTx().length > 0) {
                    addTailTile(true);
                    snake.setTilePos(gs.getTx()[0], gs.getTy()[0], 0);

                    for (int i = 1; i < gs.getTx().length; i++) {
                        addTailTile(false);
                        snake.setTilePos(gs.getTx()[i], gs.getTy()[i], i);
                    }
                }

                food.getNode().setTranslateX(gs.getFx());
                food.getNode().setTranslateY(gs.getFy());

                food.setPrice(gs.getFoodTime());
                foodTimeLbl.setText(gs.getFoodTime() + "");
                points = gs.getPoints();
                pointsLbl.setText("" + points);

                if (food.getPrice() > 0)
                    pointsCountDown(false);

            }
            } catch(IOException e){
                e.printStackTrace();
            }
    }

    private void saveGameState() {
        /////////////////////////////////////////////////////////////////////////////     ZAPIS STANU GRY - ON EXIT
//        if (points > bestScore) bestScore = points;

        double[] rgb = new double[3];
        rgb[0] = snake.getTailColor().getRed();
        rgb[1] = snake.getTailColor().getGreen();
        rgb[2] = snake.getTailColor().getBlue();
        Point2D border = snake.getHeadBorder();

        GameState.save(
                "snake.json",snake.getHeadPosition().getX(), snake.getHeadPosition().getY(),
                food.getNode().getTranslateX(), food.getNode().getTranslateY(),
                rgb,
                snake.getTailSize(), points, bestScore, food.getPrice(),
                snake.getCurrentDirection(),
                snake.getTailXs(), snake.getTailYs(),
                snake.isHeadCollidingWithTail(),
                setFps,
                border.getX(),
                border.getY(),
                fullScreen,
                soundON
        );
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////   START
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = createScene();

        scene.setOnKeyPressed(this::setOnKeyPressed);
        /////////////////////////////////////////////////////////////////   ON WINDOW CHANGE
        if (snakeBorder != null) {
            ChangeListenerMSG windowListener = new ChangeListenerMSG(snake, scene, true, width, height);
            primaryStage.widthProperty().addListener(windowListener);
            primaryStage.heightProperty().addListener(windowListener);
        }

//        primaryStage.setMinWidth(width);
//        primaryStage.setMinHeight(height);


        primaryStage.setScene(scene);
//        primaryStage.setAlwaysOnTop(true);
        primaryStage.setFullScreen(fullScreen);
        primaryStage.setTitle("Snake");
        stage = primaryStage;
        loadLastGame(false);
        primaryStage.fullScreenExitHintProperty().setValue("");
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.err.println("Game Over");

        saveGameState();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
