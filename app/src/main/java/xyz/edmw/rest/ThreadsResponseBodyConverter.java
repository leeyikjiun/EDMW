package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.MainActivity;
import xyz.edmw.generic.GenericMap;
import xyz.edmw.thread.Thread;


public class ThreadsResponseBodyConverter implements Converter<ResponseBody, GenericMap<Integer, Thread>> {

    @Override
    public GenericMap<Integer, Thread> convert(ResponseBody body) throws IOException {
        String html = body.string();
        return getThreads(html);
    }

    private GenericMap<Integer, Thread> getThreads(String html) {
        Document doc = Jsoup.parse(html);
        Element topicTab = doc.getElementById("topic-tab");
        Elements rows = topicTab.select("tr.topic-item");

        GenericMap<Integer, Thread> threads = new GenericMap<>();

        for (Element row : rows) {
            Element anchor = row.select("a.topic-title").first();
            Boolean isSticky = row.hasClass("sticky");
            String title = anchor.text().trim();
            String path = anchor.attr("href").substring(RestClient.baseUrl.length());
            String lastPost = row.select("td.cell-lastpost").first().text().trim();
            String avatar = row.select("div.topic-avatar").first().getElementsByTag("img").attr("src");
            String startedBy = row.select("div.topic-info").first().text().trim();
            String id = row.attr("data-node-id");

            threads.put(Integer.parseInt(id), new Thread(title, path, startedBy, lastPost, avatar, isSticky));
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


        return threads;
    }
}
