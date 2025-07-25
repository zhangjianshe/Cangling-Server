package cn.cangling.cs.client.repo;

import cn.cangling.cs.client.events.CsEvent;
import cn.cangling.cs.client.events.CsEventHandler;
import cn.cangling.cs.client.events.HasCsEventHandler;
import cn.cangling.cs.client.rpc.ApiResult;
import cn.cangling.cs.client.rpc.CsProxy;
import cn.cangling.cs.client.rpc.repo.Repo;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import static cn.cangling.cs.client.events.EventType.ET_SELECT;

public class RepositoryList extends Composite implements HasCsEventHandler {
    private static final RepositoryListUiBinder ourUiBinder = GWT.create(RepositoryListUiBinder.class);
    @UiField
    VerticalPanel root;

    public RepositoryList() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public HandlerRegistration addCsEventHandler(CsEventHandler handler) {
        return addHandler(handler, CsEvent.TYPE);
    }

    public void load() {
        CsProxy.getApiResult("/api/v1/repositories", null, null, new Callback<ApiResult<Repo[]>, String>() {
            @Override
            public void onSuccess(ApiResult<Repo[]> result) {
                root.clear();
                for (Repo repo : result.data) {
                    RepositoryItem item = new RepositoryItem();
                    item.setRepo(repo);
                    root.add(item);
                    item.addDomHandler(e -> {
                        fireEvent(CsEvent.create(ET_SELECT, repo));
                    }, ClickEvent.getType());
                }
            }

            @Override
            public void onFailure(String reason) {
                Window.alert(reason);
            }
        });

    }

    interface RepositoryListUiBinder extends UiBinder<VerticalPanel, RepositoryList> {
    }
}