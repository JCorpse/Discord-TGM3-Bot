package idv.sd.api.google.constant;

public enum GoogleConstant {

    Safe_Browsing("AIzaSyAZGBUSYFAOreE0vt55XrwySCpOrDn5yjw");

    private final String Key;

    private GoogleConstant(String Key) {
        this.Key = Key;
    }

    public String getKey() {
        return this.Key;
    }
}
