package cn.cangling.cs.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface CsEventHandler extends EventHandler {
    void onCsEvent(CsEvent event);
}
