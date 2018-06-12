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
import android.widget.Toast;

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
    private Spinner monthSpinner, daySpinner, yearSpinner;
    View focusView = null;
    private DatabaseReference databaseRef;
    private DatabaseReference houseRef;
    private DatabaseReference currHouseRef;
    private DatabaseReference housematesRef;
    private DatabaseReference usersRef;
    private String houseName;
    private int monthInt, dayInt, yearInt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chore);

        choreNameText = (EditText) findViewById(R.id.choreName);
        choreDescriptionText = (EditText) findViewById(R.id.choreDescription);
        assigneeSpinner = (Spinner)findViewById(R.id.assigneeSpinner);
        monthSpinner = (Spinner)findViewById(R.id.month_spinner);
        daySpinner = (Spinner)findViewById(R.id.day_spinner);
        yearSpinner = (Spinner)findViewById(R.id.year_spinner);

        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int m = localCalendar.get(Calendar.MONTH);
        int d = localCalendar.get(Calendar.DATE) - 1;
        int yyyy = localCalendar.get(Calendar.YEAR);
        Log.d(TAG, "MONTH == "+m);
        Log.d(TAG, "DAY == "+d);

        //will hold values 1 to 12 for each month
        final ArrayList<String> monthEntries = new ArrayList<String>();
        for(int i = 1; i <= 12; i++)
        {
            String ii = ""+i;
            monthEntries.add(ii);
        }

        //will hold values 1 to 31 for each day
        final ArrayList<String> dayEntries = new ArrayList<String>();
        for(int i = 1; i <= 31; i++)
        {
            String ii = ""+i;
            dayEntries.add(ii);
        }

        //will hold values 1 to 31 for each day
        final ArrayList<String> yearEntries = new ArrayList<String>();
        for(int i = yyyy; i <= yyyy+10; i++)
        {
            String ii = ""+i;
            yearEntries.add(ii);
        }


        // set all the spinners to hold the numbers
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(AddChore.this,
                android.R.layout.simple_spinner_item,
                monthEntries);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(m);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(AddChore.this,
                android.R.layout.simple_spinner_item,
                dayEntries);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setSelection(d);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(AddChore.this,
                android.R.layout.simple_spinner_item,
                yearEntries);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);



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

        housematesRef.addListenerForSingleValueEvent (new ValueEventListener () {
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

                String choreName = choreNameText.getText().toString();
                String choreDescription = choreDescriptionText.getText().toString();
                String assignee = assigneeSpinner.getSelectedItem().toString();

                if(choreName.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please specify the chore name", Toast.LENGTH_SHORT).show();
                    return;
                }

                //checks that it doesn't contain ., #, [, ]
                if (!isValidChoreName(choreName)) {
                    return;
                }

                // this call will set the values monthInt, dayInt, and yearInt for the due date
                // if the entered due date is invalid for any reason, display toast error and return.
                if(!isValidDueDate())
                {
                    return;
                }

                addToDatabase(choreName, choreDescription, assignee);
                Intent choreIntent = new Intent(AddChore.this, ChoreList.class);

                String listFilter = "false";
                choreIntent.putExtra("houseName", houseName);
                choreIntent.putExtra("listFilter", listFilter);

                choreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(choreIntent);
            }
        });

    }

    private void addToDatabase(String choreName, String choreDescription, String assignee)
    {
        //Create chore info class
        String dateCreated = getCurrentDay();
        String dueDate = buildDueDate(monthInt, dayInt, yearInt);
        AddChoreInformation choreInfo = new AddChoreInformation (choreName, choreDescription, assignee, dateCreated, dueDate);

        //add the new chore to the houseName child
        //TODO: Currently, this stores it under the name of the chore. This could lead to issues when trying to retrieve chores bc the chore name may not be unique, prob want to implement a unique id

        houseRef.child(houseName).child("Chores").child(choreName).setValue(choreInfo);
    }

    // Gets the current date for the dateCreated variable
    // Returns string of current day
    public String getCurrentDay()
    {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int dd = localCalendar.get(Calendar.DATE);
        int mm = localCalendar.get(Calendar.MONTH) + 1;
        int yyyy = localCalendar.get(Calendar.YEAR);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mm + "-");
        stringBuilder.append(dd + "-");
        stringBuilder.append(yyyy);

        return stringBuilder.toString();
    }

    public String buildDueDate(int mm, int dd, int yyyy)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mm + "-");
        stringBuilder.append(dd + "-");
        stringBuilder.append(yyyy);

        return stringBuilder.toString();
    }

    public boolean isValidDueDate()
    {
        String monthInput = monthSpinner.getSelectedItem().toString();
        monthInt = Integer.parseInt(monthInput);
        String dayInput = daySpinner.getSelectedItem().toString();
        dayInt = Integer.parseInt(dayInput);
        String yearInput = yearSpinner.getSelectedItem().toString();
        yearInt = Integer.parseInt(yearInput);
        Log.d(TAG, "MONTH INPUT === " + monthInt);
        Log.d(TAG, "DAY INPUT === " + dayInt);
        Log.d(TAG, "YEAR INPUT === " + yearInt);

        //get current date values
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int m = localCalendar.get(Calendar.MONTH)+1;
        int d = localCalendar.get(Calendar.DATE);
        int y = localCalendar.get(Calendar.YEAR);

        Log.d(TAG, "ACUTAL MONTH === " + m);
        Log.d(TAG, "ACTUAL DAY === " + d);
        Log.d(TAG, "ACTUAL YEAR === " + y);

        //TODO: actually finish the checks for the due date input and then save the due date into the database
        // If it's February
        if(monthInt == 2)
        {
            // if invalid February date and accounts for leap year!!
            if(isLeapYear(yearInt))
            {
                if(dayInt > 29)
                {
                    Toast.makeText(getApplicationContext(), "The due date entered is invalid", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else
            {
                if (dayInt > 28) {
                    Toast.makeText(getApplicationContext(), "The due date entered is invalid", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        // if it's April
        if(monthInt == 4)
        {
            // if invalid date
            if(dayInt > 30)
            {
                Toast.makeText(getApplicationContext(), "The due date entered is invalid", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // if it's June
        if(monthInt == 6)
        {
            // if invalid date
            if(dayInt > 30)
            {
                Toast.makeText(getApplicationContext(), "The due date entered is invalid", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // if it's September
        if(monthInt == 9)
        {
            // if invalid date
            if(dayInt > 30)
            {
                Toast.makeText(getApplicationContext(), "The due date entered is invalid", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // if it's November
        if(monthInt == 11)
        {
            // if invalid date
            if(dayInt > 30)
            {
                Toast.makeText(getApplicationContext(), "The due date entered is invalid", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        //check if date entered is earlier than today
        else if(Integer.parseInt(yearInput) == y)
        {
            if(monthInt < m)
            {
                Toast.makeText(getApplicationContext(), "Cannot set the due date in the past", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(monthInt == m)
            {
                if(dayInt < d)
                {
                    Toast.makeText(getApplicationContext(), "Cannot set the due date in the past", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    // returns true if it's a leap year
    public static boolean isLeapYear(int year)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    //returns true if chore name doesn't include ., #, [, ]
    public boolean isValidChoreName (String chore_name) {
        boolean is_valid = true;

        for (int i = 0; i < chore_name.length(); i++) {
            if (Character.toString(chore_name.charAt(i)).equals(".") ||
                    Character.toString(chore_name.charAt(i)).equals("[") ||
                    Character.toString(chore_name.charAt(i)).equals("]") ||
                    Character.toString(chore_name.charAt(i)).equals(".") ||
                    Character.toString(chore_name.charAt(i)).equals("#")) {
                choreNameText.setError("Chore name may not include the following characters: [ ] . #");
                focusView = choreNameText;
                focusView.requestFocus();
                return false;
            }
        }
        return true;
    }
}
