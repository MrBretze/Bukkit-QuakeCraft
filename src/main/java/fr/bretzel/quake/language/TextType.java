package fr.bretzel.quake.language;

public enum TextType {
    JSON,
    TXT;

    public static TextType fromString(String s) {
        switch (s.trim()) {
            case "TXT":
                return TextType.TXT;
            case "JSON":
                return TextType.JSON;
            default:
                return TextType.TXT;
        }
    }
}
