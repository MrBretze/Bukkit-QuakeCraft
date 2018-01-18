package fr.bretzel.quake.language;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import fr.bretzel.quake.Quake;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.HashMap;

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
        if (defaultLanguage.getLanguageName().equalsIgnoreCase(getLanguageName())) {
            if (has(k))
                return stringToComponent.get(k);
            else
                return k;
        }
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
        try {
            for (Locale locale : Locale.values()) {
                Language language = new Language(locale);
                String path = "/lang/" + locale.name().toLowerCase() + "_" + locale.name() + ".lang".trim();
                InputStream input = Quake.class.getResourceAsStream(path);

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (Strings.isNullOrEmpty(line) || line.charAt(0) == '#') {
                        continue;
                    }

                    String[] args = line.split("=");
                    String k = args[0];
                    TextType type = TextType.fromString(args[1]);
                    String value = ChatColor.translateAlternateColorCodes('&', args[2]);

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        Language.defaultLanguage = Language.getLanguage(Locale.EN);
    }

    private static String simpleJson(String str) {
        return "[\"\",{\"text\":\"" + str + "\"}]";
    }

    public enum Locale {
        EN("en_EN");

        private String locale;
        private final static HashMap<String, Locale> hashMap = Maps.newHashMap();

        Locale(String locale) {
            this.locale = locale;
        }

        public static Locale fromString(String s) {
            return hashMap.get(s);
        }

        static {
            for (Locale l : values()) {
                hashMap.put(l.locale, l);
            }
        }
    }
}
