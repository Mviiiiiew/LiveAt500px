package com.wazabi.liveat500px.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.wazabi.liveat500px.R;
import com.wazabi.liveat500px.activity.MoreInfoActivity;
import com.wazabi.liveat500px.adapter.PhotoListAdapter;
import com.wazabi.liveat500px.dao.PhotoItemCollectionDao;
import com.wazabi.liveat500px.dao.PhotoItemDao;
import com.wazabi.liveat500px.datatype.MutableInteger;
import com.wazabi.liveat500px.manager.HttpManager;
import com.wazabi.liveat500px.manager.PhotoListManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {


    /*****************
     VARIABLES

     *****************/
    public  interface  FragmentListener{
        void  onPhotoItemClicked(PhotoItemDao dao);
    }


    ListView listView;
    PhotoListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;
    Button btnNewPhotos;
    MutableInteger lastPositionInteger;



    /*****************
     FUNCTIONS
     *****************/

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);


        if(savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView,savedInstanceState);
        return rootView;
    }
    private void init(Bundle savedInstanceState) {
        photoListManager = new PhotoListManager();
        lastPositionInteger = new MutableInteger(-1);
    }

    private void initInstances(View rootView,Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        btnNewPhotos = (Button) rootView.findViewById(R.id.btnNewPhotos);
        btnNewPhotos.setOnClickListener(buttonClickListener);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new PhotoListAdapter(lastPositionInteger);
        listAdapter.setDao(photoListManager.getDao());
        listView.setAdapter(listAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);
        listView.setOnScrollListener(listviewScrollListener);
        listView.setOnItemClickListener(listViewItemClickListener);
        if(savedInstanceState == null)
        refreshData();


    }

    private void refreshData() {
        if (photoListManager.getCount() == 0)
            reloadData();
        else
            reloadDataNewer();
    }


    private void reloadDataNewer() {
        int maxId = photoListManager.getMaximumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoListAfterId(maxId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD_NEWER));
    }


    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD));
    }

    boolean isLoadingMore = false;

    private void loadMoreData() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;

        int minId = photoListManager.getMinimumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoListBeforeId(minId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_LOAD_MORE));
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("photoListManager",
                photoListManager.onSveInstanceState());
        outState.putBundle("lastPositionInteger",
                lastPositionInteger.onSaveInstanceState()  );


        // Save Instance State here
    }
    private void onRestoreInstanceState(Bundle saveInstanceState){
        photoListManager.onRestoreInstanceState(
                saveInstanceState.getBundle("photoListManager"));

        lastPositionInteger.onRestoreInstanceState(saveInstanceState.getBundle("lastPositionInteger"));


    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void showButtonNewPhotos() {
        btnNewPhotos.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoom_face_in);
        btnNewPhotos.startAnimation(anim);
    }

    private void hideButtonNewPhotos() {
        btnNewPhotos.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoom_face_out);
        btnNewPhotos.startAnimation(anim);
    }

    private void showToast(String text) {

        Toast.makeText(Contextor.getInstance().getContext(), text, Toast.LENGTH_SHORT).show();

    }

    /*****************
     LISTENER ZONE
     *****************/
    AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if(position < photoListManager.getCount()) {
                PhotoItemDao dao = photoListManager.getDao().getData().get(position);
                FragmentListener listener = (FragmentListener) getActivity();
                listener.onPhotoItemClicked(dao);
            }
        }
    };

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == btnNewPhotos) {
                listView.smoothScrollToPosition(0);
                hideButtonNewPhotos();
            }

        }
    };


    SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    AbsListView.OnScrollListener listviewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            if (absListView == listView) {


                swipeRefreshLayout.setEnabled(i == 0);
                if (i + i1 >= i2) {
                    if (photoListManager.getCount() > 0) {
                        loadMoreData();
                    }

                }
            }

        }

    };



    /*****************
     INNER CLASS
     *****************/

    class PhotoListLoadCallback implements Callback<PhotoItemCollectionDao> {

        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;

        int mode;

        public PhotoListLoadCallback(int mode) {
            this.mode = mode;
        }


        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
            swipeRefreshLayout.setRefreshing(false);
            if (response.isSuccessful()) {
                PhotoItemCollectionDao dao = response.body();
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                View c = listView.getChildAt(0);
                int top = c == null ? 0 : c.getTop();

                if (mode == MODE_RELOAD_NEWER) {
                    photoListManager.insertDaoAtTopPosition(dao);
                } else if (mode == MODE_LOAD_MORE) {
                    photoListManager.appenDaoAtBottomPosition(dao);
                    clearLoadingMoreFlagIfCapable(mode);
                } else {
                    photoListManager.setDao(dao);
                }
                listAdapter.setDao(photoListManager.getDao());
                listAdapter.notifyDataSetChanged();


                if (mode == MODE_RELOAD_NEWER) {
                    int additionalSize = (dao != null && dao.getData() != null) ? dao.getData().size() : 0;
                    listAdapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition + additionalSize, top);
                    if (additionalSize > 0)
                        showButtonNewPhotos();
                }
                showToast("Load Completed ");

            } else {
                clearLoadingMoreFlagIfCapable(mode);
                try {
                    showToast(response.errorBody().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
            clearLoadingMoreFlagIfCapable(mode);
            swipeRefreshLayout.setRefreshing(false);
            showToast(t.toString());

        }

        private void clearLoadingMoreFlagIfCapable(int mode) {
            if (mode == MODE_LOAD_MORE)
                isLoadingMore = false;
        }
    }
}
