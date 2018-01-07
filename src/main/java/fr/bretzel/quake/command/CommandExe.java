package fr.bretzel.quake.command;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.language.Language;
import org.bukkit.command.CommandExecutor;

/**
 * Created by mrbretzel on 19/07/15.
 */
public abstract class CommandExe implements CommandExecutor {

    public String getI18n(String key) {
        return Language.defaultLanguage.get(key);
    }
}
