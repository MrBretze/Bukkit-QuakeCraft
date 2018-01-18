package fr.bretzel.quake.game;

import org.bukkit.ChatColor;

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
