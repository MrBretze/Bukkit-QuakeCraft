package fr.bretzel.quake.arena;

import fr.bretzel.quake.Util;
import fr.bretzel.quake.arena.api.IArena;
import fr.bretzel.quake.arena.api.IRule;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class Arena implements IArena {

    private LinkedList<? super IRule> rules = new LinkedList<>();
    private LinkedList<Location> respawn = new LinkedList<>();
    private Location firstLocation;
    private Location secondLocation;
    private LinkedList<Block> blocks = new LinkedList<>();

    public Arena(Location firstLocation, Location secondLocation) {
        setFirstLocation(firstLocation);
        setSecondLocation(secondLocation);

        for(Block block : Util.blocksFromTwoPoints(getFirstLocation(), getSecondLocation())) {
            addBlock(block);
        }
    }

    @Override
    public void setFirstLocation(Location location) {
        this.firstLocation = location;
    }

    public void addRules(IRule rule) {
        this.rules.add(rule);
    }

    public LinkedList<Location> getRespawn() {
        return respawn;
    }

    public void setRespawn(LinkedList<Location> respawn) {
        this.respawn = respawn;
    }

    public void setRules(LinkedList<? super IRule> rules) {
        this.rules = rules;
    }

    public void addRespawn(Location location) {
        getRespawn().add(location);
    }

    @Override
    public void setSecondLocation(Location location) {
        this.secondLocation = location;
    }

    @Override
    public Location getFirstLocation() {
        return this.firstLocation;
    }

    @Override
    public Location getSecondLocation() {
        return this.secondLocation;
    }

    @Override
    public LinkedList<? super IRule> getRules() {
        return rules;
    }

    public LinkedList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(LinkedList<Block> blocks) {
        this.blocks = blocks;
    }

    public void addBlock(Block block) {
        getBlocks().add(block);
    }
}
