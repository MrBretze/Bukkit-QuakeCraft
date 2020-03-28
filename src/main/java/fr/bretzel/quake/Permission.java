package fr.bretzel.quake;

import org.bukkit.Bukkit;

/**
 * Created by mrbretzel on 20/07/15.
 */

public class Permission
{

    public static org.bukkit.permissions.Permission COMMAND_PLAYER = Bukkit.getPluginManager().getPermission("quake.command.player");
    public static org.bukkit.permissions.Permission COMMAND_GAME = Bukkit.getPluginManager().getPermission("quake.command.game");
    public static org.bukkit.permissions.Permission COMMAND_GAME_CREATE = Bukkit.getPluginManager().getPermission("quake.command.game.create");

}
