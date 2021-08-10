package idv.sd.api.google.safe.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class safeO {
    private static final String ClientID = "DC_Bot";
    private static final String Client_Ver = "0.4";
    private static HashMap<String, HashMap> Root = new HashMap<>();

    static {
        HashMap<String, String> clientMap = new HashMap<String, String>();
        clientMap.put("clientId", ClientID);
        clientMap.put("clientVersion", Client_Ver);
        Root.put("client", clientMap);
        HashMap threatInfoMap = new HashMap<>();
        List<String> threatTypes = new ArrayList<String>();
        threatTypes.add("MALWARE");
        threatTypes.add("SOCIAL_ENGINEERING");
        threatInfoMap.put("threatTypes", threatTypes);

        List<String> platformTypes = new ArrayList<String>();
        platformTypes.add("WINDOWS");
        platformTypes.add("ANDROID");
        platformTypes.add("IOS");
        threatInfoMap.put("platformTypes", platformTypes);

        List<String> threatEntryTypes = new ArrayList<String>();
        threatEntryTypes.add("URL");
        threatInfoMap.put("threatEntryTypes", threatEntryTypes);

        List<HashMap<String, String>> threatEntries = new ArrayList<HashMap<String, String>>();
        threatInfoMap.put("threatEntries", threatEntries);
        Root.put("threatInfo", threatInfoMap);
    }

    public static void setURL(String URL) {
        List<HashMap<String, String>> threatEntries = (List<HashMap<String, String>>) Root.get("threatInfo").get("threatEntries");
        if( !threatEntries.isEmpty()){
            threatEntries.clear();
        }
        HashMap<String, String> UrlMap = new HashMap<String, String>();
        UrlMap.put("url", URL);
        threatEntries.add(UrlMap);
    }

    public static HashMap<String, HashMap> getRoot() {
        return Root;
    }

    public String getClientID() {
        return ClientID;
    }

    public String getClient_Ver() {
        return Client_Ver;
    }
}
