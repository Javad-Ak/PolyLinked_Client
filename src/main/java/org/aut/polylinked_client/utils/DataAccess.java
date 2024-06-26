package org.aut.polylinked_client.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataAccess {
    private static final String[] RESOURCES_PATH = {"src/main/resources/org/aut/polylinked_client/data",
            "src/main/resources/org/aut/polylinked_client/fxmls", "src/main/resources/org/aut/polylinked_client/images",
            "src/main/resources/org/aut/polylinked_client/styles"};

    private static final Path FILE_PATH = Path.of("src/main/resources/org/aut/polylinked_client/data/data.bin");

    public enum Theme {
        LIGHT("light"), DARK("dark");

        public final String value;

        Theme(String value) {
            this.value = value;
        }
    }

    private DataAccess() {
    }

    public static void initiate() {
        try {
            for (String folder : RESOURCES_PATH) {
                if (!Files.isDirectory(Path.of(folder)))
                    throw new IOException(folder + " not found");
            }
            if (!Files.isRegularFile(FILE_PATH)) Files.createFile(FILE_PATH);
        } catch (IOException e) {
            System.err.println("Failed to initialize data files: " + e.getMessage());
            System.exit(1);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("theme", Theme.LIGHT.value);
        jsonObject.put("jwt", "null");
        jsonObject.put("privacyState", "null");
        if (FILE_PATH.toFile().length() < 1) writeData(jsonObject);
    }

    public static String getTheme() {
        try {
            return readData().getString("theme");
        } catch (JSONException e) {
            return "null";
        }
    }

    public static String getPrivacyState() {
        try {
            return readData().getString("privacyState");
        } catch (JSONException e) {
            return "null";
        }
    }

    public static String getJWT() {
        try {
            return readData().getString("jwt");
        } catch (JSONException e) {
            return "null";
        }
    }

    public static void setTheme(Theme theme) {
        JSONObject data = readData();
        data.put("theme", theme.value);
        writeData(data);
    }

    public static void setPrivacyState(String privacyState) {
        JSONObject data = readData();
        data.put("privacyState", privacyState);
        writeData(data);
    }

    public static void setJWT(String jwt) {
        JSONObject data = readData();
        data.put("jwt", jwt);
        writeData(data);
    }

    private static void writeData(JSONObject object) {
        try (FileOutputStream outputStream = new FileOutputStream(FILE_PATH.toFile())) {
            outputStream.write(object.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Failed to write data files: " + e);
            System.exit(1);
        }
    }

    private static JSONObject readData() {
        JSONObject obj = null;
        try (FileInputStream inputStream = new FileInputStream(FILE_PATH.toFile())) {
            obj = new JSONObject(new String(inputStream.readAllBytes()));
        } catch (IOException e) {
            System.err.println("Failed to read data files: " + e);
            System.exit(1);
        }
        return obj;
    }
}
