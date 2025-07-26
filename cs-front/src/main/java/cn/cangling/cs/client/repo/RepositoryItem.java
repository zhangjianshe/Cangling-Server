package cn.cangling.cs.client.repo;

import cn.cangling.cs.client.rpc.repo.Repo;
import cn.cangling.cs.client.util.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import lombok.Getter;

public class RepositoryItem extends Composite {
    private static final RepositoryItemUiBinder ourUiBinder = GWT.create(RepositoryItemUiBinder.class);
    @Getter
    Repo repo;
    @UiField
    Label lbName;
    @UiField
    Label lbSize;
    public RepositoryItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
        lbName.setText(repo.name);
        lbSize.setText(Strings.formatFileSize(repo.size));
    }

    public void setSelected(boolean selected) {
        getElement().setAttribute("selected", selected ? "true" : "");
    }

    interface RepositoryItemUiBinder extends UiBinder<HTMLPanel, RepositoryItem> {
    }
}