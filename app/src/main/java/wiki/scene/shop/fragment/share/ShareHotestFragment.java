package wiki.scene.shop.fragment.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wiki.scene.loadmore.PtrClassicFrameLayout;
import wiki.scene.loadmore.PtrDefaultHandler;
import wiki.scene.loadmore.PtrFrameLayout;
import wiki.scene.loadmore.StatusViewLayout;
import wiki.scene.loadmore.recyclerview.RecyclerAdapterWithHF;
import wiki.scene.loadmore.utils.PtrLocalDisplay;
import wiki.scene.shop.R;
import wiki.scene.shop.adapter.ShareAdapter;
import wiki.scene.shop.fragment.share.mvpview.IShareHotestView;
import wiki.scene.shop.fragment.share.presenter.ShareHotestPrsenter;
import wiki.scene.shop.itemDecoration.SpacesItemDecoration;
import wiki.scene.shop.mvp.BaseMvpFragment;

/**
 * Case By:最热
 * package:wiki.scene.shop.fragment.share
 * Author：scene on 2017/6/29 11:57
 */

public class ShareHotestFragment extends BaseMvpFragment<IShareHotestView, ShareHotestPrsenter> implements IShareHotestView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;
    @BindView(R.id.status_layout)
    StatusViewLayout statusLayout;
    Unbinder unbinder;

    private List<String> list = new ArrayList<>();
    private ShareAdapter adapter;
    private RecyclerAdapterWithHF mAdapter;

    public static ShareHotestFragment newInstance() {
        Bundle args = new Bundle();
        ShareHotestFragment fragment = new ShareHotestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_hotest, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public ShareHotestPrsenter initPresenter() {
        return new ShareHotestPrsenter(this);
    }


    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        initView();
        hideLoading();
    }

    private void initView() {
        ptrLayout.setLastUpdateTimeRelateObject(this);
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }
        adapter = new ShareAdapter(_mActivity, list);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(_mActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(PtrLocalDisplay.dp2px(1)));
        recyclerView.setAdapter(mAdapter);
        ptrLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        statusLayout.showContent();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}