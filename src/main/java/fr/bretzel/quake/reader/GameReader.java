package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.nbt.NBTTagInt;
import fr.bretzel.nbt.NBTTagList;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import org.bukkit.block.Sign;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by MrBretzel on 25/06/2015.
 */

public class GameReader {

    public static NBTTagCompound write (Game game) {
        NBTTagCompound compound = new NBTTagCompound();

        int signs = 0;
        NBTTagList list = new NBTTagList();
        for(Sign sign : game.getSignList()) {
            list.add(SignReader.write(sign));
            signs++;
        }
        compound.set("signs", list);
        compound.setInt("signs_size", signs);
        return compound;
    }

    public static Game read (NBTTagCompound compound) {
        try {
            Game game = Game.class.getDeclaredConstructor(Quake.class).newInstance(Quake.quake);

            return game;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

}
