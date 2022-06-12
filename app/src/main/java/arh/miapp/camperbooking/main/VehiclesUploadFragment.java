package arh.miapp.camperbooking.main;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.objects.Vehicle;

public class VehiclesUploadFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    MaterialButton explorer, upload;

    ArrayList<Uri> images;
    HashMap<String, String> hashMapUri;

    private Uri imageUri;
    private ProgressDialog pd;
    int counter = 0;

    /*
    owner lo pillo de user.getName,
    idUser lo pillo de idUser
    rating no le pongo pq no tiene sentido implementar algo tan costoso xd

    foto tengo que subirlo como un child, con varias fotos, numeradas despues de crear el objeto en firestore
     */

    TextInputLayout plate, owner, brand, model, fuel, city, type, description;
    TextInputLayout year, passengers, beds;
    TextInputLayout pricePerDay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vehicles_upload, container, false);
        images = new ArrayList<>();
        hashMapUri = new HashMap<>();
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Publicando vehículo...");
        viewbinding(v);


        explorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        upload.setEnabled(false);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                String plateStr = plate.getEditText().getText().toString().trim();
                uploadVehicle();
                uploadPhoto(plateStr);
                pd.dismiss();
            }
        });

        return v;
    }

    private void uploadVehicle() {
        DatabaseReference dbVehicles = FirebaseDatabase.getInstance().getReference("vehicles");
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String user = currentFirebaseUser.getUid();
        //String key = dbVehicles.push().getKey();
        String plateStr = plate.getEditText().getText().toString().trim();

        Random rnd = new Random();
        double rating = rnd.nextDouble() * 5.0;

        Map<String, Object> vehicleMap = new HashMap<>();
        vehicleMap.put("beds", Integer.parseInt(beds.getEditText().getText().toString().trim()));
        vehicleMap.put("brand", brand.getEditText().getText().toString().trim());
        vehicleMap.put("city", city.getEditText().getText().toString().trim());
        vehicleMap.put("description", description.getEditText().getText().toString().trim());
        vehicleMap.put("fuel", fuel.getEditText().getText().toString().trim());
        vehicleMap.put("model", model.getEditText().getText().toString().trim());
        vehicleMap.put("idUser", user);
        vehicleMap.put("passengers", Integer.parseInt(passengers.getEditText().getText().toString().trim()));
        vehicleMap.put("plate", plateStr);
        vehicleMap.put("pricePerDay", Double.parseDouble(pricePerDay.getEditText().getText().toString().trim()));
        vehicleMap.put("rating", rating);
        vehicleMap.put("type", type.getEditText().getText().toString().trim());
        vehicleMap.put("year", Integer.parseInt(year.getEditText().getText().toString().trim()));

        //databaseReference.child("Paso").child(invitacion).setValue(datos);
        dbVehicles.child(plateStr).setValue(vehicleMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(), "Vehículo creado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Cagaste", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos que viene de seleccionar una foto y ha salido correctamente
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                // Comprobamos que el array de seleccion de fotos no sea null
                if (data.getClipData() != null) {
                    upload.setEnabled(true);
                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSelect = 0;
                    while (currentImageSelect < countClipData) {
                        imageUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                        images.add(imageUri);
                        currentImageSelect++;
                    }
                } else {
                    upload.setEnabled(false);
                    Toast.makeText(getActivity(), "Selecciona al menos 2 imagenes\n(mantener pulsado)", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void viewbinding(View v) {
        explorer = v.findViewById(R.id.vehUpSelectImages);
        upload = v.findViewById(R.id.vehUpUpload);
        plate = v.findViewById(R.id.tilVehPlate);
        brand = v.findViewById(R.id.tilVehBrand);
        model = v.findViewById(R.id.tilVehModel);
        fuel = v.findViewById(R.id.tilVehFuel);
        city = v.findViewById(R.id.tilVehCity);
        type = v.findViewById(R.id.tilVehType);
        description = v.findViewById(R.id.tilVehDescription);
        year = v.findViewById(R.id.tilVehYear);
        passengers = v.findViewById(R.id.tilVehPassengers);
        beds = v.findViewById(R.id.tilVehBeds);
        pricePerDay = v.findViewById(R.id.tilVehPricePerDay);
    }


    public void uploadPhoto(String plate) {
        // Me crea una carpeta para la matricula
        StorageReference imageFolder = FirebaseStorage.getInstance("gs://weeknvan.appspot.com").getReference().child("vehicles/"+plate);
        for (Uri image : images) {
            // Creamos el nombre del archivo, 1234BBB-img01.jpg
            int thisCounter = ++counter;
            //TODO a ver si acierto xd
            StorageReference imageName = imageFolder.child(plate + "-img" + String.format("%02d", thisCounter));
            imageFolder.child(imageName.getName()).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFolder.child(imageName.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String miPath = String.valueOf(uri);
                            storeLink(miPath, plate, thisCounter);
                        }
                    });
                }
            });
        }
    }

    private void storeLink(String miPath, String plate, int counter) {
        // Guardo el link en mi array, para ello apunto a el
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("vehicles/" + plate + "/").child("photos");
        hashMapUri.put(plate +"-img"+ String.format("%02d", counter), miPath);
        dbr.setValue(hashMapUri);
    }
}