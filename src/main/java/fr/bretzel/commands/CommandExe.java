package fr.bretzel.commands;

import fr.bretzel.quake.Quake;
import org.bukkit.command.CommandExecutor;

/**
 * Created by mrbretzel on 19/07/15.
 */
public abstract class CommandExe implements CommandExecutor
{


    public String getI18n(String key)
    {
        return Quake.getI18n(key);
    }
}
