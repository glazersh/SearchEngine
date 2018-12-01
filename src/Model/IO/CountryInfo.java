package Model.IO;

import org.json.JSONObject;

import java.io.IOException;

public class CountryInfo {


    private String CountryName;
    private String CapitalName;
    private String Population;
    private String Currency;

    public CountryInfo(JSONObject data) throws IOException {

        this.CountryName = data.get("name").toString();
        this.CapitalName = data.get("capital").toString();
        this.Population = data.get("population").toString();
        this.Currency = data.getJSONArray("currencies").getJSONObject(0).get("name").toString();

    }



    public String getCapitalName() {
        return this.CapitalName;
    }
    public String getCountryName() {
        return this.CountryName;
    }
    public String getPopulation() { return this.Population; }
    public String getCurrency() {
        return this.Currency;
    }
}
