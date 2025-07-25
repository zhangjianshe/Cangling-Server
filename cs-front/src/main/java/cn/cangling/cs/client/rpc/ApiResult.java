package cn.cangling.cs.client.rpc;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ApiResult <T>{
    public T data;
    String message;
    Number code;

    @JsOverlay
    public final boolean isSuccess() {
        if(code == null) {
            return false;
        }
        int c = code.intValue();
        return (c == 0 || c == 200);
    }
    @JsOverlay
    public final String getMessage() {
        return message== null ? "No message" : message;
    }
    @JsOverlay
    public final T getData() {
        return data;
    }
}
