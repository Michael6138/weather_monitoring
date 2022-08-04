package app.business.enums;

public enum JsonKey {

    MAIN("main"),
    TEMP("temp"),
    WIND("wind"),
    SPEED("speed");


    private String key;

    @Override
    public String toString() {
        return getKey();
    }

    public String getKey() {
        return key;
    }

    JsonKey(String key) {
        this.key = key;
    }
}
