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

import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private String snakeImgU = "snake/img/head/Square/snakeU.png";
    private String snakeImgR = "snake/img/head/Square/snakeR.png";
    private String snakeImgD = "snake/img/head/Square/snakeD.png";
    private String snakeImgL = "snake/img/head/Square/snakeL.png";


    private int setFps = 10; // Frame per second (60fps / setFps -> 60fps/10 == 6 fps)
    private int frameCounter = 0;
    private int moveOffSet = elementsSize;
    private File scoreFile = new File("SnakeScore.txt");

    private HBox labelsBox = new HBox();
    private int bestScore = new Integer(readBestScoreFromFile(scoreFile));
    private int points = 0;
    private int foodSpeedBonus = setFps > 15 ? 0 : 15 - setFps;
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
        snake.setImgAsHead(snakeImgU);
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

                double rand = Math.random();
                if (rand < 0.33)
                    root.getChildren().add(snake.generateTile(snake.getTailColor().brighter()).getNode());
                else if (rand < 0.66)
                    root.getChildren().add(snake.generateTile().getNode());
                else
                    root.getChildren().add(snake.generateTile(snake.getTailColor().darker()).getNode());


                int newFoodCounter = 0;
                while (snake.isColliding(food)) {
                    newFoodCounter++;
                    food.newRandomFoodPosition(new Point2D(snake.getHeadBorder().getX(), snake.getHeadBorder().getY()));
                    if (newFoodCounter > 10000) {
                        System.err.println("Couldn't generate new food tile.");
                        Platform.exit();
                    }
                }

                foodTimeline.stop();
                food.resetPrice();
                foodTimeLbl.setText(Integer.toString(food.getPrice()));
                foodTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0)),
                        new KeyFrame(Duration.seconds(1), event -> {
                            food.setPrice(food.getPrice() - 1);
                            foodTimeLbl.setText(Integer.toString(food.getPrice()));
                        }
                        ));
                foodTimeline.setCycleCount(15);
                foodTimeline.play();
                setRandomFoodImg();
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.2), evt -> food.getNode().setVisible(false)),
                        new KeyFrame(Duration.seconds(0.4), evt -> food.getNode().setVisible(true))
                );
                timeline.setCycleCount(5);
                timeline.play();
        }
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

/////////////////////////////////////////////////////////////////////////////////////////////////////    CONTROL - on key pressed
    private void setOnKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();//todo naprawic zabezpieczenie skrecania

        if (KeyCode.RIGHT == code) {
            if (snake.changeMove(moveOffSet, RIGHT))
                snake.setImgAsHead(snakeImgR);
        }
        else if (KeyCode.DOWN == code) {
            if (snake.changeMove(moveOffSet, DOWN))
                snake.setImgAsHead(snakeImgD);
        }
        else if (KeyCode.LEFT == code) {
            if (snake.changeMove(moveOffSet, LEFT))
                snake.setImgAsHead(snakeImgL);
        }
        else if (KeyCode.UP == code) {
            if (snake.changeMove(moveOffSet, UP))
                snake.setImgAsHead(snakeImgU);
        }

        if (KeyCode.SPACE == code) snake.turboMove();


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

////////////////////////////////////////////////////////////////////////////////////////////////////    FILE - READ/WRITE
    private void saveToFile(String msg, File file) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
            pw.println(msg);
            pw.close ();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String readBestScoreFromFile(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            ArrayList<Integer> scores = new ArrayList<>();

            String last = "0", tmp;

            while ((tmp = br.readLine()) != null) {
                    last = tmp;
                    scores.add(Integer.parseInt(tmp));
            }
            in.close();
            return scores.isEmpty() ? "0" : Collections.max(scores).toString();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't read file... but it's not a problem.");
            return "0";
        }

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////   START
    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = createScene();
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
        System.out.println("Game Over");

        ///////////////////////////////////     ZAPIS STANU GRY
        Point2D headPosition = snake.getHeadPosition();
        Point2D foodPosition = new Point2D(food.getNode().getTranslateX(), food.getNode().getTranslateY());
        int tailLength = snake.getTailSize();

        //todo save to json


        if (points > bestScore) {
            saveToFile(Integer.toString(points), scoreFile);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////     MAIN
    public static void main(String[] args) {
        launch(args);
    }
}
