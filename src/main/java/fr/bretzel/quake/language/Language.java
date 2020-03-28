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
import fr.bretzel.quake.Quake;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by MrBretzel on 09/07/2015.
 */
public class Language
{

    private static final HashMap<String, String> maps = new HashMap<>();
    private JSONObject object;

    public Language(Locale locale)
    {
        maps.clear();
        //object = new JSONObject(fileToJson(getClass().getResourceAsStream("/lang/" + locale.getLanguage() + "_" + locale.getCountry() + ".json")));
        try
        {
            object = new JSONObject(fileToJson(new File(Quake.quake.getDataFolder() + "/lang/", locale.getLanguage() + "_" + locale.getCountry() + ".json")));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        addJsonObject(object, "");
    }


    private void addJsonArray(JSONArray array, String key)
    {
        Iterator<Object> o = array.iterator();
        while (o.hasNext())
        {
            Object object = o.next();
            if (object instanceof JSONObject)
            {
                JSONObject jsonObject = (JSONObject) object;
                for (String k : jsonObject.keySet())
                {
                    Object obj = jsonObject.get(k);
                    if (obj instanceof JSONArray)
                    {
                        JSONArray a = (JSONArray) obj;
                        addJsonArray(a, key + k + ".");

                    } else if (obj instanceof JSONObject)
                    {
                        JSONObject t = (JSONObject) obj;
                        addJsonObject(t, k + ".");
                    } else if (obj instanceof String)
                    {
                        String s = (String) obj;
                        add(formatizeKey(key + k + "."), s);
                    }
                }
            }
        }
    }

    private void addJsonObject(JSONObject jsonObject, String key)
    {
        for (String k : jsonObject.keySet())
        {
            Object o = jsonObject.get(k);
            if (o instanceof JSONArray)
            {
                JSONArray a = (JSONArray) o;
                addJsonArray(a, key + "." + k + ".");
            } else if (o instanceof JSONObject)
            {
                JSONObject o2 = (JSONObject) o;
                addJsonObject(o2, key + "." + k + ".");
            } else if (o instanceof String)
            {
                String s = (String) o;
                add(formatizeKey(key + "." + k + "."), s);
            }
        }
    }

    private String formatizeKey(String key)
    {
        if (key.startsWith("."))
        {
            key = key.replaceFirst(".", "");
        }
        if (key.endsWith(".") && key.length() > 0)
        {
            key = key.substring(0, key.length() - 1);
        }
        return key.replace("..", ".").trim();
    }

    private void add(String key, String value)
    {
        if (!maps.containsKey(key))
        {
            maps.put(key, ChatColor.translateAlternateColorCodes('&', value));
        } else
        {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The language {Key:" + key + ", Value:" + maps.get(key) + "} is already registered !");
        }
    }

    private String fileToJson(File file) throws IOException
    {
        return new String(Files.readAllBytes(file.toPath()));
    }

    public boolean hasKey(String key)
    {
        return maps.containsKey(key);
    }

    public String get(String key)
    {
        if (maps.containsKey(key))
            return maps.get(key);
        return key;
    }
}
