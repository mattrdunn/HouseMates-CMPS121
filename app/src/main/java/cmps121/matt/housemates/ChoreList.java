package cmps121.matt.housemates;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private DatabaseReference userRef;
    private DatabaseReference mCurrentUserRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String houseName;
    private ListView listView;
    private ArrayAdapter<String> aa;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);
        refreshListView();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        refreshListView();

    }

    public void refreshListView()
    {

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

        // User side of the DB reference
        userRef = databaseRef.child("users");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();            // reference to the signed-in Firebase user
        mCurrentUserRef = userRef.child(mFirebaseUser.getUid()); //gives a reference to the current user's children.


        //The list that will contain the chores
        final ArrayList <AddChoreInformation> list = new ArrayList<AddChoreInformation>();
        listView = findViewById(R.id.chore_listview);

        //Retrieve data from firebase
        choresRef.addListenerForSingleValueEvent (new ValueEventListener() {

            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    //add chore objects to list so that the other info can be passed on to next intent
                    String choreName = ds.child("choreName").getValue().toString();
                    String choreDescription = ds.child("choreDescription").getValue().toString();
                    String assignee = ds.child("assignee").getValue().toString();
                    String dateCreated = ds.child("dateCreated").getValue().toString();
                    String dueDate = ds.child("dueDate").getValue().toString();

                    AddChoreInformation chore = new AddChoreInformation(choreName, choreDescription, assignee, dateCreated, dueDate);

                    list.add(chore);
                }

                //Create new string array to hold names of chores, which will be displayed in the listView
                String[] choreItems = new String[list.size()];

                for (int idx = 0; idx < choreItems.length; idx++)
                {
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
                intent.putExtra("dateCreated", selected.dateCreated);
                intent.putExtra("dueDate", selected.dueDate);
                intent.putExtra("houseName", houseName);

                startActivity(intent);
            }
        });


        //Add a button listener for the add chore button
        final Button addChore = (Button) findViewById(R.id.add_chore);
        addChore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addChore();
            }
        });
    }

    public void addChore()
    {
        Intent addChoreIntent = new Intent(ChoreList.this, AddChore.class);
        Log.d(TAG, "H-H-H-House Name == " + houseName);
        // Pass the houseName to the new Intent
        addChoreIntent.putExtra("houseName", houseName);
        startActivity(addChoreIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_house_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deletehouse)
        {
            deleteHouse();
        }

        return super.onOptionsItemSelected(item);
    }

    // delete house from DB
    public void deleteHouse()
    {

        // Create an Intent to reference the MyHouse activity
        Intent choreIntent = new Intent(ChoreList.this, MyHouses.class);

        // Remove from house & user sides of the DB
        houseRef.child(houseName).removeValue();
        mCurrentUserRef.child("Joined Houses").child(houseName).removeValue();

        startActivity(choreIntent);
    }

}
