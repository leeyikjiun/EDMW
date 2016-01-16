package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.Forum;
import xyz.edmw.topic.Topic;

public class ForumResponseBodyConverter implements Converter<ResponseBody, Forum> {
    @Override
    public Forum convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getForum(html);
    }

    private Forum getForum(String html) {
        Document doc = Jsoup.parse(html);
        Element topicTab = doc.getElementById("topic-tab");

        int pageNum = Integer.parseInt(topicTab.select("a.primary.page").first().text().trim());
        boolean hasNextPage = !topicTab.select("a.js-pagenav-next-button").first().attr("data-page").equals("0");
        Forum forum = new Forum.Builder()
                .pageNum(pageNum)
                .hasNextPage(hasNextPage)
                .build();

        Elements rows = topicTab.select("tr.topic-item");
        for (Element row : rows) {
            Element anchor = row.select("a.topic-title").first();
            Boolean isSticky = row.hasClass("sticky");
            String title = anchor.text().trim();
            String path = anchor.attr("href").substring(RestClient.baseUrl.length());
            String lastPost = row.select("td.cell-lastpost").first().text().trim();
            String avatar = row.select("div.topic-avatar").first().getElementsByTag("img").attr("src");
            String startedBy = row.select("div.topic-info").first().text().trim();
            String id = row.attr("data-node-id");

            forum.addTopic(new Topic.Builder()
                    .id(id)
                    .title(title)
                    .path(path)
                    .lastPost(lastPost)
                    .threadstarterAvatar(avatar)
                    .startedBy(startedBy)
                    .isSticky(isSticky)
                    .build()
            );
        }

        return forum;
    }
}
