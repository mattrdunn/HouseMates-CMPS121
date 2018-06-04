package cmps121.matt.housemates;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
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
    public static final String CHANNEL_ID = "com.example.cassiarta.notifications.ANDROID";


    private static final String TAG = "Splash Screen";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        createNotificationChannel();

        //Authenticating Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Access users in database
        mUserRef = mDatabase.child("users");


        if (mFirebaseUser == null) {
            //Not logged in, launch the Log in activity
            loadSignInView();
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
    private void loadSignInView()
    {
        Intent intent = new Intent(this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
            Log.d(TAG, "Going to log in view activity");
    }

    //Creating the notification channel must happen before posting any notifications
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel";
            String description = "Description";
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
