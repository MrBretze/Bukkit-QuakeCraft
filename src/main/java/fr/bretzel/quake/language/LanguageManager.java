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

import java.util.HashMap;
import java.util.Locale;

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
