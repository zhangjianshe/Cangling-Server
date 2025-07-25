package cn.cangling.cs.client.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCsEventHandler extends HasHandlers {
    HandlerRegistration addCsEventHandler(CsEventHandler handler);
}
