package xyz.edmw.post;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.rest.RestClient;

public class PostFragment extends ListFragment {
    private static final String ARG_PATH = "path";
    private static final String ARG_PAGE = "arg_page";
    private List<Post> posts;

    public static PostFragment newInstance(String forum, int page) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, forum);
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        String path = args.getString(ARG_PATH);
        int page = args.getInt(ARG_PAGE);

        Call<List<Post>> calls = RestClient.getService().getPosts(path, page);
        calls.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    posts = response.body();
                    setListAdapter(new PostAdapter(getContext(), posts));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                posts = null;
            }
        });
    }
}
