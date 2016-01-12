package xyz.edmw.thread;

import java.io.Serializable;

public class Thread implements Serializable{
    private final String title;
    private final String path;
    private final String startedBy;
    private final String lastPost;
    private final String threadstarterAvatar;
    private final Boolean isSticky;

    public Thread(String title, String path, String startedBy, String lastPost, String threadstarterAvatar, Boolean isSticky) {
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

    public Boolean getIsSticky() {
        return isSticky;
    }
}
