package xyz.edmw.post;

import java.util.Collections;
import java.util.List;

public class Post {
    private String id;
    private String author;
    private String authorAvatar;
    private String userTitle;
    private String timestamp;
    private String postNum;
    private String message;
    private String path;
    private List<String> photos;

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

    public List<String> getPhotos() {
        return photos == null ? Collections.<String>emptyList() : photos;
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

    public String getPath() {
        return path;
    }

    public static class Builder {
        private String id;
        private String author;
        private String authorAvatar;
        private String userTitle;
        private String timestamp;
        private String postNum;
        private String message;
        private String path;
        private List<String> photos;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

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

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder photos(List<String> photos) {
            this.photos = photos;
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
            post.path = path;
            post.photos = photos;
            return post;
        }
    }
}