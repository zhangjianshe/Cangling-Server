package cn.cangling.cs.client.repo;

import cn.cangling.cs.client.component.map.CustomMap;
import cn.cangling.cs.client.events.CsEvent;
import cn.cangling.cs.client.rpc.repo.Repo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class RepoFrame extends Composite implements RequiresResize {
    private static final RepoFrameUiBinder ourUiBinder = GWT.create(RepoFrameUiBinder.class);
    private final String GROUP_BASE_MAP = "baseMap";
    @UiField
    RepositoryList repoList;
    @UiField
    CustomMap map;
    @UiField
    RepositoryInfoPanel repoInfo;
    @UiField
    DockLayoutPanel root;
    @UiField
    LayoutPanel content;

    public RepoFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        map.addTidiTuiLayerImage(GROUP_BASE_MAP);
        map.addTidiTuiLayerText(GROUP_BASE_MAP);
    }

    public void load() {
        repoList.load();
    }

    @UiHandler("repoList")
    public void repoListCs(CsEvent event) {
        switch (event.getEventType()) {
            case ET_SELECT: {
                if(!repoInfo.isVisible())
                {
                    content.setWidgetVisible(repoInfo, true);
                }
                Repo repo = event.getData();
                repoInfo.setRepository(repo);
                map.clearGroupLayer("select");
                String url = "api/v1/xyz/" + repo.name + "/{z}/{x}/{y}.png";
                map.addXyzLayer(url, "select");
                if (repo.pared) {
                    map.moveTo(repo.lng, repo.lat, repo.zoom);
                }
                break;
            }
        }
    }

    @Override
    public void onResize() {
        root.onResize();
    }

    interface RepoFrameUiBinder extends UiBinder<DockLayoutPanel, RepoFrame> {
    }
}