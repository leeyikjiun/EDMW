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
import android.widget.RelativeLayout;
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
import xyz.edmw.Insert;
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
    @Bind(R.id.reply_bar)
    RelativeLayout replyLayout;
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
    private View footer;
    private int firstPage, lastPage;
    private boolean hasNextPage;

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

        footer = getLayoutInflater().inflate(R.layout.view_footer, ultimateRecyclerView, false);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onThreadSelected(thread);
            }
        });

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

    private void onThreadLoaded(Thread thread, Insert insert, int maxLastVisiblePosition) {
        toolbar.setSubtitle("Page " + thread.getPageNum());
        List<Post> posts = thread.getPosts();
        switch (insert) {
            case Before:
                adapter.getPosts().addAll(0, posts);
                adapter.notifyItemRangeInserted(0, posts.size());
                llm.scrollToPosition(posts.size() - 1);
                firstPage = thread.getPageNum();
                break;
            case New:
                adapter = new PostAdapter(ThreadActivity.this, posts);
                adapter.setCustomLoadMoreView(footer);
                ultimateRecyclerView.setAdapter(adapter);
                firstPage = lastPage = thread.getPageNum();
                hasNextPage = thread.hasNextPage();
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
                llm.scrollToPosition(maxLastVisiblePosition + 1);
                lastPage = thread.getPageNum();
                hasNextPage = thread.hasNextPage();
                break;
        }

        // TODO temp fix
        thread.setPath(this.thread.getPath());
        this.thread = thread;

        if (thread.getReplyForm() == null) {
            replyLayout.setVisibility(View.GONE);
        } else {
            replyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
        if (thread.hasNextPage()) {
            ultimateRecyclerView.addOnItemTouchListener(disabler);        // disables scolling
            ultimateRecyclerView.showEmptyView();
            Call<Thread> call = RestClient.getService().getThread(thread.getPath(), lastPage + 1);
            call.enqueue(new LoadThreadCallback(Insert.After, maxLastVisiblePosition));
        }
    }

    @Override
    public void onClick(final View v) {
        Toast.makeText(ThreadActivity.this, "Sending...", Toast.LENGTH_SHORT).show();
        v.setEnabled(false);
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        ReplyForm replyForm = thread.getReplyForm();
        String message = this.message.getText().toString().trim();
        message = message.replace(System.getProperty("line.separator"), "<br />");
        Call<Void> call = RestClient.getService().reply(
                replyForm.getSecurityToken(),
                replyForm.getChannelId(),
                replyForm.getParentId(),
                message
        );
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    ThreadActivity.this.message.setText("");
                    onThreadSelected(thread);
                } else {
                    Toast.makeText(ThreadActivity.this, "Failed to send reply", Toast.LENGTH_SHORT).show();
                }
                v.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(ThreadActivity.this, "Failed to send reply", Toast.LENGTH_SHORT).show();
                v.setEnabled(true);
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
            Call<Thread> call = RestClient.getService().getThread(thread.getPath(), firstPage - 1);
            call.enqueue(new LoadThreadCallback(Insert.Before));
        }
    }

    public void addQuote(String quote) {
        message.append(quote + System.getProperty("line.separator"));
    }

    private class LoadThreadCallback implements Callback<Thread> {
        private final Insert insert;
        private int maxLastVisiblePosition;

        private LoadThreadCallback(Insert insert) {
            this.insert = insert;
        }

        private LoadThreadCallback(Insert insert, int maxLastVisiblePosition) {
            this.insert = insert;
            this.maxLastVisiblePosition = maxLastVisiblePosition;
        }

        @Override
        public void onResponse(Response<Thread> response, Retrofit retrofit) {
            if (response.isSuccess()) {
                onThreadLoaded(response.body(), insert, maxLastVisiblePosition);
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
}
