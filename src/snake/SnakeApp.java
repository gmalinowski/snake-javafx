package snake;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import snake.utilities.ChangeListenerMSG;
import snake.utilities.Direction;

import java.lang.*;

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
    private Snake snake = new Snake(25, 300, 300 , 25, 3, Color.GREENYELLOW.brighter(), Color.GREEN, snakeBorder); // IF LAST ARGUMENT (BORDER) == NULL THEN SNAKE CAN GO OVER THE WINDOW
    private Food food = new Food(25, Color.TOMATO, 0, 0);
    private int counter = 0, counterReset = 7;
    private Direction direction = UP;
    private int moveOffSet = 25;


////////////////////////////////////////////////////////////////////////////////////////    CREATE PANE - SCENE
    private Scene createScene() {
        root = new Pane();
        root.setPrefSize(width, height);
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
                if (snake.size() % 2 == 0)
                    root.getChildren().add(snake.generateTile(snake.getTailColor().brighter()).getNode());
                else
                    root.getChildren().add(snake.generateTile().getNode());
            }

            int newFoodCounter = 0;
            while (snake.isColliding(food)) {
                newFoodCounter++;
                food.newRandomFoodPosition(new Point2D(scene.getWidth(), scene.getHeight()));
                if (newFoodCounter > 10000) {
                    System.exit(0);
                    System.err.println("Couldn't generate new food tile.");
                }
            }

            if (snake.isHeadCollidingWithTail()) {
                System.err.println("Game Over");
                System.exit(0);
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

        if (KeyCode.ESCAPE == event.getCode()) System.exit(0);
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
///////////////////////////////////////////////////////////////////////////////////////////////////////     MAIN
    public static void main(String[] args) {
        launch(args);
    }
}
