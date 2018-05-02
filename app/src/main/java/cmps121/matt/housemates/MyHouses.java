package cmps121.matt.housemates;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MyHouses extends AppCompatActivity
{

    private static final String TAG = "MyHouses";
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_houses);
    }



}
