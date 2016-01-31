package xyz.edmw.navigation;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import xyz.edmw.MainActivity;
import xyz.edmw.R;
import xyz.edmw.User;

public class NavViewHolder {
    private final MainActivity activity;
    private final NavigationView view;
    private final NavHeaderViewHolder navHeaderViewHolder;

    public NavViewHolder(MainActivity activity, NavigationView view) {
        this.activity = activity;
        this.view = view;

        view.setNavigationItemSelectedListener(activity);
        View headerView = view.inflateHeaderView(R.layout.nav_header_main);
        navHeaderViewHolder = new NavHeaderViewHolder(activity, headerView);
    }

    public void setUser(User user) {
        navHeaderViewHolder.setUser(user);

        Menu menu = view.getMenu();
        menu.clear();
        if (user == null) {
            view.inflateMenu(R.menu.activity_main_drawer_guest);
        } else {
            view.inflateMenu(R.menu.activity_main_drawer_member);
            MenuItem messages = menu.findItem(R.id.nav_messages);
            messages.setTitle(user.getMessages());
        }
    }
}
