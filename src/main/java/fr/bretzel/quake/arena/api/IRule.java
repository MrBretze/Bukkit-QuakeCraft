package fr.bretzel.quake.arena.api;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public interface IRule {

    String getKey();

    void setKey(String key);

    Object getValue();

    void setValue(Object value);

}
