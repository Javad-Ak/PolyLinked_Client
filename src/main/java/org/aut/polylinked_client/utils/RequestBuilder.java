package org.aut.polylinked_client.utils;

import org.aut.polylinked_client.model.JsonSerializable;
import org.aut.polylinked_client.model.MediaLinked;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class RequestBuilder {
    private static final String SERVER_ADDRESS = "http://localhost:8080/";

    public enum FileType {
        IMAGE, VIDEO, AUDIO;
    }

    private RequestBuilder() {
    }

    public static HttpURLConnection buildConnection
            (String method, String endPoint, JSONObject headers, boolean doOutput) throws IOException {

        URL url = URI.create(SERVER_ADDRESS + endPoint).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        headers.toMap().forEach((k, v) -> con.setRequestProperty(k, v.toString()));
        con.setDoOutput(doOutput);
        return con;
    }

    public static <T extends MediaLinked> TreeMap<T, User> mapFromGetRequest(Class<T> cls, String endPoint, JSONObject headers) throws UnauthorizedException {
        TreeMap<T, User> map = new TreeMap<>();
        try {
            HttpURLConnection con = buildConnection("GET", endPoint, headers, false);
            if (con.getResponseCode() / 100 == 2) {
                int size = Integer.parseInt(con.getHeaderField("X-Total-Count"));
                map = MultipartHandler.readMap(con.getInputStream(), cls, size);
                con.disconnect();
            } else if (con.getResponseCode() == 401) {
                throw new UnauthorizedException("");
            }
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception ignored) {
        }
        return map.isEmpty() ? null : map;
    }

    public static <T extends JsonSerializable> List<T> arrayFromGetRequest(Class<T> cls, String endPoint, JSONObject headers) throws UnauthorizedException {
        List<T> map = new ArrayList<>();
        try {
            HttpURLConnection con = buildConnection("GET", endPoint, headers, false);
            if (con.getResponseCode() / 100 == 2) {
                int size = Integer.parseInt(con.getHeaderField("X-Total-Count"));
                map = MultipartHandler.readObjectArray(con.getInputStream(), cls, size);
                con.disconnect();
            } else if (con.getResponseCode() == 401) {
                throw new UnauthorizedException("");
            }
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception ignored) {
        }
        return map.isEmpty() ? null : map;
    }

    public static FileType fileTypeFromHeadRequest(String fileURL, JSONObject headers) throws UnauthorizedException {
        try {
            String endPoint = fileURL.substring(SERVER_ADDRESS.length());
            HttpURLConnection con = buildConnection("HEAD", endPoint, headers, false);
            if (con.getResponseCode() / 100 == 2) {
                String fileType = con.getHeaderField("Content-Type");
                if (fileType.contains("Image")) {
                    return FileType.IMAGE;
                } else if (fileType.contains("Video")) {
                    return FileType.VIDEO;
                } else if (fileType.contains("Audio")) {
                    return FileType.AUDIO;
                } else {
                    return null;
                }
            } else if (con.getResponseCode() == 401) {
                throw new UnauthorizedException("");
            }
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception ignored) {
        }
        return null;
    }
}
