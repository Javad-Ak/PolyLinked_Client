package org.aut.polylinked_client.utils;

import org.aut.polylinked_client.SceneManager;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DataAccess {

    public enum FileType {
        IMAGE, VIDEO, AUDIO;
    }

    private static final String[] RESOURCES_PATHS = {"src/main/resources/org/aut/polylinked_client/data",
            "src/main/resources/org/aut/polylinked_client/fxmls", "src/main/resources/org/aut/polylinked_client/images",
            "src/main/resources/org/aut/polylinked_client/styles",
            "src/main/resources/org/aut/polylinked_client/data/cache"};

    private static final Path FILE_PATH = Path.of("src/main/resources/org/aut/polylinked_client/data/data.bin");
    private static final Path CACHE_PATH = Path.of(RESOURCES_PATHS[4]);

    public static final String VIDEO_EXTENSIONS = "mp4, m4v, flv";
    public static final String AUDIO_EXTENSIONS = "mp3, aac, wav, aiff, m4a";
    public static final String IMAGE_EXTENSIONS = "jpg, jpeg, png, gif, bmp";

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

    public static FileType getFileType(File file) {
        if (file == null || !file.getName().contains(".")) return null;

        String ext = file.getName().split("\\.")[1];
        if (IMAGE_EXTENSIONS.contains(ext)) {
            return FileType.IMAGE;
        } else if (VIDEO_EXTENSIONS.contains(ext)) {
            return FileType.VIDEO;
        } else if (AUDIO_EXTENSIONS.contains(ext)) {
            return FileType.AUDIO;
        } else {
            return null;
        }
    }

    public static File getFile(String fileId, String URL) {
        try (Stream<Path> paths = Files.list(CACHE_PATH)) {
            for (Path path : paths.toList()) {
                if (path.toString().contains(fileId)) return path.toFile();
            }
        } catch (IOException ignored) {
        }
        return saveFile(fileId, URL);
    }

    private static File saveFile(String fileId, String URL) {
        try {
            java.net.URL url = URI.create(URL).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() / 100 != 2) return null;
            String extension = con.getHeaderField("Content-Type").split("/")[1];
            File file = new File(CACHE_PATH + "/" + fileId + "." + extension);
            if (!file.exists()) Files.createFile(file.toPath());

            FileOutputStream outputStream = new FileOutputStream(file);
            InputStream inputStream = con.getInputStream();

            byte[] buffer = new byte[1000000];
            int read;
            while ((read = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, read);

            inputStream.close();
            outputStream.close();
            con.disconnect();
            return file;
        } catch (IOException ignored) {
        }
        return null;
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
