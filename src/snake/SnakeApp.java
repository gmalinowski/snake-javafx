package snake;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import snake.utilities.ChangeListenerMSG;
import snake.utilities.Direction;
import snake.utilities.GameState;

import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static snake.utilities.Direction.*;


/**
 * javaFXtut
 * Created by Grzegorz Malinowski on 2017-04-28, 03:29.
 * gmalinowski@protonmail.com
 */
public class SnakeApp extends Application {
    private int width = 960, height = 640;
    private int elementsSize = 32;
    private Pane root;
    private Scene scene;
    private Stage stage;
    private Point2D snakeBorder = new Point2D(width, height); // if null then snake can go over the screen
    private Snake snake = new Snake(elementsSize, 320, 320 , elementsSize, 0, Color.YELLOW, Color.GREEN, snakeBorder, Direction.UP); // IF LAST ARGUMENT (BORDER) == NULL THEN SNAKE CAN GO OVER THE WINDOW
    private Food food = new Food(elementsSize, Color.ORANGE, 0, 0, 15);

    private int setFps = 15; // Frame per second (60fps / setFps -> 60fps/10 == 6 fps)
    private int frameCounter = 0;
    private int moveOffSet = elementsSize;
    private File scoreFile = new File("SnakeScore.txt");
    private String gameStateSrc = "snake.json";

    private HBox labelsBox = new HBox();
    private int bestScore = 0;//new Integer(readBestScoreFromFile(scoreFile));
    private int points = 0;
    private int foodSpeedBonus = 61 - setFps;
    private Label pointsLbl = new Label("Score: " + Integer.toString(points));
    private Label bestScoreLbl = new Label("/" + Integer.toString(bestScore));
    private Label foodTimeLbl = new Label(Integer.toString(food.getPrice()));
    private Label speedBonusLbl = new Label("Price: " + Integer.toString(foodSpeedBonus) + " + ");
    private Timeline foodTimeline = new Timeline();

    private boolean scoreBlinking = true;
    private boolean fullScreen = false;


////////////////////////////////////////////////////////////////////////////////////////    CREATE PANE - SCENE
    private Scene createScene() {

        root = new Pane();
        root.setPrefSize(width, height);
        root.getChildren().addAll(labelsBox);


        ////////////////////////////////////////////////////////////////    LABELS
//        labelsBox.getStylesheets().add(getClass().getResource("/snake/style.css").toExternalForm());
        labelsBox.setId("labelsBox");
        pointsLbl.setId("pointsLbl");
        bestScoreLbl.setId("bestScoreLbl");
        foodTimeLbl.setId("foodTimeLbl");
        speedBonusLbl.setId("speedBonusLbl");

//        HBox.setMargin(foodTimeLbl, new Insets(0, 0, 0, 25));
        HBox.setMargin(speedBonusLbl, new Insets(0, 0, 0, 25));
        labelsBox.getChildren().addAll(pointsLbl, bestScoreLbl, speedBonusLbl, foodTimeLbl);




//        root.getChildren().addAll(pointsLbl, bestScoreLbl);
        root.getStylesheets().add(getClass().getResource("/snake/style.css").toExternalForm());
        setRandomFoodImg();
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
                    pointsLblBlinking();
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
            pointsLbl.setText("Score: " + Integer.toString(points));

                addTailTile(false);

                int newFoodCounter = 0;
                while (snake.isColliding(food)) {
                    newFoodCounter++;
                    food.newRandomFoodPosition(new Point2D(snake.getHeadBorder().getX(), snake.getHeadBorder().getY()));
                    if (newFoodCounter > 10000) {
                        System.err.println("Couldn't generate new food tile.");
                        Platform.exit();
                    }
                }

                pointsCountDown(true);
                setRandomFoodImg();
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.2), evt -> food.getNode().setVisible(false)),
                        new KeyFrame(Duration.seconds(0.4), evt -> food.getNode().setVisible(true))
                );
                timeline.setCycleCount(5);
                timeline.play();
        }
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
            Platform.exit();
        }
    }

    private void pointsLblBlinking() {
            if (points > bestScore && scoreBlinking) {
                scoreBlinking = false;

                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.seconds(0.4), evt -> pointsLbl.setVisible(false)),
                            new KeyFrame(Duration.seconds(0.6), evt -> pointsLbl.setVisible(true))
                    );

                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();
            }
        }
//////////////////////////////////////
//////////////////////////////////////
    private void setRandomFoodImg() {
        Random gen = new Random();
        String foodUrl = "snake/img/food/" + Integer.toString( gen.nextInt(3) + 1) + ".png";
        food.setImageAsFood(foodUrl);
    }

    private void changeSpeed(int i) {
        if (i > 0 && i < 61) {
            setFps = i;
            foodSpeedBonus = 61 -i;
            speedBonusLbl.setText("Price: " + foodSpeedBonus + " + ");
            frameCounter = 0;
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////    CONTROL - on key pressed
    private void setOnKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        if (KeyCode.RIGHT == code) snake.setMove(RIGHT);
        else if (KeyCode.DOWN == code) snake.setMove(DOWN);
        else if (KeyCode.LEFT == code) snake.setMove(LEFT);
        else if (KeyCode.UP == code) snake.setMove(UP);



        if (KeyCode.SHIFT == code) {
            changeSpeed(setFps + 1);
        }
        if (KeyCode.SPACE == code) {
            changeSpeed(setFps - 1);
        }


        if (KeyCode.ESCAPE == code) {
            Platform.exit();
        }
        if (KeyCode.F11 == code) {
            if (fullScreen) {
                fullScreen = false;
                stage.setFullScreen(false);
            } else {
                fullScreen = true;
                stage.setFullScreen(true);
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////     LOAD GAME STATE
    private void loadLastGame() {
        try {
            GameState gs = GameState.read(gameStateSrc);
            bestScore = gs.getBestScore();
            bestScoreLbl.setText("/" + gs.getBestScore());
            if (!gs.isCollision()) {

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


                //for (int i = 0; i < gs.getTailLength(); i++) addTailTile();

                food.getNode().setTranslateX(gs.getFx());
                food.getNode().setTranslateY(gs.getFy());

                food.setPrice(gs.getFoodTime());
                foodTimeLbl.setText(gs.getFoodTime() + "");
                points = gs.getPoints();
                pointsLbl.setText("Score: " + points);

                if (food.getPrice() > 0)
                    pointsCountDown(false);

            }
            } catch(IOException e){
                e.printStackTrace();
            }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////   START
    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = createScene();
        loadLastGame();
        scene.setOnKeyPressed(this::setOnKeyPressed);
        /////////////////////////////////////////////////////////////////   ON WINDOW CHANGE
        if (snakeBorder != null) {
            ChangeListenerMSG windowListener = new ChangeListenerMSG(snake, scene, true);
            primaryStage.widthProperty().addListener(windowListener);
            primaryStage.heightProperty().addListener(windowListener);
        }

        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setFullScreen(fullScreen);
        primaryStage.setTitle("Snake");
        stage = primaryStage;
        primaryStage.show();
//        primaryStage.setResizable(false);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.err.println("Game Over");

/////////////////////////////////////////////////////////////////////////////////////////////     ZAPIS STANU GRY
        if (points > bestScore) bestScore = points;
        GameState.save(
                "snake.json",snake.getHeadPosition().getX(), snake.getHeadPosition().getY() ,
                food.getNode().getTranslateX(), food.getNode().getTranslateY(),
                snake.getTailSize(), points, bestScore, food.getPrice(),
                snake.getCurrentDirection(),
                snake.getTailXs(), snake.getTailYs(),
                snake.isHeadCollidingWithTail(),
                setFps
                );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////     MAIN
    public static void main(String[] args) {
        launch(args);
    }
}
