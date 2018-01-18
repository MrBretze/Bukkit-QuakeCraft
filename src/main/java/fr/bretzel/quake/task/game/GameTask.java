package fr.bretzel.quake.task.game;

import fr.bretzel.quake.game.Game;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GameTask implements Runnable {

    private int id = -1;
    private JavaPlugin javaPlugin;
    private Game game;

    public GameTask(JavaPlugin javaPlugin, long l, long l1, Game game) {
        this.javaPlugin = javaPlugin;
        this.id = javaPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(javaPlugin, this, l, l1);
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
}
