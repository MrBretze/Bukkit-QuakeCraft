package fr.bretzel.quake.util;

import fr.bretzel.quake.language.Language;
import org.bukkit.command.CommandExecutor;

public abstract class CommandExe implements CommandExecutor {

    public String getI18n(String key) {
        return Language.defaultLanguage.get(key);
    }
}
