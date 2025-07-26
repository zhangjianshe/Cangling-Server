package cn.cangling.cs.client.repo;

import cn.cangling.cs.client.rpc.repo.Repo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RepositoryInfoPanel extends Composite {
    private static final RepositoryInfoPanelUiBinder ourUiBinder = GWT.create(RepositoryInfoPanelUiBinder.class);
    @UiField
    Label lbName;
    @UiField
    Label lbLink;

    public RepositoryInfoPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setRepository(Repo repo) {
        lbName.setText(repo.name);
        String url = Window.Location.getProtocol() + "://" + Window.Location.getHost();
        url += "/api/v1/xyz/" + repo.name + "/{z}/{x}/{y}.png";
        lbLink.setText(url);
    }

    interface RepositoryInfoPanelUiBinder extends UiBinder<VerticalPanel, RepositoryInfoPanel> {
    }
}