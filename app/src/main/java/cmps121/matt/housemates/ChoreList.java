package cmps121.matt.housemates;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ChoreList extends AppCompatActivity
{

    private static final String TAG = "ChoreList";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);


    }
}
