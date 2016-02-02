package xyz.edmw.subscription;

import java.util.List;

import xyz.edmw.topic.Topic;

public class Subscriptions {
    private final List<Topic> topics;

    public Subscriptions(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }
}