package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.post.Post;
import xyz.edmw.post.PostActivity;
import xyz.edmw.thread.Thread;

public class ThreadResponseBodyConverter implements Converter<ResponseBody, Thread> {
    @Override
    public Thread convert(ResponseBody responseBody) throws IOException {
        String html = responseBody.string();
        return getThread(html);
    }

    public Thread getThread(String html) {
        Document doc = Jsoup.parse(html);
        Element threadViewTab = doc.getElementById("thread-view-tab");
        Thread.Builder builder = new Thread.Builder();

        Element form = doc.select("div.b-content-entry").first();
        if (form != null) {
            String securityToken = form.select("input[name=securitytoken]").first().val();
            String channelId = form.select("input[name=channelid]").first().val();
            String parentId = form.select("input[name=parentid]").first().val();

            builder = builder.securityToken(securityToken)
                    .channelId(Integer.parseInt(channelId))
                    .parentId(Integer.parseInt(parentId));
        }
        Thread thread = builder.build();

        Elements rows = threadViewTab.select("li.b-post");
        for (Element row : rows) {
            String author = row.select("div.author a").first().text().trim();
            String timestamp = row.select("div.b-post__timestamp").text().trim();
            String postNum = row.select("a.b-post__count").first().text().trim();
            String message = row.select("div.js-post__content-text").first().html();
            String authorAvatar = row.select("a.b-avatar").first().getElementsByTag("img").attr("src");
            String userTitle = row.select("div.usertitle").first().text().trim();
            String id = row.attr("data-node-id");

            Post post = new Post.Builder()
                    .author(author)
                    .timestamp(timestamp)
                    .postNum(postNum)
                    .userTitle(userTitle)
                    .authorAvatar(authorAvatar)
                    .message(message)
                    .build();
            thread.addPost(post);
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
        return thread;
    }
}
