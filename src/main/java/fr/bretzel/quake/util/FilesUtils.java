package fr.bretzel.quake.util;

import java.io.File;
import java.io.IOException;

public class FilesUtils {

    public static void createFile(File file) {
        if (file.exists())
            return;

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Creating new fine: " + file.toString());
        }
    }

    public static void createDirectory(File file) {
        if (file.exists())
            return;

        boolean b = file.mkdir();
        if (b)
            System.out.println("Creating new directory: " + file.toString());
        else
            System.out.println("Failed to create new directory: " + file.toString());
    }
}
