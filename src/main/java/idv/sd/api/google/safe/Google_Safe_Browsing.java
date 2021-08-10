package idv.sd.api.google.safe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.sd.api.google.constant.GoogleKeys;
import idv.sd.api.google.constant.ThreatType;
import idv.sd.api.google.safe.json.safeO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Google_Safe_Browsing {
    private static final String API_KEY = GoogleKeys.Safe_Browsing.getKey();

    public static void main(String[] args) {
        Google_Safe_Browsing a = new Google_Safe_Browsing();
        a.test();
    }

    private void test() {
//        isSafe("https://bit.ly/3fq5LVp");
        isSafe("https://www.telerik.com/download/fiddler/fiddler4");
    }


    public static String isSafe(String Url) {
        String result = new String();
        try {
            String ApiUrl = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + API_KEY;
            HttpClient Client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(30000)).build();

            ObjectMapper mapper = new ObjectMapper();
            safeO.setURL(Url);
            String JSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(safeO.getRoot());

            HttpRequest Req = HttpRequest.newBuilder()
                    .uri(URI.create(ApiUrl))
                    .header("content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON))
                    .build();

            HttpResponse<String> Res = Client.send(Req, HttpResponse.BodyHandlers.ofString());

            JsonNode ResJSON = mapper.readTree(Res.body());
            if (!ResJSON.isEmpty()) {
                Iterator<JsonNode> List = ResJSON.withArray("matches").elements();
                ArrayList<String> TypeList = new ArrayList<String>();
                ArrayList<String> OSList = new ArrayList<String>();
                while (List.hasNext()) {
                    JsonNode node = List.next();
                    String Type = ThreatType.CheckType(node.findValue("threatType").asText());
                    String OS = node.findValue("platformType").asText();
                    if (!TypeList.contains(Type)) {
                        TypeList.add(Type);
                    }
                    if (!OSList.contains(OS)) {
                        OSList.add(OS);
                    }
                }
                result = "==== 網址檢測(v0.1) ====\n";
                result += "Url: " + Url.replace("http", "hxxp") + "\n";
                String ThreatTypeStr = new String();
                for (int i = 0; i < TypeList.size(); ++i) {
                    ThreatTypeStr += TypeList.get(i);
                    if (i != TypeList.size() - 1) {
                        ThreatTypeStr += ",";
                    }
                }
                result += "威脅類型: " + ThreatTypeStr + "\n";
                String OSStr = new String();
                for (int i = 0; i < OSList.size(); ++i) {
                    OSStr += OSList.get(i);
                    if (i != OSList.size() - 1) {
                        OSStr += ",";
                    }
                }
                result += "作用OS: " + OSStr + "\n";
                result += "====   By Google   ====";
            } else {
                result = "==== 網址檢測(v0.1) ====\n" +
                        "====        OK       ====\n" +
                        "====   By Google    ====";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

}
