package fr.bretzel.quake;

import org.bukkit.block.Beacon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by MrBretzel on 22/06/2015.
 */

public class BeaconUtil {

    public static void  setActive(Beacon beacon, boolean active) {
        try {
            Object getHandle = beacon.getWorld().getClass().getMethod("getHandle").invoke(beacon.getWorld());

            Object blockposition = ReflectionUtils.getConstructor("BlockPosition", ReflectionUtils.PackageType.MINECRAFT_SERVER, Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(
                    beacon.getLocation().getBlockX(),
                    beacon.getLocation().getBlockY(),
                    beacon.getLocation().getBlockZ());

            Method getTileEntity = getHandle.getClass().getMethod("getTileEntity", blockposition.getClass());

            Object tileEntity = getTileEntity.invoke(getHandle, blockposition);

            Field i = tileEntity.getClass().getDeclaredField("i");

            Field j = tileEntity.getClass().getDeclaredField("j");

            Field k = tileEntity.getClass().getDeclaredField("k");

            Field l = tileEntity.getClass().getDeclaredField("l");

            l.setAccessible(true);
            l.set(tileEntity, 5);
            l.setAccessible(false);

            k.setAccessible(true);
            k.set(tileEntity, 6);
            k.setAccessible(false);

            j.setAccessible(true);
            j.set(tileEntity, 4);
            j.setAccessible(false);

            i.setAccessible(true);
            i.set(tileEntity, active);
            i.setAccessible(false);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
