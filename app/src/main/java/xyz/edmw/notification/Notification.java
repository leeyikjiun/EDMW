package xyz.edmw.notification;

import xyz.edmw.User;

public class Notification {
    private User user;
    private String title;
    private String path;
    private String postDate;
    private String type;

    public User getUser() {
        return user;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public static class Builder {
        private User user;
        private String title;
        private String path;
        private String postDate;
        private String type;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder postDate(String postDate) {
            this.postDate = postDate;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Notification build() {
            Notification notification = new Notification();
            notification.user = user;
            notification.title = title;
            notification.path = path;
            notification.postDate = postDate;
            notification.type = type;
            return notification;
        }
    }
}
