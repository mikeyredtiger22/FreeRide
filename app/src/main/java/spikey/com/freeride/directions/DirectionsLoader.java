package spikey.com.freeride.directions;


import android.os.AsyncTask;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import spikey.com.freeride.VALUES;


public class DirectionsLoader extends AsyncTask<Void, Void, DirectionsRoute> {
    private static final String TAG = DirectionsLoader.class.getSimpleName();

    private static final GeoApiContext GEO_API_CONTEXT = getGeoContext();

    private TaskRouteDataLoadedCallback routeDataLoadedListener;
    private TaskPathLoadedCallback pathLoadedListener;
    private int taskPosition;
    private LatLng start;
    private LatLng end;

    public DirectionsLoader(TaskRouteDataLoadedCallback routeDataLoadedListener,
                            TaskPathLoadedCallback pathLoadedListener, int taskPosition,
                            double startLat, double startLon, double endLat, double endLon) {
        this.routeDataLoadedListener = routeDataLoadedListener;
        this.pathLoadedListener = pathLoadedListener;
        this.taskPosition = taskPosition;
        this.start = new LatLng(startLat, startLon);
        this.end = new LatLng(endLat, endLon);
    }

    public interface TaskRouteDataLoadedCallback {
        void onRouteDataLoaded(DirectionsRoute route, int taskPosition);
    }

    public interface TaskPathLoadedCallback {
        void onPathLoaded(DirectionsRoute route, int taskPosition);
    }

    @Override
    protected DirectionsRoute doInBackground(Void... voids) {

        DirectionsRoute route = null;

        try {
            DirectionsResult result = DirectionsApi.newRequest(GEO_API_CONTEXT)
                    .mode(TravelMode.DRIVING)
                    .alternatives(false)
                    .origin(start)
                    .destination(end)
                    .await();

            if (result.routes.length > 0) {
                route = result.routes[0];
            }
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return route;
    }

    @Override
    protected void onPostExecute(DirectionsRoute route) {
        routeDataLoadedListener.onRouteDataLoaded(route, taskPosition);
        //pathLoadedListener.onPathLoaded(route, taskPosition);
    }

    /**
     * Should only be called once
     * @return Api Context for app to use google directions api
     */
    private static GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(25)
                .setApiKey(VALUES.DIRECTIONS_API_KEY)
                .setConnectTimeout(3, TimeUnit.SECONDS)
                .setReadTimeout(3, TimeUnit.SECONDS)
                .setWriteTimeout(3, TimeUnit.SECONDS);
    }
}
