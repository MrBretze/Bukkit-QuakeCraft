package fr.bretzel.quake.player;

import fr.bretzel.quake.ParticleEffect;
import fr.bretzel.quake.arena.Arena;
import fr.bretzel.quake.arena.Rule;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrBretzel on 14/06/2015.
 */

public class PlayerInfo {

    private Player player;

    private ParticleEffect effect;

    private long reload;

    private Arena arena;

    private List<Rule> rule = new ArrayList<>();

    public PlayerInfo(Player player) {
        setPlayer(player);
    }

    public Arena getArena() {
        return arena;
    }

    public long getReloadTime() {
        return reload;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Rule> getRule() {
        return rule;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setReloadTime(long reload) {
        this.reload = reload;
    }

    public void setRule(List<Rule> rule) {
        this.rule = rule;
    }
}
