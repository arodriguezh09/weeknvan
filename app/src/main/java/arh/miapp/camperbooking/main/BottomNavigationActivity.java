package arh.miapp.camperbooking.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.objects.Booking;

public class BottomNavigationActivity extends AppCompatActivity {

    Fragment searchFragment, favsFragment, userFragment, favFragmentParam, myBookingFragment;
    DatabaseReference database;
    DatabaseReference database2;
    DatabaseReference database3;
    StorageReference storageRef;
    List<Booking> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String user = currentFirebaseUser.getUid();
        bookingList = new ArrayList<>();
        searchFragment = new SearchFragment();
        favsFragment = new VehiclesFragment();
        userFragment = new UserFragment();
        myBookingFragment = new MyBookingFragment();
        database2 = FirebaseDatabase.getInstance().getReference("bookings");
        database2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    bookingList.add(booking);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BottomNavigationActivity.this, R.string.error_loading_booking, Toast.LENGTH_SHORT).show();
            }
        });
        BottomNavigationView nav = findViewById(R.id.botton_navigation);
        nav.setOnItemSelectedListener(mOnItemSelectedListener);
        loadFragment(searchFragment, false);
    }
    private NavigationBarView.OnItemSelectedListener mOnItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.searchFragment:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    loadFragment(searchFragment, false);
                    return true;
                case R.id.favsFragment:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    loadFragment(favsFragment, false);
                    return true;
                case R.id.userFragment:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    loadFragment(userFragment, false);
                    return true;
                case R.id.myBookingFragment:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    loadFragment(myBookingFragment, false);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fr, boolean toBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fr);
        if (toBackStack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void search(String city, Date checkin, Date checkout){
        favFragmentParam = new VehiclesFragment(city, checkin, checkout);
        loadFragment(favFragmentParam, true);
    }
}