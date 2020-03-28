package fr.bretzel.quake.command;

import com.google.common.collect.ImmutableList;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class QuakeTabCompleter implements TabCompleter
{
    private List<String> MAIN_CHOICES = ImmutableList.of("create", "delete", "game", "players");

    private List<String> GAME_CHOICES = ImmutableList.of("setdisplayname", "view", "setspawn", "setmaxplayer", "setminplayer", "addrespawn", "deleterespawn");

    private List<String> PLAYERS_CHOICES = ImmutableList.of("quit", "join", "setcoins", "addcoins", "removecoins", "setkill", "addkill", "removekill",
            "setkillstreak", "addkillstreak", "removekillstreak", "setwon", "addwon", "removewon");

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
            return StringUtil.copyPartialMatches(args[0], MAIN_CHOICES, new ArrayList<>());

        if (args.length > 1)
        {
            if (args[0].equalsIgnoreCase("game"))
            {
                if (args.length == 2)
                    return StringUtil.copyPartialMatches(args[1], getCurrentGames(), new ArrayList<>());

                if (Quake.gameManager.getGameByName(args[1]) != null)
                {
                    if (args.length == 3)
                        return StringUtil.copyPartialMatches(args[2], GAME_CHOICES, new ArrayList<>());

                    if (args[2].equalsIgnoreCase("view"))
                        if (args.length == 4)
                            return StringUtil.copyPartialMatches(args[3], ImmutableList.of("true", "false"), new ArrayList<>());
                }
            } else if (args[0].equalsIgnoreCase("players"))
            {
                if (args.length == 2)
                    return StringUtil.copyPartialMatches(args[1], getOnlinePlayer(), new ArrayList<>());

                if (getPlayer(args[1]) != null)
                {
                    if (args.length == 3)
                        return StringUtil.copyPartialMatches(args[2], PLAYERS_CHOICES, new ArrayList<>());

                    if (args[2].equalsIgnoreCase("join"))
                        if (args.length == 4)
                            return StringUtil.copyPartialMatches(args[3], getCurrentGames(), new ArrayList<>());
                }
            }
        }

        return ImmutableList.of();
    }

    private List<String> getCurrentGames()
    {
        List<String> list = new ArrayList<>();
        for (Game game : Quake.gameManager.getGameLinkedList())
            if (game != null && game.getName() != null)
                list.add(game.getName());

        return list;
    }

    private List<String> getOnlinePlayer()
    {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
            if (player != null && player.isOnline())
                list.add(player.getName());

        return list;
    }

    private Player getPlayer(String name)
    {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player != null && player.isOnline() && player.getName().equals(name))
                return player;
        return null;
    }
}
