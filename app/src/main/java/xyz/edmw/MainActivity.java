package xyz.edmw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
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
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.generic.GenericMap;
import xyz.edmw.post.PostActivity;
import xyz.edmw.recyclerview.RecyclerViewDisabler;
import xyz.edmw.rest.RestClient;
import xyz.edmw.sharedpreferences.MySharedPreferences;
import xyz.edmw.thread.ThreadAdapter;
import xyz.edmw.thread.Thread;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String tag = "MainActivity";

    private final int MY_LOGIN_ACTIVITY = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.list)
    UltimateRecyclerView ultimateRecyclerView;

    public static int pageNo = 1;
    public static Boolean hasNextPage = false;
    private String title;
    private static String currentForum;
    private static String currentName;

    ThreadAdapter adapter;
    LinearLayoutManager llm;
    GenericMap<Integer, Thread> threadMap;

    // SharedPreferences
    public static MySharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        preferences = new MySharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));

        // TODO CHECK IF USER IS LOGIN, SET AVATAR, USERNAME, MEMBER TITLE, HIDE LOGIN BUTTON (MIGHT WANT TO USE SHAREDPREFERENCES)

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        threadMap = new GenericMap<>();

        navigationView.setNavigationItemSelectedListener(this);

        llm = new LinearLayoutManager(getApplicationContext());
        ultimateRecyclerView.addItemDividerDecoration(getApplicationContext());
        ultimateRecyclerView.setLayoutManager(llm);
        ultimateRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                        intent.putExtra("Thread", threadMap.getValue(position));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        getApplicationContext().startActivity(intent);
                    }
                })
        );
        ultimateRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshThreads();
            }
        });
        ultimateRecyclerView.setEmptyView(R.layout.empty_progress);
        currentName = "EDMW";
        currentForum = "main-forum";

        if (isOnline()) {
            getSupportActionBar().setSubtitle("Page " + (MainActivity.pageNo));

            onForumSelected(currentName, currentForum, pageNo);
        } else {
            Toast.makeText(getApplicationContext(), "No network connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem image_load_toggle = (MenuItem) menu.findItem(R.id.action_hide_image);
        if (MySharedPreferences.getLoadImageAutomatically()) {
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
            MainActivity.currentName = "EDMW";
            MainActivity.currentForum = "main-forum";
            MainActivity.pageNo = 1;
            MainActivity.hasNextPage = false;
            toolbar.setTitle(title);
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
                MainActivity.currentName = "EDMW";
                MainActivity.currentForum = "main-forum";
                MainActivity.pageNo = 1;
                MainActivity.hasNextPage = false;
                if (threadMap != null)
                    threadMap.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                onForumSelected(MainActivity.currentName, MainActivity.currentForum, MainActivity.pageNo);
                break;
            case R.id.nav_nsfw:
                MainActivity.currentName = "nsfw";
                MainActivity.currentForum = "main-forum/nsfw";
                MainActivity.pageNo = 1;
                MainActivity.hasNextPage = false;
                if (threadMap != null)
                    threadMap.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                onForumSelected(MainActivity.currentName, MainActivity.currentForum, MainActivity.pageNo);
                break;
            case R.id.nav_metaphysics:
                MainActivity.currentName = "Metaphysics";
                MainActivity.currentForum = "metaphysics";
                MainActivity.pageNo = 1;
                MainActivity.hasNextPage = false;
                if (threadMap != null)
                    threadMap.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                onForumSelected(MainActivity.currentName, MainActivity.currentForum, MainActivity.pageNo);
                break;
            case R.id.nav_feedback:
                MainActivity.currentName = "Feedback";
                MainActivity.currentForum = "feedback";
                MainActivity.pageNo = 1;
                MainActivity.hasNextPage = false;
                if (threadMap != null)
                    threadMap.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                onForumSelected(MainActivity.currentName, MainActivity.currentForum, MainActivity.pageNo);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onForumSelected(String name, final String forum, final int pageNo) {
        title = name;
        toolbar.setTitle(title);

        ultimateRecyclerView.showEmptyView();
        Call<GenericMap<Integer, Thread>> calls = RestClient.getService().getThreads(forum, pageNo);

        calls.enqueue(new Callback<GenericMap<Integer, Thread>>() {

            @Override
            public void onResponse(Response<GenericMap<Integer, Thread>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    threadMap = response.body();
                    adapter = new ThreadAdapter(threadMap);
                    ultimateRecyclerView.hideEmptyView();
                    ultimateRecyclerView.setAdapter(adapter);

                    ultimateRecyclerView.enableLoadmore();
                    ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
                        @Override
                        public void loadMore(int itemsCount, final int maxLastVisiblePosition) {

                            if (hasNextPage) {
                                final RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();

                                ultimateRecyclerView.addOnItemTouchListener(disabler);        // disables scolling

                                Call<GenericMap<Integer, Thread>> calls = RestClient.getService().getThreads(forum, MainActivity.pageNo);
                                calls.enqueue(new Callback<GenericMap<Integer, Thread>>() {

                                    @Override
                                    public void onResponse(Response<GenericMap<Integer, Thread>> response, Retrofit retrofit) {
                                        if (response.isSuccess()) {
                                            if (hasNextPage)
                                                getSupportActionBar().setSubtitle("Page " + (MainActivity.pageNo - 1));
                                            else
                                                getSupportActionBar().setSubtitle("Page " + (MainActivity.pageNo));

                                            int itemStartRange = threadMap.size();
                                            threadMap.putAll(response.body());
                                            int itemEndRange = threadMap.size();
                                            adapter.notifyItemRangeInserted(itemStartRange, itemEndRange);
                                            llm.scrollToPosition(maxLastVisiblePosition + 1);

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
                    });
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

    public void onLogin() {
        MainActivity.currentName = "EDMW";
        MainActivity.currentForum = "main-forum";
        MainActivity.pageNo = 1;
        MainActivity.hasNextPage = false;
        if (threadMap != null)
            threadMap.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        onForumSelected(MainActivity.currentName, MainActivity.currentForum, MainActivity.pageNo);

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



    private void refreshThreads() {
        MainActivity.pageNo = 1;
        MainActivity.hasNextPage = false;
        if (threadMap != null)
            threadMap.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();

        getSupportActionBar().setSubtitle("Page " + MainActivity.pageNo);
        onForumSelected(MainActivity.currentName, MainActivity.currentForum, MainActivity.pageNo);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshThreads();
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
