package fr.bretzel.quake.language;

import com.google.common.base.Strings;
import fr.bretzel.quake.Quake;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashMap;

/**
 * Created by MrBretzel on 25/03/2017.
 */
public class Language {

    public HashMap<String, String> stringToComponent = new HashMap<>();
    private Locale locale = Locale.EN;

    public static Language defaultLanguage;

    private static HashMap<Locale, Language> localeToLanguage = new HashMap<>();

    public Language(Locale locale) {
        this.locale = locale;
    }

    public boolean has(String k) {
        if (stringToComponent.containsKey(k))
            return true;
        if (defaultLanguage == null)
            return false;
        return defaultLanguage.stringToComponent.containsKey(k);
    }

    public String get(String k) {
        if (has(k))
            return stringToComponent.get(k);
        else
            return defaultLanguage.get(k);
    }

    /**
     * This method return to language is local is null return to the default language !
     *
     * @param locale
     * @return a language
     */
    public static Language getLanguage(Locale locale) {
        if (localeToLanguage.containsKey(locale))
            return localeToLanguage.get(locale);
        return defaultLanguage;
    }

    public String getLanguageName() {
        return locale.name();
    }

    public static void enable() throws IOException {
        for (Locale locale : Locale.values()) {
            Language language = new Language(locale);
            File path = new File(Quake.quake.getDataFolder() + "/lang/" + locale.name().toLowerCase() + "_" + locale.name() + ".lang".trim());
            if (path.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String line;

                while ((line = reader.readLine()) != null) {

                    if (Strings.isNullOrEmpty(line) || line.charAt(0) == '#') {
                        continue;
                    }

                    String[] args = line.split("=");
                    String k = args[0];
                    TextType type = TextType.fromString(args[1]);
                    String value = args[2];

                    if (type == TextType.JSON) {
                        if (language.has(k)) {
                            Bukkit.getLogger().info("The key: " + k + " is already init !");
                        } else {
                            language.stringToComponent.put(k, value);
                        }
                    } else if (type == TextType.TXT) {
                        if (language.has(k)) {
                            Bukkit.getLogger().info("The key: " + k + " is already init !");
                        } else {
                            language.stringToComponent.put(k, simpleJson(value));
                        }
                    }
                }
                reader.close();
                localeToLanguage.put(locale, language);

            }
        }

        Language.defaultLanguage = Language.getLanguage(Locale.EN);

        Bukkit.broadcastMessage(defaultLanguage.getLanguageName());
    }

    private static String simpleJson(String str) {
        return "[\"\",{\"text\":\"" + str +"\"}]";
    }

    public enum Locale {
        FR,
        EN;

        public static Locale fromString(String s) {
            switch (s.trim()) {
                case "EN":
                case "en":
                case "en_EN":
                    return Locale.EN;
                case "FR":
                case "fr":
                case "fr_FR":
                    return Locale.FR;
                default:
                    return Locale.EN;
            }
        }
    }
}
