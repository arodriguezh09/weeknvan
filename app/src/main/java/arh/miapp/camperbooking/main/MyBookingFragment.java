package arh.miapp.camperbooking.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import arh.miapp.camperbooking.listadapters.ListAdapterBooking;
import arh.miapp.camperbooking.objects.Booking;
import arh.miapp.camperbooking.objects.Vehicle;

public class MyBookingFragment extends Fragment {

    TextView tvMessage404;
    List<Vehicle> vehicleList;
    List<Booking> bookingList;
    RecyclerView rvVehicles;
    Fragment frgDetails;
    DatabaseReference dbVehicles;
    StorageReference storageRef;
    ListAdapterBooking laBooking;
    String city;
    Date checkin, checkout;
    String userId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehicleList = new ArrayList<>();
        bookingList = new ArrayList<>(((BottomNavigationActivity) getActivity()).bookingList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_booking, container, false);

        tvMessage404 = v.findViewById(R.id.tvMessage404book);

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
        }
        laBooking = new ListAdapterBooking(vehicleList, bookingList, getContext());
        dbVehicles = ((BottomNavigationActivity) getActivity()).database;
        storageRef = ((BottomNavigationActivity) getActivity()).storageRef;
        dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles");
        dbVehicles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                    storageRef = FirebaseStorage.getInstance("gs://weeknvan.appspot.com").getReference("vehicles/" + vehicle.getPhoto() + ".jpg");
                    try {
                        File localfile = File.createTempFile("tmp" + vehicle.getPlate(), ".jpg");
                        storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                vehicle.setBitmap(bitmap);
                                laBooking.notifyDataSetChanged();
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
                    vehicleList.add(vehicle);
                }
                laBooking.notifyDataSetChanged();
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
        rvVehicles = (RecyclerView) v.findViewById(R.id.rvVehiclesBooking);
        rvVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        laBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frgDetails = new DetailsFragment(vehicleList.get(rvVehicles.getChildAdapterPosition(view)));
                ((BottomNavigationActivity) getActivity()).loadFragment(frgDetails, true);
            }
        });
        rvVehicles.setAdapter(laBooking);

        return v;
    }
}