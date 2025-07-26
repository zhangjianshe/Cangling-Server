package cn.cangling.cs.client;


import cn.cangling.cs.client.repo.RepoFrame;
import cn.cangling.cs.client.rpc.ApiResult;
import cn.cangling.cs.client.rpc.CsProxy;
import cn.cangling.cs.client.rpc.version.ServerVersion;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;


public class MainFrame extends Composite implements RequiresResize {
    private static final MainFrameUiBinder ourUiBinder = GWT.create(MainFrameUiBinder.class);
    @UiField
    Image logo;
    @UiField
    DockLayoutPanel root;
    @UiField
    Label lbName;
    @UiField
    Label lbVersion;
    @UiField
    RepoFrame repoFrame;

    public MainFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void onResize() {
        root.onResize();
    }


    @Override
    protected void onLoad() {
        repoFrame.load();
        load();
    }

    /**
     * 加载 版本信息
     */
    private void load() {

        CsProxy.getApiResult(CsProxy.url("/api/v1/server"), null, null,
                new Callback<ApiResult<ServerVersion>, String>() {
                    @Override
                    public void onFailure(String reason) {

                    }

                    @Override
                    public void onSuccess(ApiResult<ServerVersion> result) {
                        if (result.isSuccess()) {
                            if (result.data.hasNewVersion()) {
                                lbVersion.setText("Ver: "+result.data.version + "->" + result.data.latestVersion);
                            } else {
                                lbVersion.setText("Ver: "+result.data.version);
                            }
                            logo.setTitle(result.data.author);
                        }
                    }
                });
    }

    interface MainFrameUiBinder extends UiBinder<DockLayoutPanel, MainFrame> {
    }

}