package xyz.edmw.rest;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Converter;
import xyz.edmw.subscription.Subscriptions;
import xyz.edmw.topic.Topic;

public class SubscriptionsResponseBodyConverter implements Converter<ResponseBody, Subscriptions> {
    @Override
    public Subscriptions convert(ResponseBody value) throws IOException {
        String html = value.string();
        return getSubscriptions(html);
    }

    private Subscriptions getSubscriptions(String html) {
        Document doc = Jsoup.parse(html);
        Element privateMessageContainer = doc.getElementById("privateMessageContainer");

        List<Topic> topics = new ArrayList<>();
        for (Element topicItem : privateMessageContainer.select("tr.topic-item")) {
            Topic topic = Topic.Builder.from(topicItem).build();
            topics.add(topic);
        }
        return new Subscriptions(topics);
    }
}
