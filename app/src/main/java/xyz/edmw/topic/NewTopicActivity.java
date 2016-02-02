package xyz.edmw.topic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.rest.RestClient;
import xyz.edmw.settings.MainSharedPreferences;

public class NewTopicActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.topic_title)
    EditText title;
    @Bind(R.id.topic_message)
    EditText message;
    @Bind(R.id.topic_post)
    Button post;

    private TopicForm topicForm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(new MainSharedPreferences(this).getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create New Topic");

        Call<TopicForm> call = RestClient.getService().getTopic();
        call.enqueue(new Callback<TopicForm>() {
            @Override
            public void onResponse(Response<TopicForm> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    topicForm = response.body();
                } else {
                    topicForm = null;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                topicForm = null;
            }
        });

        post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (topicForm == null) {
            Toast.makeText(NewTopicActivity.this, "Failed to create new topic", Toast.LENGTH_SHORT).show();
        } else {
            Call<Void> call = RestClient.getService().postTopic(
                    topicForm.getSecurityToken(),
                    topicForm.getParentId(),
                    title.getText().toString().trim(),
                    message.getText().toString().trim()
            );
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        Toast.makeText(NewTopicActivity.this, "Successfully created new topic", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NewTopicActivity.this, "Failed to create new topic", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(NewTopicActivity.this, "Failed to create new topic", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
