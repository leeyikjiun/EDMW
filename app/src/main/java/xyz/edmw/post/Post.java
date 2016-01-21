package xyz.edmw.post;

public class Post {
    private String id;
    private String author;
    private String authorAvatar;
    private String userTitle;
    private String timestamp;
    private String postNum;
    private String message;

    private Post() {

    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPostNum() {
        return postNum;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return id.equals(post.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public static class Builder {
        private String id;
        private String author;
        private String authorAvatar;
        private String userTitle;
        private String timestamp;
        private String postNum;
        private String message;

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder postNum(String postNUm) {
            this.postNum = postNUm;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder userTitle(String userTitle) {
            this.userTitle = userTitle;
            return this;
        }

        public Builder authorAvatar(String authorAvatar) {
            this.authorAvatar = authorAvatar;
            return this;
        }

        public Post build() {
            Post post = new Post();
            post.id = id;
            post.author = author;
            post.timestamp = timestamp;
            post.postNum = postNum;
            post.message = message;
            post.userTitle = userTitle;
            post.authorAvatar = authorAvatar;
            return post;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }
    }
}