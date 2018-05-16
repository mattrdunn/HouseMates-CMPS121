package cmps121.matt.housemates;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MyHouses extends AppCompatActivity
{

    private static final String TAG = "MyHouses";
    private ListView houseList;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsersRef;
    private DatabaseReference mCurrentUserRef;
    private DatabaseReference directRef;
    private ArrayAdapter<String> aa;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_houses);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();            // reference to the signed-in Firebase user
        mDatabase = FirebaseDatabase.getInstance().getReference(); // main database reference
        mUsersRef = mDatabase.child("users");                      // reference to the users child
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


}
