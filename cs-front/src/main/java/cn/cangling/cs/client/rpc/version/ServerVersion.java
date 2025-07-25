package cn.cangling.cs.client.rpc.version;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ServerVersion {
    public String name;
    public String version;
    public String author;
    public String email;
    public String compileTime;
    public String gitHash;
}
