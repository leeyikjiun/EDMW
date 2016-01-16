package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.post.Post;
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

        Element form = doc.select("form[data-message-type=reply]").first();
        if (form != null) {
            String securityToken = form.select("input[name=securitytoken]").first().val();
            String channelId = form.select("input[name=channelid]").first().val();
            String parentId = form.select("input[name=parentid]").first().val();

            builder = builder
                    .securityToken(securityToken)
                    .channelId(Integer.parseInt(channelId))
                    .parentId(Integer.parseInt(parentId));
        }

        Element primaryPage = threadViewTab.select("a.primary.page").first();
        if (primaryPage != null) {
            int pageNum = Integer.parseInt(primaryPage.text().trim());
            boolean hasNextPage = !threadViewTab.select("a.js-pagenav-next-button").first().attr("data-page").equals("0");
            builder = builder
                    .pageNum(pageNum)
                    .hasNextPage(hasNextPage);
        }

        String title = doc.select("h1.main-title").first().text().trim();
        String path = doc.head().select("link[rel=canonical]").attr("href");
        path = path.substring(RestClient.baseUrl.length());
        Thread thread = builder
                .title(title)
                .path(path)
                .build();

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
        return thread;
    }
}
