package cmps121.matt.housemates;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

import static cmps121.matt.housemates.SplashScreen.CHANNEL_ID;

public class MyHouses extends AppCompatActivity
{

    private static final String TAG = "My Houses";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference houseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mCurrentUserRef;
    private ArrayAdapter<String> aa;
    private ListView listView;

    //notification stuff
    int notificationID = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_houses);

        //NOTIFICATION STUFF

        Button notification_button = (Button) findViewById(R.id.notify);
        notification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                addNotification();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();            // reference to the signed-in Firebase user
        mDatabase = FirebaseDatabase.getInstance().getReference(); // main database reference
        mUsersRef = mDatabase.child("users");                      // reference to the users child
        houseRef = mDatabase.child("houses");
        mCurrentUserRef = mUsersRef.child(mFirebaseUser.getUid()); //gives a reference to the current user's children.

        // The list that will contain the user's list of houses
        final ArrayList<String> list = new ArrayList<>();

        listView = findViewById(R.id.house_listview);

        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String userKey = ds.getKey();
                    //Looks at children userID and gets the keys
                    //such as Enrolled classes, email, firstName etc.
                    DatabaseReference userKeyDatabase = mCurrentUserRef.child(userKey);
                    ValueEventListener valueEventListener = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot data: dataSnapshot.getChildren())
                            {
                                //Gets all classes in Enrolled Classes
                                String houses = data.getKey();
                                list.add(houses);
                                Log.d("data children: ", data.getKey());
                            }
                            aa = new ArrayAdapter<String>(MyHouses.this, R.layout.house_list_view, list);
                            listView.setAdapter(aa);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        { }
                    };
                    userKeyDatabase.addListenerForSingleValueEvent(valueEventListener);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            { }
        };
        mCurrentUserRef.addListenerForSingleValueEvent(eventListener);

        //CREATE HOUSE BUTTON
        Button createHouse = (Button) findViewById(R.id.create_house);
        createHouse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MyHouses.this, CreateHouse.class);
                startActivity(intent);
            }
        });

        //JOIN HOUSE BUTTON
        Button joinHouse = (Button) findViewById(R.id.join_house);
        joinHouse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MyHouses.this, JoinHouse.class);
                startActivity(intent);
            }
        });


        //IF ARRAY IS CLICKED
        //Takes you to class page based on the what class on the array is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent intent = new Intent(MyHouses.this, ChoreList.class);
                String houseName = list.get(position);
                intent.putExtra("houseName", houseName);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_out_menu, menu);
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
        if (id == R.id.action_logout)
        {
            mFirebaseAuth.signOut();
            loadSignInView();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadSignInView()
    {
        Intent intent = new Intent(this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Log.d(TAG, "Going to log in view activity");
    }

    private void addNotification() {

        //eventually, this should take us to choreDetail of the specific chore we are being notified of
        //currently, just dummy data being passed in
        Intent intent = new Intent (this, ChoreDetail.class);
        intent.putExtra("choreName", "Chore name");
        intent.putExtra("choreDescription", "Chore description");
        intent.putExtra("assignee", "A");
        intent.putExtra("dateCreated", "Date created");
        intent.putExtra("dueDate", "Date due");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_round);
        mBuilder.setContentTitle("You have a new notification");
        mBuilder.setContentText("Notification description");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, mBuilder.build());
    }
}
