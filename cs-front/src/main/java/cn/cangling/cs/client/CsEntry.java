package cn.cangling.cs.client;

import cn.cangling.cs.client.resource.CsResource;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class CsEntry implements EntryPoint {
    public void onModuleLoad() {
        CsResource.INSTANCE.css().ensureInjected();
        RootLayoutPanel root = RootLayoutPanel.get();
        root.add(new MainFrame());
    }
}
