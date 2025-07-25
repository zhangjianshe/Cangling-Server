package cn.cangling.cs.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @author Firstname Lastname
 * @since 1.0
 */
public interface CsResource extends ClientBundle {
    public CsResource INSTANCE = GWT.create(CsResource.class);

    @Source("main.css")
    CsCss css();

    @Source("image/logo.svg")
    TextResource logo();
}
