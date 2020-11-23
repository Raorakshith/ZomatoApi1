package com.example.zomatoapi1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
String put,category;
Button search_btn;
TextView search;
String query,Url;
private RecyclerView recyclerView;
List<Items> items;
Adapter adapter,adapter1;
    SearchView searchView;
    TextView inv;
    private FusedLocationProviderClient client;
    private GoogleApiClient googleApiClient;
    private Location mlocation;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    TextView locationtext,ondoc,ond;
    double lats,longs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        searchView=findViewById(R.id.searchbar);
        inv=findViewById(R.id.invis);
        locationtext=findViewById(R.id.updatelocation);
        searchView.setQueryHint("Search for restaurants");
        searchView=findViewById(R.id.searchbar);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        items=new ArrayList<>();

                Url = "https://developers.zomato.com/api/v2.1/search?&lat=27&lon=153";
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, Url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject jsonObject=new JSONObject(response.toString());
                                    JSONArray jsonArray=jsonObject.getJSONArray("restaurants");
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                        JSONObject jsonObject2=jsonObject1.getJSONObject("restaurant");
                                        JSONObject jsonObject3=jsonObject2.getJSONObject("location");
                                       Toast.makeText(MainActivity.this, "name is:"+jsonObject2.getString("name").toString()+jsonObject3.getString("address").toString()+jsonObject2.getString("photos_url").toString(), Toast.LENGTH_SHORT).show();
                                       Items item = new Items();
                                       item.setName(jsonObject2.getString("name").toString());
                                       item.setLocation(jsonObject3.getString("address").toString());
                                       item.setPhotos_url(jsonObject2.getString("photos_url").toString());
                                       items.add(item);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                adapter=new Adapter(getApplicationContext(),items);
                                recyclerView.setAdapter(adapter);
                                //Toast.makeText(MainActivity.this, "Response is:"+response, Toast.LENGTH_SHORT).show();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error is:"+error, Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public Map<String,String> getHeaders() throws AuthFailureError{
                        Map<String,String> params= new HashMap<String, String>();
                        params.put("user-key","1b3c8b37ea96785391fa55c288ac385c");
                        params.put("Accept","application/json");
                        return params;
                    }
                };
               // StringRequest objectRequest = new StringRequest(Request.Method.GET, Url, new ResponseListener(), new ErrorListener());
                requestQueue.add(postRequest);
        if(searchView!=null){
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inv.setVisibility(View.INVISIBLE);
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    search(newText);
                    return true;
                }
            });
        }
            }
    private void search(String string){

        ArrayList<Items> myList= new ArrayList<>();
        for (Items object:items){
            if(object.getName().toLowerCase().contains(string.toLowerCase())){
                myList.add(object);
            }
        }
        adapter1=new Adapter(MainActivity.this,myList);
        recyclerView.setAdapter(adapter1);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        mlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(mlocation==null){
            startLocationUpdates();
        }
        if(mlocation!=null){
            lats=mlocation.getLatitude();
            longs=mlocation.getLongitude();
            String lat= String.valueOf(lats);
            String longo= String.valueOf(longs);
            items.clear();
            Url = "https://developers.zomato.com/api/v2.1/search?&lat="+lat+"&lon="+longo;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, Url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response.toString());
                                JSONArray jsonArray=jsonObject.getJSONArray("restaurants");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                    JSONObject jsonObject2=jsonObject1.getJSONObject("restaurant");
                                    JSONObject jsonObject3=jsonObject2.getJSONObject("location");
                                    Toast.makeText(MainActivity.this, "name is:"+jsonObject2.getString("name").toString()+jsonObject3.getString("address").toString()+jsonObject2.getString("photos_url").toString(), Toast.LENGTH_SHORT).show();
                                    Items item = new Items();
                                    item.setName(jsonObject2.getString("name").toString());
                                    item.setLocation(jsonObject3.getString("address").toString());
                                    item.setPhotos_url(jsonObject2.getString("photos_url").toString());
                                    items.add(item);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            adapter=new Adapter(getApplicationContext(),items);
                            recyclerView.setAdapter(adapter);
                            //Toast.makeText(MainActivity.this, "Response is:"+response, Toast.LENGTH_SHORT).show();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Error is:"+error, Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError{
                    Map<String,String> params= new HashMap<String, String>();
                    params.put("user-key","1b3c8b37ea96785391fa55c288ac385c");
                    params.put("Accept","application/json");
                    return params;
                }
            };
            // StringRequest objectRequest = new StringRequest(Request.Method.GET, Url, new ResponseListener(), new ErrorListener());
            requestQueue.add(postRequest);
            getAddress(lats,longs);
            //Toast.makeText(getContext(), "Location:"+lat+longo, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "Location not updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MainActivity.this, "Your current location is:"+location, Toast.LENGTH_SHORT).show();
    }

    protected void startLocationUpdates() {
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(1000).setFastestInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    public Address getAddress(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());
        try{
            addresses=geocoder.getFromLocation(latitude,longitude,1);
            String address=addresses.get(0).getLocality()+","+addresses.get(0).getSubAdminArea();
            locationtext.setText(address);
            // Toast.makeText(getContext(), ""+addresses, Toast.LENGTH_SHORT).show();
            return addresses.get(0);
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }
    }
//    private class ResponseListener implements Response.Listener {
//
//        @Override
//        public void onResponse(Object response) {
//
//            try {
//                JSONObject jsonObject= new JSONObject((String) response);
//                JSONArray parse = jsonObject.getJSONArray("hints");
//                for(int i=0; i<1;i++){
//                    JSONObject get=parse.getJSONObject(i);
//                    // String food=get.getString("food");
//                    JSONObject foo = get.getJSONObject("food");
//                    String foods=foo.getString("label");
//                    //food.setText(foods);
//                    JSONObject nut=foo.getJSONObject("nutrients");
//                    String calorie=nut.getString("ENERC_KCAL");
//                    //double dCal = Double.parseDouble(calorie);
//                    //calorie = String.format("%.0f",dCal);
//                    //cal.setText(String.format("%.0f",dCal) + " kcal");
////                    String proteins=nut.getString("PROCNT");
////                    prot = proteins;
////                    dProt= Double.parseDouble(proteins);
////                    texts1.setText(String.format("%.0f",dProt));
////                    String fats=nut.getString("FAT");
////                    fat = fats;
////                    dFat= Double.parseDouble(fats);
////                    texts2.setText(String.format("%.0f",dFat));
////                    String carbo=nut.getString("CHOCDF");
////                    carb = carbo;
////                    dCarb= Double.parseDouble(carbo);
////                    texts3.setText(String.format("%.0f",dCarb));
//                    // res = calorie;
//                }} catch (JSONException e) {
//                e.printStackTrace();
//            }
//            JSONObject jsonObjects = null;
//            try {
//                jsonObjects = new JSONObject((String) response);
//                JSONArray hint=jsonObjects.getJSONArray("hints");
//                for(int k=0;k<hint.length();k++) {
//                    JSONObject gets = hint.getJSONObject(k);
//                    // String list=gets.getString("food");
//                    JSONObject hin = gets.getJSONObject("food");
//
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//
//
//        }
//    }
//
//    private class ErrorListener implements Response.ErrorListener {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            Toast.makeText(MainActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
//        }
//
//    }
//    public Map<String,String> getHeaders() throws AuthFailureError{
//        Map<String,String> params= new HashMap<String, String>();
//        params.put("user-key","1b3c8b37ea96785391fa55c288ac385c");
//        params.put("Accept","application/json");
//        return params;
//    }
