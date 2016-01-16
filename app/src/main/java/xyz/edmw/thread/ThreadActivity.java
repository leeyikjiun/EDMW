package xyz.edmw.thread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.post.PostAdapter;
import xyz.edmw.recyclerview.RecyclerViewDisabler;
import xyz.edmw.rest.RestClient;
import xyz.edmw.topic.Topic;

public class ThreadActivity extends AppCompatActivity implements UltimateRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    UltimateRecyclerView ultimateRecyclerView;

    private static final String tag = "ThreadActivity";
    private static final RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
    public static YouTubePlayer youTubePlayer;
    public static boolean isFullscreen;

    private PostAdapter adapter;
    private LinearLayoutManager llm;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        Topic topic = i.getParcelableExtra("Topic");

        thread = new Thread.Builder()
                .title(topic.getTitle())
                .path(topic.getPath())
                .pageNum(1)
                .build();
        Log.d("topic title", topic.getTitle());
        Log.d("thread title", thread.getTitle());
        getSupportActionBar().setTitle(thread.getTitle());

        llm = new LinearLayoutManager(getApplicationContext());
        ultimateRecyclerView.addItemDividerDecoration(getApplicationContext());
        ultimateRecyclerView.setLayoutManager(llm);
        ultimateRecyclerView.setHasFixedSize(false);
        ultimateRecyclerView.enableLoadmore();
        ultimateRecyclerView.setOnLoadMoreListener(this);
        ultimateRecyclerView.setDefaultOnRefreshListener(this);

        onThreadSelected(thread);
    }

    private void onThreadSelected(Thread thread) {
        ultimateRecyclerView.showEmptyView();
        Call<Thread> calls = RestClient.getService().getThread(thread.getPath(), thread.getPageNum());
        calls.enqueue(new Callback<Thread>() {
            @Override
            public void onResponse(Response<Thread> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    onThreadLoaded(response.body());
                } else {
                    Toast.makeText(getApplicationContext(), "Fail to retrieve posts", Toast.LENGTH_SHORT).show();
                    ultimateRecyclerView.hideEmptyView();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Fail to retrieve posts", Toast.LENGTH_SHORT).show();
                ultimateRecyclerView.hideEmptyView();
            }
        });
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
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void onThreadLoaded(Thread thread) {
        toolbar.setSubtitle("Page " + thread.getPageNum());
        if (adapter == null) {
            adapter = new PostAdapter(ThreadActivity.this, thread.getPosts());
            ultimateRecyclerView.setAdapter(adapter);
        } else {
            adapter.insertPosts(thread.getPosts());
        }
        ultimateRecyclerView.hideEmptyView();
        this.thread.setPageNum(thread.getPageNum());
        this.thread.hasNextPage(thread.hasNextPage());
    }

    @Override
    public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
        if (thread.hasNextPage()) {
            ultimateRecyclerView.addOnItemTouchListener(disabler);        // disables scolling

            ultimateRecyclerView.showEmptyView();
            Call<Thread> calls = RestClient.getService().getThread(thread.getPath(), thread.getPageNum() + 1);
            calls.enqueue(new Callback<Thread>() {
                @Override
                public void onResponse(Response<Thread> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        onThreadLoaded(response.body());
                        llm.scrollToPosition(maxLastVisiblePosition + 1);
                        ultimateRecyclerView.removeOnItemTouchListener(disabler);        // disables scolling
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail to retrieve posts", Toast.LENGTH_SHORT).show();
                        ultimateRecyclerView.showEmptyView();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Fail to retrieve posts", Toast.LENGTH_SHORT).show();
                    ultimateRecyclerView.hideEmptyView();
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        adapter = null;
        thread.setPageNum(1);
        onThreadSelected(thread);
    }
}