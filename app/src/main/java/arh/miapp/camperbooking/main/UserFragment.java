package arh.miapp.camperbooking.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.login.LoginFragment;
import arh.miapp.camperbooking.login.MainActivity;
import arh.miapp.camperbooking.objects.User;

public class UserFragment extends Fragment {

    DatabaseReference userDB;

    User user;

    TextInputLayout tilUserName;
    TextInputLayout tilUserSurname;
    TextInputLayout tilUserNif;
    TextInputLayout tilUserMail;
    TextInputLayout tilUserPhone;

    EditText etUserName;
    EditText etUserSurname;
    EditText etUserNif;
    EditText etUserMail;
    EditText etUserPhone;

    Fragment frgVehUpload;

    Button bUserEdit;
    Button bLogout;
    Button bAddVehicle;

    boolean found = false;
    boolean edit = true;
    String userKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        viewBinding(v);
        frgVehUpload = new VehiclesUploadFragment();
        user = new User();
        userDB = FirebaseDatabase.getInstance().getReference("users");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = currentFirebaseUser.getUid();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User u = dataSnapshot.getValue(User.class);
                    if (u.getIdUser().equals(userId)) {
                        userKey = dataSnapshot.getKey();
                        found = true;
                        user = u;
                        break;
                    }
                }
                if (found) {
                    retrieveUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), R.string.error_loading_users, Toast.LENGTH_SHORT).show();
            }
        });
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), R.string.logout_warning, Toast.LENGTH_SHORT).show();
                goLogin();
            }
        });
        bUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit) {
                    // Permito la edicion de los campos
                    submitMode();
                    edit = false;
                } else {
                    // Cambio el comportamiento del botón para usarlo para guardar los cambios y subirlos al la bbdd
                    if (etUserName.getText().equals("") || etUserPhone.getText().equals("") || etUserSurname.getText().equals("") ||
                            etUserNif.getText().equals("") || etUserMail.getText().equals("")) {
                        Toast.makeText(getActivity(), R.string.required, Toast.LENGTH_SHORT).show();
                    }else{
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String user = currentFirebaseUser.getUid();
                        String key = userDB.push().getKey();
                        Map<String, Object> bookingMap = new HashMap<>();
                        bookingMap.put("firstName", etUserName.getText().toString());
                        bookingMap.put("idUser", user);
                        bookingMap.put("lastName", etUserSurname.getText().toString());
                        bookingMap.put("nif", etUserNif.getText().toString());
                        bookingMap.put("phone", etUserPhone.getText().toString());
                        bookingMap.put("mail", etUserMail.getText().toString());
                        // Si lo he encontrado antes, existe, así que actualizo.
                        if (found){
                            userDB.child(userKey).updateChildren(bookingMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), R.string.success_user_details, Toast.LENGTH_SHORT).show();
                                    editMode();
                                    edit = true;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), R.string.try_later, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Si no lo he encontrado antes, no existe, así que lo creo.
                            userDB.child(key).updateChildren(bookingMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), R.string.success_user_details, Toast.LENGTH_SHORT).show();
                                    editMode();
                                    edit = true;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), R.string.try_later, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });
        bAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BottomNavigationActivity) getActivity()).loadFragment(frgVehUpload, true);
            }
        });
        return v;
    }

    private void retrieveUser() {
        etUserName.setText(user.getFirstName());
        etUserSurname.setText(user.getLastName());
        etUserNif.setText(user.getNif());
        etUserMail.setText(user.getMail());
        etUserPhone.setText(user.getPhone());
    }

    private void viewBinding(View v) {
        tilUserName = v.findViewById(R.id.tilUserName);
        tilUserSurname = v.findViewById(R.id.tilUserSurname);
        tilUserNif = v.findViewById(R.id.tilUserNif);
        tilUserMail = v.findViewById(R.id.tilUserMail);
        tilUserPhone = v.findViewById(R.id.tilUserPhone);
        etUserName = v.findViewById(R.id.etUserName);
        etUserSurname = v.findViewById(R.id.etUserSurname);
        etUserNif = v.findViewById(R.id.etUserNif);
        etUserMail = v.findViewById(R.id.etUserMail);
        etUserPhone = v.findViewById(R.id.etUserPhone);
        bUserEdit = v.findViewById(R.id.bUserEdit);
        bLogout = v.findViewById(R.id.bLogout);
        bAddVehicle = v.findViewById(R.id.bAddVehicle);
        editMode();
    }

    private void editMode() {
        tilUserName.setEnabled(false);
        tilUserSurname.setEnabled(false);
        tilUserNif.setEnabled(false);
        tilUserMail.setEnabled(false);
        tilUserPhone.setEnabled(false);
        bUserEdit.setText(R.string.edit);
    }

    private void submitMode() {
        tilUserName.setEnabled(true);
        tilUserSurname.setEnabled(true);
        tilUserNif.setEnabled(true);
        tilUserMail.setEnabled(true);
        tilUserPhone.setEnabled(true);
        bUserEdit.setText(R.string.save_changes);
    }

    public void goLogin() {
        Intent it = new Intent(getActivity(), MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        // TODO extra: si queremos hacer una transicion lo hacemos asi. pillara la info desde el tema que tenga predefinido, mirar xml
        //startActivity(it, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}