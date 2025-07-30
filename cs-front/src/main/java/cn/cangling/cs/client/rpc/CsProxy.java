package cn.cangling.cs.client.rpc;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.XMLHttpRequest;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Cs ApiProxy
 */
public class CsProxy {

    /**
     * get api result
     *
     * @param url
     * @param headers
     * @param data
     * @param callback
     * @param <T>
     */
    public static <T> void postApiResult(String url, JsPropertyMap headers, String data, Callback<ApiResult<T>, String> callback) {
        httpFetch("POST", url, headers, data, new Callback<String, String>() {
            @Override
            public void onFailure(String reason) {
                if (callback != null) {
                    callback.onFailure(reason);
                }
            }

            @Override
            public void onSuccess(String result) {
                try {
                    Object obj = Global.JSON.parse(result);
                    if (obj == null) {
                        if (callback != null) {
                            callback.onFailure("Invalid Json");
                        } else {
                            DomGlobal.console.log("Invalid Json");
                        }
                        return;
                    }
                    ApiResult<T> apiResult = Js.uncheckedCast(obj);
                    if (apiResult.isSuccess()) {
                        if (callback != null) {
                            callback.onSuccess(apiResult);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure(apiResult.getMessage());

                        } else {
                            DomGlobal.console.log(apiResult.getMessage());
                        }
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    } else {
                        DomGlobal.console.log(e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * get api result
     *
     * @param url
     * @param headers
     * @param data
     * @param callback
     * @param <T>
     */
    public static <T> void getApiResult(String url, JsPropertyMap headers, String data, Callback<ApiResult<T>, String> callback) {
        httpFetch("GET", url, headers, data, new Callback<String, String>() {
            @Override
            public void onFailure(String reason) {
                if (callback != null) {
                    callback.onFailure(reason);
                }
            }

            @Override
            public void onSuccess(String result) {
                try {
                    Object obj = Global.JSON.parse(result);
                    if (obj == null) {
                        callback.onFailure("Invalid Json");
                        return;
                    }
                    ApiResult<T> apiResult = Js.uncheckedCast(obj);
                    if (apiResult.isSuccess()) {
                        callback.onSuccess(apiResult);
                    } else {
                        callback.onFailure(apiResult.getMessage());
                    }
                } catch (Exception e) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public static void httpFetch(String method, String url, JsPropertyMap headers, String data, Callback<String, String> callback) {
        XMLHttpRequest request = new XMLHttpRequest();
        request.open(method, url);
        if (headers != null) {
            headers.forEach(key -> request.setRequestHeader(key, headers.get(key).toString()));
        }
        request.setRequestHeader("Content-Type", "application/json");
        request.onreadystatechange = new XMLHttpRequest.OnreadystatechangeFn() {
            @Override
            public Object onInvoke(Event p0) {
                if (request.readyState == XMLHttpRequest.DONE) {
                    if (callback != null) {
                        if (request.status == 200) {
                            callback.onSuccess(request.responseText);
                        } else {
                            callback.onFailure(request.responseText);
                        }
                    }
                }
                return true;
            }
        };
        request.onerror = p0 -> {
            if (callback != null) {
                callback.onFailure(p0.toString());
            }
            return null;
        };
        request.send(data);
    }

    public static String url(String path) {
        return GWT.getHostPageBaseURL() + path;
    }
}
