package wiki.scene.shop.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunfusheng.glideimageview.GlideImageView;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;
import wiki.scene.loadmore.utils.SceneLogUtil;
import wiki.scene.shop.R;
import wiki.scene.shop.ShopApplication;
import wiki.scene.shop.config.AppConfig;
import wiki.scene.shop.entity.UserInfo;
import wiki.scene.shop.event.ChooseAvaterResultEvent;
import wiki.scene.shop.event.RegisterSuccessEvent;
import wiki.scene.shop.mvp.BaseMvpActivity;
import wiki.scene.shop.ui.mine.mvpview.IMineInfoView;
import wiki.scene.shop.ui.mine.presenter.MineInfoPresenter;
import wiki.scene.shop.utils.ToastUtils;
import wiki.scene.shop.utils.UpdatePageUtils;
import wiki.scene.shop.widgets.LoadingDialog;

/**
 * Case By:个人资料
 * package:wiki.scene.shop.fragment.mine
 * Author：scene on 2017/6/29 17:44
 */

public class MineInfoActivity extends BaseMvpActivity<IMineInfoView, MineInfoPresenter> implements IMineInfoView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.user_avater)
    GlideImageView userAvater;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.sex_male)
    RadioButton sexMale;
    @BindView(R.id.sex_female)
    RadioButton sexFemale;
    @BindView(R.id.sex_radiogroup)
    RadioGroup sexRadiogroup;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.phone_number)
    TextView phoneNumber;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_info);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        UpdatePageUtils.updatePagePosition(AppConfig.POSITION_SETTING_AVATAR, 0);
    }

    private void initToolbar() {
        toolbarTitle.setText(R.string.person_data);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        loadingDialog = LoadingDialog.getInstance(this);
        userAvater.loadCircleImage(ShopApplication.userInfo.getAvatar(), R.drawable.ic_default_avater);
        username.setText(ShopApplication.userInfo.getNickname());
        phoneNumber.setText(ShopApplication.userInfo.getMobile());
        sexRadiogroup.check(ShopApplication.userInfo.getSex() == 1 ? R.id.sex_male : R.id.sex_female);
        username.setSelection(username.getText().toString().trim().length());
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    public void chooseAvater() {
        ImgSelConfig config = new ImgSelConfig.Builder(MineInfoActivity.this, loader)
                // 是否多选
                .multiSelect(false)
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

        ImgSelActivity.startActivity(MineInfoActivity.this, config, AppConfig.CHOOSE_AVATER_REQUEST_CODE);
    }

    @OnClick(R.id.user_avater)
    public void onClickUserAvater() {
        chooseAvater();
    }

    @OnClick(R.id.save)
    public void onClickSave() {
        presenter.updateInfo();
    }

    @Override
    public void hideLoading() {
        loadingDialog.cancelLoadingDialog();
    }

    @Override
    public MineInfoPresenter initPresenter() {
        return new MineInfoPresenter(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == AppConfig.CHOOSE_AVATER_REQUEST_CODE) {
                try {
                    List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
                    if (pathList != null && pathList.size() > 0) {
                        changeUserAvater(pathList.get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SceneLogUtil.e("选择头像异常了");
                }
            }
        }
    }

    public void changeUserAvater(String filePath) {
        SceneLogUtil.e("changeUserAvater");
        Luban.compress(this, new File(filePath)).launch(new OnCompressListener() {
            @Override
            public void onStart() {
                showLoading("正在上传头像...");
            }

            @Override
            public void onSuccess(File file) {
                SceneLogUtil.e("压缩完成");
                presenter.updateUserAvater(file.getAbsolutePath());
            }

            @Override
            public void onError(Throwable e) {
                SceneLogUtil.e("压缩失败");
                hideLoading();
                showFail("头像上传失败");
            }
        });

    }

    @Override
    public void showLoading(String msg) {
        loadingDialog.showLoadingDialog(msg);
    }

    @Override
    public void showLoading(@StringRes int resId) {
        showLoading(getString(resId));
    }

    @Override
    public String getNickName() {
        return username.getText().toString().trim();
    }

    @Override
    public int getSex() {
        return sexMale.isChecked() ? 1 : 2;
    }

    @Override
    public void showSuccess() {

    }

    @Override
    public void showFail(String str) {
        ToastUtils.getInstance(MineInfoActivity.this).showToast(str);
    }

    @Override
    public void updateUserAvaterSuccess(String filePath) {
        EventBus.getDefault().post(new ChooseAvaterResultEvent(filePath));
        userAvater.loadCircleImage(filePath, R.drawable.ic_default_avater);
        UserInfo userInfo = ShopApplication.userInfo;
        userInfo.setAvatar(filePath);
        EventBus.getDefault().post(new RegisterSuccessEvent(userInfo));
    }

    @Override
    public void updateUserInfoSuccess() {
        UserInfo userInfo = ShopApplication.userInfo;
        userInfo.setNickname(getNickName());
        userInfo.setSex(getSex());
        EventBus.getDefault().post(new RegisterSuccessEvent(userInfo));
    }

}
