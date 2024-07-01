package org.aut.polylinked_client.utils;

import org.aut.polylinked_client.model.JsonSerializable;
import org.aut.polylinked_client.model.MediaLinked;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.exceptions.NotAcceptableException;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class RequestBuilder {
    private static final String SERVER_ADDRESS = "http://localhost:8080/";

    private RequestBuilder() {
    }

    public static HttpURLConnection buildConnection
            (String method, String endPoint, JSONObject headers, boolean doOutput) throws IOException {

        URL url = URI.create(SERVER_ADDRESS + endPoint).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        if (headers != null) headers.toMap().forEach((k, v) -> con.setRequestProperty(k, v.toString()));
        con.setDoOutput(doOutput);
        return con;
    }

    public static JSONObject jsonFromGetRequest(String endPoint, JSONObject headers) throws UnauthorizedException {
        try {
            HttpURLConnection con = buildConnection("GET", endPoint, headers, false);
            if (con.getResponseCode() / 100 == 2) {
                JSONObject object = JsonHandler.getObject(con.getInputStream());
                con.disconnect();

                return object;
            } else if (con.getResponseCode() == 401) {
                throw new UnauthorizedException("JWT invalid");
            }
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception ignored) {
        }
        return null;
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
                throw new UnauthorizedException("JWT invalid");
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
                throw new UnauthorizedException("JWT invalid");
            }
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception ignored) {
        }
        return map.isEmpty() ? null : map;
    }

    public static void sendMediaLinkedRequest(String method, String endPoint, JSONObject headers, MediaLinked mediaLinked, File file) throws NotAcceptableException, UnauthorizedException {
        try {
            HttpURLConnection con = buildConnection(method, endPoint, headers, true);
            OutputStream os = con.getOutputStream();
            MultipartHandler.writeObject(os, mediaLinked);
            MultipartHandler.writeFromFile(os, file);
            os.close();

            if (con.getResponseCode() == 401) {
                throw new UnauthorizedException("JWT invalid");
            } else if (con.getResponseCode() / 100 != 2) {
                throw new NotAcceptableException("Unknown");
            }
        } catch (IOException e) {
            throw new NotAcceptableException("Unknown");
        }

    }
}
