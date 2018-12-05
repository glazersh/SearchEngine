package Model.IO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class DBCountries {

    private HashMap<String, Countries> DBCountry;
    public DBCountries(String WebServiceURL) throws IOException {

        this.DBCountry = new HashMap<>();
        ReadCountries request;
        request = new ReadCountries();
        JSONObject jsonDetails = request.post(WebServiceURL);
        JSONArray result = jsonDetails.getJSONArray("result");

        for (Object obj: result){
            JSONObject data = (JSONObject)obj;
            Countries country = new Countries(data);
            this.DBCountry.put(country.getCapitalName().toUpperCase(),country);
        }
    }


    public Countries getCountryByCapital(String capitalName) {
        return this.DBCountry.get(capitalName);
    }

    public void resetAll() {
        DBCountry.clear();


    }
}
