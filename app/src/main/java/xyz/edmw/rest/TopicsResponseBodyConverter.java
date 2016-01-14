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
import xyz.edmw.MainActivity;
import xyz.edmw.topic.Topic;

public class TopicsResponseBodyConverter implements Converter<ResponseBody, List<Topic>> {
    private static final int responsesPerPage = 15;

    @Override
    public List<Topic> convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getTopics(html);
    }

    private List<Topic> getTopics(String html) {
        Document doc = Jsoup.parse(html);
        Element topicTab = doc.getElementById("topic-tab");
        Elements rows = topicTab.select("tr.topic-item");

        List<Topic> topics = new ArrayList<>(rows.size());
        for (Element row : rows) {
            Element anchor = row.select("a.topic-title").first();
            Boolean isSticky = row.hasClass("sticky");
            String title = anchor.text().trim();
            String path = anchor.attr("href").substring(RestClient.baseUrl.length());
            String lastPost = row.select("td.cell-lastpost").first().text().trim();
            String avatar = row.select("div.topic-avatar").first().getElementsByTag("img").attr("src");
            String startedBy = row.select("div.topic-info").first().text().trim();
            String id = row.attr("data-node-id");

            topics.add(new Topic.Builder()
                            .title(title)
                            .path(path)
                            .lastPost(lastPost)
                            .threadstarterAvatar(avatar)
                            .startedBy(startedBy)
                            .isSticky(isSticky)
                            .build()
            );
        }

        // Check if there are more to load
        Elements nextElements = doc.getElementsByAttributeValue("rel", "next");
        if (nextElements.size() == 3) {
            Element next = nextElements.last();
            MainActivity.pageNo = Integer.parseInt(next.attr("data-page"));
            MainActivity.hasNextPage = true;
        } else {
            MainActivity.hasNextPage = false;
        }

        return topics;
    }
}
