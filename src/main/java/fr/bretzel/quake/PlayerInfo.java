package fr.bretzel.quake;

import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.DashTask;
import fr.bretzel.quake.game.task.ReloadTask;
import fr.bretzel.quake.inventory.BasicGun;
import fr.bretzel.quake.reader.PlayerInfoReader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.*;
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

    public double getReloadTime() {
        return reload;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public Player getPlayer() {
        return player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setReloadTime(double reload) {
        this.reload = reload;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public void setReload(long reload) {
        this.reload = reload;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    public boolean isInGame() {
        return Quake.gameManager.getGameByPlayer(getPlayer()) != null;
    }

    public void give(BasicGun basicGun) {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setItem(0, basicGun.getStack());
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

    public void dash() {
        if(isDash()) {
            setDash(false);
            Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new DashTask(this), (long) (getReloadTime() * 35));
            getPlayer().setVelocity(getPlayer().getLocation().getDirection().multiply(1.5));
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
        NBTTagCompound c = PlayerInfoReader.write(this);
        try {
            NBTCompressedStreamTools.wrhite(c, new FileOutputStream(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
