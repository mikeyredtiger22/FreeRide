package spikey.com.freeride.directions;

//Using Java Client/Server Library - testing difference

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import spikey.com.freeride.VALUES;

//import com.google.maps.model.LatLng;

public class DirectionsLoader2 {

    DirectionsApi directionsApi;
    GeoApiContext geoApiContext;

    public DirectionsLoader2() {

    }

    public DirectionsResult getDirections(LatLng start, LatLng end) {
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING)
                    .origin(new com.google.maps.model.LatLng(start.latitude, start.longitude))
                    .destination(new com.google.maps.model.LatLng(end.latitude, end.longitude))
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(VALUES.DIRECTIONS_API_KEY)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }
}
