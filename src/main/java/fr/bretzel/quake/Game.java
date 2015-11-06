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
    private int version = 1;
    private String name;
    private String displayName;
    private GameState state = GameState.UNKNOW;
    private JSON json;

    public Game(Map map, String json) {
        this.json = new JSON(json);
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    @Override
    public HashMap<String, Object> save() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("version", version);
        data.put("displayName", getDisplayName());
        data.put("name", getName());
        data.put("gameState", getState().name());
        data.put("maxPlayer", getMaxPlayer());
        data.put("minPlayer", getMinPlayer());
        data.put("maxKill", getMaxKill());
        return data;
    }

    public enum GameState {
        STARTING,
        STARTED,
        UNKNOW,
        WAITING
    }
}
