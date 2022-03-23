package idv.sd.api.gov.constant;

public enum GovConstant {

    CWD_KEY("CWB-B36B502D-01C6-4DFC-AE99-845AF60CD88A");

    private final String Key;

    private GovConstant(String Key) {
        this.Key = Key;
    }

    public String getKey() {
        return this.Key;
    }
}
