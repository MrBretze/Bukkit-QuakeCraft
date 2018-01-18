package fr.bretzel.quake.task;


import fr.bretzel.quake.PlayerInfo;

public abstract  class SchootTask implements Runnable {

    private PlayerInfo info;

    public SchootTask(PlayerInfo info) {
        this.info = info;
    }

    public PlayerInfo getInfo() {
        return info;
    }
}
