package fr.bretzel.quake.arena;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.arena.api.IArena;
import org.bukkit.Location;

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class ArenaManager {

    private LinkedList<Arena> arenaLinkedList = new LinkedList<>();

    private Quake quake;

    public ArenaManager(Quake quake) {
        this.quake = quake;
    }

    public void registerArena(String name, Location loc1, Location loc2) {

    }

    public IArena getArenaByName(String name) {
        Arena arena = null;
        byte[] bytes = name.getBytes();
        for(Arena a : arenaLinkedList) {
            if(bytes == a.getNameByByte() && a.getName().equals(name)) {
                arena = a;
            }
        }
        return arena;
    }

    public boolean containsArena(Arena arena) {
        return arenaLinkedList.contains(arena);
    }

    public boolean containsArena(String arena) {
        return arenaLinkedList.contains(getArenaByName(arena));
    }

    public Quake getQuake() {
        return quake;
    }
}
