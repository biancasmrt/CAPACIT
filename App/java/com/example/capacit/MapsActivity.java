package com.example.capacit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    //PlaceAutocompleteFragment placeAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //for search bar
        /*final TextView txtVw = findViewById(R.id.placeName);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        *//*AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(filter);*//*
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                txtVw.setText(place.getName());
            }

            @Override
            public void onError(Status status) {
                txtVw.setText(status.toString());
            }
        });*/
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                Context mContext = MapsActivity.this;

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                title.setSingleLine(false);

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setGravity(Gravity.CENTER);

                info.addView(title);
                info.addView(snippet);

                return info;

            }
        });

        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomGesturesEnabled(true);

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        final BitmapDescriptor original = BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            Marker lastClicked = null;

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (lastClicked != null) {
                    lastClicked.setIcon(original);
                }
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                lastClicked = marker;
                return true;
            }
        });

        // Add a marker in CafeIntermezzo and move the camera
        LatLng cafeInter = new LatLng(33.783223, -84.383540);
        int cafeInterPerc = 80;
        int cafeInterWait = 10;
        boolean cafeInterOptIn = true;
        int cafeInterFillcolor = 0xff388E3C; //green
        MarkerOptions cafeInterMark = new MarkerOptions().position(cafeInter).title("Cafe Intermezzo").snippet("Current Capacity: "+cafeInterPerc+"%").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
        if (cafeInterPerc >= 100 && cafeInterOptIn) {
            cafeInterMark.snippet("Current Capacity: "+cafeInterPerc+"%\n"+"Predicted Wait Time: "+cafeInterWait+" mins");
            cafeInterFillcolor = 0xFFF44336; //red
        } else if (cafeInterPerc >= 80 && cafeInterPerc <= 99) {
            cafeInterFillcolor = 0xFFFFEB3B; //yellow
        }
        mMap.addMarker(cafeInterMark);
        Polygon cafeInterPolygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(33.783232, -84.383690),
                        new LatLng(33.783165, -84.383545),
                        new LatLng(33.783285, -84.383409),
                        new LatLng(33.783131, -84.383157),
                        new LatLng(33.783298, -84.383033),
                        new LatLng(33.783516, -84.383497),
                        new LatLng(33.783232, -84.383690)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        cafeInterPolygon.setTag("alpha");
        cafeInterPolygon.setFillColor(cafeInterFillcolor);
        cafeInterPolygon.setStrokeWidth(2);

        //Add next building
        LatLng sugarFactory = new LatLng(33.783706, -84.383973);
        int sugarFactoryPerc = 100;
        int sugarFactoryWait = 10;
        boolean sugarFactoryOptIn = true;
        int sugarFactoryFillcolor = 0xff388E3C; //green
        MarkerOptions sugarFactoryMark = new MarkerOptions().position(sugarFactory).title("Sugar Factory American Brasserie").snippet("Current Capacity: "+sugarFactoryPerc+"%").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
        if (sugarFactoryPerc >= 100 && sugarFactoryOptIn) {
            sugarFactoryMark.snippet("Current Capacity: "+sugarFactoryPerc+"%\n"+"Predicted Wait Time: "+sugarFactoryWait+" mins");
            sugarFactoryFillcolor = 0xFFF44336; //red
        } else if (sugarFactoryPerc >= 80 && sugarFactoryPerc <= 99) {
            sugarFactoryFillcolor = 0xFFFFEB3B; //yellow
        }
        mMap.addMarker(sugarFactoryMark);
        Polygon sugarFactoryPolygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(33.783590, -84.384139),
                        new LatLng(33.783616, -84.384109),
                        new LatLng(33.783598, -84.384077),
                        new LatLng(33.783581, -84.384077),
                        new LatLng(33.783552, -84.384010),
                        new LatLng(33.783730, -84.383780),
                        new LatLng(33.783897, -84.383908),
                        new LatLng(33.783937, -84.383983),
                        new LatLng(33.783652, -84.384233),
                        new LatLng(33.783590, -84.384139)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        sugarFactoryPolygon.setTag("alpha");
        sugarFactoryPolygon.setFillColor(sugarFactoryFillcolor);
        sugarFactoryPolygon.setStrokeWidth(2);

        //Add next building
        LatLng exhale = new LatLng(33.783603, -84.383259);
        int exhalePerc = 20;
        int exhaleWait = 10;
        boolean exhaleOptIn = true;
        int exhaleFillcolor = 0xff388E3C; //green
        MarkerOptions exhaleMark = new MarkerOptions().position(exhale).title("Exhale Spa").snippet("Current Capacity: "+exhalePerc+"%").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
        if (exhalePerc >= 100 && exhaleOptIn) {
            exhaleMark.snippet("Current Capacity: "+exhalePerc+"%\n"+"Predicted Wait Time: "+exhaleWait+" mins");
            exhaleFillcolor = 0xFFF44336; //red
        } else if (exhalePerc >= 80 && exhalePerc <= 99) {
            exhaleFillcolor = 0xFFFFEB3B; //yellow
        }
        mMap.addMarker(exhaleMark);
        Polygon exhalePolygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(33.783556, -84.383431),
                        new LatLng(33.783384, -84.383109),
                        new LatLng(33.783411, -84.383071),
                        new LatLng(33.783710, -84.383055),
                        new LatLng(33.783699, -84.383270),
                        new LatLng(33.783556, -84.383431)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        exhalePolygon.setTag("alpha");
        exhalePolygon.setFillColor(exhaleFillcolor);
        exhalePolygon.setStrokeWidth(2);

        //Add next building
        LatLng sushi = new LatLng(33.783525, -84.384402);
        int sushiPerc = 20;
        int sushiWait = 10;
        boolean sushiOptIn = true;
        int sushiFillcolor = 0xff388E3C; //green
        MarkerOptions sushiMark = new MarkerOptions().position(sushi).title("RA Sushi Bar").snippet("Current Capacity: "+sushiPerc+"%").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
        if (sushiPerc >= 100 && sushiOptIn) {
            sushiMark.snippet("Current Capacity: "+sushiPerc+"%\n"+"Predicted Wait Time: "+sushiWait+" mins");
            sushiFillcolor = 0xFFF44336; //red
        } else if (exhalePerc >= 80 && exhalePerc <= 99) {
            sushiFillcolor = 0xFFFFEB3B; //yellow
        }
        mMap.addMarker(sushiMark);
        Polygon sushiPolygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(33.783467, -84.384488),
                        new LatLng(33.783458, -84.384434),
                        new LatLng(33.783416, -84.384445),
                        new LatLng(33.783389, -84.384268),
                        new LatLng(33.783570, -84.384214),
                        new LatLng(33.783607, -84.384447),
                        new LatLng(33.783467, -84.384488)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        sushiPolygon.setTag("alpha");
        sushiPolygon.setFillColor(sushiFillcolor);
        sushiPolygon.setStrokeWidth(2);

        //Add next building
        LatLng tag = new LatLng(33.783215, -84.382640);
        int tagPerc = 100;
        int tagWait = 20;
        boolean tagOptIn = true;
        int tagFillcolor = 0xff388E3C; //green
        MarkerOptions tagMark = new MarkerOptions().position(tag).title("TAG Concept Salons").snippet("Current Capacity: "+tagPerc+"%").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
        if (tagPerc >= 100 && tagOptIn) {
            tagMark.snippet("Current Capacity: "+tagPerc+"%\n"+"Predicted Wait Time: "+tagWait+" mins");
            tagFillcolor = 0xFFF44336; //red
        } else if (tagPerc >= 80 && tagPerc <= 99) {
            tagFillcolor = 0xFFFFEB3B; //yellow
        }
        mMap.addMarker(tagMark);
        Polygon tagPolygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(33.783126, -84.382522),
                        new LatLng(33.783110, -84.382516),
                        new LatLng(33.783104, -84.382420),
                        new LatLng(33.783175, -84.382412),
                        new LatLng(33.783184, -84.382433),
                        new LatLng(33.783322, -84.382428),
                        new LatLng(33.783331, -84.382784),
                        new LatLng(33.783139, -84.382814),
                        new LatLng(33.783126, -84.382522)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        tagPolygon.setTag("alpha");
        tagPolygon.setFillColor(tagFillcolor);
        tagPolygon.setStrokeWidth(2);

        //Add next building
        LatLng olive = new LatLng(33.783416, -84.382594);
        int olivePerc = 100;
        int oliveWait = 20;
        boolean oliveOptIn = false;
        int oliveFillcolor = 0xff388E3C; //green
        MarkerOptions oliveMark = new MarkerOptions().position(olive).title("Olive Bistro Midtown").snippet("Current Capacity: "+olivePerc+"%").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
        if (olivePerc >= 100 && oliveOptIn) {
            oliveMark.snippet("Current Capacity: "+olivePerc+"%\n"+"Predicted Wait Time: "+oliveWait+" mins");
        } else if (olivePerc>=100){
            oliveFillcolor = 0xFFF44336; //red
        } else if (olivePerc >= 80 && olivePerc <= 99) {
            oliveFillcolor = 0xFFFFEB3B; //yellow
        }
        mMap.addMarker(oliveMark);
        Polygon olivePolygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(33.783349, -84.382430),
                        new LatLng(33.783503, -84.382425),
                        new LatLng(33.783507, -84.382787),
                        new LatLng(33.783376, -84.382795),
                        new LatLng(33.783349, -84.382430)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        olivePolygon.setTag("alpha");
        olivePolygon.setFillColor(oliveFillcolor);
        olivePolygon.setStrokeWidth(2);

        //Add next building
        LatLng panera = new LatLng(33.784063, -84.384127);
        int paneraPerc = 90;
        int paneraWait = 20;
        boolean paneraOptIn = false;
        int paneraFillcolor = 0xff388E3C; //green
        MarkerOptions paneraMark = new MarkerOptions().position(panera).title("Panera Bread").snippet("Current Capacity: "+paneraPerc+"%").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
        if (paneraPerc >= 100 && paneraOptIn) {
            paneraMark.snippet("Current Capacity: "+paneraPerc+"%\n"+"Predicted Wait Time: "+paneraWait+" mins");
        } else if (paneraPerc>=100){
            paneraFillcolor = 0xFFF44336; //red
        } else if (paneraPerc >= 80 && paneraPerc <= 99) {
            paneraFillcolor = 0xFFFFEB3B; //yellow
        }
        mMap.addMarker(paneraMark);
        Polygon paneraPolygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(33.783811, -84.384364),
                        new LatLng(33.783784, -84.384262),
                        new LatLng(33.783920, -84.384141),
                        new LatLng(33.783931, -84.384034),
                        new LatLng(33.784158, -84.383937),
                        new LatLng(33.784223, -84.384243),
                        new LatLng(33.783811, -84.384364)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        paneraPolygon.setTag("alpha");
        paneraPolygon.setFillColor(paneraFillcolor);
        paneraPolygon.setStrokeWidth(2);
    }
}
