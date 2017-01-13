package fr.bretzel.quake;

import fr.bretzel.quake.api.IArena;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Quake extends JavaPlugin {

    private List<IArena> arenas = new ArrayList<>();

    public static Quake INSTANCE = JavaPlugin.getPlugin(Quake.class);

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public static void registerGame(Arena arena) {

    }
}
