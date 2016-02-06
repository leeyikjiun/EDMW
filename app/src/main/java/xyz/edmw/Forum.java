package xyz.edmw;

import java.util.ArrayList;
import java.util.Collections;
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
    public static final Forum music_entertainment = new Builder()
            .title("Entertainment")
            .path("music-entertainment")
            .pageNum(1)
            .hasNextPage(false)
            .build();
    public static final Forum sex_and_love = new Builder()
            .title("Sex & Love")
            .path("sex-love-sponsored-by-www-alicemaple-com")
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
    private int pageNum = 1;
    private boolean hasNextPage;
    private List<Topic> topics;
    private User user;

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

    public void addTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<>();
        }
        topics.add(topic);
    }

    public List<Topic> getTopics() {
        return topics == null ? Collections.<Topic>emptyList() : topics;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void hasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
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
