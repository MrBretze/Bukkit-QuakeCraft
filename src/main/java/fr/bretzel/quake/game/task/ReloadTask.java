package fr.bretzel.quake.game.task;

import fr.bretzel.quake.SchootTask;
import fr.bretzel.quake.PlayerInfo;

/**
 * Created by MrBretzel on 22/06/2015.
 */
public class ReloadTask extends SchootTask {

    public ReloadTask(PlayerInfo info) {
        super(info);
    }

    @Override
    public void run() {
        getInfo().setShoot(true);
    }
}
