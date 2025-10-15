package org.smartregister.chw.core.util;

import com.mapbox.geojson.BoundingBox;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.MultiLineString;
import com.mapbox.geojson.MultiPoint;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.List;

/**
 * Minimal GeoJSON utilities to avoid depending on Mapbox Turf.
 */
public final class GeoJsonUtils {
    private GeoJsonUtils() {}

    private static class MutableBounds {
        double minLng = Double.POSITIVE_INFINITY;
        double minLat = Double.POSITIVE_INFINITY;
        double maxLng = Double.NEGATIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        void update(double lng, double lat) {
            minLng = Math.min(minLng, lng);
            minLat = Math.min(minLat, lat);
            maxLng = Math.max(maxLng, lng);
            maxLat = Math.max(maxLat, lat);
        }
    }

    /**
     * Computes a bounding box [minLng, minLat, maxLng, maxLat] for all geometries
     * in the provided FeatureCollection. Returns null if collection is empty.
     */
    public static double[] bbox(FeatureCollection fc) {
        if (fc == null || fc.features() == null || fc.features().isEmpty()) return null;
        MutableBounds b = new MutableBounds();
        for (Feature f : fc.features()) collect(b, f.geometry());
        if (b.minLng == Double.POSITIVE_INFINITY) return null;
        return new double[]{b.minLng, b.minLat, b.maxLng, b.maxLat};
    }

    private static void collect(MutableBounds b, Geometry g) {
        if (g == null) return;
        if (g instanceof Point) {
            Point p = (Point) g;
            b.update(p.longitude(), p.latitude());
        } else if (g instanceof MultiPoint) {
            for (Point p : ((MultiPoint) g).coordinates()) b.update(p.longitude(), p.latitude());
        } else if (g instanceof LineString) {
            for (Point p : ((LineString) g).coordinates()) b.update(p.longitude(), p.latitude());
        } else if (g instanceof MultiLineString) {
            for (List<Point> line : ((MultiLineString) g).coordinates())
                for (Point p : line) b.update(p.longitude(), p.latitude());
        } else if (g instanceof Polygon) {
            for (List<Point> ring : ((Polygon) g).coordinates())
                for (Point p : ring) b.update(p.longitude(), p.latitude());
        } else if (g instanceof MultiPolygon) {
            for (List<List<Point>> poly : ((MultiPolygon) g).coordinates())
                for (List<Point> ring : poly)
                    for (Point p : ring) b.update(p.longitude(), p.latitude());
        }
    }
}
