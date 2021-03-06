package wiki.scene.shop.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wiki.scene.loadmore.utils.SceneLogUtil;
import wiki.scene.shop.R;
import wiki.scene.shop.adapter.ImageListAdapter;
import wiki.scene.shop.config.AppConfig;
import wiki.scene.shop.http.api.ApiUtil;
import wiki.scene.shop.mvp.BaseMvpActivity;
import wiki.scene.shop.ui.mine.mvpview.IShareOrderView;
import wiki.scene.shop.ui.mine.presenter.ShareOrderPresenter;
import wiki.scene.shop.utils.ToastUtils;
import wiki.scene.shop.widgets.CustomeGridView;
import wiki.scene.shop.widgets.LoadingDialog;

/**
 * 晒单
 * Created by scene on 17-8-14.
 */

public class ShareOrderActivity extends BaseMvpActivity<IShareOrderView, ShareOrderPresenter> implements IShareOrderView, ImageListAdapter.OnClickImageListListener {
    public final static String ARG_GOODS_NAME = "goods_name";
    public final static String ARG_CYCLE_CODE = "cycle_code";
    public final static String ARG_ORDER_ID = "order_id";
    public final static String ARG_CYCLE_ID = "cycle_id";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_text)
    TextView toolbarText;
    @BindView(R.id.goods_name)
    TextView goodsName;
    @BindView(R.id.goods_cycle_code)
    TextView goodsCycleCode;
    @BindView(R.id.share_content)
    EditText shareContent;
    @BindView(R.id.imageGridView)
    CustomeGridView imageGridView;

    Unbinder unbinder;

    private List<String> imageList;
    private ImageListAdapter adapter;

    private String orderId;
    private String cycleId;


    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_share_order);
        unbinder = ButterKnife.bind(this);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        toolbarText.setText(R.string.send);
        toolbarTitle.setText(R.string.bottom_tab_share);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {
        loadingDialog = LoadingDialog.getInstance(ShareOrderActivity.this);
        Intent intent = getIntent();
        if (intent != null) {
            String goodsNameStr = intent.getStringExtra(ARG_GOODS_NAME);
            String goodsCycleCodeStr = intent.getStringExtra(ARG_CYCLE_CODE);
            orderId = intent.getStringExtra(ARG_ORDER_ID);
            cycleId = intent.getStringExtra(ARG_CYCLE_ID);
            goodsName.setText(goodsNameStr);
            goodsCycleCode.setText(String.format(getString(R.string.share_goods_times), goodsCycleCodeStr));
        }

        imageList = new ArrayList<>();
        imageList.add("add");
        adapter = new ImageListAdapter(ShareOrderActivity.this, imageList);
        imageGridView.setAdapter(adapter);
        adapter.setOnClickImageListListener(this);
        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imageList.size() - 1) {
                    chooseImages(10 - imageList.size());
                }
            }
        });
    }

    @Override
    public ShareOrderPresenter initPresenter() {
        return new ShareOrderPresenter(this);
    }

    @Override
    public void showLoading(@StringRes int resId) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(ApiUtil.SHARE_ORDER_TAG);
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClickImageListDelete(int position) {
        imageList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    public void chooseImages(int number) {
        ImgSelConfig config = new ImgSelConfig.Builder(ShareOrderActivity.this, loader)
                // 是否多选
                .multiSelect(true)
                .rememberSelected(false)
                .maxNum(number)
                .btnText(getString(R.string.confirm))
                // 确定按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(getResources().getColor(R.color.colorPrimary))
                // 返回图标ResId
                .backResId(R.drawable.ic_toolbar_back)
                .title(getString(R.string.image))
                .titleColor(Color.WHITE)
                .titleBgColor(getResources().getColor(R.color.colorPrimary))
                .allImagesText(getString(R.string.all_images))
                .needCrop(false)
                .cropSize(1, 1, 200, 200)
                // 第一个是否显示相机
                .needCamera(true)
                .build();

        ImgSelActivity.startActivity(ShareOrderActivity.this, config, AppConfig.CHOOSE_AVATER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == AppConfig.CHOOSE_AVATER_REQUEST_CODE) {
                try {
                    //imageList.clear();
                    List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
                    if (pathList != null && pathList.size() > 0) {
                        imageList.addAll(0, pathList);
                    }
                    //imageList.add("add");
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    SceneLogUtil.e("选择图片异常了");
                }
            }
        }
    }

    @Override
    public void showProgressDialog(String msg) {
        try {
            loadingDialog.showLoadingDialog(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgressDialog(@StringRes int resId) {
        try {
            loadingDialog.showLoadingDialog(getString(resId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (loadingDialog != null) {
            loadingDialog.cancelLoadingDialog();
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        ToastUtils.getInstance(ShareOrderActivity.this).showToast(resId);
    }

    @Override
    public void showMessage(String msg) {
        ToastUtils.getInstance(ShareOrderActivity.this).showToast(msg);
    }

    @Override
    public void shareSuccess() {
        onBackPressed();
        showMessage(R.string.share_order_success);
    }

    @Override
    public void compressSuccess(List<File> fileList) {
        try {
            presenter.shareOrder(shareContent.getText().toString().trim(), orderId, cycleId, fileList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.toolbar_text)
    public void onClickSend() {
        if (shareContent.getText().toString().trim().isEmpty()) {
            showMessage(R.string.please_edit_content);
            return;
        }
        showProgressDialog(R.string.loading);
        if (imageList.size() > 1) {
            //压缩图片
            presenter.compressImages(ShareOrderActivity.this, imageList);
        } else {
            presenter.shareOrder(shareContent.getText().toString().trim(), orderId, cycleId, null);
        }
    }

}
