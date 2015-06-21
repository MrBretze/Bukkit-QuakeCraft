package fr.bretzel.quake;

import fr.bretzel.quake.player.PlayerInfo;

/**
 * Created by MrBretzel on 21/06/2015.
 */

public abstract  class SchootTask implements Runnable {

    private PlayerInfo info;

    public SchootTask(PlayerInfo info) {
        this.info = info;
    }

    public PlayerInfo getInfo() {
        return info;
    }
}
