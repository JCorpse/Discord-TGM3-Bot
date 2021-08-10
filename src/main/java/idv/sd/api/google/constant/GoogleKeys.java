package idv.sd.api.google.constant;

public enum GoogleKeys {

    Safe_Browsing("AIzaSyAZGBUSYFAOreE0vt55XrwySCpOrDn5yjw");

    private final String Key;

    private GoogleKeys(String Key) {
        this.Key = Key;
    }

    public String getKey() {
        return this.Key;
    }
}
