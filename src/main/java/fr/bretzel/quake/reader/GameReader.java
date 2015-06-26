package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.Game;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.File;
import java.util.UUID;

/**
 * Created by MrBretzel on 25/06/2015.
 */

public class GameReader {

    public static NBTTagCompound write (Game game) {
        NBTTagCompound compound = new NBTTagCompound();

        int signs = 0;
        NBTTagCompound s = new NBTTagCompound();
        for(Sign sign : game.getSignList()) {
            s.set(String.valueOf(signs), SignReader.write(sign));
            signs++;
        }
        s.setInt("size", signs);
        compound.set("signs", s);

        int respawns = 0;
        NBTTagCompound r = new NBTTagCompound();
        for (Location loc : game.getRespawn()) {
            r.setString(String.valueOf(respawns), Util.toStringLocation(loc));
            respawns++;
        }
        r.setInt("size", respawns);
        compound.set("respawns", r);

        int players = 0;
        NBTTagCompound p = new NBTTagCompound();
        for (UUID uuid : game.getPlayerList()) {
            p.setString(String.valueOf(players), uuid.toString());
            players++;
        }
        p.setInt("size", players);
        compound.set("players", p);

        compound.setString("location1", Util.toStringLocation(game.getFirstLocation()));
        compound.setString("location2", Util.toStringLocation(game.getSecondLocation()));
        compound.setString("spawn", Util.toStringLocation(game.getSpawn()));

        return compound;
    }

    public static Game read (NBTTagCompound compound, File file) {
        Game game = new Game();

        game.setName(file.getName().replace(".dat", ""));
        game.setFirstLocation(Util.toLocationString(compound.getString("location1")));
        game.setSecondLocation(Util.toLocationString(compound.getString("location2")));
        game.setSpawn(Util.toLocationString(compound.getString("spawn")));

        for (Block b : Util.blocksFromTwoPoints(game.getFirstLocation(), game.getSecondLocation())) {
            game.addBlock(b);
        }

        if (compound.hasKey("signs")) {
            NBTTagCompound s = compound.getCompound("signs");
            int size = s.getInt("size");
            if(s.hasKey(String.valueOf(0))) {
                for(int i = 0; i >= size; i++) {
                    game.addSign(SignReader.read(s.getCompound(String.valueOf(i))));
                }
            }
        }

        if (compound.hasKey("respawns")) {
            NBTTagCompound s = compound.getCompound("respawns");
            int size = s.getInt("size");
            if(s.hasKey(String.valueOf(0))) {
                for(int i = 0; i >= size; i++) {
                    game.addRespawn(Util.toLocationString(s.getString(String.valueOf(i))));
                }
            }
        }

        if (compound.hasKey("players")) {
            NBTTagCompound s = compound.getCompound("players");
            int size = s.getInt("size");
            if(s.hasKey(String.valueOf(0))) {
                for(int i = 0; i >= size; i++) {
                    game.addPlayer(UUID.fromString(s.getString(String.valueOf(i))));
                }
            }
        }
        return game;
    }

}
