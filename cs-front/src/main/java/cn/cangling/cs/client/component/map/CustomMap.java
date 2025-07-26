package cn.cangling.cs.client.component.map;

import cn.cangling.cs.client.util.Strings;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import elemental2.dom.DomGlobal;
import ol.*;
import ol.layer.Layer;
import ol.proj.Projection;
import ol.source.Xyz;
import ol.source.XyzOptions;

/**
 * Map Component
 * this map crs is 3857
 */
public class CustomMap extends Composite implements RequiresResize {
    Map map;

    Projection proj4326;// =  Projection.get("EPSG:4326");
    Projection proj3857;// =  Projection.get("EPSG:3857");

    public CustomMap() {
        HTMLPanel panel = new HTMLPanel("");
        panel.setWidth("100%");
        panel.setHeight("100%");
        initWidget(panel);

        proj4326 = Projection.get("EPSG:4326");
        proj3857 = Projection.get("EPSG:3857");


        MapOptions mapOptions = OLFactory.createOptions();
        mapOptions.setTarget(panel.getElement());

        View view = OLFactory.createView();
        mapOptions.setView(view);
        view.setCenter(to3857(116.404, 39.915));
        view.setZoom(4);

        map = new Map(mapOptions);


    }

    public Coordinate to3857(double lng, double lat) {
        return Projection.transform(new Coordinate(lng, lat), proj4326, proj3857);
    }

    public Coordinate to4326(double x, double y) {
        return Projection.transform(new Coordinate(x, y), proj3857, proj4326);
    }

    @Override
    public void onResize() {
        map.updateSize();
    }

    public Layer addOsmLayer() {
        ol.layer.Tile layer = new ol.layer.Tile();
        layer.setSource(new ol.source.Osm());
        map.addLayer(layer);
        return layer;
    }

    public Layer addTidiTuiLayerImage(String group) {
        String url = Window.Location.getProtocol() + "//t{0-7}.tianditu.gov.cn/DataServer?T=img_w&x={x}&y={y}&l={z}&tk=4dad0308e0356588887e03a2a91f2e90";
        DomGlobal.console.log(url);
        return addXyzLayer(url, group);
    }

    public Layer addTidiTuiLayerText(String group) {
        String url = Window.Location.getProtocol() + "//t{0-7}.tianditu.gov.cn/DataServer?T=cia_w&x={x}&y={y}&l={z}&tk=4dad0308e0356588887e03a2a91f2e90";
        DomGlobal.console.log(url);
        return addXyzLayer(url, group);
    }

    public void removeLayer(Layer layer) {
        map.removeLayer(layer);
    }

    /**
     * 如果 group 为空,清除所有图层 清除没有设定 groupName 的图层
     *
     * @param group
     */
    public void clearGroupLayer(String group) {

        map.getLayers().forEach((item, index, array) -> {
            if (Strings.isBlank(group)) {
                if (Strings.isBlank(item.get("groupName"))) {
                    map.removeLayer(item);
                }
            } else {
                if (group.equals(item.get("groupName"))) {
                    map.removeLayer(item);
                }
            }
        });
    }

    public Layer addXyzLayer(String url, String group) {
        ol.layer.Tile layer = new ol.layer.Tile();

        XyzOptions xyzOptions = OLFactory.createOptions();
        xyzOptions.setUrl(url);
        Xyz xyz = OLFactory.createXyz(xyzOptions);
        layer.set("groupName", group);
        layer.setSource(xyz);
        map.addLayer(layer);
        return layer;
    }

    public void moveToChina() {
        map.getView().setCenter(to3857(116.404, 39.915));
        map.getView().setZoom(4);
    }

    public void moveTo(Double lng, Double lat, Double zoom) {
        map.getView().setCenter(to3857(lng, lat));
        map.getView().setZoom(zoom);
    }
}
