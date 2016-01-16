package xyz.edmw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.marshalchen.ultimaterecyclerview.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.recyclerview.RecyclerViewDisabler;
import xyz.edmw.rest.RestClient;
import xyz.edmw.sharedpreferences.MainSharedPreferences;
import xyz.edmw.thread.ThreadActivity;
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

    private static final String tag = "MainActivity";
    private static final int MY_LOGIN_ACTIVITY = 1;
    private static final RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
    private String title;
    private Forum forum;

    private TopicAdapter adapter;
    private LinearLayoutManager layoutManager;

    // SharedPreferences
    public static MainSharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        preferences = new MainSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));

        // TODO CHECK IF USER IS LOGIN, SET AVATAR, USERNAME, MEMBER TITLE, HIDE LOGIN BUTTON (MIGHT WANT TO USE SHAREDPREFERENCES)

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        ultimateRecyclerView.setLayoutManager(layoutManager);

        ultimateRecyclerView.addItemDividerDecoration(getApplicationContext());
        ultimateRecyclerView.enableLoadmore();
        ultimateRecyclerView.setOnLoadMoreListener(this);
        ultimateRecyclerView.setDefaultOnRefreshListener(this);
        ultimateRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), ThreadActivity.class);
                        intent.putExtra("Topic", adapter.getTopic(position));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                })
        );

        forum = Forum.edmw;

        if (isOnline()) {
            toolbar.setSubtitle("Page " + forum.getPageNum());
            onForumSelected(forum);
        } else {
            Toast.makeText(getApplicationContext(), "No network connected", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem image_load_toggle = (MenuItem) menu.findItem(R.id.action_hide_image);
        if (MainSharedPreferences.getLoadImageAutomatically()) {
            image_load_toggle.setChecked(true);
        } else {
            image_load_toggle.setChecked(false);
        }

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
            case R.id.nav_metaphysics:
                forum = Forum.metaphysics;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
            case R.id.nav_feedback:
                forum = Forum.feedback;
                forum.setPageNum(1);
                adapter = null;
                onForumSelected(forum);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onForumSelected(final Forum forum) {
        title = forum.getTitle();
        toolbar.setTitle(title);

        ultimateRecyclerView.showEmptyView();
        Call<Forum> calls = RestClient.getService().getForum(forum.getPath(), forum.getPageNum());
        calls.enqueue(new Callback<Forum>() {
            @Override
            public void onResponse(Response<Forum> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    onForumLoaded(response.body());
                } else {
                    Toast.makeText(getApplicationContext(), "Fail to retrieve threads", Toast.LENGTH_SHORT).show();
                    ultimateRecyclerView.hideEmptyView();
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

    private void onForumLoaded(Forum forum) {
        toolbar.setSubtitle("Page " + forum.getPageNum());
        if (adapter == null) {
            adapter = new TopicAdapter(this, forum.getTopics());
            ultimateRecyclerView.setAdapter(adapter);
        } else {
            adapter.insertTopics(forum.getTopics());
        }
        ultimateRecyclerView.hideEmptyView();
        this.forum.setPageNum(forum.getPageNum());
        this.forum.hasNextPage(forum.hasNextPage());
    }

    @Override
    public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
        if (forum.hasNextPage()) {
            ultimateRecyclerView.addOnItemTouchListener(disabler);        // disables scolling
            ultimateRecyclerView.showEmptyView();

            Call<Forum> calls = RestClient.getService().getForum(forum.getPath(), forum.getPageNum() + 1);
            calls.enqueue(new Callback<Forum>() {
                @Override
                public void onResponse(Response<Forum> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        onForumLoaded(response.body());
                        layoutManager.scrollToPosition(maxLastVisiblePosition + 1);
                        ultimateRecyclerView.removeOnItemTouchListener(disabler);     // scrolling is enabled again
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail to retrieve threads", Toast.LENGTH_SHORT).show();
                        ultimateRecyclerView.hideEmptyView();
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

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                onRefresh();
                return true;
            case R.id.action_hide_image:

                //Changing to disable
                if (item.isChecked()) {
                    item.setChecked(false);
                    preferences.putBoolean("image_load_check", false);

                    Toast.makeText(this, "Please restart the application", Toast.LENGTH_SHORT).show();
                } else {
                    //Changing to enable
                    item.setChecked(true);
                    preferences.putBoolean("image_load_check", true);

                    Toast.makeText(this, "Please restart the application", Toast.LENGTH_SHORT).show();
                }

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
}
