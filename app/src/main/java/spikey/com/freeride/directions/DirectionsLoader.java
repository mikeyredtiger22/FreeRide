package spikey.com.freeride.directions;


import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import spikey.com.freeride.VALUES;


public class DirectionsLoader extends AsyncTask<Void, Void, ArrayList<LatLng>> {
    private static final String TAG = DirectionsLoader.class.getSimpleName();

    private DirectionsResult callback;
    private int loaderId;
    private LatLng start;
    private LatLng end;

    public DirectionsLoader(DirectionsResult callback, int loaderId, LatLng start, LatLng end) {
        this.callback = callback;
        this.loaderId = loaderId;
        this.start = start;
        this.end = end;
    }

    public interface DirectionsResult {
        void receiveDirectionsResult(ArrayList<LatLng> points, int loaderId);
    }

    @Override
    protected ArrayList<LatLng> doInBackground(Void... voids) {
        String directionsUrl = getDirectionsUrl(start, end);
        String response = downloadUrl(directionsUrl);
        Log.d(TAG, "Loader Id: " + loaderId + " received response"); //: " + response);
        return routeDataToPoints(response);
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> response) {
        callback.receiveDirectionsResult(response, loaderId);
    }

    private String getDirectionsUrl(LatLng start, LatLng end) {

        String startString = "origin=" + start.latitude + "," + start.longitude;
        String endString = "destination=" + end.latitude + "," + end.longitude;

        //TODO add no alternative routes option
        //todo configure other parameters
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving"; //todo can change for user
        String key = "key=" + VALUES.DIRECTIONS_API_KEY;
        // Building the parameters to the web service
        String parameters = startString + "&" + endString + "&" + sensor + "&" + mode + "&" + key;

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) {
        String data = "";
        InputStream iStream = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder response = new StringBuilder();

            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            data = response.toString();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (iStream != null) {
                    iStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    private ArrayList<LatLng> routeDataToPoints(String response) {

        ArrayList<LatLng> points = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(response);
            JsonParser parser = new JsonParser();
            List<List<HashMap<String, String>>> routes = parser.parse(jObject);

            if (routes.isEmpty()) {
                Log.d(TAG, "Directions result is empty");
                return points;
            }

            List<HashMap<String, String>> path = routes.get(0);
            for (int j = 0; j < path.size(); j++) {

                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return points;
    }
}
