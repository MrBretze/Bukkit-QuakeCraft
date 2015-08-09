package fr.bretzel.quake;

import org.bukkit.Bukkit;

/**
 * Created by mrbretzel on 20/07/15.
 */

public class Permission {

    public static org.bukkit.permissions.Permission COMMAND_CREATE = Bukkit.getPluginManager().getPermission("quake.command.create");
    public static org.bukkit.permissions.Permission COMMAND_DELETE = Bukkit.getPluginManager().getPermission("quake.command.delete");
    public static org.bukkit.permissions.Permission COMMAND_STOP = Bukkit.getPluginManager().getPermission("quake.command.stop");
    public static org.bukkit.permissions.Permission COMMAND_PLAYER = Bukkit.getPluginManager().getPermission("quake.command.player");

}
