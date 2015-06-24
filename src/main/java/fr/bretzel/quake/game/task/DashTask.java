package fr.bretzel.quake.game.task;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.SchootTask;

/**
 * Created by MrBretzel on 24/06/2015.
 */
public class DashTask extends SchootTask {

    public DashTask(PlayerInfo info) {
        super(info);
    }

    @Override
    public void run() {
        getInfo().setDash(true);
    }
}
