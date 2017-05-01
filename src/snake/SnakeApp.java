package snake;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.omg.PortableServer.POA;
import snake.utilities.ChangeListenerMSG;
import snake.utilities.Direction;

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
    private int width = 600, height = 600;
    private Pane root;
    private Scene scene;
    private Point2D snakeBorder = new Point2D(600, 600); // if null then snake can go over the screen
    private Snake snake = new Snake(25, 300, 300 , 25, 3, Color.YELLOW, Color.GREEN, snakeBorder); // IF LAST ARGUMENT (BORDER) == NULL THEN SNAKE CAN GO OVER THE WINDOW
    private Food food = new Food(25, Color.ORANGE, 0, 0);
    private int counter = 0, counterReset = 8;
    private Direction direction = UP;
    private int moveOffSet = 25;
    private File scoreFile = new File("SnakeScore.txt");

    private HBox labelsBox = new HBox();
    private int bestScore = new Integer(readBestScoreFromFile(scoreFile));
    private int points = 0;
    private int foodPrice = 14 - counterReset;
    private Label pointsLbl = new Label("Score: " + Integer.toString(points));
    private Label bestScoreLbl = new Label("Best: " + Integer.toString(bestScore));

    private boolean scoreBlinking = true;


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

        HBox.setMargin(bestScoreLbl, new Insets(0, 0, 0, 25));
        labelsBox.getChildren().addAll(pointsLbl, bestScoreLbl);
        labelsBox.setPadding(new Insets(5, 20, 0, 5));


//        root.getChildren().addAll(pointsLbl, bestScoreLbl);
        root.getStylesheets().add(getClass().getResource("/snake/style.css").toExternalForm());
        food.newRandomFoodPosition(new Point2D(width, height));
        root = food.addToScene(root);
        root = snake.addToScene(root);

    /////////////////////////////////////////////////////////////////// CALL UPDATE
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();

    ///////////////////////////////////////////////////////////////////


        return new Scene(root);
    }

//////////////////////////////////////////////////////////////////////////////////////////////  ON UPDATE
    private void update() {
        counter++;//todo zamienic na mierzenie czasu, aby snake poruszal sie caly czas ze zblizona predkoscia
        if (counter == counterReset) {
            counter = 0;
            move(direction, moveOffSet);

            if (snake.isColliding(food)) {
                points += foodPrice;
                pointsLbl.setText("Score: " + Integer.toString(points));

                double rand = Math.random();
                if (rand < 0.33)
                    root.getChildren().add(snake.generateTile(snake.getTailColor().brighter()).getNode());
                else if (rand < 0.66)
                    root.getChildren().add(snake.generateTile().getNode());
                else
                    root.getChildren().add(snake.generateTile(snake.getTailColor().darker()).getNode());
            }

            // LABEL ANIMATION
            if (points > bestScore && scoreBlinking) {
                scoreBlinking = false;
//                if (pointsLbl.getTextFill() != Color.WHITE)
//                    pointsLbl.setTextFill(Color.WHITE);
//                else
//                    pointsLbl.getStylesheets().add(getClass().getResource("/snake/style.css").toExternalForm());


                Runnable r = () -> {
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.seconds(0.4), evt -> pointsLbl.setVisible(false)),
                            new KeyFrame(Duration.seconds(0.6), evt -> pointsLbl.setVisible(true))
//                            new KeyFrame(Duration.seconds(0.8), evt -> pointsLbl.setVisible(false)),
//                            new KeyFrame(Duration.seconds(0.85), evt -> pointsLbl.setVisible(true))
                    );

                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();
                };

                Thread t = new Thread(r);
                t.run();


            }

            int newFoodCounter = 0;
            while (snake.isColliding(food)) {
                newFoodCounter++;
                food.newRandomFoodPosition(new Point2D(scene.getWidth(), scene.getHeight()));
                if (newFoodCounter > 10000) {
                    System.err.println("Couldn't generate new food tile.");
                    Platform.exit();
                }
            }

            if (snake.isHeadCollidingWithTail()) {
                Platform.exit();
            }

        }

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////    MOVE
    private void move(Direction direction, int offSet) {
        switch (direction) {
            case UP:
                snake.move(0, -offSet);
                break;
            case RIGHT:
                snake.move(offSet, 0);
                break;
            case DOWN:
                snake.move(0, offSet);
                break;
            case LEFT:
                snake.move(-offSet, 0);
                break;
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////    CONTROL
    private void setOnKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();//todo naprawic zabezpieczenie skrecania
        if (KeyCode.RIGHT == event.getCode() && direction != LEFT) direction = RIGHT;
        else if (KeyCode.DOWN == event.getCode() && direction != UP) direction = DOWN;
        else if (KeyCode.LEFT == event.getCode() && direction != RIGHT) direction = LEFT;
        else if (KeyCode.UP == event.getCode() && direction != DOWN) direction = UP;

        if (KeyCode.ESCAPE == event.getCode()) {
            Platform.exit();
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
        }
        return "";
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////   START
    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = createScene();
        scene.setOnKeyPressed(this::setOnKeyPressed);
        /////////////////////////////////////////////////////////////////   ON WINDOW CHANGE
        if (snakeBorder != null) {//TODO okno zmienia rozmiar o wielokrotnosc jedzenia/snake
            ChangeListenerMSG windowListener = new ChangeListenerMSG(snake, scene, true);
            scene.widthProperty().addListener(windowListener);
            scene.heightProperty().addListener(windowListener);
        }

        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
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
