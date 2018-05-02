package cmps121.matt.housemates;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    View focusView = null;
    private static FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initializes Firebase authentication object
        auth = FirebaseAuth.getInstance();

        // gets a reference to the database
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // initialize inputs from EditTexts
        nameInput = (EditText) findViewById(R.id.sign_up_name);
        emailInput = (EditText) findViewById(R.id.sign_up_email);
        passwordInput = (EditText) findViewById(R.id.sign_up_password);
        confirmPasswordInput = (EditText) findViewById(R.id.confirm_password);

        // initialize Sign Up button and checks for clicks
        Button signUp = (Button) findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                authenticateUser();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // Main authentication function. Calls boolean functions to check inputs, and if valid, creates
    // user with corresponding input values. If inputs are invalid, return with error.
    //----------------------------------------------------------------------------------------------

    public void authenticateUser()
    {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = nameInput.getText().toString().trim();
        String confirmPassword = nameInput.getText().toString().trim();

        if(badName(name)) // checks if the name is bad. If true, return.
            return;
        else if(badPassword(password, confirmPassword)) // checks if the passwords are bad. If true, return.
        {
            return;
        }
        else
        {

        }
    }

    //----------------------------------------------------------------------------------------------
    // boolean helper functions that check for valid inputs
    //----------------------------------------------------------------------------------------------

    // Returns true if the name is bad; otherwise, return false
    public boolean badName(String name)
    {
        if(name.isEmpty())
        {
            nameInput.setError(getString(R.string.error_field_required));
            focusView = nameInput;
            focusView.requestFocus();
            return true;
        }
        else
            return false;


    }

    // Checks for valid password input based on length and whether passwords match
    public boolean badPassword(String password, String confirmPassword)
    {
        if(password.isEmpty())
        {
            nameInput.setError(getString(R.string.error_field_required));
            focusView = passwordInput;
            focusView.requestFocus();
            return true;
        }
        else if(password.equals(confirmPassword))
        {
            nameInput.setError(getString(R.string.error_passwords_inequal));
            focusView = confirmPasswordInput;
            focusView.requestFocus();
            return true;
        }
        else
            return false;
    }

}
