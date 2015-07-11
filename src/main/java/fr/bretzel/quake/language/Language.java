/**
 * Copyright 2015 Loïc Nussbaumer
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake.language;

import org.bukkit.ChatColor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by MrBretzel on 09/07/2015.
 */

public class Language {

    private Properties properties = new Properties();
    private char[] colors = {'§', '&'};
    private String parm1 = "en";
    private String parm2 = "EN";
    private HashMap<Object, Object> keys = new HashMap<>();

    private File file;

    public Language(File file, String parm1, String parm2) {
        setParm1(parm1);
        setParm2(parm2);
        setFile(file);

        try {
            properties.load(new BufferedInputStream(getClass().getResourceAsStream(("/lang/" + parm1 + "_" + parm2 + ".lang"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Object key : properties.keySet()) {
            Object value = properties.get(key);
            if (value instanceof String) {
                String v = (String) value;
                for (char c : colors) {
                    v = ChatColor.translateAlternateColorCodes(c, v);
                }
                addValue(value, key);
            }
        }
    }

    public HashMap<Object, Object> getKeys() {
        return keys;
    }

    public void setKeys(HashMap<Object, Object> keys) {
        this.keys = keys;
    }

    public void addValue(Object o, Object value) {
        if (getKeys().containsKey(o) && getKeys().get(o).equals(value))
            getKeys().remove(o);
        getKeys().put(o, value);
    }

    public char[] getColors() {
        return colors;
    }

    public void setColors(char[] colors) {
        this.colors = colors;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getParm1() {
        return parm1;
    }

    public void setParm1(String parm1) {
        this.parm1 = parm1;
    }

    public String getParm2() {
        return parm2;
    }

    public void setParm2(String parm2) {
        this.parm2 = parm2;
    }

    public String get(String key) {
        return (String) getKeys().get(key);
    }
}
