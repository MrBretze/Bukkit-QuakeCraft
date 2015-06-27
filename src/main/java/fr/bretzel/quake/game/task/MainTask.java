package fr.bretzel.quake.game.task;

import fr.bretzel.quake.Chrono;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.game.State;

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
            if(game.getState() == State.STARTED) {
                Chrono chrono = manager.getChronoGame(game);
                if(chrono == null) {
                    chrono = new Chrono();
                    chrono.start();
                    manager.getGameChrono().put(game, chrono);
                    break;
                } else {
                    chrono.pause();
                    chrono.resume();
                    if (chrono.getMinute() >= manager.getMaxMinute() || chrono.getHeure() >= 1) {
                        game.stop();
                        chrono.stop();
                        manager.getGameChrono().remove(game);
                        break;
                    }
                    chrono.start();
                }
            }
        }
    }
}
