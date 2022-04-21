package idv.sd.api.gov.constant;

public enum GovConstant {

    CWD_KEY(System.getenv("CWD_KEY"));

    private final String Key;

    private GovConstant(String Key) {
        this.Key = Key;
    }

    public String getKey() {
        return this.Key;
    }
}
