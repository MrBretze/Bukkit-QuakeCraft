package fr.bretzel.quake.arena;

import fr.bretzel.quake.arena.api.IRule;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class Rule implements IRule {

    private Object value;
    private String key;

    public Rule(String keys, Object value) {
        setKey(keys);
        setValue(value);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }
}
