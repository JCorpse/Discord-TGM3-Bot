package idv.sd.api.google.constant;

import java.util.EnumSet;
import java.util.Iterator;

public enum ThreatType {

    MALWARE("MALWARE", "惡意軟體"),

    SOCIAL_ENGINEERING("SOCIAL_ENGINEERING", "社交工程(釣魚)"),

    UNWANTED_SOFTWARE("UNWANTED_SOFTWARE", "流氓軟體"),
    POTENTIALLY_HARMFUL_APPLICATION("POTENTIALLY_HARMFUL_APPLICATION", "潛在有害的應用程序");

    private final String en;
    private final String cn;

    ThreatType(String en, String cn) {
        this.en = en;
        this.cn = cn;
    }

    public String getEn() {
        return en;
    }

    public String getCn() {
        return cn;
    }

    public static String CheckType(String en) {
        EnumSet<ThreatType> enumSet = EnumSet.allOf(ThreatType.class);
        Iterator<ThreatType> enumSetI = enumSet.iterator();
        while (enumSetI.hasNext()) {
            ThreatType type = enumSetI.next();
            if (en.equalsIgnoreCase(type.en)) {
                return type.getCn();
            }
        }
        return "未知";
    }
}
