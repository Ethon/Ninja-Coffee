package ninja.wooo.ninjacoffee;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ninja.wooo.ninjacoffee.model.CoffeeMachine;
import ninja.wooo.ninjacoffee.serializer.SharedPreferencesStore;


/**
 * Allows you to add/modify/remove CoffeeMachines.
  */
public class ManageActivity extends ActionBarActivity {

    private SharedPreferencesStore store;
    private Spinner coffeeMachineSpinner;
    private EditText nameText;
    private Button okButton;
    private Button resetButton;
    private Button deleteButton;
    private Button newMachineButton;

    private List<CoffeeMachine> coffeeMachines;
    private CoffeeMachineAdapter coffeeMachineAdapter;
    private CoffeeMachine selectedCoffeeMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        store = new SharedPreferencesStore(this.getSharedPreferences(getString(R.string.coffee_credit), Context.MODE_PRIVATE));

        nameText = (EditText) findViewById(R.id.nameText);
        coffeeMachineSpinner = (Spinner) findViewById(R.id.coffeeMachineSpinner2);

        okButton = (Button) findViewById(R.id.okButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        newMachineButton = (Button) findViewById(R.id.newMachineButton);

        // retrieve all the coffee machines from the store
        coffeeMachines = store.loadAll();
        coffeeMachineAdapter = new CoffeeMachineAdapter(this, coffeeMachines);
        coffeeMachineSpinner.setAdapter(coffeeMachineAdapter);

        coffeeMachineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                onCoffeeMachineSelectionChanged(coffeeMachines.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                onCoffeeMachineSelectionChanged(null);
            }
        });

        newMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCoffeeMachine();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCoffeeMachine.setName(nameText.getText().toString());
                store.saveOrUpdate(selectedCoffeeMachine);
                coffeeMachineAdapter.notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store.delete(selectedCoffeeMachine);
                CoffeeMachine tmpMachine = selectedCoffeeMachine;
                selectedCoffeeMachine = null;
                coffeeMachineAdapter.remove(tmpMachine);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCoffeeMachineSelectionChanged(selectedCoffeeMachine);
            }
        });
    }

    private void newCoffeeMachine() {
        CoffeeMachine newMachine = new CoffeeMachine();
        newMachine.setName("New Machine");
        store.saveOrUpdate(newMachine);
        // just by convenience we append the id of the machine to the name
        // this should simplify the activity a bit
        newMachine.setName("New Machine " + newMachine.getId());
        store.saveOrUpdate(newMachine);

        coffeeMachineAdapter.add(newMachine);
        // new machine will be appended to the Spinner and set as selected
        coffeeMachineSpinner.setSelection(coffeeMachines.size() - 1);
    }

    protected void onStart() {
        super.onStart();

        this.coffeeMachines = store.loadAll();
        coffeeMachineAdapter.setCoffeeMachineList(coffeeMachines);

        if (coffeeMachines.size() > 0) {
            // restore the last coffee machine
            long lastMachine = store.getLastSelectedCoffeeMachine();
            int index = CoffeeMachineUtils.indexOfMachine(coffeeMachines, lastMachine);
            if (index == -1) {
                // well, someone removed this machine, we want to fallback to a suitable index
                index = 0;
            }
            coffeeMachineSpinner.setSelection(index);
        } else {
            // if no machine is available, we will create one for the user - he may change it by
            // using the OK button
            newCoffeeMachine();
        }
    }

    private void onCoffeeMachineSelectionChanged(CoffeeMachine coffeeMachine) {
        okButton.setEnabled(coffeeMachine != null);
        deleteButton.setEnabled(coffeeMachine != null);
        resetButton.setEnabled(coffeeMachine != null);
        nameText.setEnabled(coffeeMachine != null);
        selectedCoffeeMachine = coffeeMachine;
        if (coffeeMachine == null) {
            nameText.setText("---");
            return;
        }

        nameText.setText(coffeeMachine.getName());
        store.setLastSelectedCoffeeMachine(selectedCoffeeMachine.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
