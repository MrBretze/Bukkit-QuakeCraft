package fr.bretzel.quake.arena;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.TagString;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by MrBretzel on 19/06/2015.
 */

public class SignReader {

    public static Sign read(TagCompound compound) {
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

            sign.update();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return sign;
    }

    public static TagCompound write(Sign sign, String name) {
        TagCompound compound = new TagCompound(name);
        try {
            compound.setTag(new TagString("line1", sign.getLine(0)));
            compound.setTag(new TagString("line2", sign.getLine(1)));
            compound.setTag(new TagString("line3", sign.getLine(2)));
            compound.setTag(new TagString("line4", sign.getLine(3)));

            compound.setTag(new TagString("join", String.valueOf(sign.getMetadata("join").get(0).asBoolean())));

            compound.setTag(new TagString("game", sign.getMetadata("game").get(0).asString()));

            compound.setTag(new TagString("location", Util.toStringLocation(sign.getLocation())));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return compound;
    }
}
