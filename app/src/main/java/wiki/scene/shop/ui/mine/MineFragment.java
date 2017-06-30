package wiki.scene.shop.ui.mine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import wiki.scene.shop.R;
import wiki.scene.shop.activity.LoginActivity;
import wiki.scene.shop.event.ChooseAvaterResultEvent;
import wiki.scene.shop.ui.mine.mvpview.IMineView;
import wiki.scene.shop.ui.mine.presenter.MinePresenter;
import wiki.scene.shop.mvp.BaseMainMvpFragment;

/**
 * Case By:我的
 * package:wiki.scene.shop.fragment.indiana
 * Author：scene on 2017/6/26 14:13
 */
public class MineFragment extends BaseMainMvpFragment<IMineView, MinePresenter> implements IMineView {

    @BindView(R.id.user_avater)
    ImageView userAvater;
    @BindView(R.id.image_level)
    TextView imageLevel;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.level)
    TextView level;
    @BindView(R.id.score)
    TextView score;
    @BindView(R.id.coin_number)
    TextView coinNumber;

    private ProgressDialog progressDialog;

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(_mActivity);
        progressDialog.setMessage("正在上传");
    }

    @OnClick(R.id.user_avater)
    public void onClickUserAvater() {
        startActivity(new Intent(_mActivity, MineInfoActivity.class));
    }

    /**
     * 充值
     */
    @OnClick(R.id.recharge)
    public void onClickRecharge() {
        presenter.recharge();
    }

    /**
     * 夺宝记录
     */
    @OnClick(R.id.indiana_record)
    public void onClickIndianaRecord() {
        presenter.recharge();
    }

    @Override
    public void showLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    @Override
    public MinePresenter initPresenter() {
        return new MinePresenter(this);
    }


    /**
     * 登录
     */
    @Override
    public void enterLogin() {
        startActivity(new Intent(_mActivity, LoginActivity.class));
    }

    /**
     * 夺宝记录
     */
    @Override
    public void enterIndianaRecord() {

    }

    /**
     * 中奖记录
     */
    @Override
    public void enterWinRecord() {

    }

    /**
     * 夺宝攻略
     */
    @Override
    public void enterIndianaRaiders() {

    }

    /**
     * 晒单
     */
    @Override
    public void enterMineShare() {

    }

    /**
     * 我的红包
     */
    @Override
    public void enterMineRed() {

    }

    /**
     * 我的收货地址
     */
    @Override
    public void enterReceiverAddress() {

    }

    /**
     * 服务中心
     */
    @Override
    public void enterServiceCenter() {

    }

    /**
     * 分享app
     */
    @Override
    public void enterShareApp() {

    }

    /**
     * 设置
     */
    @Override
    public void enterSetting() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeUserAvater(ChooseAvaterResultEvent event) {
        Glide.with(this).load("file://" + event.avaterPath)
                .bitmapTransform(new CropCircleTransformation(_mActivity))
                .into(userAvater);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}