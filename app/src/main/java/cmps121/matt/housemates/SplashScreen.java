package cmps121.matt.housemates;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity
{

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserRef;

    private static final String TAG = "Splash Screen";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Authenticating Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Access users in database
        mUserRef = mDatabase.child("users");


        Log.d(TAG,"we out here boys");
        if (mFirebaseUser == null) {
            //Not logged in, launch the Log in activity
            loadLogInView();
        }
        else
        {

            mUserId = mFirebaseUser.getUid();
            Log.d("mUserId", mUserId);

            //Checks on if the user is a professor and is trying to access the student view
            mUserRef.child(mUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

//                    UserInformation user = dataSnapshot.getValue(UserInformation.class);
//                    if (user == null) {
//                        Log.d(TAG, "This is a null user.");
//                    }
//                    else
//                    {
////                        Log.d("StudentRef", "First Name: " + user.getFirstName() + " Last Name: " + user.getLastName() + ", ID: " + user.getStudentId());
////                        loadStudentView(); // load student page
//                        startActivity(new Intent (SplashScreen.this, MyHouses.class));
//                        finish();
//                    }

                    // Placeholder to go to MyHouses activity
                    startActivity(new Intent (SplashScreen.this, MyHouses.class));
                    finish();

                }

                @Override
                public void onCancelled(DatabaseError error)
                {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            ProgressBar prog = (ProgressBar) findViewById(R.id.progressBar);
            prog.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);


        }
    }

    //Allows for the loading of the log-in page
    private void loadLogInView()
        {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
            Log.d(TAG, "Going to log in view activity");
        }


}
