package arh.miapp.camperbooking.main;

import android.content.Intent;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.listadapters.ListAdapterUser;
import arh.miapp.camperbooking.login.MainActivity;
import arh.miapp.camperbooking.objects.User;
import arh.miapp.camperbooking.objects.Vehicle;

public class UserFragment extends Fragment {

    DatabaseReference userDB;
    DatabaseReference dbVehicles;
    StorageReference storageRef;

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

    Fragment frgVehUpload = new VehiclesUploadFragment();
    Fragment frgDetails;

    Button bUserEdit;
    Button bLogout;
    Button bAddVehicle;

    RecyclerView userRvVehicle;

    boolean found = false;
    boolean edit = true;
    String userKey;
    String uid;
    ArrayList<Vehicle> vehicles;

    ListAdapterUser listAdapterUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        viewBinding(v);
        user = new User();

        vehicles = new ArrayList<>();

        // Busco a mi usuario para mostrar sus detalles
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDB = FirebaseDatabase.getInstance().getReference("users");
        userDB.orderByChild("idUser").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userKey = dataSnapshot.getKey();
                    retrieveUser(dataSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), R.string.error_loading_users, Toast.LENGTH_SHORT).show();
            }
        });

        listAdapterUser = new ListAdapterUser(vehicles, getContext());
        // Busco mis vehículos para mostrar sus detalles
        getVehicles();
        /*
        dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles");
        dbVehicles.orderByChild("idUser").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicles.clear();
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
                                listAdapterUser.notifyDataSetChanged();
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
                    vehicles.add(vehicle);
                }
                listAdapterUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), R.string.error_loading_vehicles, Toast.LENGTH_SHORT).show();

            }
        });

         */

        userRvVehicle = (RecyclerView) v.findViewById(R.id.userRvVehicle);
        userRvVehicle.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frgDetails = new DetailsFragment(vehicles.get(userRvVehicle.getChildAdapterPosition(view)));
                ((BottomNavigationActivity) getActivity()).loadFragment(frgDetails, true);
            }
        });
        userRvVehicle.setAdapter(listAdapterUser);
        // Listener al boton de logout
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), R.string.logout_warning, Toast.LENGTH_SHORT).show();
                goLogin();
            }
        });

        // Listener al boton de editar/guardar multifuncional
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
                    } else {
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
                        if (found) {
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

                            // ***************************************************************************************************************************************
                            //  Esta opcion ya no esta disponible, ya que con la nueva actualizacion se guardan los detalles desde el registro. Aun asi, la mantengo.
                            // ***************************************************************************************************************************************

                            // Si no lo he encontrado antes, no existe, asi que lo creo.
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

        // Listener al botón de añadir vehículo, abrirá el fragment
        bAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BottomNavigationActivity) getActivity()).loadFragment(frgVehUpload, true);
            }
        });
        return v;
    }

    private void getVehicles() {
        // buscamos los vehículos que nos interesen
        dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles");
        dbVehicles.orderByChild("idUser").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicles.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                    vehicles.add(vehicle);
                }
                getPhotos();
                listAdapterUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), R.string.error_loading_vehicles, Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void getPhotos(){
        for(Vehicle ve : vehicles){
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
                                    listAdapterUser.notifyDataSetChanged();
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

    private void retrieveUser(User user) {
        etUserName.setText(user.getFirstName());
        etUserSurname.setText(user.getLastName());
        etUserNif.setText(user.getNif());
        etUserMail.setText(user.getMail());
        etUserPhone.setText(user.getPhone());
    }

    private void viewBinding(@NonNull View v) {
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
        userRvVehicle = v.findViewById(R.id.userRvVehicle);

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
        //startActivity(it, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}