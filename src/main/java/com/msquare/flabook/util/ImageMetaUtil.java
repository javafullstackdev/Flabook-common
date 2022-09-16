package com.msquare.flabook.util;

import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

import java.util.*;

public class ImageMetaUtil {

	public ImageMetaUtil(Metadata metadata) {
		super();
		this.metadata = metadata;
	}

	private final Metadata metadata;

	@SuppressWarnings("SameParameterValue")
	private <T extends Directory> Collection<T> getMetaDirectoriesOfType(Class<T> t) {
		if(metadata == null)
			return Collections.emptyList();

		return metadata.getDirectoriesOfType(t); //NOSONAR
	}

	public List<GeoLocation> getGeoLocation() {
		List<GeoLocation> locations = new ArrayList<>();

		Collection<GpsDirectory> gpsDirectories = getMetaDirectoriesOfType(GpsDirectory.class);
		for (GpsDirectory gpsDirectory : gpsDirectories) {
			// Try to read out the location, making sure it's non-zero
			GeoLocation geoLocation = gpsDirectory.getGeoLocation();
			if (geoLocation != null && !geoLocation.isZero()) {
				locations.add(geoLocation);
			}
		}
		return locations;
	}
}
