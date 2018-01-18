package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.util.Util;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.io.File;

public class GameReader {

    //TODO: Remove all reader !

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
        for (Location loc : game.getRespawns()) {
            r.setString(String.valueOf(respawns), Util.toStringLocation(loc));
            respawns++;
        }
        r.setInt("size", respawns);
        compound.set("respawns", r);

        int players = 0;
        NBTTagCompound p = new NBTTagCompound();
        for (PlayerInfo uuid : game.getPlayerList()) {
            p.setString(String.valueOf(players), uuid.getUUID().toString());
            players++;
        }
        p.setInt("size", players);
        compound.set("players", p);

        compound.setString("location1", Util.toStringLocation(game.getFirstLocation()));
        compound.setString("location2", Util.toStringLocation(game.getSecondLocation()));
        compound.setString("spawn", Util.toStringLocation(game.getSpawn()));
        compound.setString("displayName", game.getDisplayName());
        compound.setInt("maxPlayer", game.getMaxPlayer());
        compound.setInt("minPlayer", game.getMinPlayer());
        return compound;
    }

    public static Game read (NBTTagCompound compound, File file) {
        Game game = new Game();

        game.setName(file.getName().replace(".dat", ""));
        game.setFile(file);
        game.setFirstLocation(Util.toLocationString(compound.getString("location1")));
        game.setSecondLocation(Util.toLocationString(compound.getString("location2")));
        game.setSpawn(Util.toLocationString(compound.getString("spawn")));
        game.setMaxPlayer(compound.getInt("maxPlayer"));
        game.setMinPlayer(compound.getInt("minPlayer"));

        if (compound.hasKey("signs")) {
            NBTTagCompound s = compound.getCompound("signs");
            int size = s.getInt("size");
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    game.addSign(SignReader.read(s.getCompound(String.valueOf(i))));
                }
            }
        }

        if (compound.hasKey("respawns")) {
            NBTTagCompound s = compound.getCompound("respawns");
            int size = s.getInt("size");
            int vsize = size - 1;
            if (size > 0) {
                for (int i = 0; i <= vsize; i++) {
                    if (size != size - 1) {
                        game.addRespawn(Util.toLocationString(s.getString(String.valueOf(i))).subtract(0.0, 1.0, 0.0));
                    }
                }
            }
        }

        /*if (compound.hasKey("players")) {
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
        }*/

        game.setDisplayName(compound.getString("displayName"));
        ScoreboardAPI api = new ScoreboardAPI(game);
        game.setScoreboardManager(api);
        Team team = api.getScoreboard().registerNewTeam(game.getName());
        team.setNameTagVisibility(NameTagVisibility.NEVER);
        game.setTeam(team);
        return game;
    }

}
