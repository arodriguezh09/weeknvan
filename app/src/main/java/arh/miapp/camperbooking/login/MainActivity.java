package arh.miapp.camperbooking.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import arh.miapp.camperbooking.main.BottomNavigationActivity;
import arh.miapp.camperbooking.R;
//import arh.miapp.camperbooking.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth;

    private TextInputLayout tilUser;
    private TextInputLayout tilPass;
    private InputMethodManager imm;
    private Intent it;
    //private FragmentTransaction ft;
    //protected ActivityMainBinding ab;
    private Fragment frgLogin, frgSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ab = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(ab.getRoot());
        mAuth = FirebaseAuth.getInstance();
        frgLogin = new LoginFragment();
        frgSignUp = new SignUpFragment();
        // Cuando creamos la activity, se lanza el fragment login
        getSupportFragmentManager().beginTransaction().add(R.id.container, frgLogin).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loginSuccesful(currentUser.getEmail());
        }
    }

    public void hideKbrd(View view) {
        try {
            // Ocultar el teclado tras introducir un número
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO extra: Adecentar la excepción
        }
    }

    public void changeSignUp(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, frgSignUp).addToBackStack(null).commit();
    }

    public void loginSuccesful(String mail) {
        Intent it = new Intent(this, BottomNavigationActivity.class);
        it.putExtra("mail", mail);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }

}