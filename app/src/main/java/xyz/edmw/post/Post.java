package xyz.edmw.post;

public class Post {
    private String author;
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

    public static class Builder {
        private String author;
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

        public Post build() {
            Post post = new Post();
            post.author = author;
            post.timestamp = timestamp;
            post.postNum = postNum;
            post.message = message;
            return post;
        }
    }
}
