package chon.group;

import java.util.ArrayList;
import java.util.List;

import chon.group.game.domain.agent.Agent;
import chon.group.game.domain.agent.Cannon;
import chon.group.game.domain.agent.Fireball;
import chon.group.game.domain.agent.Weapon;
import chon.group.game.domain.environment.Environment;
import chon.group.game.drawer.EnvironmentDrawer;
import chon.group.game.drawer.JavaFxMediator;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The {@code Engine} class represents the main entry point of the application
 * and serves as the game engine for "Chon: The Learning Game."
 * <p>
 * This class extends {@link javafx.application.Application} and manages the
 * game initialization, rendering, and main game loop using
 * {@link javafx.animation.AnimationTimer}.
 * </p>
 * 
 * <h2>Responsibilities</h2>
 * <ul>
 * <li>Set up the game environment, agents, and graphical components.</li>
 * <li>Handle keyboard input for controlling the protagonist agent.</li>
 * <li>Execute the game loop for updating and rendering the game state.</li>
 * </ul>
 */
public class Engine extends Application {

    /* If the game is paused or not. */
    private boolean isPaused = false;
    final static int WIDTH = 320;
    final static int HEIGHT = 280;
    final static int ASPECT_RATIO = WIDTH / HEIGHT;
    private boolean isSlowMoving = false;
    private boolean isSlowMovingUp = false;
    private boolean isWaitingForFish = false;

    /**
     * Main entry point of the application.
     *
     * @param args command-line arguments passed to the application.
     */

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application and initializes the game environment, agents,
     * and graphical components.
     * <p>
     * This method sets up the game scene, handles input events, and starts the
     * game loop using {@link AnimationTimer}.
     * </p>
     *
     * @param theStage the primary stage for the application.
     */
    @Override
    public void start(Stage theStage) {
        try {
            /* Initialize the game environment and agents */
            Environment environment = new Environment(0, 0, WIDTH, HEIGHT, "/images/environment/Sky.png");
            //Agent chonBota = new Agent(0, 0, 30, 22, 3, 1000, "/images/agents/chonBota.png", false);
            Weapon cannon = new Cannon(320, 390, 0, 0, 3, 0, "", false);
            Weapon fireball = new Fireball(400, 390, 0, 0, 3, 0, "", false);
            //chonBota.setWeapon(fireball);

            //Agent chonBot = new Agent(290, 138, 30, 22, 1, 500, "/images/agents/chonBot.png", true);
            //environment.setProtagonist(chonBota);
            //environment.getAgents().add(chonBot);
            Agent fishingRod = new Agent(144, -138, 250, 32, 8, 500, "/images/agents/fishingRod.png", false);
            environment.setProtagonist(fishingRod);
            environment.setPauseImage("/images/environment/pause.png");
            environment.setSeaImage("/images/environment/Sea.png");
            environment.setGameOverImage("/images/environment/gameover.png");

            /* Set up the graphical canvas */
            Canvas canvas = new Canvas(environment.getWidth(), environment.getHeight());
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setImageSmoothing(false);
            EnvironmentDrawer mediator = new JavaFxMediator(environment, gc);

            /* Set up the scene and stage */
            StackPane root = new StackPane();
            Scene scene = new Scene(root, environment.getWidth(), environment.getHeight());
            theStage.setTitle("Chon: The Learning Game");
            theStage.setScene(scene);

            root.getChildren().add(canvas);
            theStage.show();
            
            adjustCanvasSize(canvas, scene.getWidth(), scene.getHeight());
            double decorationWidth = theStage.getWidth() - scene.getWidth();
            double decorationHeight = theStage.getHeight() - scene.getHeight();

            /* Adjust the stage size to maintain the aspect ratio */
            theStage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                double contentWidth = newWidth.doubleValue() - decorationWidth;
                double newContentHeight = contentWidth / ASPECT_RATIO;
                double newStageHeight = newContentHeight + decorationHeight;

                if (Math.abs(theStage.getHeight() - newStageHeight) > 1) {
                    theStage.setHeight(newStageHeight);
                }
            });
            theStage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                double contentHeight = newHeight.doubleValue() - decorationHeight;
                double newContentWidth = contentHeight * ASPECT_RATIO;
                double newStageWidth = newContentWidth + decorationWidth;

                if (Math.abs(theStage.getWidth() - newStageWidth) > 1) {
                    theStage.setWidth(newStageWidth);
                }
            });
            scene.widthProperty().addListener((obs, oldVal, newVal) -> {
                adjustCanvasSize(canvas, scene.getWidth(), scene.getHeight());
            });
            scene.heightProperty().addListener((obs, oldVal, newVal) -> {
                adjustCanvasSize(canvas, scene.getWidth(), scene.getHeight());
            });

            /* Handle keyboard input */
            ArrayList<String> input = new ArrayList<String>();
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                public void handle(KeyEvent e) {
                    String code = e.getCode().toString();
                    input.clear();

                    System.out.println("Pressed: " + code);

                    if (code.equals("P")) {
                        isPaused = !isPaused;
                    }

                    if (!isPaused && !input.contains(code)) {
                        input.add(code);
                    }

                }
            });

            scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                public void handle(KeyEvent e) {
                    String code = e.getCode().toString();
                    System.out.println("Released: " + code);
                    input.remove(code);
                }
            });

            /* Start the game loop */
            new AnimationTimer() {
                /**
                 * The game loop, called on each frame.
                 *
                 * @param now the timestamp of the current frame in nanoseconds.
                 */
                @Override
                public void handle(long arg0) {
                    /* Helps scaling the screen. */
                    double canvasWidth = canvas.getWidth();
                    double canvasHeight = canvas.getHeight();
                    double scaleX = canvas.getWidth() / WIDTH;
                    double scaleY = canvas.getHeight() / HEIGHT;
                    gc.save();
                    gc.clearRect(0, 0, canvasWidth, canvasHeight);
                    gc.scale(scaleX, scaleY);

                    mediator.clearEnvironment();
                    /* Branching the Game Loop */
                    /* If the agent died in the last loop */
                    if (environment.getProtagonist().isDead()) {
                        /* Still prints ongoing messages (e.g., last hit taken) */
                        environment.updateMessages();
                        environment.updateShots();
                        mediator.drawBackground();
                        mediator.drawAgents();
                        mediator.drawShots();
                        mediator.drawMessages();
                        /* Rendering the Game Over Screen */
                        mediator.drawGameOver();
                    } else {
                        if (isPaused) {
                            mediator.drawBackground();
                            mediator.drawAgents();
                            mediator.drawMessages();
                            mediator.drawShots();
                            /* Rendering the Pause Screen */
                            mediator.drawPauseScreen();
                        } else {
                            /* Forces up or down movement if space is pressed or no fish caught. */
                            if (isSlowMovingUp || isSlowMoving) {
                                int posY = environment.getProtagonist().getPosY();

                                if (isSlowMoving) {
                                    int delta = 10;
                                    if (posY + delta >= 0) {
                                        environment.getProtagonist().setPosY(0);
                                        isSlowMoving = false;
                                        environment.getProtagonist().setSpeed(8);
                                        startFishingWait();
                                    } else {
                                        environment.getProtagonist().setPosY(posY + delta);
                                    }
                                }

                                if (isSlowMovingUp) {
                                    int delta = -10;
                                    if (posY + delta <= -138) {
                                        environment.getProtagonist().setPosY(-138);
                                        isSlowMovingUp = false;
                                        environment.getProtagonist().setSpeed(8);
                                    } else {
                                        environment.getProtagonist().setPosY(posY + delta);
                                    }
                                }
                            }
                            /* Update the protagonist's movements if input exists */
                            if (!input.isEmpty()) {
                                /* Fishing Rod goes to the sea */
                                if (input.contains("SPACE")) {
                                    input.remove("SPACE");
                                    if (!isSlowMoving && !isSlowMovingUp) isSlowMoving = true;
                                }
                                /* Fishing Rod's Movements (LEFT AND RIGHT ONLY) */
                                if (!isSlowMoving && !isWaitingForFish) environment.getProtagonist().move(input);
                                environment.checkBorders();
                            }
                            /* Render the game environment and agents */
                            environment.detectCollision();
                            environment.updateShots();
                            environment.updateMessages();
                            mediator.drawBackground();
                            mediator.drawAgents();
                            mediator.drawSea();
                            mediator.drawShots();
                            mediator.drawMessages();
                        }
                    }
                    gc.restore();
                }
            }.start();
            theStage.show();

        } catch (

        Exception e) {
            e.printStackTrace();
        }
    }

    private void adjustCanvasSize(Canvas canvas, double maxWidth, double maxHeight) {
        double aspectRatio = (double) WIDTH / HEIGHT;

        double newWidth = maxWidth;
        double newHeight = maxWidth / aspectRatio;

        if (newHeight > maxHeight) {
            newHeight = maxHeight;
            newWidth = newHeight * aspectRatio;
        }

        canvas.setWidth(newWidth);
        canvas.setHeight(newHeight);
    }

    private void startFishingWait() {
        isWaitingForFish = true;
        
        int waitMillis = 2000 + (int)(Math.random() * 2000); // 2000–4000ms
        PauseTransition wait = new PauseTransition(Duration.millis(waitMillis));

        wait.setOnFinished(event -> {
            isWaitingForFish = false;

            // 🎣 Determine fishing outcome
            boolean caughtFish = Math.random() < 0.5; // 50% chance

            if (caughtFish) {
                System.out.println("You caught a fish!");
                // You can trigger animation, sound, or add to inventory here
            } else {
                isSlowMovingUp = true;
                System.out.println("No fish this time.");
            }
        });

        wait.play();
    }
}