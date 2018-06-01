package cmps121.matt.housemates;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class AddChore extends AppCompatActivity {
    private static final String TAG = "Chores";
    private EditText choreNameText, choreDescriptionText;
    private Spinner assigneeSpinner;
    View focusView = null;
    private DatabaseReference databaseRef;
    private DatabaseReference houseRef;
    private DatabaseReference currHouseRef;
    private DatabaseReference housematesRef;
    private DatabaseReference usersRef;
    private String houseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chore);

        choreNameText = (EditText) findViewById(R.id.choreName);
        choreDescriptionText = (EditText) findViewById(R.id.choreDescription);
        assigneeSpinner = (Spinner)findViewById(R.id.assigneeSpinner);

        //creates a reference to the entire DB
        databaseRef = FirebaseDatabase.getInstance().getReference();

        //creates a reference to the houses child
        houseRef = databaseRef.child("houses");

        //get the houseName we are in by receiving it from the passed in Intent
        Intent i = getIntent();
        houseName = i.getStringExtra("houseName");

        //get a database reference to the current house we are in
        currHouseRef = houseRef.child(houseName);

        //create reference to the housemates of this current house
        housematesRef = currHouseRef.child("Housemates");

        //create reference to Users
        usersRef = databaseRef.child("users");

        //will hold the names of housemates the user can assign the chore to
        final ArrayList<String> assigneeChoices = new ArrayList<String>();

        //populate the Spinner w/ users from the database
        //For every user in houses>"housemates", match that housemate's ID with the corresponding
        //name under that ID's key in Users

        housematesRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    //the current userID we are looking at as we iterate through Housemates
                    final String housematesUserID = ds.getKey();

                    //need to look for this userID in the users child to get the associated name
                    usersRef.addValueEventListener (new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data: dataSnapshot.getChildren()) {
                                //check to see if the current user (data) equals currUserID (from Housemates)
                                //if true, add to assigneeChoices

                                final String usersUserID = data.getKey();

                                if ((usersUserID.toString()).equals(housematesUserID)) {
                                    Log.d(TAG, "inside if");
                                    //get the name associated w/ usersUserID
                                    //add that name to assigneeChoices
                                    String name = data.child("name").getValue().toString();
                                    Log.d(TAG, name);

                                    assigneeChoices.add(name);
                                    Log.d(TAG, "assigneeChoices = " + assigneeChoices.toString());

                                }

                            }

                            Log.d(TAG, "(after datasnapshots) assigneeChoices = " + assigneeChoices.toString());

                            ArrayAdapter<String> assigneeAdapter = new ArrayAdapter<>(AddChore.this,
                                    android.R.layout.simple_spinner_item,
                                    assigneeChoices);
                            assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            assigneeSpinner.setAdapter(assigneeAdapter);

                        }
                        public void onCancelled(DatabaseError databaseError) {
                        }

                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //When this button is pressed, add the chore to the database
        Button addChoreButton = (Button) findViewById(R.id.addChore);
        addChoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Get info from user-inputted fields
                //TODO: check input fields for periods, #, [, and ] because firebase doesn't like them
                String choreName = choreNameText.getText().toString();
                String choreDescription = choreDescriptionText.getText().toString();
                String assignee = assigneeSpinner.getSelectedItem().toString();

                addToDatabase(choreName, choreDescription, assignee);
                finish();

            }
        });

    }

    private void addToDatabase(String choreName, String choreDescription, String assignee) {
        //Create chore info class
        String dateCreated = getCurrentDay();
        String dueDate = "placeholder";
        AddChoreInformation choreInfo = new AddChoreInformation (choreName, choreDescription, assignee, dateCreated, dueDate);

        //add the new chore to the houseName child
        //TODO: Currently, this stores it under the name of the chore. This could lead to issues when trying to retrieve chores bc the chore name may not be unique, prob want to implement a unique id

        houseRef.child(houseName).child("Chores").child(choreName).setValue(choreInfo);
    }

    // Gets the current date for the dateCreated variable
    // Returns string of current day
    public String getCurrentDay() {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int dd = localCalendar.get(Calendar.DATE);
        int mm = localCalendar.get(Calendar.MONTH) + 1;
        int yyyy = localCalendar.get(Calendar.YEAR);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mm + "-");
        stringBuilder.append(dd + "-");
        stringBuilder.append(yyyy);
        //TODO: add logic for due date. Have to add field in the layout for it, either calendar view or whatever.
        return stringBuilder.toString();
    }
}
