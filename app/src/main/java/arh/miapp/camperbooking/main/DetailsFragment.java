package arh.miapp.camperbooking.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.Zoomy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.listadapters.SliderAdapter;
import arh.miapp.camperbooking.objects.Vehicle;

public class DetailsFragment extends Fragment {

    Vehicle vehicle;
    Bundle b;

    ImageView ivDetails;
    SliderView sliderView;
    ArrayList<Bitmap> images;
    SliderAdapter sliderAdapter;

    TextView tvDetailsPrice;
    TextView tvDetailsBrand;
    TextView tvDetailsModel;
    TextView tvDetailsType;
    TextView tvDetailsFuel;
    TextView tvDetailsPlate;
    //TextView tvDetailsOwner;
    TextView tvDetailsMore;
    TextView tvDetailsSeats;
    TextView tvDetailsSleep;

    RatingBar rbDetailsRating;

    Button bDetailsBook;

    StorageReference storageRef;
    DatabaseReference dbVehicles;


    public DetailsFragment(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        viewBinding(v);
        /*

        storageRef = ((BottomNavigationActivity) getActivity()).storageRef;
        storageRef = FirebaseStorage.getInstance("gs://weeknvan.appspot.com").getReference("vehicles/" + vehicle.getPhoto() + ".jpg");
        try {
            File localfile = File.createTempFile("tmp" + vehicle.getPlate(), ".jpg");
            storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    ivDetails.setImageBitmap(bitmap);
                    ivDetails.setVisibility(View.VISIBLE);
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
         */
        images = new ArrayList<>();
        getPhotos();
        chargeData();


        sliderAdapter = new SliderAdapter(images);
        sliderView.setSliderAdapter(sliderAdapter);

        bDetailsBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frgBooking = new BookingFragment(vehicle);
                ((BottomNavigationActivity) getActivity()).loadFragment(frgBooking, true);
            }
        });

        return v;
    }

    private void getPhotos() {
        images.clear();
        dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles/" + vehicle.getPlate());
        dbVehicles.child("photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String path = dataSnapshot.getValue(String.class);
                    storageRef = FirebaseStorage.getInstance("gs://weeknvan.appspot.com").getReference(path);
                    try {
                        File localfile = File.createTempFile("tmp" + vehicle.getPlate() + "thumbnail", ".jpg");
                        storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                images.add(bitmap);
                                sliderAdapter.notifyDataSetChanged();
                                //ivDetails.setImageBitmap(bitmap);
                                //ivDetails.setVisibility(View.VISIBLE);
                                //la.notifyDataSetChanged();
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

    private void viewBinding(View v) {
        ivDetails = (ImageView) v.findViewById(R.id.ivDetails);
        bDetailsBook = (Button) v.findViewById(R.id.bDetailsBook);
        tvDetailsPrice = (TextView) v.findViewById(R.id.tvDetailsPrice);
        tvDetailsBrand = (TextView) v.findViewById(R.id.tvDetailsBrand);
        tvDetailsModel = (TextView) v.findViewById(R.id.tvDetailsModel);
        tvDetailsType = (TextView) v.findViewById(R.id.tvDetailsType);
        tvDetailsFuel = (TextView) v.findViewById(R.id.tvDetailsFuel);
        tvDetailsPlate = (TextView) v.findViewById(R.id.tvDetailsPlate);
        //tvDetailsOwner = (TextView) v.findViewById(R.id.tvDetailsOwner);
        tvDetailsMore = (TextView) v.findViewById(R.id.tvDetailsMore);
        tvDetailsSeats = (TextView) v.findViewById(R.id.tvDetailsSeats);
        tvDetailsSleep = (TextView) v.findViewById(R.id.tvDetailsSleep);
        rbDetailsRating = (RatingBar) v.findViewById(R.id.rbDetailsRating);
        sliderView = (SliderView) v.findViewById(R.id.detailsSlider);
    }

    private void chargeData() {
        DecimalFormat df = new DecimalFormat("0.00");

        tvDetailsPrice.setText(df.format(vehicle.getPricePerDay()) + "???/" + getString(R.string.day));
        tvDetailsBrand.setText(getString(R.string.brand) + ": " + vehicle.getBrand());
        tvDetailsModel.setText(getString(R.string.model) + ": " + vehicle.getModel());
        tvDetailsType.setText(getString(R.string.vehicle_type) + ": " + vehicle.getType());
        tvDetailsFuel.setText(getString(R.string.fuel) + ": " + vehicle.getFuel());
        tvDetailsPlate.setText(getString(R.string.plate) + ": " + vehicle.getPlate());
        //tvDetailsOwner.setText(getString(R.string.owner) + ": " + vehicle.getOwner());
        tvDetailsMore.setText(getString(R.string.details) + ": " + vehicle.getDescription());
        tvDetailsSeats.setText(getString(R.string.seats) + ": " + vehicle.getPassengers());
        tvDetailsSleep.setText(getString(R.string.beds) + ": " + vehicle.getBeds());
        rbDetailsRating.setRating((float) vehicle.getRating());
    }
}