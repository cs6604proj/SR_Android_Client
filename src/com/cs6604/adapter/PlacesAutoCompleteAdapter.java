package com.cs6604.adapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.cs6604.smartroute.HttpRetriever;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable{
	
	private static final String LOG_TAG = "PlacesAutoCompleteAdapter";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyAMgtwQrvoX99yV1jZf9J-vxeXwdRJyRgw";
	private ArrayList<String> resultList;
	
	HttpRetriever httpRetriever = new HttpRetriever();

	private ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;

	    String jsonResults = null;
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?sensor=false&key=" + API_KEY);
	        sb.append("&components=country:us");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	        String url = sb.toString();
	        jsonResults = httpRetriever.retrieve(url);
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } 

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<String>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }

	    return resultList;
	}
	
	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }

	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return resultList.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = autocomplete(constraint.toString());

	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}

