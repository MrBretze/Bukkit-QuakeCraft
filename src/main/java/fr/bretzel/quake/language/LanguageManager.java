/**
 * Copyright 2015 Lo?c Nussbaumer
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

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Loic on 30/07/2015.
 */

public class LanguageManager {

    private Language defaultLanguage = new Language(Locale.US);
    private Language selectedLanguage;
    private Locale defaultLocale = Locale.US;
    private Locale selectedLocale;
    private static HashMap<Locale, Language> maps = new HashMap<>();

    public LanguageManager(Locale locale) {
        setSelectedLanguage(new Language(locale));
        setSelectedLocale(locale);

        maps.put(getDefaultLocale(), getDefaultLanguage());
        maps.put(getSelectedLocale(), getSelectedLanguage());
    }

    public String getI18n(String key) {
        String value;
        if(getSelectedLanguage().hasKey(key)) {
            value = getSelectedLanguage().get(key);
        } else {
            value = getDefaultLanguage().get(key);
        }
        return value;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
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

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setSelectedLocale(Locale selectedLocale) {
        this.selectedLocale = selectedLocale;
    }
}
