package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.topic.RecentPosts;
import xyz.edmw.topic.Topic;

public class RecentPostsResponseBodyConverter implements Converter<ResponseBody, RecentPosts> {
    @Override
    public RecentPosts convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getRecentPosts(html);
    }

    private RecentPosts getRecentPosts(String html) {
        Document doc = Jsoup.parse(html);
        Element topicList = doc.select("tbody.topic-list").first();

        RecentPosts recentPosts = new RecentPosts();
        for (Element topicItem : topicList.select("tr.topic-item")) {
            Topic topic = Topic.Builder.from(topicItem).build();
            recentPosts.add(topic);
        }
        return recentPosts;
    }
}
