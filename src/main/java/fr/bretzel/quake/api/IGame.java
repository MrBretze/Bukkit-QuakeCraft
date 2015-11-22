package fr.bretzel.quake.api;

import fr.bretzel.quake.Config;
import fr.bretzel.quake.Map;

/**
 * Created by MrBretzel on 13/11/15.
 */
public interface IGame {

    Map getMap();
    
    Config getConfig();
}
