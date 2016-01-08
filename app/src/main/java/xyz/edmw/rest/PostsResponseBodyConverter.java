package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Converter;
import xyz.edmw.post.Post;

public class PostsResponseBodyConverter implements Converter<ResponseBody, List<Post>> {
    @Override
    public List<Post> convert(ResponseBody responseBody) throws IOException {
        String html = responseBody.string();
        return getPosts(html);
    }

    public List<Post> getPosts(String html) {
        Document doc = Jsoup.parse(html);
        Element threadViewTab = doc.getElementById("thread-view-tab");
        Elements rows = threadViewTab.select("li.b-post");
        List<Post> posts = new ArrayList<>(rows.size());
        for (Element row : rows) {
            String author = row.select("div.author a").first().text().trim();
            String timestamp = row.select("div.b-post__timestamp").text().trim();
            String postNum = row.select("a.b-post__count").first().text().trim();
            String message = row.select("div.js-post__content-text").first().html();
            posts.add(new Post.Builder()
                    .author(author)
                    .timestamp(timestamp)
                    .postNum(postNum)
                    .message(message)
                    .build());
        }
        return posts;
    }
}
