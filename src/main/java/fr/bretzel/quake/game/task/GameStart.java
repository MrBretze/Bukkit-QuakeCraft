package fr.bretzel.quake.game.task;

import fr.bretzel.quake.GameTask;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.game.event.GameStartEvent;
import fr.bretzel.quake.inventory.BasicGun;
import fr.bretzel.quake.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Created by MrBretzel on 22/06/2015.
 */

public class GameStart extends GameTask {

    public GameStart(JavaPlugin javaPlugin, long l, long l1, Game game) {
        super(javaPlugin, l, l1, game);
    }

    int minSecQuake = getGame().getSecLaunch();

    @Override
    public void run() {
        if (minSecQuake > 0) {
            for (UUID id : getGame().getPlayerList()) {
                Player p = Bukkit.getPlayer(id);
                if (p.isOnline()) {
                    p.sendMessage(ChatColor.AQUA + "The game start in: " + Util.getChatColorByInt(minSecQuake) + String.valueOf(minSecQuake));
                }
            }
            minSecQuake--;
        }
        if (minSecQuake <= 0) {
            GameStartEvent event = new GameStartEvent(getGame());
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) {
                getGame().reset();
                return;
            }
            getGame().setState(State.STARTED);
            Quake.gameManager.signEvent.actualiseJoinSignForGame(getGame());
            for(UUID id : getGame().getPlayerList()) {
                Player p = Bukkit.getPlayer(id);
                if(p.isOnline()) {
                    PlayerInfo info = Quake.getPlayerInfo(p);
                    info.give(new BasicGun(info));
                }
            }
            cancel();
        }
    }
}
