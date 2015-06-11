package fr.bretzel.quake;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrBretzel on 11/06/2015.
 */
public class Util {


    public static List<Location> getLocationByDirection(Player player, int range, double distance) {
        List<Location> list = new ArrayList<>();
        Location playerEyes = player.getEyeLocation();
        Vector direction = playerEyes.getDirection().normalize();
        Location f = playerEyes.clone();
        Vector progress = direction.clone().multiply(distance);
        int maxRange = 100 * range / 70;
        int loop = 0;
        boolean wallHack = false;

        while (loop < maxRange) {
            ++loop;
            f.add(progress);

            if(!wallHack && f.getBlock().getType().isSolid()) {
                break;
            }
            //ParticleEffect.FLAME.display(0f, 0f, 0f, 0f, 1, f, 100);
            list.add(f);

        }

        return list;
    }

    /**
     *         ArrayList target = new ArrayList();
     Location playerEyes = player.getEyeLocation();
     Vector direction = playerEyes.getDirection().normalize();
     ArrayList targets = new ArrayList();
     List<Player> lx = new ArrayList<>(Bukkit.getOnlinePlayers());
     int testLoc = lx.size();

     for(int loc = 0; loc < testLoc; ++loc) {
     Player block = lx.get(loc);
     if(block != player && block.getLocation().distanceSquared(playerEyes) < (double)(maxRange * maxRange)) {
     targets.add(block);
     }
     }

     Location var35 = playerEyes.clone();
     Vector progress = direction.clone().multiply(0.7D);
     maxRange = 100 * maxRange / 70;
     int loop = 0;

     while(loop < maxRange) {
     ++loop;
     var35.add(progress);
     Block var34 = var35.getBlock();
     if(!wallHack && var34.getType().isSolid()) {
     break;
     }

     double var37 = var35.getX();
     double ly = var35.getY();
     double lz = var35.getZ();
     Iterator var29 = Bukkit.getOnlinePlayers().iterator();

     while(var29.hasNext()) {
     Player possibleTarget = (Player) var29.next();

     try {
     ParticleEffect.FLAME.display(0.0F, 0.0F, 0.0F, 0.0F, 1, var35, 100);
     } catch (Exception var33) {
     var33.printStackTrace();
     }
     }

     var29 = targets.iterator();

     while(var29.hasNext()) {
     Player var38 = (Player)var29.next();
     if(var38.getUniqueId() != player.getUniqueId()) {
     Location var36 = var38.getLocation().add(0.0D, 0.85D, 0.0D);
     double px = var36.getX();
     double py = var36.getY();
     double pz = var36.getZ();
     boolean dX = Math.abs(var37 - px) < 0.7D * aiming;
     boolean dY = Math.abs(ly - py) < 1.7D * aiming;
     boolean dZ = Math.abs(lz - pz) < 0.7D * aiming;
     if(dX && dY && dZ && !target.contains(var38)) {
     target.add(var38);
     }
     }
     }
     }
     */

}
