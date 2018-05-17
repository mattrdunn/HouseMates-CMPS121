package cmps121.matt.housemates;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AddChore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chore);

        Button addChore = (Button) findViewById(R.id.addChore);

        addChore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity (new Intent(AddChore.this, ChoreList.class));
            }
        });

    }


/*

    Commented out because it doesn't like the array adapter methods

    // Spinner function to choose an assignee for the task.
    // Later, this should be implemented using CursorAdapter to have the choices selected from database
    // Because the ArrayAdapter class hard codes the choices.
    Spinner spinner = (Spinner) findViewById(R.id.assigneeSpinner);
    // Create an ArrayAdapter using string array (assigneeArray) and default spinner layout
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assigneeArray, android.R.layout.simple_spinner_item);
    //Specify layout to use when list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    //Apply the adapter to the spinner
    spinner.setAdapter(adapter);*/


}
