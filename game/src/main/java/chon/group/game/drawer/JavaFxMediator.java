package chon.group.game.drawer;

import java.util.Iterator;

import chon.group.game.domain.agent.Agent;
import chon.group.game.domain.agent.Shot;
import chon.group.game.domain.environment.Environment;
import chon.group.game.messaging.Message;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * The {@code JavaFxMediator} class serves as an intermediary for rendering the
 * game environment
 * and its elements using JavaFX. It coordinates the interaction between the
 * {@link Environment}
 * and the {@link JavaFxDrawer} to manage graphical rendering.
 */
public class JavaFxMediator implements EnvironmentDrawer {

    private final Environment environment;
    private final JavaFxDrawer drawer;

    /**
     * Constructs a JavaFxMediator with the specified environment and graphics
     * context.
     *
     * @param environment The game environment containing agents and the
     *                    protagonist.
     * @param gc          The {@link GraphicsContext} used for rendering.
     */
    public JavaFxMediator(Environment environment, GraphicsContext gc) {
        this.environment = environment;
        this.drawer = new JavaFxDrawer(gc, this);
    }

    /**
     * Clears the environment by erasing all drawn elements on the screen.
     */
    @Override
    public void clearEnvironment() {
        drawer.clearScreen(this.environment.getWidth(), this.environment.getHeight());
    }

    @Override
    public void drawSea() {
        drawer.drawImage (this.environment.getSeaImage(),
            this.environment.getPosX(),
            this.environment.getPosY() + 192,
            this.environment.getWidth(),
            this.environment.getHeight() - 192,
            this.environment.getScale());
    }

    @Override
    public void drawFish() {
        drawer.drawImage (this.environment.getFish().getImage(),
            this.environment.getFish().getPosX(),
            this.environment.getFish().getPosY(),
            this.environment.getFish().getWidth(),
            this.environment.getFish().getHeight(),
            this.environment.getScale());
    }

    @Override
    public void drawInputKey() {
        drawer.drawImage(this.environment.getCatchKeyImage(),
                this.environment.getPosX() + 142,
                this.environment.getPosY() + 80,
                36,
                38,
                this.environment.getScale());
    }

    /**
     * Draws the background image of the environment.
     */
    @Override
    public void drawBackground() {
        drawer.drawImage(this.environment.getImage(),
                this.environment.getPosX(),
                this.environment.getPosY(),
                this.environment.getWidth(),
                this.environment.getHeight(),
                this.environment.getScale());
    }

    /**
     * Renders all agents and the protagonist within the environment,
     * including their health bars and status panels.
     */
    @Override
    public void drawAgents() {
        for (Agent agent : this.environment.getAgents()) {
            drawer.drawImage(agent.getImage(),
                    agent.getPosX(),
                    agent.getPosY(),
                    agent.getWidth(),
                    agent.getHeight(),
                    this.environment.getScale());
            drawer.drawLifeBar(agent.getHealth(),
                    agent.getFullHealth(),
                    agent.getWidth(),
                    agent.getPosX(),
                    agent.getPosY(),
                    Color.DARKRED,
                    this.environment.getScale());
        }
        drawer.drawImage(this.environment.getProtagonist().getImage(),
                this.environment.getProtagonist().getPosX(),
                this.environment.getProtagonist().getPosY(),
                this.environment.getProtagonist().getWidth(),
                this.environment.getProtagonist().getHeight(),
                this.environment.getScale());
        drawer.drawLifeBar(this.environment.getProtagonist().getHealth(),
                this.environment.getProtagonist().getFullHealth(),
                this.environment.getProtagonist().getWidth(),
                this.environment.getProtagonist().getPosX(),
                this.environment.getProtagonist().getPosY(),
                Color.GREEN,
                this.environment.getScale());
        drawer.drawStatusPanel(this.environment.getProtagonist().getPosX(),
                this.environment.getProtagonist().getPosY(),
                this.environment.getScale());
        drawer.drawStatusPanel(this.environment.getProtagonist().getPosX(),
                this.environment.getProtagonist().getPosY(),
                this.environment.getScale());
    }

    /**
     * Draws the protagonist's life bar on the screen.
     */
    @Override
    public void drawLifeBar() {
        drawer.drawLifeBar(
                this.environment.getProtagonist().getHealth(),
                this.environment.getProtagonist().getFullHealth(),
                this.environment.getProtagonist().getWidth(),
                this.environment.getProtagonist().getPosX(),
                this.environment.getProtagonist().getPosY(),
                Color.GREEN,
                this.environment.getScale());
    }

    /**
     * Draws the protagonist's status panel on the screen.
     */
    @Override
    public void drawStatusPanel() {
        drawer.drawStatusPanel(this.environment.getProtagonist().getPosX(),
                this.environment.getProtagonist().getPosY(),
                this.environment.getScale());
    }

    /**
     * Draws the pause screen overlay, displaying a pause image centered within the
     * environment.
     */
    @Override
    public void drawPauseScreen() {
        drawer.drawScreen(this.environment.getPauseImage(),
                (int) this.environment.getPauseImage().getWidth(),
                (int) this.environment.getPauseImage().getHeight(),
                this.environment.getWidth(),
                this.environment.getHeight(),
                this.environment.getScale());
    }

    /**
     * Draws the pause screen overlay, displaying a pause image centered within the
     * environment.
     */
    @Override
    public void drawGameOver() {
        drawer.drawScreen(this.environment.getGameOverImage(),
                (int) this.environment.getPauseImage().getWidth(),
                (int) this.environment.getPauseImage().getHeight(),
                this.environment.getWidth(),
                this.environment.getHeight(),
                this.environment.getScale());
    }

    /**
     * Draws damage messaages that appear when agents take damage.
     * The message float upward and fade out over time.
     */
    @Override
    public void drawMessages() {
        Iterator<Message> iterator = this.environment.getMessages().iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            drawer.drawMessages(message.getSize(),
                    message.getOpacity(),
                    Color.BLACK,
                    Color.WHEAT,
                    String.valueOf(message.getMessage()),
                    message.getPosX(),
                    message.getPosY(),
                    this.environment.getScale());
        }
    }

    @Override
    public void drawShots() {
        Iterator<Shot> iterator = this.environment.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();          
            drawer.drawImage(shot.getImage(),
                    shot.getPosX(),
                    shot.getPosY(),
                    shot.getWidth(),
                    shot.getHeight(),
                    this.environment.getScale());
        }
    }
}
