package fr.bretzel.quake.player;

import fr.bretzel.quake.ParticleEffect;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.arena.Arena;
import fr.bretzel.quake.arena.Rule;

import fr.bretzel.quake.nbt.TagCompound;
import fr.bretzel.quake.nbt.stream.NbtInputStream;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private TagCompound compound;

    public PlayerInfo(Player player) {
        setPlayer(player);

        File file = new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator, player.getUniqueId() + ".dat");

        if(!file.exists()) {
            try {
                if(!Quake.quake.getDataFolder().exists()) {
                    Quake.quake.getDataFolder().mkdir();
                }

                file.createNewFile();

            } catch (IOException e) {}

            compound = new TagCompound("player");

        } else {
            try {
                NbtInputStream stream = new NbtInputStream(new FileInputStream(file));
                compound = (TagCompound) stream.readTag();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public TagCompound getCompound() {
        return compound;
    }

    public void setCompound(TagCompound compound) {
        this.compound = compound;
    }
}
