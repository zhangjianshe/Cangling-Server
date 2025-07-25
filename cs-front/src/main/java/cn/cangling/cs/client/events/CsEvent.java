package cn.cangling.cs.client.events;

import com.google.gwt.event.shared.GwtEvent;
import lombok.Getter;
import lombok.Setter;

public class CsEvent extends GwtEvent<CsEventHandler> {
    public static Type<CsEventHandler> TYPE = new Type<CsEventHandler>();
    @Setter
    Object data;
    @Getter
    EventType eventType;

    public CsEvent() {
        this.eventType = EventType.ET_NONE;
        this.data = null;
    }

    public CsEvent(EventType eventType, Object data) {
        this.data = data;
        this.eventType = eventType;
    }

    public static GwtEvent<?> create(EventType eventType, Object data) {
        return new CsEvent(eventType, data);
    }

    public static GwtEvent<?> messageEvent(String message) {
        return new CsEvent(EventType.ET_MESSAGE, message);
    }

    public <T> T getData() {
        return (T) data;
    }

    public Type<CsEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CsEventHandler handler) {
        handler.onCsEvent(this);
    }
}
