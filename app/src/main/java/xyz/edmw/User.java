package xyz.edmw;

public class User {
    private String name;
    private String avatar;
    private String profile;
    private String recentPosts;
    private String messages;

    private User() {

    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRecentPosts() {
        return recentPosts;
    }

    public String getMessages() {
        return messages;
    }

    public static class Builder {
        private String name;
        private String avatar;
        private String profile;
        private String recentPosts;
        private String messages;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder recentPosts(String recentPosts) {
            this.recentPosts = recentPosts;
            return this;
        }

        public Builder messages(String messages) {
            this.messages = messages;
            return this;
        }

        public User build() {
            User user = new User();
            user.name = name;
            user.avatar = avatar;
            user.profile = profile;
            user.recentPosts = recentPosts;
            user.messages = messages;
            return user;
        }
    }
}
