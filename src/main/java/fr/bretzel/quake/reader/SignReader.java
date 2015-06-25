package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by MrBretzel on 19/06/2015.
 */

public class SignReader {

    public static Sign read(NBTTagCompound compound) {
        Sign sign = null;
        try {
            Location location = Util.toLocationString(compound.getString("location"));
            sign = (Sign) location.getBlock().getState();

            sign.setLine(0, compound.getString("line1"));
            sign.setLine(1, compound.getString("line2"));
            sign.setLine(2, compound.getString("line3"));
            sign.setLine(3, compound.getString("line4"));

            sign.setMetadata("join", new FixedMetadataValue(Quake.quake, Boolean.valueOf(compound.getString("join"))));
            sign.setMetadata("game", new FixedMetadataValue(Quake.quake, String.valueOf(compound.getString("game"))));
            sign.setMetadata("name", new FixedMetadataValue(Quake.quake, String.valueOf(compound.getString("name"))));

            sign.update();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return sign;
    }

    public static NBTTagCompound write(Sign sign) {
        NBTTagCompound compound = new NBTTagCompound();
        try {
            sign = (Sign) sign.getLocation().getWorld().getBlockAt(sign.getLocation()).getState();

            compound.setString("line1", sign.getLine(0));
            compound.setString("line2", sign.getLine(1));
            compound.setString("line3", sign.getLine(2));
            compound.setString("line4", sign.getLine(3));

            compound.setBoolean("join", sign.getMetadata("join").get(0).asBoolean());
            compound.setString("game", sign.getMetadata("game").get(0).asString());
            compound.setString("name", sign.getMetadata("name").get(0).asString());

            compound.setString("location", Util.toStringLocation(sign.getLocation()));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return compound;
    }
}
