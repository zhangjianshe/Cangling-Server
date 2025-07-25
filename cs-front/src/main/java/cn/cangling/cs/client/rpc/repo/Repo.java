package cn.cangling.cs.client.rpc.repo;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Repo {
    public String name;
    public Double lng;
    public Double lat;
    public Double zoom;
    public Double size;
    public String url;
    public boolean pared;
}
