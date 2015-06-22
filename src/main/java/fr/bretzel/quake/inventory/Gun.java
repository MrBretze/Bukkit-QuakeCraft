package fr.bretzel.quake.inventory;

import fr.bretzel.quake.PlayerInfo;

/**
 * Created by MrBretzel on 22/06/2015.
 */

public abstract class Gun implements IGun {

    private PlayerInfo playerInfo;

    public Gun(PlayerInfo info) {
        setPlayerInfo(info);
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
}
