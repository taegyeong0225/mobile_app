package com.example.android_final_app;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrequencyFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap; // 구글 맵 객체
    private FusedLocationProviderClient fusedLocationClient; // 위치 제공자 클라이언트
    private PlacesClient placesClient; // Places API 클라이언트

    // 기본 설정 위치 -> 인하공업전문대학
    private final double DEFAULT_LATITUDE = 37.449432;
    private final double DEFAULT_LONGITUDE = 126.657348;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.maps_layout,container,false);

        // SupportMapFragment를 통해 지도 객체를 얻음
        SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment!=null){
            mapFragment.getMapAsync(this);
        }

        // FusedLocationProviderClient 초기화
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(requireActivity());

        // Places API 초기화
        Places.initialize(requireContext(), "AIzaSyB6_kA7AnPl0sM5kpJer5h8zLD3y0gqpe8");
        placesClient = Places.createClient(requireContext());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        // 인하공업전문대학 위치로 초기 카메라 설정
        LatLng inhaGateLatLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inhaGateLatLng, 17));
        Marker marker = mMap.addMarker(new MarkerOptions().position(inhaGateLatLng).title("인하공업전문대학")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        // 마커의 정보 창을 항상 표시
        if (marker != null) {
            marker.showInfoWindow();
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // 인하공업전문대학 근처 맛집 표시
        fetchAndSaveNearbyPlaces(inhaGateLatLng);
    }

    // 주어진 위치 근처의 장소를 찾고 마커로 표시하는 메서드
    // 음식점 목록을 가져와서 저장하는 메서드
    private void fetchAndSaveNearbyPlaces(LatLng location) {
        String locationString = location.latitude + "," + location.longitude;
        int radius = 1000;
        String apiKey = "AIzaSyB6_kA7AnPl0sM5kpJer5h8zLD3y0gqpe8";
        String nearbySearchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + locationString + "&radius=" + radius + "&type=restaurant&key=" + apiKey;

        new Thread(() -> {
            try {
                URL url = new URL(nearbySearchUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String response = stringBuilder.toString();
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray results = jsonResponse.getJSONArray("results");

                List<String> restaurantList = new ArrayList<>();
                requireActivity().runOnUiThread(() -> {
                    try {
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            JSONObject geometry = place.getJSONObject("geometry");
                            JSONObject locationObj = geometry.getJSONObject("location");
                            double lat = locationObj.getDouble("lat");
                            double lng = locationObj.getDouble("lng");
                            String name = place.getString("name");

                            LatLng placeLatLng = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(name));
                            restaurantList.add(name);
                        }

                        // 저장 메서드 호출
                        saveRestaurantList(restaurantList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    // 음식점 저장 하기
    private void saveRestaurantList(List<String> restaurantList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray(restaurantList);
        editor.putString("restaurantList", jsonArray.toString());
        editor.apply();

        // 데이터 저장 확인 로그
        Log.i("FrequencyFragment", "Saved restaurant list: " + jsonArray.toString());
    }

}
