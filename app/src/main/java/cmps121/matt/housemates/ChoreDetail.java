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
        final String dateCreated = i.getStringExtra("dateCreated");
        final String dueDate = i.getStringExtra("dueDate");

        TextView title = (TextView) findViewById(R.id.chore_title);
        TextView dateCreatedView = (TextView) findViewById(R.id.date_view);
        TextView dueDateView = (TextView) findViewById(R.id.time_view);
        TextView description = (TextView) findViewById(R.id.descrption_detail);
        TextView assignee = (TextView) findViewById(R.id.person_detail);

        title.setText(choreName);
        dateCreatedView.setText("Created " + dateCreated);
        dueDateView.setText("Finish by " + dueDate);
        description.setText(choreDescription);
        assignee.setText("Assignee: " +choreAssignee);
    }
}
