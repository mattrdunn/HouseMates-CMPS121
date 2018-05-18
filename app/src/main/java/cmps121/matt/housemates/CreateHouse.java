package cmps121.matt.housemates;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateHouse extends AppCompatActivity
{

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
        houseRef = databaseRef.child("classes");

        // Get current logged into Firebase user
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();


        houseNameInput = (EditText) findViewById(R.id.create_house_name);
        housePasswordInput = (EditText) findViewById(R.id.create_house_password);

        Button createClass = (Button) findViewById(R.id.createHouse);
        createClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get info from edittexts
                String houseName = houseNameInput.getText().toString();
                String housePassword = housePasswordInput.getText().toString();

                addToDatabase(houseName,housePassword);

                // Quick fix for now, we're making pushing another activity on the stack on top
                // of the rest instead of finishing activities b/c it won't refresh currrently
                Intent intent = new Intent(CreateHouse.this,MyHouses.class );
                startActivity(intent);
            }
        });

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
}
