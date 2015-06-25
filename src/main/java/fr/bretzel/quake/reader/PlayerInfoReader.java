package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.ParticleEffect;
import fr.bretzel.quake.PlayerInfo;

import java.io.FileInputStream;
import java.io.IOException;


/**
 * Created by MrBretzel on 25/06/2015.
 */
public class PlayerInfoReader {

    public static NBTTagCompound write (PlayerInfo playerInfo) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("effect", playerInfo.getEffect().name());
        compound.setDouble("reload", playerInfo.getReloadTime());
        return compound;
    }

    public static void read (PlayerInfo player) {
        NBTTagCompound compound = new NBTTagCompound();
        try {
            compound = NBTCompressedStreamTools.read(new FileInputStream(player.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (compound.hasKey("effect")) {
            player.setEffect(ParticleEffect.fromName(compound.getString("effect")));
        } else {
            player.setEffect(ParticleEffect.FIREWORKS_SPARK);
        }
        if (compound.hasKey("reload")) {
            player.setReloadTime(compound.getDouble("reload"));
        }
    }
}
