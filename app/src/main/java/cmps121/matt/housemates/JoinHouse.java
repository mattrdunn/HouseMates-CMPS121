package cmps121.matt.housemates;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinHouse extends AppCompatActivity {

    EditText houseNameInput;
    EditText housePasswordInput;
    private static final String TAG = "Join House";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference databaseRef;
    private DatabaseReference houseRef;
    private View focusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_house);

        houseNameInput = (EditText) findViewById(R.id.join_house_name);
        housePasswordInput = (EditText) findViewById(R.id.join_house_password);

        // General reference to whole DB
        databaseRef = FirebaseDatabase.getInstance().getReference();
        // House specific reference in DB
        houseRef = databaseRef.child("houses");

        // Get current logged into Firebase user
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();

        // initialize Join House button and checks for clicks
        Button joinHouse = (Button) findViewById(R.id.join_house_button);
        joinHouse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checkValidHouse();
            }
        });
    }

    // BAREBONES VERSION TODO: actually do everything in the description
    // This method will validate the literal inputs, then run Firebase checks to make sure it's valid.
    // It will then call addUserToHouse() to add the user to the house.
    public void checkValidHouse()
    {
        final String houseName = houseNameInput.getText().toString().trim();
        final String housePassword = housePasswordInput.getText().toString().trim();
        addUserToHouse(houseName);
        startNext(houseName);
    }

    // Method that will save the user to the list of members in this house, and
    // save the house into the user's list of houses
    public void addUserToHouse(String houseName)
    {
        // Store the user into the house
        houseRef.child(houseName).child("Housemates").child(mFirebaseUser.getUid()).setValue(mFirebaseUser.getDisplayName());

        // Also push the housename into the user's child in Firebase
        databaseRef.child("users").child(mFirebaseUser.getUid()).child("Joined Houses").child(houseName).setValue(houseName);
    }

    // Returns true if the name is bad; otherwise, return false
    public boolean badName(String houseName)
    {
        if(houseName.isEmpty())
        {
            Log.d(TAG, "THE HOUSE NAME IS EMPTY");
            houseNameInput.setError(getString(R.string.error_field_required));
            focusView = houseNameInput;
            focusView.requestFocus();
            return true;
        }
        else
            return false;
    }

    //Start the next intent, passing in the houseName
    public void startNext (String houseName) {

        //Create new intent to go to ChoreList page
        Intent choreListIntent = new Intent(JoinHouse.this, ChoreList.class);

        //Pass houseName to the new choreList activity
        choreListIntent.putExtra("houseName", houseName);

        startActivity(choreListIntent);
    }
}
