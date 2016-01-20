package xyz.edmw.thread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.post.Post;
import xyz.edmw.post.PostAdapter;
import xyz.edmw.recyclerview.RecyclerViewDisabler;
import xyz.edmw.rest.RestClient;
import xyz.edmw.topic.Topic;

public class ThreadActivity extends AppCompatActivity implements UltimateRecyclerView.OnLoadMoreListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    UltimateRecyclerView ultimateRecyclerView;
    @Bind(R.id.reply_message)
    EditText message;
    @Bind(R.id.reply)
    ImageButton reply;

    private static final String tag = "ThreadActivity";
    private static final String ARG_TOPIC = "arg_topic";
    private static final String ARG_PAGE_NUM = "ar_page_num";
    private static final RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
    public static YouTubePlayer youTubePlayer;
    public static boolean isFullscreen;

    private PostAdapter adapter;
    private LinearLayoutManager llm;
    private Thread thread;

    public static void startInstance(Context context, Topic topic, int pageNum) {
        Intent intent = new Intent(context, ThreadActivity.class);
        intent.putExtra(ARG_TOPIC, topic);
        intent.putExtra(ARG_PAGE_NUM, pageNum);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        Topic topic = i.getParcelableExtra(ARG_TOPIC);
        int pageNum = i.getIntExtra(ARG_PAGE_NUM, 1);

        thread = new Thread.Builder()
                .title(topic.getTitle())
                .path(topic.getPath())
                .pageNum(pageNum)
                .build();
        getSupportActionBar().setTitle(thread.getTitle());

        llm = new LinearLayoutManager(getApplicationContext());
        ultimateRecyclerView.addItemDividerDecoration(getApplicationContext());
        ultimateRecyclerView.setLayoutManager(llm);
        ultimateRecyclerView.setHasFixedSize(false);
        ultimateRecyclerView.enableLoadmore();
        ultimateRecyclerView.setOnLoadMoreListener(this);
        ultimateRecyclerView.setDefaultOnRefreshListener(this);

        reply.setOnClickListener(this);

        onThreadSelected(thread);
    }

    private void onThreadSelected(Thread thread) {
        ultimateRecyclerView.showEmptyView();
        Call<Thread> call = RestClient.getService().getThread(thread.getPath(), thread.getPageNum());
        call.enqueue(new LoadThreadCallback(Insert.New));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem image_load_toggle = (MenuItem) menu.findItem(R.id.action_hide_image);
        image_load_toggle.setVisible(false);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen){
            youTubePlayer.setFullscreen(false);
        } else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                thread.setPageNum(1);
                onThreadSelected(thread);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onThreadLoaded(Thread thread, Insert insert) {
        toolbar.setSubtitle("Page " + thread.getPageNum());
        List<Post> posts = thread.getPosts();
        switch (insert) {
            case Before:
                adapter.getPosts().addAll(0, posts);
                adapter.notifyItemRangeInserted(0, posts.size());
                llm.scrollToPosition(posts.size() - 1);
                break;
            case New:
                adapter = new PostAdapter(ThreadActivity.this, posts);
                ultimateRecyclerView.setAdapter(adapter);
                break;
            case After:
                List<Post> adapterPosts = adapter.getPosts();
                int positionStart = adapterPosts.size();
                int itemCount = 0;
                for (Post post : posts) {
                    if (!adapterPosts.contains(post)) {
                        adapterPosts.add(post);
                        ++itemCount;
                    }
                }
                adapter.notifyItemRangeInserted(positionStart, itemCount);
                llm.scrollToPosition(positionStart + 1);
                break;
        }

        // TODO temp fix
        thread.setPath(this.thread.getPath());
        this.thread = thread;
    }

    @Override
    public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
        if (thread.hasNextPage()) {
            ultimateRecyclerView.addOnItemTouchListener(disabler);        // disables scolling
            ultimateRecyclerView.showEmptyView();
            Call<Thread> call = RestClient.getService().getThread(thread.getPath(), thread.getPageNum() + 1);
            call.enqueue(new LoadThreadCallback(Insert.After));
        }
    }

    @Override
    public void onClick(View v) {
        Call<Void> call = RestClient.getService().reply(
                thread.getSecurityToken(),
                thread.getChannelId(),
                thread.getParentId(),
                message.getText().toString().trim()
        );
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    message.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    onThreadSelected(thread);
                } else {
                    Toast.makeText(ThreadActivity.this, "Failed to send reply", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(ThreadActivity.this, "Failed to send reply", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        if (thread.getPageNum() == 1) {
            onThreadSelected(thread);
        } else {
            ultimateRecyclerView.addOnItemTouchListener(disabler);        // disables scolling
            ultimateRecyclerView.showEmptyView();
            Call<Thread> call = RestClient.getService().getThread(thread.getPath(), thread.getPageNum() - 1);
            call.enqueue(new LoadThreadCallback(Insert.Before));
        }
    }

    private class LoadThreadCallback implements Callback<Thread> {
        private final Insert insert;

        private LoadThreadCallback(Insert insert) {
            this.insert = insert;
        }

        @Override
        public void onResponse(Response<Thread> response, Retrofit retrofit) {
            if (response.isSuccess()) {
                onThreadLoaded(response.body(), insert);
            } else {
                Toast.makeText(getApplicationContext(), "Fail to retrieve posts", Toast.LENGTH_SHORT).show();
            }
            ultimateRecyclerView.hideEmptyView();
            ultimateRecyclerView.removeOnItemTouchListener(disabler);        // disables scolling
        }

        @Override
        public void onFailure(Throwable t) {
            t.printStackTrace();
            Toast.makeText(getApplicationContext(), "Fail to retrieve posts", Toast.LENGTH_SHORT).show();
            ultimateRecyclerView.hideEmptyView();
            ultimateRecyclerView.removeOnItemTouchListener(disabler);        // disables scolling
        }
    }

    private enum Insert {
        Before, New, After,
    }
}
