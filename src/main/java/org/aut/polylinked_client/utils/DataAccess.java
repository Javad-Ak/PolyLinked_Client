package org.aut.polylinked_client.utils;

import org.aut.polylinked_client.SceneManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataAccess {
    private static final String[] RESOURCES_PATHS = {"src/main/resources/org/aut/polylinked_client/data",
            "src/main/resources/org/aut/polylinked_client/fxmls", "src/main/resources/org/aut/polylinked_client/images",
            "src/main/resources/org/aut/polylinked_client/styles"};

    private static final Path FILE_PATH = Path.of("src/main/resources/org/aut/polylinked_client/data/data.bin");

    private DataAccess() {
    }

    public static void initiate() {
        try {
            for (String folder : RESOURCES_PATHS) {
                if (!Files.isDirectory(Path.of(folder)))
                    throw new IOException(folder + " not found");
            }
            if (!Files.isRegularFile(FILE_PATH)) Files.createFile(FILE_PATH);
        } catch (IOException e) {
            System.err.println("Failed to initialize data files: " + e.getMessage());
            System.exit(1);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("theme", SceneManager.Theme.LIGHT.value);
        jsonObject.put("jwt", "none");
        jsonObject.put("userId", "none");
        jsonObject.put("fullName", "none");
        if (FILE_PATH.toFile().length() < 1) writeData(jsonObject);
    }

    public static String getTheme() {
        return readData().getString("theme");
    }

    public static String getJWT() {
        return readData().getString("jwt");
    }

    public static String getUserId() {
        return readData().getString("userId");
    }

    public static String getFullName() {
        return readData().getString("fullName");
    }

    public static void setTheme(SceneManager.Theme theme) {
        JSONObject data = readData();
        data.put("theme", theme.value);
        writeData(data);
    }

    public static void setJWT(String jwt) {
        JSONObject data = readData();
        data.put("jwt", jwt);
        writeData(data);
    }

    public static void setUserId(String userId) {
        JSONObject data = readData();
        data.put("userId", userId);
        writeData(data);
    }

    public static void setFullName(String fullName) {
        JSONObject data = readData();
        data.put("fullName", fullName);
        writeData(data);
    }

    private static void writeData(JSONObject object) {
        try (FileOutputStream outputStream = new FileOutputStream(FILE_PATH.toFile(), false)) {
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
