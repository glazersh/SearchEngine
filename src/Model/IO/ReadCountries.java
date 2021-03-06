package Model.IO;


import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ReadCountries {

    public JSONObject post(String url) throws IOException {

        URL address = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) address.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        String json ="{\"result\":";
        Scanner scan = new Scanner(address.openStream());
        while (scan.hasNext())
            json += scan.nextLine();
        scan.close();
        json += "}";
        return new JSONObject(json);
    }
}