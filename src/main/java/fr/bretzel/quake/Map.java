package fr.bretzel.quake;

import fr.bretzel.quake.api.IMap;
import fr.bretzel.quake.api.IQuakePlayer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

/**
 * Created by MrBretzel on 30/10/2015.
 */
public class Map implements IMap {

    private World world;
    private ArrayList<Location> locations = new ArrayList<>();
    private DoubleLocation selection;
    private ArrayList<IQuakePlayer> players;

    public Map(DoubleLocation doubleLocation) {
        this.selection = doubleLocation;
        this.world = this.selection.getWorld();
        this.players = new ArrayList<>();
    }

    public void addPlayer(QuakePlayer player) {

    }

    public boolean containsPlayer(IQuakePlayer player) {
        for (IQuakePlayer qp : players)
            if (qp.getUuid().toString().equalsIgnoreCase(player.getUuid().toString()))
                return true;
        return false;
    }
}
