package cn.cangling.cs.client.repo;

import cn.cangling.cs.client.component.map.CustomMap;
import cn.cangling.cs.client.events.CsEvent;
import cn.cangling.cs.client.rpc.CsProxy;
import cn.cangling.cs.client.rpc.repo.Repo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import ol.Coordinate;


public class RepoFrame extends Composite implements RequiresResize {
    private static final RepoFrameUiBinder ourUiBinder = GWT.create(RepoFrameUiBinder.class);

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
    @UiField
    CheckBox checkTiandi;
    @UiField
    CheckBox checkTiandiText;
    Repo currentRepo = null;

    public RepoFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        map.addTidiTuiLayerImage(MapNames.GROUP_BASEMAP,0);
        map.addTidiTuiLayerText(MapNames.GROUP_BASEMAP,100);
        checkTiandi.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                switchBaseMap(MapNames.MAP_TIANDITU_IMG, event.getValue(),0);
            }
        });
        checkTiandiText.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                switchBaseMap(MapNames.MAP_TIANDITU_TXT, event.getValue(),100);
            }
        });
    }

    /**
     * 切换地图
     *
     * @param value
     */
    private void switchBaseMap(String mapName, Boolean value,int zIndex) {
        if (value) {
            map.addLayerByName(MapNames.GROUP_BASEMAP, mapName,zIndex);
        } else {
            map.removeLayerByName(MapNames.GROUP_BASEMAP, mapName);
        }
    }

    public void load() {
        repoList.load();
    }

    @UiHandler("repoList")
    public void repoListCs(CsEvent event) {
        switch (event.getEventType()) {
            case ET_SELECT: {
                if (!repoInfo.isVisible()) {
                    content.setWidgetVisible(repoInfo, true);
                }
                if (currentRepo != null) {
                    // save currentRepo's init Location
                    currentRepo.zoom = (int) map.getZoom();
                    Coordinate center = map.getCenter();
                    currentRepo.lng = center.lon();
                    currentRepo.lat = center.lat();
                    String data = Global.JSON.stringify(currentRepo);
                    DomGlobal.console.log(data);
                    CsProxy.postApiResult("/api/v1/xyz/" + Global.encodeURIComponent(currentRepo.name) + "/update", null, data, null);
                }
                Repo repo = event.getData();
                currentRepo = repo;
                repoInfo.setRepository(repo);
                map.clearGroupLayer("select");
                String url = "api/v1/xyz/" + Global.encodeURIComponent(repo.name) + "/{z}/{x}/{y}.png";
                map.addXyzLayer(url, MapNames.GROUP_SELECTED, repo.name,50);
                if (repo.pared) {
                    map.moveTo(repo.lng, repo.lat, Double.valueOf(repo.zoom));
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