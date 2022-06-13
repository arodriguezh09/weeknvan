package arh.miapp.camperbooking.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.listadapters.ListAdapter;
import arh.miapp.camperbooking.objects.Booking;
import arh.miapp.camperbooking.objects.Vehicle;

public class VehiclesFragment extends Fragment {

    TextView tvVehiclesTitle;
    TextView tvMessage404;
    List<Vehicle> vehicleList;
    List<Booking> bookingList;
    RecyclerView rvVehicles;
    Fragment frgDetails;
    DatabaseReference dbVehicles;
    StorageReference storageRef;
    ListAdapter la;
    String city;
    Date checkin, checkout;
    boolean searchAll = false;
    String userId;

    public VehiclesFragment() {
        searchAll = true;
    }

    public VehiclesFragment(String city, Date checkin, Date checkout) {
        this.city = city;
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public VehiclesFragment(String userId) {
        this.userId = userId;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehicleList = new ArrayList<>();
        bookingList = new ArrayList<>(((BottomNavigationActivity) getActivity()).bookingList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vehicles, container, false);
        tvVehiclesTitle = v.findViewById(R.id.tvVehiclesTitle);
        tvMessage404 = v.findViewById(R.id.tvMessage404);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        if (checkin != null || checkout != null) {
            String stringCheckin = formatter.format(checkin);
            String stringCheckout = formatter.format(checkout);
            String tvContent = "";
            if (!city.equals("") && !stringCheckin.equals(stringCheckout)) {
                tvContent = getString(R.string.search) + getString(R.string.search_city) + city + getString(R.string.search_from) + stringCheckin + getString(R.string.search_to) + stringCheckout;
            } else if (!city.equals("")) {
                tvContent = getString(R.string.search) + getString(R.string.search_city) + city;
            } else if (!stringCheckin.equals(stringCheckout)) {
                tvContent = getString(R.string.search) + getString(R.string.search_from) + stringCheckin + getString(R.string.search_to) + stringCheckout;
            } else {
                tvContent = getString(R.string.search_all);
            }
            tvVehiclesTitle.setText(tvContent);
        }
        if (userId != null) {
            tvVehiclesTitle.setText("Mis reservas");
        }
        la = new ListAdapter(vehicleList, getContext());
        dbVehicles = ((BottomNavigationActivity) getActivity()).database;
        storageRef = ((BottomNavigationActivity) getActivity()).storageRef;
        dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles");
        dbVehicles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                    // Si tengo filtros
                    if (!searchAll && userId == null) {
                        boolean reserved = false;
                        for (Booking booking : bookingList) {
                            if (booking.isReserved(checkin, checkout, vehicle.getPlate())) {
                                reserved = true;
                            }
                        }
                        // si no esta reservada y la ciudad me vale, añade
                        if (!reserved && (city.equals("") || vehicle.getCity().equals(city))) {
                            // Si tengo filtros:
                            vehicleList.add(vehicle);
                            la.notifyDataSetChanged();
                        }
                    } else if (userId != null) {
                        for (Booking booking : bookingList) {
                            if (userId.equals(booking.getIdUser()) && vehicle.getPlate().equals(booking.getPlate())) {
                                vehicleList.add(vehicle);
                            }
                        }
                    } else {
                        //Si busco todos
                        vehicleList.add(vehicle);
                    }
                }
                // Me bajo las fotos de los vehiculos
                getPhotos();
                la.notifyDataSetChanged();
                checkList();
            }

            private void checkList() {
                if (!vehicleList.isEmpty()) {
                    tvMessage404.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), R.string.error_loading_vehicles, Toast.LENGTH_SHORT).show();

            }
        });

        rvVehicles = (RecyclerView) v.findViewById(R.id.rvVehicles);
        rvVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        la.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frgDetails = new DetailsFragment(vehicleList.get(rvVehicles.getChildAdapterPosition(view)));
                ((BottomNavigationActivity) getActivity()).loadFragment(frgDetails, true);
            }
        });
        rvVehicles.setAdapter(la);

        return v;
    }

    private void getPhotos() {
        for(Vehicle ve : vehicleList){
            dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles/"+ve.getPlate());
            dbVehicles.child("photos").limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String path = dataSnapshot.getValue(String.class);
                        storageRef = FirebaseStorage.getInstance("gs://weeknvan.appspot.com").getReference(path);
                        try {
                            File localfile = File.createTempFile("tmp" + ve.getPlate() + "thumbnail", ".jpg");
                            storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                    ve.setBitmap(bitmap);
                                    la.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), R.string.error_loading_images, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), R.string.error_loading_images, Toast.LENGTH_SHORT).show();
                }
            });
        }

            /*
            storageRef = FirebaseStorage.getInstance("gs://weeknvan.appspot.com").getReference("vehicles/" + ve.getPhoto() + ".jpg");
            try {
                File localfile = File.createTempFile("tmp" + ve.getPlate(), ".jpg");
                storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        ve.setBitmap(bitmap);
                        la.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), R.string.error_loading_images, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Puede que el servidor haya expirado, dimelo y lo arreglo", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
             */

    }
}