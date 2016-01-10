package xyz.edmw;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.post.PostPagerFragment;
import xyz.edmw.thread.Thread;
import xyz.edmw.thread.ThreadFragment;
import xyz.edmw.thread.ThreadPagerFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ThreadFragment.OnThreadSelectedListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    private final int numPages = 15; // TODO
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        onForumSelected("EDMW", "main-forum", numPages);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            toolbar.setTitle(title);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.nav_login):
                LoginFragment fragment = LoginFragment.newInstance();
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
                break;
            case (R.id.nav_edmw):
                onForumSelected("EDMW", "main-forum", numPages);
                break;
            case R.id.nav_nsfw:
                onForumSelected("NSFW", "main-forum/nsfw", numPages);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onForumSelected(String name, String forum, int numPages) {
        title = name;
        toolbar.setTitle(title);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, ThreadPagerFragment.newInstance(forum, numPages))
                .commit();
    }

    @Override
    public void onThreadSelected(Thread thread) {
        onThreadSelected(thread, 1);
    }

    @Override
    public void onThreadSelected(Thread thread, int pageNum) {
        toolbar.setTitle(thread.getTitle()); // TODO fix action bar truncating long thread titles

        PostPagerFragment fragment = PostPagerFragment.newInstance(thread.getPath(), thread.getNumPages(), pageNum);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onLogin() {
        onForumSelected("EDMW", "main-forum", numPages);
    }
}
