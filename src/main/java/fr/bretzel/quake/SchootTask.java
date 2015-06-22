package fr.bretzel.quake;

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
