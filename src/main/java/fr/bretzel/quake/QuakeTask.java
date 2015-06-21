package fr.bretzel.quake;

import fr.bretzel.quake.game.Game;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MrBretzel on 21/06/2015.
 */

public abstract class QuakeTask implements Runnable {

    private int id = -1;
    private JavaPlugin javaPlugin;
    private Game game;

    public QuakeTask(JavaPlugin javaPlugin, long l, long l1, Game game) {
        this.javaPlugin = javaPlugin;
        this.id = javaPlugin.getServer().getScheduler().scheduleAsyncRepeatingTask(javaPlugin, this, l, l1);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public int getId() {
        return id;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public void cancel() {
        getJavaPlugin().getServer().getScheduler().cancelTask(getId());
    }

    @Override
    public void run() {

    }
}
