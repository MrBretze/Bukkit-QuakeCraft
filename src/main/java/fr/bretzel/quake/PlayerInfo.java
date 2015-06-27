package fr.bretzel.quake;

import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.DashTask;
import fr.bretzel.quake.game.task.ReloadTask;
import fr.bretzel.quake.inventory.Gun;
import fr.bretzel.quake.reader.PlayerInfoReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by MrBretzel on 14/06/2015.
 */

public class PlayerInfo {

    private Player player;
    private ParticleEffect effect = ParticleEffect.FIREWORKS_SPARK;
    private double reload = 1.5;
    private Game game;
    private Location firstLocation = null;
    private Location secondLocation = null;
    private boolean shoot = true;
    private boolean dash = true;
    private File file;
    private Random random = new Random();

    public PlayerInfo(Player player) {
        setPlayer(player);

        File mk = new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator);

        mk.mkdir();

        setFile(new File(mk, player.getUniqueId().toString() + ".dat"));

        try {
            if(!getFile().exists()) {
                getFile().createNewFile();
                NBTTagCompound compound = new NBTTagCompound();
                compound.setDouble("reload", getReloadTime());
                compound.setString("effect", getEffect().getName());
                NBTCompressedStreamTools.wrhite(compound, new FileOutputStream(getFile()));
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        PlayerInfoReader.read(this);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public double getReloadTime() {
        return reload;
    }

    public void setReloadTime(double reload) {
        this.reload = reload;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    public void setReload(long reload) {
        this.reload = reload;
    }

    public boolean isInGame() {
        return Quake.gameManager.getGameByPlayer(getPlayer()) != null;
    }

    public void give(Gun gun) {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setItem(0, gun.getStack());
    }

    public boolean isShoot() {
        return shoot;
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public boolean isDash() {
        return dash;
    }

    public void setDash(boolean dash) {
        this.dash = dash;
    }

    public void setBoard(ScoreboardManager manager) {
        getPlayer().setScoreboard(manager.getScoreboard());
    }

    public void dash() {
        if(isDash()) {
            setDash(false);
            Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new DashTask(this), (long) (getReloadTime() * 35));
            Vector v = getPlayer().getEyeLocation().getDirection().multiply(1.5D);
            v.setY(0.7D);
            getPlayer().setVelocity(v);
        }
    }

    public void shoot() {
        PlayerShootEvent shoot = new PlayerShootEvent(getPlayer(), Quake.gameManager.getGameByPlayer(getPlayer()));
        Bukkit.getPluginManager().callEvent(shoot);
        if(shoot.isCancelled()) {
            return;
        }

        Game game = Quake.gameManager.getGameByPlayer(getPlayer());

        if(isShoot()) {
            setShoot(false);
            Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new ReloadTask(this), (long) (getReloadTime() * 20));

            LinkedList<Location> locs = Util.getLocationByDirection(getPlayer(), 200, 0.5);
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.FIREWORK_LARGE_BLAST, random.nextFloat(), random.nextFloat());
            for(Location l : locs) {
                getEffect().display(0.0F, 0.0F, 0.0f, 0.0F, 1, l, 200);
            }

            List<Player> pList = Util.getPlayerListInDirection(locs, getPlayer(), 0.59D);

            for(Player player : pList) {
                player.setMetadata("killer", new FixedMetadataValue(Quake.quake, getPlayer().getDisplayName()));
                Util.shootFirework(player.getEyeLocation());
                game.respawn(player);
            }
        }
    }

    public void save() {
        try {
            NBTCompressedStreamTools.wrhite(PlayerInfoReader.write(this), new FileOutputStream(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
