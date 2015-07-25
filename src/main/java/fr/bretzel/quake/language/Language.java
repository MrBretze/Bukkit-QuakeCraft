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


import fr.bretzel.json.JSONArray;
import fr.bretzel.json.JSONObject;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by MrBretzel on 09/07/2015.
 */

public class Language {

    private Locale locale = Locale.getDefault();
    private JSONObject object;
    private static HashMap<String, String> maps = new HashMap<>();

    public Language(Locale locale) {
        this.locale = locale;

        object = new JSONObject(fileToJson(getClass().getResourceAsStream("/lang/" + locale.getCountry() + "_" + locale.getLanguage() + ".json")));

        for(String keys : object.keySet()) {
            Object value = object.get(keys);
            if(value instanceof String) {
                String v = (String) value;
                if(v.contains("§")) {
                    v = ChatColor.translateAlternateColorCodes('§', v);
                } else if(v.contains("&")) {
                    v = ChatColor.translateAlternateColorCodes('&', v);
                }
                if(!maps.containsKey(keys)) {
                    maps.put(keys, v);
                } else {
                    throw new AllReadyRegisterdException("The language is already registered !");
                }
            } else if(value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                Iterator<Object> iterator = array.iterator();
                while (iterator.hasNext()) {
                    Object v = iterator.hasNext();
                    if(v instanceof JSONObject) {
                        JSONObject ob2 = (JSONObject) v;
                        //TODO: car il se fait tard !
                    }
                }
            }
        }
    }

    public static String get(String keys) {
        return maps.get(keys);
    }

    public Locale getLocale() {
        return locale;
    }

    public static String fileToJson(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
