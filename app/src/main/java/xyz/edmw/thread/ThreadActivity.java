package xyz.edmw.thread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import xyz.edmw.notification.Notification;
import xyz.edmw.post.Post;
import xyz.edmw.post.PostAdapter;
import xyz.edmw.recyclerview.RecyclerViewDisabler;
import xyz.edmw.rest.ApiService;
import xyz.edmw.rest.RestClient;
import xyz.edmw.settings.MainSharedPreferences;
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
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_PATH = "arg_path";
    private static final String ARG_PAGE_NUM = "arg_page_num";
    private static final RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
    public static YouTubePlayer youTubePlayer;
    public static boolean isFullscreen;

    private final int numPosts = 15;
    private PostAdapter adapter;
    private LinearLayoutManager llm;
    private Thread prevThread, nextThread;
    private View footer;
    private int firstPage, lastPage;
    private boolean hasNextPage;
    private boolean isLoadingPrevThread, isLoadingNextThread;
    private boolean loadPrev, loadMore;
    private String path;
    private ReplyForm replyForm;
    private String id;
    private boolean isSubscribed;

    public static Intent newInstance(Context context, String title, String path) {
        Intent intent = new Intent(context, ThreadActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_PATH, path);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void startInstance(Context context, Notification notification) {
        Intent intent = newInstance(context, notification.getTitle(), notification.getPath());
        context.startActivity(intent);
    }

    public static void startInstance(Context context, String title, String path) {
        Intent intent = newInstance(context, title, path);
        context.startActivity(intent);
    }

    public static void startInstance(Context context, Topic topic, int pageNum) {
        Intent intent = newInstance(context, topic.getTitle(), topic.getPath());
        intent.putExtra(ARG_PAGE_NUM, pageNum);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(new MainSharedPreferences(this).getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                onThreadSelected(path, lastPage, Insert.After);
            }
        });

        reply.setOnClickListener(this);

        Intent i = getIntent();
        String title = i.getStringExtra(ARG_TITLE);
        path = i.getStringExtra(ARG_PATH);
        getSupportActionBar().setTitle(title);
        int pageNum = i.getIntExtra(ARG_PAGE_NUM, -1);
        if (pageNum == -1) {
            onThreadSelected(path, Insert.New);
        } else {
            onThreadSelected(path, pageNum, Insert.New);
        }
    }

    private void onThreadSelected(String path, int pageNum, Insert insert) {
        ultimateRecyclerView.showEmptyView();
        ApiService service = RestClient.getService();
        Call<Thread> call = pageNum == -1 ? service.getThread(path) : service.getThread(path, pageNum);
        call.enqueue(new LoadThreadCallback(insert));
    }

    private void onThreadSelected(String path, Insert insert) {
        ultimateRecyclerView.showEmptyView();
        Call<Thread> call = RestClient.getService().getThread(path);
        call.enqueue(new LoadThreadCallback(insert));
    }

    public void onPostSelected(String id) {
        List<Post> posts = adapter.getPosts();
        for (int i = posts.size()-1; i>=0;--i) {
            if (id.equals(posts.get(i).getId())) {
                llm.scrollToPosition(i);
                return;
            }
        }
        String path = String.format("%s?p=%s#%s", this.path, id, id);
        onThreadSelected(path, Insert.Before);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread, menu);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_subscribe);
        String title = isSubscribed ? "Unsubscribe" : "Subscribe";
        item.setTitle(title);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO fix exact page
        String url = RestClient.baseUrl + path;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                onThreadSelected(path, 1, Insert.New);
                return true;
            case R.id.action_subscribe:
                final String action = isSubscribed ? "delete" : "add";
                final String err = isSubscribed ? "unsubscribe" : "subscribe";
                Call<Void> call = RestClient.getService().follow(action, id, "follow_contents", replyForm.getSecurityToken());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            isSubscribed = !isSubscribed;
                        } else {
                            Toast.makeText(ThreadActivity.this, "Failed to " + err, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(ThreadActivity.this, "Failed to " + err, Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            case (R.id.action_share):
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, "EDMW.XYZ");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share Thread"));
                return true;
            case (R.id.action_open_browser):
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onThreadLoaded(Thread thread, Insert insert) {
        toolbar.setSubtitle("Page " + thread.getPageNum());

        int visibility = (replyForm = thread.getReplyForm()) == null ? View.GONE : View.VISIBLE;
        replyLayout.setVisibility(visibility);

        List<Post> posts = thread.getPosts();
        switch (insert) {
            case Before:
                adapter.getPosts().addAll(0, posts);
                adapter.notifyItemRangeInserted(0, posts.size());
                llm.scrollToPosition(posts.size() - 1);
                break;
            case New:
                adapter = new PostAdapter(ThreadActivity.this, posts);
                adapter.setCustomLoadMoreView(footer);
                ultimateRecyclerView.setAdapter(adapter);
                String id = null;
                if (path.contains("?p=")) {
                    id = path.substring(path.lastIndexOf("?p=") + 3);
                } else if (path.contains("/node/")) {
                    id = path.substring(6);
                }
                if (!TextUtils.isEmpty(id)) {
                    for (int i = posts.size()-1; i >= 0; --i) {
                        if (id.equals(posts.get(i).getId())) {
                            llm.scrollToPosition(i);
                            break;
                        }
                    }
                }
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
                break;
        }
        path = thread.getPath();
        id = thread.getId();
        isSubscribed = thread.isSubscribed();
        loadPrev = loadMore = false;

        if ((insert.equals(Insert.Before) || insert.equals(Insert.New))
            && (firstPage = thread.getPageNum()) > 1) {
                loadPrevThread();
        }
        if (insert.equals(Insert.New) || insert.equals(Insert.After)) {
            lastPage = thread.getPageNum();
            if (hasNextPage = thread.hasNextPage()) {
                loadNextThread();
            }
        }
    }

    @Override
    public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
        loadMore = true;
        if (isLoadingNextThread) {
            return;
        }
        if (nextThread == null) {
            if (hasNextPage) {
                ultimateRecyclerView.addOnItemTouchListener(disabler);
                onThreadSelected(path, lastPage + 1, Insert.After);
            }
            return;
        }
        onThreadLoaded(nextThread, Insert.After);
    }

    @Override
    public void onClick(final View v) {
        String message = this.message.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(ThreadActivity.this, "Please write something", Toast.LENGTH_SHORT).show();
            return;
        }

        // prevent people from sending multiple times when network is slow
        v.setEnabled(false);
        Toast.makeText(ThreadActivity.this, "Sending...", Toast.LENGTH_SHORT).show();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

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
                    onThreadSelected(path, lastPage, Insert.After);
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
        loadPrev = true;
        if (firstPage == 1) {
            onThreadSelected(path, firstPage, Insert.New);
            return;
        }

        if (isLoadingPrevThread) {
            return;
        }
        if (prevThread == null) {
            if (firstPage > 1) {
                ultimateRecyclerView.addOnItemTouchListener(disabler);
                onThreadSelected(path, firstPage - 1, Insert.Before);
            }
            return;
        }
        onThreadLoaded(prevThread, Insert.Before);
    }

    public void addQuote(String quote) {
        message.append(quote + System.getProperty("line.separator"));
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

    private void loadPrevThread() {
        Call<Thread> call = RestClient.getService().getThread(path, firstPage - 1);
        call.enqueue(loadPrevThreadCallback);
    }

    private final Callback<Thread> loadPrevThreadCallback = new Callback<Thread>() {
        @Override
        public void onResponse(Response<Thread> response, Retrofit retrofit) {
            isLoadingPrevThread = false;
            if (response.isSuccess()) {
                Thread thread = response.body();
                if (loadPrev) {
                    onThreadLoaded(thread, Insert.Before);
                } else {
                    prevThread = thread;
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            isLoadingPrevThread = false;
        }
    };

    private void loadNextThread() {
        isLoadingNextThread = true;
        Call<Thread> call = RestClient.getService().getThread(path, lastPage + 1);
        call.enqueue(loadNextThreadCallback);
    }

    private final Callback<Thread> loadNextThreadCallback = new Callback<Thread>() {
        @Override
        public void onResponse(Response<Thread> response, Retrofit retrofit) {
            isLoadingNextThread = false;
            if (response.isSuccess()) {
                Thread thread = response.body();
                if (loadMore) {
                    onThreadLoaded(thread, Insert.After);
                } else {
                    nextThread = thread;
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            isLoadingNextThread = false;
        }
    };
}
