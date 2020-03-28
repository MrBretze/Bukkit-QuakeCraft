package fr.bretzel.quake.command;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SetNoUpdate implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            PlayerInfo playerInfo = Quake.getPlayerInfo(player);
            if (playerInfo.getFirstLocation() == null)
            {
                sender.sendMessage(ChatColor.RED + "Please select the first location");
                return false;
            }

            if (playerInfo.getSecondLocation() == null)
            {
                sender.sendMessage(ChatColor.RED + "Please select the second location");
                return false;
            }

            if (args.length <= 0)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /SetNoUpdate <block> {tag}");
                return false;
            }

            String tag = args.length >= 2 ? args[1] : "";

            Material material = Material.matchMaterial(args[0], false);

            if (material == null)
            {
                sender.sendMessage(ChatColor.RED + "The block: " + args[0] + " d'ont exist !");
                return false;
            }

            int minz = Math.min(playerInfo.getFirstLocation().getBlockZ(), playerInfo.getSecondLocation().getBlockZ());
            int maxz = Math.max(playerInfo.getFirstLocation().getBlockZ(), playerInfo.getSecondLocation().getBlockZ());

            int miny = Math.min(playerInfo.getFirstLocation().getBlockY(), playerInfo.getSecondLocation().getBlockY());
            int maxy = Math.max(playerInfo.getFirstLocation().getBlockY(), playerInfo.getSecondLocation().getBlockY());

            int minx = Math.min(playerInfo.getFirstLocation().getBlockX(), playerInfo.getSecondLocation().getBlockX());
            int maxx = Math.max(playerInfo.getFirstLocation().getBlockX(), playerInfo.getSecondLocation().getBlockX());

            for (int z = minz; z <= maxz; z++)
            {
                for (int x = minx; x <= maxx; x++)
                {
                    for (int y = maxy; y >= miny; y--)
                    {
                        Location location = new Location(player.getWorld(), x, y, z);
                        if (location.getBlock().getType() != material)
                        {
                            location.getBlock().setType(material, false);
                            location.getBlock().setBlockData(material.createBlockData(tag), true);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 1)
        {
            List<String> strings = new ArrayList<>();
            for (Material m : Material.values())
                if (m.isBlock() && !m.isLegacy())
                    strings.add(m.name().toLowerCase());
            return StringUtil.copyPartialMatches(args[0], strings, new ArrayList<>());
        }
        return null;
    }
}
