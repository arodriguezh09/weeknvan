package arh.miapp.camperbooking.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import arh.miapp.camperbooking.R;
import arh.miapp.camperbooking.main.BottomNavigationActivity;
import arh.miapp.camperbooking.objects.User;

public class SignUpFragment extends Fragment {

    private TextInputLayout tilRegMail;
    private TextInputLayout tilRegPass;
    private TextInputLayout tilRegPass2;
    private TextInputLayout tilRegFirstName;
    private TextInputLayout tilRegLastName;
    private TextInputLayout tilRegPhone;
    private TextInputLayout tilRegNif;
    private Button bSignUp;
    private AwesomeValidation awesomeValidation;
    private boolean processing;
    private DatabaseReference database3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        processing = false;
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
        findViews(v);
        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                awesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
                // Pongo a null todos los campos antes de comprobarlos
                tilRegMail.setError(null);
                tilRegPass.setError(null);
                tilRegPass2.setError(null);
                tilRegFirstName.setError(null);
                tilRegLastName.setError(null);
                tilRegPhone.setError(null);
                tilRegNif.setError(null);
                // Pillo el contenido de los til
                String mail = tilRegMail.getEditText().getText().toString().trim();
                String pass = tilRegPass.getEditText().getText().toString().trim();
                String pass2 = tilRegPass2.getEditText().getText().toString().trim();
                String firstName = tilRegFirstName.getEditText().getText().toString().trim();
                String lastName = tilRegLastName.getEditText().getText().toString().trim();
                String phone = tilRegPhone.getEditText().getText().toString().trim();
                String nif = tilRegNif.getEditText().getText().toString().trim();
                // Reviso que los campos no estén vacios TODO cambiar por algo mas elegante
                if (mail.isEmpty() || pass.isEmpty() || pass2.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || nif.isEmpty()) {
                    if (mail.isEmpty()) {
                        tilRegMail.setError(getString(R.string.required));
                    }
                    if (pass.isEmpty()) {
                        tilRegPass.setError(getString(R.string.required));
                    }
                    if (pass2.isEmpty()) {
                        tilRegPass2.setError(getString(R.string.required));
                    }
                    if (firstName.isEmpty()) {
                        tilRegFirstName.setError(getString(R.string.required));
                    }
                    if (lastName.isEmpty()) {
                        tilRegLastName.setError(getString(R.string.required));
                    }
                    if (phone.isEmpty()) {
                        tilRegPhone.setError(getString(R.string.required));
                    }
                    if (nif.isEmpty()) {
                        tilRegNif.setError(getString(R.string.required));
                    }
                    return;
                }
                // Valido el mail
                awesomeValidation.clear();
                awesomeValidation.addValidation(getActivity(), R.id.tilRegMail, Patterns.EMAIL_ADDRESS, R.string.error_mail);
                if (!awesomeValidation.validate()) {
                    // TODO extra: el awesomeValidation hace una buena porquería con el material design, currarse esto si hay tiempo
                    // si en vez de meter ese style me hago uno que tenga a este como padre y le pongo lo siguiente, queda bien pro
                    // <item name="android:textColor">@color/color_error</item>
                    // <item name="android:textSize">11sp</item>
                    // https://stackoverflow.com/questions/33709066/how-to-set-textinputlayout-error-message-colour
                    tilRegMail.setErrorTextAppearance(R.style.TextAppearance_MaterialComponents_Caption);
                    tilRegMail.setError(getString(R.string.error_mail));
                    return;
                }
                // Valido la pass
                awesomeValidation.clear();
                awesomeValidation.addValidation(getActivity(), R.id.tilRegPass, ".{6,}", R.string.error_pass);
                if (!awesomeValidation.validate()) {
                    tilRegPass.setErrorTextAppearance(R.style.TextAppearance_MaterialComponents_Caption);
                    tilRegPass.setError(getString(R.string.error_pass));
                    return;
                }
                // Valido la pass2 con la pass
                awesomeValidation.clear();
                awesomeValidation.addValidation(getActivity(), R.id.tilRegPass2, R.id.tilRegPass, R.string.error_pass_match);
                if (!awesomeValidation.validate()) {
                    tilRegPass2.setErrorTextAppearance(R.style.TextAppearance_MaterialComponents_Caption);
                    tilRegPass2.setError(getString(R.string.error_pass_match));
                    return;
                }
                // Si pasamos las validaciones, llegaremos aquí
                ((MainActivity) getActivity()).mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO push del usuario a realtime database
                            database3 = FirebaseDatabase.getInstance().getReference("users");
                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String user = currentFirebaseUser.getUid();
                            String key = database3.push().getKey();
                            Map<String, Object> bookingMap = new HashMap<>();
                            bookingMap.put("firstName", firstName);
                            bookingMap.put("idUser", user);
                            bookingMap.put("lastName", lastName);
                            bookingMap.put("nif", nif);
                            bookingMap.put("phone", phone);
                            bookingMap.put("mail", mail);
                            database3.child(key).updateChildren(bookingMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), R.string.try_later, Toast.LENGTH_SHORT).show();
                                }
                            });


                            Toast.makeText(getActivity(), R.string.success_signup, Toast.LENGTH_SHORT).show();
                            // TODO extra: preguntar a Angel como puedo hacer para que haga eso SOLO si el backstack size es mayor a 0 ó a 1, idk.
                            // solucion fulera
                            if (!processing) {
                                processing = true;
                                getActivity().onBackPressed();
                            }
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            showToast(errorCode);
                        }
                    }
                });
            }
        });
        return v;
    }

    private void findViews(View v) {
        tilRegMail = (TextInputLayout) v.findViewById(R.id.tilRegMail);
        tilRegPass = (TextInputLayout) v.findViewById(R.id.tilRegPass);
        tilRegPass2 = (TextInputLayout) v.findViewById(R.id.tilRegPass2);
        tilRegFirstName = (TextInputLayout) v.findViewById(R.id.tilRegFirstName);
        tilRegLastName = (TextInputLayout) v.findViewById(R.id.tilRegLastName);
        tilRegPhone = (TextInputLayout) v.findViewById(R.id.tilRegPhone);
        tilRegNif = (TextInputLayout) v.findViewById(R.id.tilRegNif);
        bSignUp = (Button) v.findViewById(R.id.bSignUp);
    }

    private void showToast(String errorCode) {

        switch (errorCode) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(getActivity(), "El formato del token personalizado es incorrecto. Por favor revise la documentación.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(getActivity(), "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(getActivity(), "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(getActivity(), "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                tilRegMail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(getActivity(), "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                tilRegPass.requestFocus();
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(getActivity(), "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(getActivity(), "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(getActivity(), "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(getActivity(), "La dirección de correo electrónico ya está siendo utilizada por otra cuenta.", Toast.LENGTH_LONG).show();
                tilRegMail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(getActivity(), "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(getActivity(), "La cuenta de usuario ha sido inhabilitada por un administrador.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(getActivity(), "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(getActivity(), "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(getActivity(), "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(getActivity(), "La contraseña proporcionada no es válida.", Toast.LENGTH_LONG).show();
                tilRegPass.requestFocus();
                break;

        }
    }
}