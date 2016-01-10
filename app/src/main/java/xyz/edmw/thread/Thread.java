package xyz.edmw.thread;

public class Thread {
    private final String title;
    private final String path;
    private final String startedBy;
    private final String lastPost;
    private final String threadstarterAvatar;
    private final boolean isSticky;
    private int numPages = 15; //TODO

    public Thread(String title, String path, String startedBy, String lastPost, String threadstarterAvatar, boolean isSticky) {
        this.title = title;
        this.path = path;
        this.startedBy = startedBy;
        this.lastPost = lastPost;
        this.threadstarterAvatar = threadstarterAvatar;
        this.isSticky = isSticky;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public String getLastPost() {
        return lastPost;
    }

    public String getThreadstarterAvatar() {
        return threadstarterAvatar;
    }

    public Boolean isSticky() {
        return isSticky;
    }

    public int getNumPages() {
        return numPages;
    }
}
