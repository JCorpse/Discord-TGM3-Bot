package idv.sd.api.gov.cwb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.sd.api.google.safe.json.safeO;
import idv.sd.api.gov.constant.GovConstant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Cwd_Earthquake {
    private  static final String API_KEY = GovConstant.CWD_KEY.getKey();

    public static void main(String[] args) {
        Cwd_Earthquake a = new Cwd_Earthquake();
        a.test();
    }

    private void test() {
        getEarthquakeReport();
    }

    public static String getEarthquakeReport() {
        String Report = "";
        String ApiUrl = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/E-A0015-001?Authorization=" + API_KEY + "&limit=1&format=JSON&areaName=";
        try {
            HttpClient Client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(30000)).build();

            HttpRequest Req = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(ApiUrl))
                    .header("content-Type", "application/json")
                    .build();

            HttpResponse<String> Res = Client.send(Req, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode ResJSON = mapper.readTree(Res.body());
            if (!ResJSON.isEmpty()) {
                Report += "==== " + ResJSON.with("records").get("datasetDescription").asText() + "(v0.1) ====\n";
                Report += ResJSON.with("records").get("earthquake").get(0).get("reportContent").asText() + "\n";
                Report += ResJSON.with("records").get("earthquake").get(0).get("reportRemark").asText() + "\n";
                Report += ResJSON.with("records").get("earthquake").get(0).get("reportImageURI").asText() + "\n";
                Report += ResJSON.with("records").get("earthquake").get(0).get("earthquakeInfo").get("originTime").asText() + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Report;
    }
}
