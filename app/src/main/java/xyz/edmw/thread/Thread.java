package xyz.edmw.thread;

public class Thread {
    private String title;
    private  String path;
    private  String startedBy;
    private  String lastPost;
    private  String threadstarterAvatar;
    private  boolean isSticky;
    private int numPages;

    private Thread() {

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

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public static class Builder {
        private  String title;
        private String path;
        private  String startedBy;
        private  String lastPost;
        private  String threadstarterAvatar;
        private  boolean isSticky;
        private int numPages;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder startedBy(String startedBy) {
            this.startedBy = startedBy;
            return this;
        }

        public Builder lastPost(String lastPost) {
            this.lastPost = lastPost;
            return this;
        }

        public Builder threadstarterAvatar(String threadstarterAvatar) {
            this.threadstarterAvatar = threadstarterAvatar;
            return this;
        }

        public Builder isSticky(boolean isSticky) {
            this.isSticky = isSticky;
            return this;
        }

        public Builder numPages(int numPages) {
            this.numPages = numPages;
            return this;
        }

        public Thread build() {
            Thread thread = new Thread();
            thread.title = title;
            thread.path = path;
            thread.startedBy = startedBy;
            thread.lastPost = lastPost;
            thread.threadstarterAvatar = threadstarterAvatar;
            thread.isSticky = isSticky;
            thread.numPages = numPages;
            return thread;
        }
    }
}
