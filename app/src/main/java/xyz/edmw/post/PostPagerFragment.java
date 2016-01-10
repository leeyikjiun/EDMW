package xyz.edmw.post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;

public class PostPagerFragment extends Fragment {
    private static final String ARG_PATH = "arg_path";
    private static final String ARG_PAGES = "arg_num_pages";
    private static final String ARG_PAGE_NUM = "arg_page_num";

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    public static PostPagerFragment newInstance(String path, int numPages, int pageNum) {
        PostPagerFragment fragment = new PostPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        args.putInt(ARG_PAGES, numPages);
        args.putInt(ARG_PAGE_NUM, pageNum);
        fragment.setArguments(args);
        return fragment;
    }

    public PostPagerFragment() {

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        String path = args.getString(ARG_PATH);
        int numPages = args.getInt(ARG_PAGES);
        int pageNum = args.getInt(ARG_PAGE_NUM);

        viewPager.setAdapter(new PostPagerAdapter(getChildFragmentManager(), path, numPages));
        viewPager.setCurrentItem(pageNum - 1);
    }
}
