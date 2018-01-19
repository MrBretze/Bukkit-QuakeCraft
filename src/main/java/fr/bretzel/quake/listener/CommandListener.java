package fr.bretzel.quake.listener;

import fr.bretzel.quake.Quake;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void preCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/reload") || event.getMessage().startsWith("/rl")) {
            Quake.isReload = true;
        }
    }
}
