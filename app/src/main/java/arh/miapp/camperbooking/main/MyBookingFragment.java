package arh.miapp.camperbooking.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.List;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.listadapters.ListAdapterBooking;
import arh.miapp.camperbooking.objects.Booking;
import arh.miapp.camperbooking.objects.Vehicle;

public class MyBookingFragment extends Fragment {

    TextView tvMessage404;

    List<Vehicle> vehicleList;
    List<Booking> bookingListOut;
    List<Booking> bookingListIn;

    RecyclerView rvVehiclesOut;
    RecyclerView rvVehiclesIn;

    Fragment frgDetails;

    DatabaseReference dbVehicles;
    DatabaseReference dbBooking;
    //referencia a las fotos
    StorageReference storageRef;
    String uid;

    ListAdapterBooking laBookingIn;
    ListAdapterBooking laBookingOut;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehicleList = new ArrayList<>();
        bookingListOut = new ArrayList<>();
        bookingListIn = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_booking, container, false);
        // View binding del mensaje vacio
        tvMessage404 = v.findViewById(R.id.tvMessage404book);

        // Recuperamos el id del usuario para buscar sus reservas y sus vehiculos
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Creo el listadapterbooking
        laBookingIn = new ListAdapterBooking(vehicleList, bookingListIn, getContext());
        laBookingOut = new ListAdapterBooking(vehicleList, bookingListOut, getContext());
        dbBooking = FirebaseDatabase.getInstance().getReference("bookings");
        dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles");

        getBookingsIn();
        getBookingsOut();
        getVehicles();

        rvVehiclesOut = (RecyclerView) v.findViewById(R.id.rvVehiclesBookingOut);
        rvVehiclesOut.setLayoutManager(new LinearLayoutManager(getContext()));
        laBookingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plate = bookingListOut.get(rvVehiclesOut.getChildAdapterPosition(view)).getPlate();
                Vehicle vehicle = new Vehicle();
                for (Vehicle v : vehicleList) {
                    if (v.getPlate().equals(plate)) {
                        vehicle = v;
                    }
                }
                frgDetails = new DetailsFragment(vehicle);
                ((BottomNavigationActivity) getActivity()).loadFragment(frgDetails, true);
            }
        });
        rvVehiclesOut.setAdapter(laBookingOut);

        rvVehiclesIn = (RecyclerView) v.findViewById(R.id.rvVehiclesBookingIn);
        rvVehiclesIn.setLayoutManager(new LinearLayoutManager(getContext()));
        laBookingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plate = bookingListIn.get(rvVehiclesIn.getChildAdapterPosition(view)).getPlate();
                Vehicle vehicle = new Vehicle();
                for (Vehicle v : vehicleList) {
                    if (v.getPlate().equals(plate)) {
                        vehicle = v;
                    }
                }
                frgDetails = new DetailsFragment(vehicle);
                ((BottomNavigationActivity) getActivity()).loadFragment(frgDetails, true);
            }
        });
        rvVehiclesIn.setAdapter(laBookingIn);

        ItemTouchHelper.SimpleCallback simpleCallbackIn = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Booking b = bookingListIn.get(viewHolder.getAdapterPosition());
                dbBooking.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Booking booking = dataSnapshot.getValue(Booking.class);
                            if (booking.getPlate().equals(b.getPlate()) && booking.getCheckin().equals(b.getCheckin())) {
                                bookingListIn.clear();
                                dbBooking.child(dataSnapshot.getKey()).removeValue();
                                laBookingIn.notifyDataSetChanged();
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        ItemTouchHelper itemTouchHelperIn = new ItemTouchHelper(simpleCallbackIn);
        itemTouchHelperIn.attachToRecyclerView(rvVehiclesIn);

        ItemTouchHelper.SimpleCallback simpleCallbackOut = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Booking b = bookingListOut.get(viewHolder.getAdapterPosition());
                dbBooking.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Booking booking = dataSnapshot.getValue(Booking.class);
                            if (booking.getPlate().equals(b.getPlate()) && booking.getCheckin().equals(b.getCheckin())) {
                                bookingListOut.clear();
                                dbBooking.child(dataSnapshot.getKey()).removeValue();
                                laBookingOut.notifyDataSetChanged();
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        ItemTouchHelper itemTouchHelperOut = new ItemTouchHelper(simpleCallbackOut);
        itemTouchHelperOut.attachToRecyclerView(rvVehiclesOut);

        return v;
    }

    private void getBookingsOut() {
        // Lanzamos una query para buscar las reservas hechas por el usuario
        dbBooking.orderByChild("idUser").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingListOut.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking b = dataSnapshot.getValue(Booking.class);
                    bookingListOut.add(b);
                }
                laBookingOut.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getBookingsIn() {
        // Array de matriculas del usuario que buscaremos
        ArrayList<String> plates = new ArrayList<>();
        // Lanzamos una query para buscar las reservas hechas por el usuario
        dbVehicles.orderByChild("idUser").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vehicle v = dataSnapshot.getValue(Vehicle.class);
                    plates.add(v.getPlate());
                }
                // Si no tenemos vehículos, nos damos con un canto en los dientes
                if (plates.isEmpty()) {
                    return;
                }
                bookingListIn.clear();
                // Por cada matricula iteramos las reservas
                for (String plate : plates) {
                    //Lanzamos la query en busca de reservas con la matrícula
                    dbBooking.orderByChild("plate").equalTo(plate).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                // Itera, lee bien matriculas
                                Booking b = dataSnapshot.getValue(Booking.class);
                                bookingListIn.add(b);
                            }
                            laBookingIn.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getVehicles() {
        // buscamos los vehículos que nos interesen
        dbVehicles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                    vehicleList.add(vehicle);
                }
                laBookingOut.notifyDataSetChanged();
                laBookingIn.notifyDataSetChanged();
                getPhotos();
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
    }

    public void getPhotos() {
        for (Vehicle ve : vehicleList) {
            dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles/" + ve.getPlate());
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
                                    laBookingOut.notifyDataSetChanged();
                                    laBookingIn.notifyDataSetChanged();
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
    }
}