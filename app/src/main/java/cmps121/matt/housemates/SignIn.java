package cmps121.matt.housemates;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

public class SignIn extends AppCompatActivity
{

    private Button logInButton, signUpButton;

    private ProgressBar progressBar;
    private EditText loginInputEmail, loginInputPassword;
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private View focusView = null;

    private static int result = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Input from the login screen
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginInputEmail = (EditText) findViewById(R.id.email);
        loginInputPassword = (EditText) findViewById(R.id.password);

        signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        logInButton = findViewById(R.id.sign_in_button);
        logInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                submitForm();
            }
        });
    }

    private void submitForm()
    {
        final String email = loginInputEmail.getText().toString().trim();
        String password = loginInputPassword.getText().toString().trim();

        if(!checkEmail())
            return;
        if(!checkPassword())
            return;

        progressBar.setVisibility(View.VISIBLE);
        //authenticate user
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                // If sign in fails, Log a message to the LogCat. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                progressBar.setVisibility(View.GONE);
                if(!task.isSuccessful())
                {
                    //checks if email is in database
                    auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>()
                    {

                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                // getProviders().size() will return size 1. if email ID is available.
                                result = task.getResult().getProviders().size();
                                if(result != 1)
                                {
                                    loginInputEmail.setError("This email is not registered.");
                                    focusView = loginInputEmail;
                                    focusView.requestFocus();
                                }
                                else
                                {
                                    loginInputPassword.setError("Incorrect password entered");
                                    focusView = loginInputPassword;
                                    focusView.requestFocus();
                                }
                            }
                        }
                    });
                }
                //if authentication is successful
                else
                {
                    startActivity(new Intent (SignIn.this, SplashScreen.class));
                    finish();
                }
            }
        });

    }

    // checks to make sure that the emails field is not empty nor is it invalid
    private boolean checkEmail()
    {
        String email = loginInputEmail.getText().toString().trim();
        if (email.isEmpty())
        {
            loginInputEmail.setError(getString(R.string.error_field_required));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            loginInputEmail.setError(getString(R.string.error_invalid_email));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    // make sure to see that the password field is not empty
    private boolean checkPassword()
    {
        String password = loginInputPassword.getText().toString().trim();
        if (password.isEmpty())
        {
            loginInputPassword.setError(getString(R.string.error_field_required));
            focusView = loginInputPassword;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}

