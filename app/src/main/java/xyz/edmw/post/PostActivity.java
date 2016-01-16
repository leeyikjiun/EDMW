package xyz.edmw.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import xyz.edmw.generic.GenericMap;
import xyz.edmw.recyclerview.RecyclerViewDisabler;
import xyz.edmw.rest.RestClient;
import xyz.edmw.thread.Thread;
import xyz.edmw.topic.Topic;

public class PostActivity extends AppCompatActivity {
    private static final String tag = "PostActivity";
    public static YouTubePlayer youTubePlayer;
    public static boolean fullScreen;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    UltimateRecyclerView ultimateRecyclerView;

    public static int pageNo = 1;
    public static Boolean hasNextPage = false;
    private String path;

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
        path = topic.getPath();

        toolbar.setTitle(topic.getTitle());

        llm = new LinearLayoutManager(getApplicationContext());
        ultimateRecyclerView.addItemDividerDecoration(getApplicationContext());
        ultimateRecyclerView.setLayoutManager(llm);
        ultimateRecyclerView.setHasFixedSize(false);
        ultimateRecyclerView.setEmptyView(R.layout.empty_progress);

        toolbar.setSubtitle("Page " + (PostActivity.pageNo));
        onThreadSelected(path, pageNo);
    }

    private void onThreadSelected(final String path, int pageNo) {

        ultimateRecyclerView.showEmptyView();

        Call<Thread> calls = RestClient.getService().getThread(path, pageNo);
        calls.enqueue(new Callback<Thread>() {

            @Override
            public void onResponse(Response<Thread> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    thread = response.body();
                    adapter = new PostAdapter(PostActivity.this, thread.getPosts());
                    ultimateRecyclerView.hideEmptyView();
                    ultimateRecyclerView.setAdapter(adapter);

                    ultimateRecyclerView.enableLoadmore();
                    ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
                        @Override
                        public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                            if (hasNextPage) {
                                final RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();

                                ultimateRecyclerView.addOnItemTouchListener(disabler);        // disables scolling

                                Call<Thread> calls = RestClient.getService().getThread(path, PostActivity.pageNo);
                                calls.enqueue(new Callback<Thread>() {

                                    @Override
                                    public void onResponse(Response<Thread> response, Retrofit retrofit) {
                                        if (response.isSuccess()) {
                                            if (hasNextPage)
                                                toolbar.setSubtitle("Page " + (PostActivity.pageNo - 1));
                                            else
                                                toolbar.setSubtitle("Page " + (PostActivity.pageNo));

                                            int itemStartRange = thread.getPosts().size();
                                            thread.addPosts(response.body().getPosts());
                                            int itemEndRange = thread.getPosts().size();
                                            adapter.notifyItemRangeInserted(itemStartRange, itemEndRange);
                                            llm.scrollToPosition(maxLastVisiblePosition + 1);
                                            ultimateRecyclerView.removeOnItemTouchListener(disabler);        // disables scolling


                                        } else {
                                            Toast.makeText(getApplicationContext(), "Fail to retrieve threads", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        t.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "Fail to retrieve threads", Toast.LENGTH_SHORT).show();
                                        ultimateRecyclerView.hideEmptyView();
                                    }
                                });
                            }
                        }
                    });

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
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resetData();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if (fullScreen){
            youTubePlayer.setFullscreen(false);
        } else{
            resetData();
            super.onBackPressed();
        }

    }

    private void resetData() {
        super.onDetachedFromWindow();
        pageNo = 1;
        hasNextPage = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                PostActivity.pageNo = 1;
                PostActivity.hasNextPage = false;
                if(thread.getPosts() != null)
                    thread.getPosts().clear();
                if(adapter != null)
                    adapter.notifyDataSetChanged();
                toolbar.setSubtitle("Page " + PostActivity.pageNo);
                onThreadSelected(this.path, PostActivity.pageNo);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
