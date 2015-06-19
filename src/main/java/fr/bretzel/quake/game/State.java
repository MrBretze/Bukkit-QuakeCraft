package fr.bretzel.quake.game;

import org.bukkit.ChatColor;

/**
 * Created by MrBretzel on 20/06/2015.
 */
public enum State {

    STARTED(ChatColor.RED + "Started"),
    WAITING(ChatColor.GOLD + "Waiting");

    private String name;

    private State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
