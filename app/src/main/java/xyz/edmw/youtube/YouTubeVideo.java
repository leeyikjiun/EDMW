package xyz.edmw.youtube;

public class YouTubeVideo {
    private final String title;
    private final String thumbnail;

    public YouTubeVideo(String title, String thumbnail) {
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
