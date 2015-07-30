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
