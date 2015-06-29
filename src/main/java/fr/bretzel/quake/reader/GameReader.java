package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
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
        compound.setString("displayName", game.getDisplayName());

        return compound;
    }

    public static Game read (NBTTagCompound compound, File file) {
        Game game = new Game();

        System.out.print("Init new game by file: " + file.toString());

        game.setName(file.getName().replace(".dat", ""));
        game.setFile(file);
        game.setFirstLocation(Util.toLocationString(compound.getString("location1")));
        game.setSecondLocation(Util.toLocationString(compound.getString("location2")));
        game.setSpawn(Util.toLocationString(compound.getString("spawn")));

        for (Block b : Util.blocksFromTwoPoints(game.getFirstLocation(), game.getSecondLocation())) {
            game.addBlock(b);
        }

        System.out.print("Init signs");

        if (compound.hasKey("signs")) {
            NBTTagCompound s = compound.getCompound("signs");
            int size = s.getInt("size");
            int vsize = size - 1;
            System.out.print("Init signs size:" + size);
            if (size > 0) {
                for (int i = 0; i <= vsize; i++) {
                    System.out.print("Init signs: " + i);
                    game.addSign(SignReader.read(s.getCompound(String.valueOf(i))));
                }
            }
        }

        System.out.print("Init respawn");
        if (compound.hasKey("respawns")) {
            NBTTagCompound s = compound.getCompound("respawns");
            int size = s.getInt("size");
            int vsize = size - 1;
            System.out.print("Init respawn size: " + size);
            if (size > 0) {
                for (int i = 0; i <= vsize; i++) {
                    System.out.print("Init respawn: " + i);
                    if (size != size - 1) {
                        game.addRespawn(Util.toLocationString(s.getString(String.valueOf(i))));
                    }
                }
            }
        }

        if (compound.hasKey("players")) {
            NBTTagCompound s = compound.getCompound("players");
            int size = s.getInt("size");
            int vsize = size - 1;
            if (size > 0) {
                for (int i = 0; i < vsize; i++) {
                    try {
                        game.addPlayer(UUID.fromString(s.getString(String.valueOf(i))));
                    } catch (IllegalArgumentException e) {
                        e.fillInStackTrace();
                    }
                }
            }
        }

        game.setDisplayName(compound.getString("displayName"));
        game.setScoreboardManager(new ScoreboardAPI(game));
        return game;
    }

}
