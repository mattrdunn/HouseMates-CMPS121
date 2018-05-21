package cmps121.matt.housemates;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChoreList extends AppCompatActivity
{

    private static final String TAG = "ChoreList";
    private DatabaseReference databaseRef;
    private DatabaseReference houseRef;
    private DatabaseReference currHouseRef;
    private DatabaseReference choresRef;
    private String houseName;
    private ListView listView;
    private ArrayAdapter<String> aa;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);

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
        choresRef = currHouseRef.child("Chores");

        //The list that will contain the chores
        final ArrayList <AddChoreInformation> list = new ArrayList<AddChoreInformation>();
        listView = findViewById(R.id.chore_listview);

        //Retrieve data from firebase
        choresRef.addValueEventListener (new ValueEventListener() {

           @Override
           public void onDataChange (DataSnapshot dataSnapshot) {
               for (DataSnapshot ds: dataSnapshot.getChildren()) {

                   //add chore objects to list so that the other info can be passed on to next intent
                   String choreName = ds.child("choreName").getValue().toString();
                   String choreDescription = ds.child("choreDescription").getValue().toString();
                   String assignee = ds.child("assignee").getValue().toString();
                   AddChoreInformation chore = new AddChoreInformation(choreName, choreDescription, assignee);
                   list.add(chore);
               }

               //Create new string array to hold names of chores, which will be displayed in the listView
               String[] choreItems = new String[list.size()];

               for (int idx = 0; idx < choreItems.length; idx++) {
                   choreItems[idx] = list.get(idx).choreName;
               }

               aa = new ArrayAdapter<String>(ChoreList.this, R.layout.chore_list_view, choreItems);
               listView.setAdapter(aa);

           }

           @Override
           public void onCancelled (DatabaseError databaseError) {

           }
        });



        //IF ARRAY IS CLICKED
        //Set a Listener for when a listView item is clicked
        listView.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                AddChoreInformation selected = list.get(position);

                Intent intent = new Intent(ChoreList.this, ChoreDetail.class);

                intent.putExtra("choreName", selected.choreName);
                intent.putExtra("choreDescription", selected.choreDescription);
                intent.putExtra("assignee", selected.assignee);

                startActivity(intent);
            }
        });

        //Add a button listener for the add chore button
        Button addChore = (Button) findViewById(R.id.addChore);
        addChore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create an Intent to reference the addChore activity, passing in the name of the house
                Intent addChoreIntent = new Intent(ChoreList.this, AddChore.class);

                // Pass the houseName to the new Intent
                addChoreIntent.putExtra("houseName", houseName);
                startActivity(addChoreIntent);
            }
        });

    }
}
