package idv.sd.api.gov.cwb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import idv.sd.api.gov.constant.GovConstant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Cwd_Earthquake {
    private static final String API_KEY = GovConstant.CWD_KEY.getKey();
    private static Random random = new Random();

    public static void main(String[] args) {
        Cwd_Earthquake a = new Cwd_Earthquake();
        a.test();
    }

    private void test() {
        getEarthquakeReport();
    }

    public static EmbedCreateSpec getEarthquakeReport() {
        EmbedCreateSpec Embed = null;
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
                Embed = EmbedCreateSpec.builder()
//                        .color(Color.of(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                        .title(ResJSON.with("records").get("datasetDescription").asText() + "(v0.1)")
                        .description(ResJSON.with("records").get("earthquake").get(0).get("reportContent").asText().replaceAll("[0-9]{2}/[0-9]{2}-[0-9]{2}:[0-9]{2}", ""))
                        .image(ResJSON.with("records").get("earthquake").get(0).get("reportImageURI").asText())
                        .url(ResJSON.with("records").get("earthquake").get(0).get("web").asText())
                        .addField(ResJSON.with("records").get("earthquake").get(0).get("earthquakeInfo").get("magnitude").get("magnitudeType").asText(), ResJSON.with("records").get("earthquake").get(0).get("earthquakeInfo").get("magnitude").get("magnitudeValue").asText(), true)
                        .addField("發生時間", ResJSON.with("records").get("earthquake").get(0).get("earthquakeInfo").get("originTime").asText(), true)
                        .addField("發生位置", ResJSON.with("records").get("earthquake").get(0).get("earthquakeInfo").get("epiCenter").get("location").asText(), true)
                        .timestamp(Instant.now())
                        .build();
//                Report[0] = "==== " +  + " ====\n";
//                Report[0] += ResJSON.with("records").get("earthquake").get(0).get("reportContent").asText() + "\n";
//                Report[0] += ResJSON.with("records").get("earthquake").get(0).get("reportRemark").asText() + "\n";
////                Report[0] += ResJSON.with("records").get("earthquake").get(0).get("reportImageURI").asText() + "\n";
//                Report[1] = ResJSON.with("records").get("earthquake").get(0).get("reportImageURI").asText();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Embed;
    }
}
