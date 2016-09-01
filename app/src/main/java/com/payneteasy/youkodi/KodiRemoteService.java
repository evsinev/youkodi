package com.payneteasy.youkodi;

import android.widget.Toast;

import com.payneteasy.youkodi.model.MediaInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class KodiRemoteService {

    public String playYoutube(String aUrl) {
        try {
            if(isYoutube(aUrl)) {
                String id = getVideoId(aUrl);
                return doPost("http://192.168.3.122/jsonrpc"
                        , ("{'jsonrpc': '2.0'" +
                                ", 'method': 'Player.Open'" +
                                ", 'params': { 'item': {'file' : 'plugin://plugin.video.youtube/?action=play_video&videoid="+id+"' }}}")
                                .replace("'", "\"")
                );
            } else {
                return doPost("http://192.168.3.122/jsonrpc"
                        , ("{'jsonrpc': '2.0'" +
                                ", 'method': 'Player.Open'" +
                                ", 'id': 'play-on-xbmc'" +
                                ", 'params': { 'item': {'file' : '"+aUrl+"' }}}")
                                .replace("'", "\"")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    private boolean isYoutube(String aUrl) {
        return aUrl.contains("youtu.be") || aUrl.contains("youtube.com");
    }

    private String doPost(String aUrl, String aBody) throws IOException {

        System.out.println("request url: " + aUrl);
        System.out.println("request body: " + aBody);

        HttpURLConnection con = (HttpURLConnection) new URL(aUrl).openConnection();
        try {
            con.setConnectTimeout(30_000);
            con.setReadTimeout(30_000);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            OutputStream out = con.getOutputStream();
            try {
                out.write(aBody.getBytes(StandardCharsets.UTF_8));
                out.flush();

                InputStream in = con.getInputStream();
                try {
                    return con.getResponseCode() + " " +readText(in);
                } finally {
                    in.close();
                }

            } finally {
                out.close();
            }
        } finally {
            con.disconnect();
        }
    }

    private String doGet(String aUrl) throws IOException {

        HttpURLConnection con = (HttpURLConnection) new URL(aUrl).openConnection();
        try {
            con.setConnectTimeout(30_000);
            con.setReadTimeout(30_000);
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

                InputStream in = con.getInputStream();
                try {
                    return readText(in);
                } finally {
                    in.close();
                }
        } finally {
            con.disconnect();
        }
    }

    private String readText(InputStream aInputStream) throws IOException {
        LineNumberReader in = new LineNumberReader(new InputStreamReader(aInputStream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;

        while (  ( line = in.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    public String getVideoId(String aUrl) {
        int pos = aUrl.lastIndexOf('/');
        return aUrl.substring(pos + 1);
    }

    public MediaInfo getMediaInfo(String aUrl) {
        try {
            String response = doGet("http://noembed.com/embed?url="+aUrl);
            System.out.println("response is "+ response);

            JSONObject json = new JSONObject(response);
            String title =  json.getString("title");
            String imageUrl = json.getString("thumbnail_url");

            MediaInfo info = new MediaInfo(title);
            info.imageUrl = imageUrl;
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            return new MediaInfo(e.getMessage());
        }
    }
}
