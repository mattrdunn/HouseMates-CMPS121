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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinHouse extends AppCompatActivity
{

    EditText houseNameInput;
    EditText housePasswordInput;
    private static final String TAG = "Join House";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference databaseRef;
    private DatabaseReference houseRef;
    private View focusView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

    // This method will validate the literal inputs, then run Firebase checks to make sure it's valid.
    // It will then call addUserToHouse() to add the user to the house.
    // TODO: Still need to notify if you already joined the house
    public void checkValidHouse()
    {
        final String houseName = houseNameInput.getText().toString().trim();
        final String housePassword = housePasswordInput.getText().toString().trim();

        // Initial checks for the edit texts fields
        if (badName(houseName))
        {
            return;
        }
        else if (badPassword(housePassword))
        {
            return;
        }
        else
        {

            houseRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    // Counter to iterate through all the childs in houses
                    int counter = 1;
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {
                        // gets all houses inside class child
                        String classKeys = data.getKey();
                        if (classKeys.equals(houseName))
                        {
                            DatabaseReference userKeyDatabase = houseRef.child(classKeys);

                            ValueEventListener eventListener = new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.getKey().equals(houseName))
                                    {
                                        Log.d("Get Key", dataSnapshot.getKey().toString());
                                        String housePass = dataSnapshot.child("housePassword").getValue().toString();

                                        if (housePass.equals(housePassword))
                                        {
                                            // Add user normally without any problems
                                            addUserToHouse(houseName);
                                            startNext(houseName);
                                        }
                                        else
                                        {
                                            housePasswordInput.setError("The entered password was incorrect");
                                            housePasswordInput.requestFocus();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            userKeyDatabase.addListenerForSingleValueEvent(eventListener);
                        }
                        else
                        {

                            // This counter checking every child if this house even exists
                            if (counter >= dataSnapshot.getChildrenCount())
                            {

                                houseNameInput.setError("This house name does not exist");
                                houseNameInput.requestFocus();

                                Log.d("Invalid House Name: ", houseName);
                            }
                            counter++;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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

    //----------------------------------------------------------------------------------------------
    // boolean helper functions that check for valid inputs
    //----------------------------------------------------------------------------------------------


    // Checks password if its empty
    public boolean badPassword(String password)
    {
        if(password.isEmpty())
        {
            Log.d(TAG, "THE PASSWORD IS EMPTY");
            housePasswordInput.setError(getString(R.string.error_field_required));
            focusView = housePasswordInput;
            focusView.requestFocus();
            return true;
        }
        else
            return false;
    }


    // Returns true if the house name is bad; otherwise, return false
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
