package fr.bretzel.quake.game.task;

import fr.bretzel.quake.Chrono;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by MrBretzel on 24/06/2015.
 */
public class MainTask extends BukkitRunnable {

    private final GameManager manager;

    public MainTask(GameManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Game game : manager.getGameLinkedList()) {
            Chrono c = manager.getChronoGame(game);
            if (c != null) {
                c.pause();
                if(c.getMinute() > manager.maxMinute || c.getHeure() > 0) {
                    game.stop();
                    c.stop();
                    manager.getGameChrono().remove(game);
                    return;
                }
                c.start();
            }
        }
    }
}
