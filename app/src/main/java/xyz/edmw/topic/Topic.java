package xyz.edmw.topic;

import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.nodes.Element;

import xyz.edmw.rest.RestClient;

public class Topic implements Parcelable {
    private String id;
    private String title;
    private  String path;
    private  String startedBy;
    private  String lastPost;
    private  String threadstarterAvatar;
    private  boolean isSticky;
    private int numPages;
    private String lastPostPath;

    private Topic() {

    }

    protected Topic(Parcel in) {
        id = in.readString();
        title = in.readString();
        path = in.readString();
        startedBy = in.readString();
        lastPost = in.readString();
        threadstarterAvatar = in.readString();
        isSticky = in.readByte() != 0;
        numPages = in.readInt();
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

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

    public boolean isSticky() {
        return isSticky;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeString(startedBy);
        dest.writeString(lastPost);
        dest.writeString(threadstarterAvatar);
        dest.writeByte((byte) (isSticky ? 1 : 0));
        dest.writeInt(numPages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        return id.equals(topic.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public int getNumPages() {
        return numPages;
    }

    public static class Builder {
        private String id;
        private  String title;
        private String path;
        private  String startedBy;
        private  String lastPost;
        private  String threadstarterAvatar;
        private  boolean isSticky;
        private int numPages;

        public Builder id(String id) {
            this.id = id;
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

        public Topic build() {
            Topic topic = new Topic();
            topic.id = id;
            topic.title = title;
            topic.path = path;
            topic.startedBy = startedBy;
            topic.lastPost = lastPost;
            topic.threadstarterAvatar = threadstarterAvatar;
            topic.isSticky = isSticky;
            topic.numPages = numPages;
            return topic;
        }

        public static Builder from(Element topicItem) {
            int baseUrlLen = RestClient.baseUrl.length();

            String id = topicItem.attr("data-node-id");
            boolean isSticky = topicItem.hasClass("sticky");
            String avatar = topicItem.select("div.topic-avatar img").attr("src").replace("thumb=1", "thumb=0");

            Element anchor = topicItem.select("a.topic-title").first();
            String title = anchor.text().trim();
            String path = anchor.attr("href").substring(baseUrlLen);

            Element cell = topicItem.select("td.cell-lastpost").first();
            String lastPost = cell.text().trim();
            String lastPostPath = cell.select("div.lastpost-by a").attr("href").substring(baseUrlLen);
//            String avatar = cell.select("a.avatar img").attr("src").replace("thumb=1", "thumb=0");
            String startedBy = topicItem.select("div.topic-info").first().text().trim();

            return new Topic.Builder()
                    .id(id)
                    .title(title)
                    .path(path)
                    .lastPost(lastPost)
                    .threadstarterAvatar(avatar)
                    .startedBy(startedBy)
                    .isSticky(isSticky);
        }
    }
}
