package fr.bretzel.quake;

import fr.bretzel.quake.api.IArena;
import fr.bretzel.quake.api.IConfig;
import fr.bretzel.quake.api.IGame;

public class Game implements IGame {

    private IArena arena;
    private int maxPlayer;
    private int minPlayer;
    private int maxKill;
    private Config config;
    private GameState state = GameState.UNKNOW;

    public Game(IArena arena, Config config) {
        this.arena = arena;
        this.config = config;
    }

    public IConfig getConfig() {
        return config;
    }

    public int getMaxKill() {
        return maxKill;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public IArena getArena() {
        return arena;
    }

    public enum GameState {
        STARTING,
        STARTED,
        UNKNOW,
        WAITING
    }
}
