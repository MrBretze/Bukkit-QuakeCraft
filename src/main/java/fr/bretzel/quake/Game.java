package fr.bretzel.quake;

import fr.bretzel.quake.api.IGame;

/**
 * Created by MrBretzel on 30/10/2015.
 */
public class Game implements IGame {

    private Map map;
    private int maxPlayer;
    private int minPlayer;
    private int maxKill;
    private Config config;
    private GameState state = GameState.UNKNOW;

    public Game(Map map, Config config) {
        this.map = map;
        this.config = config;
    }

    public Config getConfig() {
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

    public Map getMap() {
        return map;
    }

    public enum GameState {
        STARTING,
        STARTED,
        UNKNOW,
        WAITING
    }
}
