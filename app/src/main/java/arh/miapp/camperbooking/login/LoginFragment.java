package arh.miapp.camperbooking.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import arh.miapp.camperbooking.R;

public class LoginFragment extends Fragment {

    private TextInputLayout tilMail;
    private TextInputLayout tilPass;
    private Button btLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        tilMail = (TextInputLayout) v.findViewById(R.id.tilUser);
        tilPass = (TextInputLayout) v.findViewById(R.id.tilPass);
        btLogin = (Button) v.findViewById(R.id.bLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tilMail.setError(null);
                tilPass.setError(null);
                String mail = tilMail.getEditText().getText().toString().trim();
                String pass = tilPass.getEditText().getText().toString();
                if (mail.isEmpty() || pass.isEmpty()){
                    if (mail.isEmpty()) {
                        tilMail.setError(getString(R.string.required));
                    }
                    if (pass.isEmpty()) {
                        tilPass.setError(getString(R.string.required));
                    }
                    return;
                }

                ((MainActivity) getActivity()).mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ((MainActivity) getActivity()).loginSuccesful(mail);
                        } else {
                            Toast.makeText(getActivity(), R.string.error_login, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        return v;
    }
}