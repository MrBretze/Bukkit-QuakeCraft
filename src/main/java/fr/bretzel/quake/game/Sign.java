package fr.bretzel.quake.game;

import org.bukkit.Location;

public class Sign {

    private Location location;
    private Game game;
    private boolean join;


    public Sign(Location location, Game game) {
        this.join = game != null;
        this.game = game;
        this.location = location;
    }

    /**
     * @return To the sign location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return is sign join a game.
     */
    public boolean isJoin() {
        return join;
    }

    /**
     *
     * @return To the game if this is null in not a join sign.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Set a valid game to join on click.
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }
}
