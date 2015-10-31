package fr.bretzel.quake;

import java.util.HashMap;

/**
 * Created by MrBretzel on 30/10/2015.
 */
public class Game implements Savable {

    private Map map;
    private int maxPlayer;
    private int minPlayer;
    private int maxKill;
    private Config config;

    public Game(Map map, Config config) {

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

    @Override
    public HashMap<String, Object> save() {
        return null;
    }

    public enum GameState {
        STARTING,
        STARTED,
        UNKNOW,
        WAITING
    }
}
