package fr.bretzel.quake;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MrBretzel on 30/10/2015.
 */
public class Map implements Savable {

    private World world;
    private ArrayList<Location> locations = new ArrayList<>();
    private DoubleLocation selection;

    public Map(DoubleLocation doubleLocation) {
        this.selection = doubleLocation;
        this.world = this.selection.getWorld();
    }

    @Override
    public HashMap<String, Object> save() {
        return null;
    }
}
