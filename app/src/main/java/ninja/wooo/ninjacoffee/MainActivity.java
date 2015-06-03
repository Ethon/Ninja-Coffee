package ninja.wooo.ninjacoffee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ninja.wooo.ninjacoffee.model.CoffeeMachine;
import ninja.wooo.ninjacoffee.serializer.SharedPreferencesStore;


public class MainActivity extends ActionBarActivity {
    // TODO: I've tried DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) however this will always display the AM/PM in my emulator...
    // So for now I simply formatted it this way:
    private static final DateFormat LAST_CHANGE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private Spinner coffeeMachineSpinner;
    private Button drinkCoffeeButton;
    private EditText editText;
    private TextView lastChangeText;

    // Data will be stored in the sharedPreferences for now
    private SharedPreferencesStore store;

    // actual data model found in the store
    private List<CoffeeMachine> coffeeMachines;

    private CoffeeMachine selectedMachine;
    private TextWatcher editTextWatcher;



    private void showManageActivity() {
        Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // create a combo with all the items
        coffeeMachines = store.loadAll();
        CoffeeMachineAdapter coffeeMachineAdapter = new CoffeeMachineAdapter(this, coffeeMachines);
        coffeeMachineSpinner.setAdapter(coffeeMachineAdapter);

        // if there are no coffee machines, it must be the first start, go and open the settings to create a new machine
        if (coffeeMachines.size() == 0) {
            // open the settings activity
            showManageActivity();
            return;
        }

        // restore the last coffee machine
        long lastMachine = store.getLastSelectedCoffeeMachine();
        int index = CoffeeMachineUtils.indexOfMachine(coffeeMachines, lastMachine);
        if (index == -1) {
            // well, someone removed this machine, we want to fallback to a suitable index
            index = 0;
        }

        coffeeMachineSpinner.setSelection(index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.coffee_credit), Context.MODE_PRIVATE);
        this.store = new SharedPreferencesStore(sharedPreferences);

        editText = (EditText) findViewById(R.id.coffeeCreditField);
        lastChangeText = (TextView) findViewById(R.id.lastChangeField);
        drinkCoffeeButton = (Button) findViewById(R.id.drinkCoffeeButton);
        coffeeMachineSpinner = (Spinner) findViewById(R.id.coffeeMachineSpinner);


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

        editTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (selectedMachine == null) {
                    // no-op if no machine has been selected
                    return;
                }

                if (!s.toString().trim().isEmpty() && !s.toString().trim().equals("-")) {
                    selectedMachine.setCount(Integer.parseInt(s.toString().trim()));
                } else {
                    selectedMachine.setCount(0);
                    editText.setText("0");
                }
                Date now = new Date();
                selectedMachine.setLast(now);
                lastChangeText.setText(LAST_CHANGE_FORMAT.format(now));

                store.saveOrUpdate(selectedMachine);
            }
        };

        editText.addTextChangedListener(editTextWatcher);
    }

    private void onCoffeeMachineSelectionChanged(CoffeeMachine selectedMachine) {
        this.selectedMachine = selectedMachine;
        editText.setEnabled(selectedMachine != null);
        drinkCoffeeButton.setEnabled(selectedMachine != null);

        // in case no machine available, reset the texts
        if (selectedMachine == null) {
            editText.setText("---"); // ha, job references here^^ :D
            lastChangeText.setText("---");
            return;
        }

        // otherwise use the data from the selected machine in our fields
        // but we need to make sure that the listener won't be invoked, otherwise it will overwrite the last time stamp is going on ;)
        editText.removeTextChangedListener(editTextWatcher);
        editText.setText(selectedMachine.getCount() + "");
        lastChangeText.setText(LAST_CHANGE_FORMAT.format(selectedMachine.getLast()));
        editText.addTextChangedListener(editTextWatcher);

        store.setLastSelectedCoffeeMachine(selectedMachine.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            showManageActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDrinkCoffee(View view) {
        int credit = Integer.parseInt(editText.getText().toString());
        // this will invoke the listener and update the coffee machine
        editText.setText(String.valueOf(credit - 1));
    }
}
