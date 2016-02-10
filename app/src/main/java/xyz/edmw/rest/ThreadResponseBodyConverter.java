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
import xyz.edmw.thread.ReplyForm;
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

        Thread thread = getThread(doc);

        Elements rows = threadViewTab.select("li.b-post");
        for (Element row : rows) {
            String author = row.select("div.author a").first().text().trim();
            String timestamp = row.select("div.b-post__timestamp").text().trim();
            Element a = row.select("a.b-post__count").first();
            String postNum = a.text().trim();
            String path = a.attr("href");
            path = path.substring(RestClient.baseUrl.length());
            String message = row.select("div.js-post__content-text").first().html();
            String authorAvatar = row.select("a.b-avatar").first().getElementsByTag("img").attr("src");
            String userTitle = row.select("div.usertitle").first().text().trim();
            String id = row.attr("data-node-id");

            Element footer = row.select("div.b-post__footer").first();
            boolean hasFooter = footer != null;

            Post.Builder postBuilder = new Post.Builder()
                    .id(id)
                    .author(author)
                    .timestamp(timestamp)
                    .postNum(postNum)
                    .userTitle(userTitle)
                    .authorAvatar(authorAvatar)
                    .message(message)
                    .path(path)
                    .hasFooter(hasFooter);

            if (hasFooter) {
                boolean canEdit = footer.getElementById("edit-" + id) != null;
                Element vote = footer.getElementById("vote-" + id);
                boolean hasLike = vote.hasClass("voted");
                String numLikes = vote.select("span.votecount").text().trim();
                postBuilder = postBuilder
                        .canEdit(canEdit)
                        .hasLike(hasLike)
                        .numLikes(Integer.parseInt(numLikes));
            }

            Elements imgs = row.select("img.b-gallery-thumbnail-list__thumbnail");
            if (imgs != null) {
                List<String> photos = new ArrayList<>();
                for (Element img : imgs) {
                    String source = img.attr("src");
                    source = source.substring(0, source.lastIndexOf("&type=thumb"));
                    photos.add(source);
                }
                postBuilder = postBuilder.photos(photos);
            }

            postBuilder = postBuilder.threadID(thread.getId());

            Post post = postBuilder.build();
            thread.addPost(post);
        }
        return thread;
    }

    public Thread getThread(Document doc) {
        Element threadViewTab = doc.getElementById("thread-view-tab");
        Thread.Builder threadBuilder = new Thread.Builder();

        Element form = doc.select("form[data-message-type=reply]").first();
        if (form != null) {
            String securityToken = form.select("input[name=securitytoken]").val();
            String channelId = form.select("input[name=channelid]").val();
            String parentId = form.select("input[name=parentid]").val();
            ReplyForm replyForm = new ReplyForm(securityToken, Integer.parseInt(channelId), Integer.parseInt(parentId));

            threadBuilder = threadBuilder
                    .replyForm(replyForm);
        }

        Element primaryPage = threadViewTab.select("a.primary.page").first();
        if (primaryPage != null) {
            int pageNum = Integer.parseInt(primaryPage.text().trim());
            boolean hasNextPage = !threadViewTab.select("a.js-pagenav-next-button").first().attr("data-page").equals("0");
            threadBuilder = threadBuilder
                    .pageNum(pageNum)
                    .hasNextPage(hasNextPage);
        }

        String title = doc.select("h1.main-title").first().text().trim();
        String path = doc.head().select("link[rel=canonical]").attr("href");
        path = path.substring(RestClient.baseUrl.length());
        int index = path.indexOf("/page");
        if (index >= 0) {
            path = path.substring(0, index);
        }

        String id = doc.select("input[name=nodeid]").val();
        boolean isSubscribed = !doc.select("button.isSubscribed").isEmpty();
        return threadBuilder
                .id(id)
                .title(title)
                .path(path)
                .isSubscribed(isSubscribed)
                .build();
    }
}
