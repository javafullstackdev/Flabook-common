package com.msquare.flabook.util;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GeoUtils {

	private final GeometryFactory geometryFactory;

	private static class Singleton {
		private static final GeoUtils instance = new GeoUtils();
	}

	public static GeoUtils getInstance() {
		return Singleton.instance;
	}

	private GeoUtils() {
		geometryFactory = new GeometryFactory() ;
	}

	private static final double RADIUS = 0.07; //KM

	public Polygon createCircle(double lng, double lat) {
		return createCircle(lng, lat, RADIUS);
	}

	@SuppressWarnings("unused")
	public Polygon createPolygon(Coordinate[] coordinates) {
		return geometryFactory.createPolygon(coordinates);
	}

	public Polygon createCircle(double lng, double lat, double radius) {
		GeometricShapeFactory geometryShapeFactory = new GeometricShapeFactory() ;
		geometryShapeFactory.setCentre(new Coordinate(lng, lat));
		geometryShapeFactory.setNumPoints(7);
		geometryShapeFactory.setSize( (radius * 2)/88.1);
		return geometryShapeFactory.createCircle();
	}


	public Point createPoint(double lng, double lat) {
		return geometryFactory.createPoint(new Coordinate(lng, lat));
	}

	@SuppressWarnings("unused")
	public MultiPolygon createMultiPolygon(Polygon[] polygons) {
		return geometryFactory.createMultiPolygon(polygons);
	}

	/**
	 * 국립공원 좌표 포인트를 줄이기 위해 사용
	 * @param geom geometry
	 * @param percentage 남길 포인트 %
	 * @return geometry
	 */
	public static Geometry simplify (Geometry geom, int percentage) {
		GeometryFactory factory = geom.getFactory();
		if (geom instanceof MultiPolygon) {
			MultiPolygon mp = (MultiPolygon)geom;
			Polygon[] polys = new Polygon[mp.getNumGeometries()];
			for (int i = 0; i < mp.getNumGeometries(); i += 1) {
				polys[i] = (Polygon)simplify(mp.getGeometryN(i), percentage);
			}
			return factory.createMultiPolygon(polys);
		} else if (geom instanceof Polygon) {
			return simplify((Polygon)geom, percentage);
		} else if (geom.getGeometryType().equals("GeometryCollection")) {
			GeometryCollection gc = (GeometryCollection)geom;
			Geometry[] geoms = new Geometry[gc.getNumGeometries()];
			for (int i = 0; i < gc.getNumGeometries(); i += 1) {
				geoms[i] = simplify(gc.getGeometryN(i), percentage);
			}
			return factory.createGeometryCollection(geoms);
		} else {
			return(geom);
		}
	}

	/** */
	private static final Comparator<Polygon> POLYGON_AREA_COMPARATOR = (p1, p2) -> {
		double a1 = p1.getArea();
		double a2 = p2.getArea();
		return Double.compare(a2, a1);
	};

	/** */
	@SuppressWarnings("unused")
	private static final Comparator<Polygon> POLYGON_CONTAINMENT_COMPARATOR = (p1, p2) -> {
		if (p1.contains(p2)) {
			return(1);
		} else if (p2.contains(p1)) {
			return(-1);
		} else {
			return(0);
		}
	};

	public static Polygon simplify (Polygon p, int percentage) {
		GeometryFactory factory = p.getFactory();

		List<Coordinate> coordinates = new ArrayList<>();
		int points = p.getCoordinates().length;
		if(1000 > points)
			return p;

		int num = (int)Math.ceil((double)percentage / 100.0f *  points);
		int interval = (num > 0) ? points / num : 1;
		for(int i = 0 ; i < points ; i++) {
			if(i == 0 || i % interval == 0)
				coordinates.add(p.getCoordinates()[i]);
		}
		coordinates.add(coordinates.get(0));
		return factory
				.createPolygon(coordinates.toArray(new Coordinate[0]));
	}

	@SuppressWarnings("unused")
	public static Polygon repair (Polygon p) { //NOSONAR
		GeometryFactory factory = p.getFactory();
		IsValidOp isValidOp = new IsValidOp(p);
		TopologyValidationError err = isValidOp.getValidationError();
		while (err != null) {
			if ((err.getErrorType() == TopologyValidationError.SELF_INTERSECTION) ||
					(err.getErrorType() == TopologyValidationError.RING_SELF_INTERSECTION) ||
					(err.getErrorType() == TopologyValidationError.DISCONNECTED_INTERIOR)) {
				Geometry boundary = p.getBoundary();
				// calling union will re-node the boundary curve to eliminate self-intersections
				// see http://lists.jump-project.org/pipermail/jts-devel/2006-November/001815.html
				boundary = boundary.union(boundary);
				Polygonizer polygonizer = new Polygonizer();
				polygonizer.add(boundary);
				@SuppressWarnings("unchecked")
				Collection<Polygon> c = polygonizer.getPolygons();
				if (!c.isEmpty()) {
					Polygon[] polys = c.toArray(new Polygon[0]);
					Arrays.sort(polys, POLYGON_AREA_COMPARATOR);
					p = polys[0];
				} else {
					log.error("unable to fix polygon: {}", err);
					p = factory.createPolygon(null, null);
				}
			} else if (err.getErrorType() == TopologyValidationError.TOO_FEW_POINTS) {
				LinearRing exterior = (LinearRing)p.getExteriorRing();
				Coordinate[] coords = CoordinateArrays.removeRepeatedPoints(exterior.getCoordinates());
				if (coords.length < 4) {
					p = factory.createPolygon(null, null);
				} else {
					exterior = factory.createLinearRing(coords);
					List<LinearRing> validInteriorRings = new ArrayList<>(p.getNumInteriorRing());
					for (int i = 0; i < p.getNumInteriorRing(); i += 1) {
						LinearRing s = (LinearRing)p.getInteriorRingN(i);
						coords = CoordinateArrays.removeRepeatedPoints(s.getCoordinates());
						if (coords.length >= 4) {
							validInteriorRings.add(factory.createLinearRing(coords));
						}
					}
					p = factory.createPolygon(exterior, GeometryFactory.toLinearRingArray(validInteriorRings));
				}
			} else {
				log.error("error {}", err);
				p = factory.createPolygon(null, null);
			}
			isValidOp = new IsValidOp(p);
			err = isValidOp.getValidationError();
		}
		return(p);
	}
}
