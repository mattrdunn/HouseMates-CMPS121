package cmps121.matt.housemates;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChoreDetail extends AppCompatActivity
{
    private static final String TAG = "ChoreDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_detail);

        Intent i = getIntent();
        final String choreName = i.getStringExtra("choreName");
        final String choreDescription = i.getStringExtra("choreDescription");
        final String choreAssignee = i.getStringExtra("assignee");

        TextView title = (TextView) findViewById(R.id.chore_title);
        TextView description = (TextView) findViewById(R.id.description_detail);
        TextView assignee = (TextView) findViewById(R.id.assignee_detail);

        title.setText(choreName);
        description.setText(choreDescription);
        assignee.setText(choreAssignee);
    }
}
