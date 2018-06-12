package cmps121.matt.housemates;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateHouse extends AppCompatActivity
{
    private static final String TAG = "CreateHouse";
    View focusView = null;

    private EditText houseNameInput, housePasswordInput;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference databaseRef;
    private DatabaseReference houseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_house);

        // General reference to whole DB
        databaseRef = FirebaseDatabase.getInstance().getReference();
        // House specific reference in DB
        houseRef = databaseRef.child("houses");

        // Get current logged into Firebase user
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();


        houseNameInput = (EditText) findViewById(R.id.create_house_name);
        housePasswordInput = (EditText) findViewById(R.id.create_house_password);

        Button createClass = (Button) findViewById(R.id.create_house_button);
        createClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                authenticateHouse();
            }
        });

    }

    public void authenticateHouse()
    {
        // Retrieve info from edit texts
        final String houseName = houseNameInput.getText().toString();
        final String housePassword = housePasswordInput.getText().toString();



        if(badHouseName(houseName))
        {
            return;
        }
        else if(badPassword(housePassword))
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
                        // gets all house names inside house child
                        String classKeys = data.getKey();
                        if (classKeys.equals(houseName))
                        {
                            DatabaseReference userKeyDatabase = houseRef.child(classKeys);

                            ValueEventListener eventListener = new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.getKey().equals(houseName))
                                    {
                                        houseNameInput.setError("This house name already exists");
                                        houseNameInput.requestFocus();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            };
                            userKeyDatabase.addListenerForSingleValueEvent(eventListener);


                        }
                        else
                        {

                            // This counter checking every child for duplicate house names
                            if(counter >= dataSnapshot.getChildrenCount())
                            {
                                addToDatabase(houseName,housePassword);
                                Log.d(TAG, "House name doesn't exist, so this works");

                                // Quick fix for now, we're making pushing another activity on the stack on top
                                // of the rest instead of finishing activities b/c it won't refresh currently
                                //TODO: Pass the houseName to the MyHouses intent, just wondering but who wrote this?
                                //could've possibly been me -cassia
                                Intent intent = new Intent(CreateHouse.this,MyHouses.class );
                                startActivity(intent);
                            }
                            counter++;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }

    }



    private void addToDatabase(String houseName, String housePassword)
    {
        // Create the house information class for specific fields
        HouseInformation hinfo = new HouseInformation(houseName,housePassword);

        // Push into DB this currently does not account for overwriting the same house
        houseRef.child(houseName).setValue(hinfo);

        // Store the user into that same house
        houseRef.child(houseName).child("Housemates").child(mFirebaseUser.getUid()).setValue(mFirebaseUser.getDisplayName());

        // Also push the housename into the user
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

    // Returns true if the name is bad; otherwise, return false
    public boolean badHouseName(String name)
    {
        if(name.isEmpty())
        {
            Log.d(TAG, "THE HOUSENAME IS EMPTY");
            houseNameInput.setError(getString(R.string.error_field_required));
            focusView = houseNameInput;
            focusView.requestFocus();
            return true;
        }

        if (!no_special_characters(name)) {
            return true;
        }
        else
            return false;
    }

    public boolean no_special_characters (String houseName) {

        for (int i = 0; i < houseName.length(); i++) {
            if (Character.toString(houseName.charAt(i)).equals(".") ||
                    Character.toString(houseName.charAt(i)).equals("[") ||
                    Character.toString(houseName.charAt(i)).equals("]") ||
                    Character.toString(houseName.charAt(i)).equals(".") ||
                    Character.toString(houseName.charAt(i)).equals("#")) {
                houseNameInput.setError("House name may not include the following characters: [ ] . #");
                focusView = houseNameInput;
                focusView.requestFocus();
                return false;
            }
        }
        return true;
    }

}
