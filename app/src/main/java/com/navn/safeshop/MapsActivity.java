package com.navn.safeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.navn.safeshop.requests.GetAverageReviews;
import com.navn.safeshop.requests.GetCompanyId;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.navn.safeshop.requests.GooglePostIdToken;
import com.navn.safeshop.requests.ValidateUserByEmail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private RequestQueue mQueue;
    private SearchView SearchBar;
    private GoogleMap mMap;
    private ImageButton searchButton;
    private ListView listViewOfCompanies;
    public Requests requestsClass = new Requests();
    public GetAverageReviews getAverageReviewData;
    public GetCompanyId getCompanyId;
    int companyId;
    Integer user_id;
    String user_name;
    private String API_KEY = getApplicationContext().getString(R.string.Maps_API);
    private String mapsUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=";
    public Button ProfileButton;
    private ArrayList<String> listOfAddresses = new ArrayList<>();
    private ArrayList<String> attrs_string_list = new ArrayList<>();
    private ArrayList<Integer> attrs_length_list = new ArrayList<>();
    private ArrayList<String> photoReferenceList = new ArrayList<>();
    private ArrayList<CompanyListObject> listOfCompanies;
    private CompanyListAdapter arrayAdapter;
    private InfoToPass infoObject;
    private FusedLocationProviderClient fusedLocationClient;
    Location mlocation = new Location("");
    Location companyLocation = new Location("");
    CompanyListObject companyListObject;
    String shortAddress;
    String Name;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    boolean loggedIn = false;
    LoggedIntoAppInfo loginObject;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    boolean rememberMe;
    boolean googleRememberMe;
    GoogleSignInClient mGoogleSignInClient;
    private boolean isInfoWindowShown = false;
    private SecretKey secretKey;


    public MapsActivity() throws UnsupportedEncodingException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try {
            secretKey = LoginActivity.getKeyFromPassword(getApplicationContext().getString(R.string.service3),getApplicationContext().getString(R.string.service4));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        ProfileButton = findViewById(R.id.ProfileButton);
        SearchBar = findViewById(R.id.SearchBar);
        SearchBar.requestFocus();
        //Set focus to search bar right away
        if(SearchBar.requestFocus()){
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(SearchBar, InputMethodManager.SHOW_IMPLICIT);
        }

        getWindow().setSharedElementsUseOverlay(false);

        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());

        listViewOfCompanies = findViewById(R.id.ListOfStores);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mQueue = Volley.newRequestQueue(this);

        Bundle extras = getIntent().getExtras();
        infoObject = (InfoToPass) getIntent().getSerializableExtra("InfoToPassObj");
        loginObject = (LoggedIntoAppInfo) getIntent().getSerializableExtra("LoggedIntoAppInfo");



        if(infoObject!=null){
            //If infoObject is not null, it means that MapsActivity was opened by LoginActivity after a successful login
            loggedIn = infoObject.isLoggedIn();
        }else{

            //If user just opened app, this code will run
            //Get remember me value, and validate login info based on that value -- this is if user has logged in with a username and password, and not google
            prefs = getSharedPreferences("checkbox", MODE_PRIVATE);
            editor = prefs.edit();

            String storedPassword = prefs.getString("password","");
            String storedEmail = prefs.getString("email", "");

            rememberMe = prefs.getBoolean("rememberMe", false);
            googleRememberMe = prefs.getBoolean("googleRememberMe", false);
            Log.d("Debug", "Norm: " + rememberMe);
            Log.d("Debug", "Goog: " + googleRememberMe);

            if(   (!storedPassword.equals(""))  &&   (!storedEmail.equals(""))   ) {

                if (googleRememberMe) {
                    ValidateGoogleSignIn();
                } else if (rememberMe) {
                    Log.d("Creds","Pass: " + storedPassword + "Email: "+  storedEmail);
                    if (secretKey != null) {

                        try {
                            String decryptedEmail = LoginActivity.decrypt(storedEmail, secretKey);
                            String decryptedPassword = LoginActivity.decrypt(storedPassword, secretKey);

                            ValidateLoginInfo(decryptedEmail, decryptedPassword);
                        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("Debug", "Secret key was null, so user wasn't logged in via remember me!");

                    }
                }
            }else{
                Log.d("Debug", "Stored email and password are null");

            }
        }

if(loggedIn){
    user_id = infoObject.getUser_id();
    user_name = infoObject.getUser_name();
}else{
    infoObject = new InfoToPass();
}
        if(loginObject!=null){
            if(loginObject.isFromMapsActivity()){
                SearchBar.setQuery(loginObject.getSearchQuery(), true);

            }else{
                Log.d("Debug", "loginObject is not null and is not from maps activity. why is it calling this??!");
            }
        }else{
            loginObject = new LoggedIntoAppInfo(SearchBar.getQuery().toString());
            Log.d("Debug", "loginObject is null");
        }

        //Request location updates, so that fusedLocationProviderClient doesn't always return a null location
        SearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBar.requestFocus();
                if(SearchBar.requestFocus()){
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(SearchBar, InputMethodManager.SHOW_IMPLICIT);
                }

            }
        });
        SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    showSearchResultsOnMap(SearchBar.getQuery().toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });

        listOfCompanies = new ArrayList<>();
        arrayAdapter = new CompanyListAdapter(this, R.layout.company_result_list, listOfCompanies);
        listViewOfCompanies.setAdapter(arrayAdapter);


        listViewOfCompanies.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                view.setTransitionName("sharedElementTest");
                infoObject.setCompany_name(listOfCompanies.get(i).getCompanyName());
                infoObject.setCompany_address(listOfAddresses.get(i));

                //Html Attrs for loop junky garbage logic that hopefully works tomorrow!!!
                if (i == 0) {
                    //If the first item on the list is clicked, pass over the first set of html attrs to infoObject
                    if (attrs_length_list.get(0) == 1) {
                        infoObject.setHtml_attrs(new ArrayList<String>(attrs_string_list.subList(0, attrs_length_list.get(0))));
                    } else {
                        infoObject.setHtml_attrs(new ArrayList<String>(attrs_string_list.subList(0, attrs_length_list.get(0) - 1)));
                    }
                } else {
                    int startIndex = 0;
                    int endIndex = 0;
                    for (int z = 0; z <= i; z++) {
                        startIndex += attrs_length_list.get(z);

                    }
                    startIndex -= 1; //To account for the list indices starting from 0 instead of 1
                    endIndex = startIndex + attrs_length_list.get(i);
                    infoObject.setHtml_attrs(new ArrayList<String>(attrs_string_list.subList(startIndex, endIndex)));
                }

                infoObject.setPhoto_reference(photoReferenceList.get(i));

                OpenMainActivity(infoObject);
                System.out.println(i);

                //Show a toast message with the company name and address that has been clicked on

                Toast.makeText(MapsActivity.this, listOfCompanies.get(i).getCompanyName() + '\n' + listOfCompanies.get(i)
                        .getCompanyAddress(), Toast.LENGTH_SHORT).show();
            }

        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
         mLocationCallback = new LocationCallback(){
             @Override
             public void onLocationResult(LocationResult locationResult) {
                 if (locationResult == null) {
                     Toast.makeText(MapsActivity.this, "LOCATION WAS NOT SET", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 for (Location location : locationResult.getLocations()) {
                     if (location != null) {
                         Toast.makeText(MapsActivity.this, "LOCATION WAS SET", Toast.LENGTH_SHORT).show();

                         mlocation = location;
                         fusedLocationClient.removeLocationUpdates(mLocationCallback);
                     }
                 }
             }
         } ;

        //If permissions aren't granted, ask for permission and return from this function (to not do something without permissions)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1000);
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public void ValidateGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        System.out.println("Server Id: +" + R.string.server_client_id);
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                handleSignInResult(task);
            }
        });
    }
    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            GooglePostIdToken googlePostIdToken = new GooglePostIdToken(idToken, getApplicationContext());
            googlePostIdToken.execute().get();
            checkErrorMessages(googlePostIdToken, idToken);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void checkErrorMessages(GooglePostIdToken googlePostIdToken, final String idTokenString){
        String error_message = googlePostIdToken.getError_message();
        Boolean new_account = googlePostIdToken.getNew_account();
        if(error_message.equals("")){
            //Check for new_account being true, but log the user in regardless of new account being true or false- since there is no error message
            if(new_account == true){
                //Do something later - maybe welcome them?
            }
            Integer user_id = googlePostIdToken.getUser_id();
            String user_name = googlePostIdToken.getUser_name();
            String email = googlePostIdToken.getEmail();
            Integer google_login = 1; // Set login with google variable equal to 1;
            editor.putBoolean("googleRememberMe", true);
            editor.apply();
            infoObject = new InfoToPass();
            infoObject.setUser_id(user_id);
            infoObject.setUser_name(user_name);
            infoObject.setEmail(email);
            infoObject.setGoogle_login(google_login);
            infoObject.setLoggedIn(true);
            loggedIn = true;

        } else{
            //If Invalid ID Token - show toast message with error
            if(error_message.equals("Invalid ID Token")){
               Log.d("Debug","Couldn't sign in with Google - Invalid ID Token") ;
                editor.putBoolean("googleRememberMe", false);
                editor.apply();
            }
        }
    }

    public void ValidateLoginInfo(String emailInput, String passwordInput){
        System.out.println (emailInput);

        if(!(emailInput.equals(""))&&!(passwordInput.equals(""))) {

            //Create validate request class objects
            ValidateUserByEmail validateUserByEmail = new ValidateUserByEmail(emailInput, passwordInput, getApplicationContext());
            validateUserByEmail.setUserEmail(emailInput);
            validateUserByEmail.setPassword(passwordInput);
            try {
                validateUserByEmail.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (validateUserByEmail.isUserFound().equals("true") && validateUserByEmail.isPasswordMatches().equals("true")) {
                Log.d("Debug", "Remember me login successful!");

                //Get User Id from validation response, and then pass user id onto next activity
                Integer user_id = validateUserByEmail.getUser_id();
                String user_name = validateUserByEmail.getUser_name();
                String email = emailInput;
                Integer google_login = validateUserByEmail.getGoogle_login();

                infoObject = new InfoToPass();
                infoObject.setUser_id(user_id);
                infoObject.setUser_name(user_name);
                infoObject.setEmail(email);
                infoObject.setGoogle_login(google_login);
                infoObject.setLoggedIn(true);
                loggedIn = true;


            } else {
                Log.d("Debug", "Remember me credentials are incorrect");
                editor.putBoolean("rememberMe", false);
                editor.apply();
            }
        }


        }
    private void showSearchResultsOnMap(String searchBarQuery) throws UnsupportedEncodingException {

        String query = URLEncoder.encode(searchBarQuery, "utf-8");
        String url = mapsUrl + query + "&inputtype=textquery&fields=photos,formatted_address,name,geometry&key=" + API_KEY;
        System.out.println("URL: " + url);
        //HTTP Get Request to Json file
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // remove existing markers from previous search
                        mMap.clear();
                        // remove existing store items from previous search
                        arrayAdapter.clear();
                        //clear array list of addresses
                        listOfAddresses.clear();

                        try {
                            final JSONArray jsonArray = response.getJSONArray("candidates"); //Get The candidates array from the JSON Response


                            for (int i = 0; i < jsonArray.length(); i++) {
                                arrayAdapter.clear();

                                JSONObject companyObject = jsonArray.getJSONObject(i);
                                //Get the nested JSONObjects and JSONArrays in the companyObject Objects
                                JSONObject Geometry = companyObject.getJSONObject("geometry");
                                JSONObject Location = Geometry.getJSONObject("location");
                                if(companyObject.getJSONArray("photos") !=null){
                                    JSONArray photosArray = companyObject.getJSONArray("photos");
                                    JSONObject photo = photosArray.getJSONObject(0); //Use only the first picture for now
                                    String photoreference = photo.getString("photo_reference"); //This int value is the location in the array where the photoreference string is
                                    System.out.println("PHOTOREFERENCE: " + photoreference);

                                    JSONArray attrs = photo.getJSONArray("html_attributions"); //This int value is the location in the array where the html_attributes array is

                                    for (int z = 0; z < attrs.length(); z++) {
                                        //Loop through the html_attributes array and append all the strings into a list of attributes
                                        String attrs_string = attrs.getString(z);
                                        System.out.println("ATTRS_STRING: " + attrs_string);
                                        attrs_string_list.add(attrs_string);
                                        //Add length of html_attrs array to list in order to do attrs_string_list.get(attrs_number_list.get(i)) to always get
                                        //the correct number of html attributes to send to the next activity (in the on list view click listener)
                                        attrs_length_list.add(attrs.length());
                                    }

                                    //Add the photo reference string to the photo reference list
                                    photoReferenceList.add(photoreference);
                                }else{
                                    Toast.makeText(getApplicationContext(), "No Photos!!!", Toast.LENGTH_LONG).show();
                                }
                                String Lat = Location.getString("lat");

                                String Lng = Location.getString("lng");
                                companyLocation.setLatitude(Float.parseFloat(Lat));
                                companyLocation.setLongitude(Float.parseFloat(Lng));


                                //Add the address of the company to the address list
                                String Address = companyObject.getString("formatted_address");
                                listOfAddresses.add(Address);
                                Log.d("Address" + Integer.toString(i), listOfAddresses.get(i));

                                    //Set company name
                                    Name = companyObject.getString("name");

                                    //Set Company Address
                                    shortAddress = Address.substring(0, Address.indexOf(','));

                                //If permissions aren't granted, ask for permission and return from this function (to not do something without permissions)
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1000);
                                    return;
                                }
                                //Add company marker to map
                                populateMarkers(Lat, Lng, Name,Address);
                                final int finalI = i;
                                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                                    @Override
                                    public void onSuccess(android.location.Location location) {
                                        mlocation = location;
                                        try {
                                        JSONObject companyObject = jsonArray.getJSONObject(finalI);

                                                //Set company name
                                                Name = companyObject.getString("name");

                                                //Set Company Address
                                                String Address = companyObject.getString("formatted_address");
                                                shortAddress = Address.substring(0, Address.indexOf(','));

                                                //Set company Location
                                                JSONObject Geometry = companyObject.getJSONObject("geometry");
                                        JSONObject Location = null;

                                            Location = Geometry.getJSONObject("location");

                                        String Lat = Location.getString("lat");
                                                String Lng = Location.getString("lng");
                                                companyLocation.setLatitude(Float.parseFloat(Lat));
                                                companyLocation.setLongitude(Float.parseFloat(Lng));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if(mlocation!=null){
                                            companyListObject = new CompanyListObject(Name,shortAddress, Float.toString(mlocation.distanceTo(companyLocation)/1609));

                                        }else{
                                            companyListObject = new CompanyListObject(Name,shortAddress, Float.toString(-1));
                                        }



                                        Log.e("Company Info",companyListObject.getCompanyDistance() + companyListObject.getCompanyName());
                                        //Add company list object to arrayAdapter
                                        arrayAdapter.add(companyListObject);


                                    }
                                });


                                if(mlocation!=null&&companyLocation!=null) {


                                    System.out.println("Location - m" + "Lat: " + mlocation.getLatitude() + " Long: " + mlocation.getLongitude());
                                    System.out.println("Location - company" + "Lat: " + companyLocation.getLatitude() + " Long: " + companyLocation.getLongitude());
                                    System.out.println("Distance to: " + mlocation.distanceTo(companyLocation) / 1609);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    public void OpenMainActivity(InfoToPass infoObject){
        Intent intent  = new Intent(this, CompanyInfo.class);
        intent.putExtra("InfoToPassObj", infoObject) ;
        startActivity(intent);
    }
    public void OpenProfileActivity(InfoToPass infoObject){
        Intent intent  = new Intent(this, ProfileActivity.class);
        intent.putExtra("InfoToPassObj", infoObject) ;
        startActivity(intent);
    }
    public void OpenLoginActivity(){
        Intent intent  = new Intent(this, LoginActivity.class);
        intent.putExtra("InfoToPassObj", infoObject);
        intent.putExtra("LoggedIntoAppInfo", loginObject);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                if(loggedIn){
                    OpenProfileActivity(infoObject);
                }else{
                   loginObject.setSearchQuery(SearchBar.getQuery().toString());
                    OpenLoginActivity();
                }       
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                //If permission is granted, request location updates, else tell the user to please enable location services
                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        LocationServices.getFusedLocationProviderClient(MapsActivity.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enable location services", Toast.LENGTH_LONG);
                    }
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                MarkerTag markerTag = (MarkerTag) marker.getTag();
                infoObject.setCompany_name(markerTag.getMarkerName());
                infoObject.setCompany_address(markerTag.getMarkerAddress());
                OpenMainActivity(infoObject);
            }
        });


    }

    public void populateMarkers(String lat, String lng, String Name, String Address) {
        // short address for the title of markers - just the address not the city and state
        String shortAddress = Address.substring(0, Address.indexOf(','));
        double latValue = Double.parseDouble(lat);
        double lngValue = Double.parseDouble(lng);
        LatLng anyMarker = new LatLng(latValue, lngValue);
        Marker marker = mMap.addMarker(new MarkerOptions().position(anyMarker).title(Name + " " + shortAddress));
        marker.setTag(new MarkerTag(Name,Address));
       // mMap.addMarker(new MarkerOptions().position(anyMarker).title(Name + " " + shortAddress)).setTag(new MarkerTag(Name,Address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(anyMarker));
        mMap.setMinZoomPreference(10);
    }
}