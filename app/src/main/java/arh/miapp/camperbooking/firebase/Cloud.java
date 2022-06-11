package arh.miapp.camperbooking.firebase;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.objects.Booking;
import arh.miapp.camperbooking.objects.User;
import arh.miapp.camperbooking.objects.Vehicle;

public class Cloud {

    // Apuntamos a Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Sacamos el id y el mail del usuario de Firebase Auth
    private String userId;
    private String mail;

    // Creamos los arrays que necesitaremos más adelante
    private ArrayList<User> users;
    private ArrayList<Booking> bookingsAsUser;
    private ArrayList<Booking> bookingsAsOwner;
    private ArrayList<Vehicle> vehicles;

    public Cloud(){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();
        mail = currentUser.getEmail();
    }

}
