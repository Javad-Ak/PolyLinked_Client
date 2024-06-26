package org.aut.polylinked_client.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.*;


public class RequestBuilder {
    private RequestBuilder() {
    }

    public static HttpURLConnection buildConnection
            (String method, String endPoint, JSONObject headers, boolean doOutput) throws IOException {

        URL url = URI.create("http://localhost:8080/" + endPoint).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        headers.toMap().forEach((k, v) -> con.setRequestProperty(k, v.toString())); // not needed
        con.setDoOutput(doOutput);
        return con;
    }
}
