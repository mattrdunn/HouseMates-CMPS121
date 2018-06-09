package cmps121.matt.housemates;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChoreDetail extends AppCompatActivity
{
    private static final String TAG = "ChoreDetail";

    // Database references
    private DatabaseReference databaseRef;
    private DatabaseReference houseRef;
    private String houseName, choreName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_detail);

        //creates a reference to the entire DB
        databaseRef = FirebaseDatabase.getInstance().getReference();

        //creates a reference to the houses child
        houseRef = databaseRef.child("houses");


        // Get description parameters from previous activity
        Intent i = getIntent();
        choreName = i.getStringExtra("choreName");
        final String choreDescription = i.getStringExtra("choreDescription");
        final String choreAssignee = i.getStringExtra("assignee");
        final String dateCreated = i.getStringExtra("dateCreated");
        final String dueDate = i.getStringExtra("dueDate");
        houseName = i.getStringExtra("houseName");


        TextView title = (TextView) findViewById(R.id.chore_title);
        TextView dateCreatedView = (TextView) findViewById(R.id.date_view);
        TextView dueDateView = (TextView) findViewById(R.id.time_view);
        TextView description = (TextView) findViewById(R.id.descrption_detail);
        TextView assignee = (TextView) findViewById(R.id.person_detail);

        title.setText(choreName);
        dateCreatedView.setText("Created " + dateCreated);
        dueDateView.setText("Finish by " + dueDate);
        description.setText(choreDescription);
        assignee.setText(choreAssignee);
    }


    // Creation of menu item (delete chore)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_chore_menu, menu);
        return true;
    }

    // Menu on click
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deletechore)
        {
            deleteChore();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteChore()
    {
        Intent choreIntent = new Intent(ChoreDetail.this, ChoreList.class);
        // Delete chore from DB
        // This putExtra is necessary for chorelist to have a reference to the initial houseName
        String listFilter = "false";
        String restartActivity = "true";
        choreIntent.putExtra("houseName", houseName);
        choreIntent.putExtra("listFilter", listFilter);

        houseRef.child(houseName).child("Chores").child(choreName).removeValue();

        choreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(choreIntent);
    }
}
