/**
 * Copyright 2015 Lo?c Nussbaumer
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake.language;

import fr.bretzel.quake.Quake;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Loic on 30/07/2015.
 */

public class LanguageManager {

    private static HashMap<Locale, Language> maps = new HashMap<>();
    private Language defaultLanguage = new Language(Locale.US);
    private Language selectedLanguage;
    private Locale defaultLocale = Locale.US;
    private Locale selectedLocale;

    public LanguageManager(Locale locale) {
        setSelectedLanguage(new Language(locale));
        setSelectedLocale(locale);

        maps.put(getDefaultLocale(), getDefaultLanguage());
        maps.put(getSelectedLocale(), getSelectedLanguage());
    }

    private String get(String key) {
        if (getSelectedLanguage().hasKey(key)) {
            return getSelectedLanguage().get(key);
        } else {
            return getDefaultLanguage().get(key);
        }
    }

    public String getI18n(String key) {
        if (key.contains("/")) {
            StringBuilder builder = new StringBuilder();
            for (String s : key.split("/"))
                builder.append(s).append(".");
            return builder.toString();
        }
        return get(key);
    }

    public static void init(Quake plugin) {
        CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            try {
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                byte[] buffer = new byte[1024];
                while (true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null)
                        break;
                    String name = e.getName();
                    if (name.startsWith("lang/") && name.endsWith(".json")) {
                        new File(plugin.getDataFolder() + "/lang/").mkdir();
                        File f = new File(plugin.getDataFolder() + "/lang/", name.replace("lang/", ""));
                        if (!f.exists())
                            f.createNewFile();
                        FileOutputStream out = new FileOutputStream(f);

                        int len;
                        while ((len = zip.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                        out.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            plugin.logInfo("ERROR LANGUAGE NOT INITIALED !");
        }
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    public void setSelectedLocale(Locale selectedLocale) {
        this.selectedLocale = selectedLocale;
    }
}
