package xyz.edmw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.navigation.NavViewHolder;
import xyz.edmw.notification.NotificationActivity;
import xyz.edmw.recyclerview.RecyclerViewDisabler;
import xyz.edmw.rest.RestClient;
import xyz.edmw.settings.MainSharedPreferences;
import xyz.edmw.settings.SettingsActivity;
import xyz.edmw.topic.TopicActivity;
import xyz.edmw.topic.TopicAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UltimateRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.list)
    UltimateRecyclerView ultimateRecyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private static final String tag = "MainActivity";
    private static final int MY_LOGIN_ACTIVITY = 1;
    private static final RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();

    // SharedPreferences
    public static MainSharedPreferences preferences;

    private String title;
    private Forum forum;
    private Forum nextForum;
    private TopicAdapter adapter;
    private LinearLayoutManager layoutManager;
    private NavViewHolder navViewHolder;
    private boolean isLoadingNextForum;
    private boolean loadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new MainSharedPreferences(this);
        setTheme(preferences.getThemeId());
        super.onCreate(savedInstanceState);

        // TODO hack to get fresco initialized
        RestClient.getService();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // TODO CHECK IF USER IS LOGIN, SET AVATAR, USERNAME, MEMBER TITLE, HIDE LOGIN BUTTON (MIGHT WANT TO USE SHAREDPREFERENCES)

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navViewHolder = new NavViewHolder(this, navigationView);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        ultimateRecyclerView.setLayoutManager(layoutManager);
        ultimateRecyclerView.enableLoadmore();
        ultimateRecyclerView.setOnLoadMoreListener(this);
        ultimateRecyclerView.setDefaultOnRefreshListener(this);

        forum = Forum.edmw;

        if (isOnline()) {
            toolbar.setSubtitle("Page " + forum.getPageNum());
            onForumSelected(forum);
        } else {
            Toast.makeText(getApplicationContext(), "No network connected", Toast.LENGTH_SHORT).show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.nav_login):
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, MY_LOGIN_ACTIVITY);
                break;
            case (R.id.nav_logout):
                    Call<Void> call = RestClient.getService().logout();
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Response<Void> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Toast.makeText(MainActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                break;
            case (R.id.nav_messages):
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            case (R.id.nav_edmw):
                forum = Forum.edmw;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_nsfw:
                forum = Forum.nsfw;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_moneytalk:
                forum = Forum.money_talk;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_metaphysics:
                forum = Forum.metaphysics;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_stylegrooming:
                forum = Forum.style_grooming;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_technology:
                forum = Forum.technology;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_music_entertainment:
                forum = Forum.music_entertainment;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_sex_and_love:
                forum = Forum.sex_and_love;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
            case R.id.nav_feedback:
                forum = Forum.feedback;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onForumSelected(final Forum forum) {
        onForumSelected(forum, Insert.New);
    }

    private void onForumSelected(final Forum forum, Insert insert) {
        title = forum.getTitle();
        toolbar.setTitle(title);

        ultimateRecyclerView.showEmptyView();
        Call<Forum> call = RestClient.getService().getForum(forum.getPath(), forum.getPageNum());
        call.enqueue(new LoadForumCallback(insert));
    }

    private void onForumLoaded(Forum forum, Insert insert) {
        toolbar.setSubtitle("Page " + forum.getPageNum());
        switch (insert) {
            case After:
                adapter.insertTopics(forum.getTopics());
                break;
            case New:
                adapter = new TopicAdapter(this, forum.getTopics());
                ultimateRecyclerView.setAdapter(adapter);
                break;
        }
        loadMore = false;

        forum.setTitle(this.forum.getTitle());
        forum.setPath(this.forum.getPath());
        this.forum = forum;

        if (forum.hasNextPage()) {
            loadNextForum();
        }

        User user = forum.getUser();
        navViewHolder.setUser(user);
        int visibility = user == null ? View.GONE : View.VISIBLE;
        fab.setVisibility(visibility);
    }

    @Override
    public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
        loadMore = true;
        if (isLoadingNextForum) {
            return;
        }
        if (nextForum == null) {
            if (forum.hasNextPage()) {
                ultimateRecyclerView.addOnItemTouchListener(disabler);
                forum.setPageNum(forum.getPageNum() + 1);
                onForumSelected(forum, Insert.After);
            }
            return;
        }
        onForumLoaded(nextForum, Insert.After);
    }

    public void onLogin() {
        forum = Forum.edmw;
        forum.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        onForumSelected(forum);

        // TODO GET USER AVATAR URL, USERNAME, MEMBER TITLE AND SET TO DRAWER
        // TODO HIDE LOGIN BUTTON.
        /*
        Elements userElements = doc.getElementsByClass("b-menu__username-avatar");
        if(!userElements.isEmpty()) {
            String userAvatar = userElements.attr("src");
            String userName = userElements.attr("alt");

            System.out.println(userAvatar);
            System.out.println(userName);
        }
         */
    }

    @Override
    public void onRefresh() {
        forum.setPageNum(1);
        adapter = null;
        onForumSelected(forum);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (MY_LOGIN_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    onLogin();
                    break;
                }
            }
        }
    }

    private class LoadForumCallback implements Callback<Forum> {
        private final Insert insert;

        private LoadForumCallback(Insert insert) {
            this.insert = insert;
        }

        @Override
        public void onResponse(Response<Forum> response, Retrofit retrofit) {
            if (response.isSuccess()) {
                onForumLoaded(response.body(), insert);
            } else {
                Toast.makeText(getApplicationContext(), "Fail to retrieve threads", Toast.LENGTH_SHORT).show();
            }
            ultimateRecyclerView.hideEmptyView();
            ultimateRecyclerView.removeOnItemTouchListener(disabler);
        }

        @Override
        public void onFailure(Throwable t) {
            t.printStackTrace();
            Toast.makeText(getApplicationContext(), "Fail to retrieve threads", Toast.LENGTH_SHORT).show();
            ultimateRecyclerView.hideEmptyView();
            ultimateRecyclerView.removeOnItemTouchListener(disabler);
        }
    }

    private void loadNextForum() {
        isLoadingNextForum = true;
        Call<Forum> call = RestClient.getService().getForum(forum.getPath(), forum.getPageNum() + 1);
        call.enqueue(loadNextForumCallback);
    }

    private final Callback<Forum> loadNextForumCallback = new Callback<Forum>() {
        @Override
        public void onResponse(Response<Forum> response, Retrofit retrofit) {
            isLoadingNextForum = false;
            if (response.isSuccess()) {
                Forum forum = response.body();
                if (loadMore) {
                    onForumLoaded(forum, Insert.After);
                } else {
                    nextForum = forum;
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            isLoadingNextForum = false;
        }
    };
}
