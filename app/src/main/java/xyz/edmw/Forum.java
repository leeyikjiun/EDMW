package xyz.edmw;

import java.util.List;

import xyz.edmw.topic.Topic;

public class Forum {
    public static final Forum edmw = new Builder()
            .title("EDMW")
            .path("main-forum")
            .pageNum(1)
            .hasNextPage(false)
            .build();
    public static final Forum nsfw = new Builder()
            .title("NSFW")
            .path("main-forum/nsfw")
            .pageNum(1)
            .hasNextPage(false)
            .build();
    public static final Forum metaphysics = new Builder()
            .title("Metaphysics")
            .path("metaphysics")
            .pageNum(1)
            .hasNextPage(false)
            .build();
    public static final Forum feedback = new Builder()
            .title("Feedback")
            .path("feedback")
            .pageNum(1)
            .hasNextPage(false)
            .build();

    private String title;
    private String path;
    private int pageNum;
    private boolean hasNextPage;
    private List<Topic> topics;

    public void clear() {
        pageNum = 1;
        hasNextPage = false;
        if (topics != null) {
            topics.clear();
        }
    }

    private Forum() {

    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }


    public int getPageNum() {
        return pageNum;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public static class Builder {
        private String title;
        private String path;
        private int pageNum;
        private boolean hasNextPage;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder pageNum(int pageNum) {
            this.pageNum = pageNum;
            return this;
        }

        public Builder hasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            return this;
        }

        public Forum build() {
            Forum forum = new Forum();
            forum.title = title;
            forum.path = path;
            forum.pageNum = pageNum;
            forum.hasNextPage = hasNextPage;
            return forum;
        }
    }
}
