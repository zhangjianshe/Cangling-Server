package cn.cangling.cs.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class MessagePanel extends Composite {
    private static final MessagePanelUiBinder ourUiBinder = GWT.create(MessagePanelUiBinder.class);
    @UiField
    Label lbMessage;
    public MessagePanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setMessage(String message) {
        lbMessage.setText(message);
    }

    interface MessagePanelUiBinder extends UiBinder<HTMLPanel, MessagePanel> {
    }

    public MessagePanel height(String height) {
        setHeight(height);
        return this;
    }

    public static MessagePanel create(String message) {
        MessagePanel panel = new MessagePanel();
        panel.setMessage(message);
        return panel;
    }
}