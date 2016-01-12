package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

import retrofit.Converter;
import xyz.edmw.generic.GenericMap;
import xyz.edmw.post.Post;
import xyz.edmw.post.PostActivity;

public class PostsResponseBodyConverter implements Converter<ResponseBody, GenericMap<Integer, Post>> {
    @Override
    public GenericMap<Integer, Post> convert(ResponseBody responseBody) throws IOException {
        String html = responseBody.string();
        return getPosts(html);
    }

    public GenericMap<Integer, Post> getPosts(String html) {
        Document doc = Jsoup.parse(html);
        Element threadViewTab = doc.getElementById("thread-view-tab");
        Elements rows = threadViewTab.select("li.b-post");

        GenericMap<Integer, Post> postMap = new GenericMap<>();

        for (Element row : rows) {
            String author = row.select("div.author a").first().text().trim();
            String timestamp = row.select("div.b-post__timestamp").text().trim();
            String postNum = row.select("a.b-post__count").first().text().trim();
            String message = row.select("div.js-post__content-text").first().html();
            String authorAvatar = row.select("a.b-avatar").first().getElementsByTag("img").attr("src");
            String userTitle = row.select("div.usertitle").first().text().trim();

            String id = row.attr("data-node-id");

            postMap.put(Integer.parseInt(id), new Post(author, timestamp, postNum, message, authorAvatar, userTitle));

        }
        // Check if there are more to load
        Elements nextElements = doc.getElementsByAttributeValue("rel", "next");
        if (nextElements.size() == 2) {
            Element next = nextElements.last();
            PostActivity.pageNo = Integer.parseInt(next.attr("data-page"));
            PostActivity.hasNextPage = true;
        } else {
            PostActivity.hasNextPage = false;
        }
        return postMap;
    }


}
