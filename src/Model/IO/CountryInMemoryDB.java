package Model.IO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class CountryInMemoryDB {

    private HashMap<String, CountryInfo> CountryDB;
    public CountryInMemoryDB(String WebServiceURL) throws IOException {

        this.CountryDB = new HashMap<>();
        HTTPWebRequest request;
        request = new HTTPWebRequest();
        JSONObject jsonDetails = request.post(WebServiceURL);
        JSONArray result = jsonDetails.getJSONArray("result");

        for (Object obj: result){
            JSONObject data = (JSONObject)obj;
            CountryInfo country = new CountryInfo(data);
            this.CountryDB.put(country.getCapitalName(),country);
        }
    }


    public CountryInfo getCountryByCapital(String capitalName) {
        return this.CountryDB.get(capitalName);
    }
}
