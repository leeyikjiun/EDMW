package xyz.edmw.settings;

public enum DownloadImage {
    Always, Wifi, Never;

    public static DownloadImage getEnum(String value) {
        switch (value) {
            case "Always":
                return Always;
            case "Only on WiFi":
                return Wifi;
            case "Never":
                return Never;
            default:
                return null;
        }
    }
}
