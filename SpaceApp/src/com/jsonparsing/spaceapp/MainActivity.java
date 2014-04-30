package com.jsonparsing.spaceapp;

import com.jsonparsing.spaceapp.R;
import java.util.ArrayList;
import java.util.HashMap;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class MainActivity extends ListActivity {
 
    private ProgressDialog pDialog;
 
    // URL to get contacts JSON
    // Make url in JSON format(http://jsonviewer.stack.hu/):
    // http://jsonviewer.stack.hu/#http://mydeatree.appspot.com/api/v1/public_ideas/
    // http://api.androidhive.info/contacts/ works!
    // http://mydeatree.appspot.com/api/v1/public_ideas/ works!
    private static String url = "http://mydeatree.appspot.com/api/v1/public_ideas/";
 
    // JSON Node names
    private static final String TAG_META = "meta";
    private static final String TAG_OBJECTS = "objects";
    private static final String TAG_CHILD = "children_count";
    private static final String TAG_CONTRIBUTORS = "contributors";
    private static final String TAG_CRE_DATE = "created_date";
    private static final String TAG_ID = "id";
    private static final String TAG_MOD_DATE = "modified_date";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PARENT = "parent";
    private static final String TAG_PUBLIC = "public";
    private static final String TAG_RESOURCE = "resource_uri";
    private static final String TAG_TEXT = "text";
    private static final String TAG_TITLE = "title";
 
    // contacts JSONArray
    JSONArray contacts = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        contactList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
 

 
        // Calling async task to get json
        new GetContacts().execute();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
 
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("SpaceApp Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                	
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject meta = jsonObj.getJSONObject(TAG_META);
                    // Getting JSON Array node
                    contacts = jsonObj.getJSONArray(TAG_OBJECTS);
 
                    // looping through All Objects
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                         
                        String id = c.getString(TAG_ID);
                        String date = c.getString(TAG_CRE_DATE);
                        String text = c.getString(TAG_TEXT);
                        String title = c.getString(TAG_TITLE);
 
                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_CRE_DATE, date);
                        contact.put(TAG_TEXT, text);
                        contact.put(TAG_TITLE, title);
 
                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[] { TAG_CRE_DATE, TAG_TEXT,
                    		TAG_TITLE }, new int[] { R.id.name,
                            R.id.email, R.id.mobile });
 
            setListAdapter(adapter);
        }
 
    }
 
}