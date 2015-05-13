package ninja.wooo.ninjacoffee;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    public static final String PREF_LAST_COFFEE_CHANGE = "lastCoffeeChange";
    public static final String PREF_COFFEE_COUNT = "coffeeCount";

    private SharedPreferences sharedPreferences;
    private EditText editText;
    private TextView lastChangeText;
    // TODO: I've tried DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) however this will always display the AM/PM in my emulator...
    // So for now I simply formatted it this way:
    private DateFormat lastChangeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences(getString(R.string.coffee_credit), Context.MODE_PRIVATE);
        editText = (EditText) findViewById(R.id.coffeeCreditField);
        lastChangeText = (TextView) findViewById(R.id.lastChangeField);


        editText.setText(sharedPreferences.getString(PREF_COFFEE_COUNT, "0"));

        Long lastCoffeeChange = sharedPreferences.getLong(PREF_LAST_COFFEE_CHANGE, 0L);
        if (lastCoffeeChange != 0L) {
            lastChangeText.setText(lastChangeFormat.format(new Date(lastCoffeeChange)));
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!s.toString().trim().isEmpty()) {
                    editor.putString(PREF_COFFEE_COUNT, s.toString());
                } else {
                    editor.putString(PREF_COFFEE_COUNT, "0");
                    editText.setText("0");
                }
                Date now = new Date();
                editor.putLong(PREF_LAST_COFFEE_CHANGE, now.getTime());
                lastChangeText.setText(lastChangeFormat.format(now));
                editor.commit();
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDrinkCoffee(View view) {
        int credit = Integer.parseInt(editText.getText().toString());
        editText.setText(String.valueOf(credit - 1));

        /*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("coffeeCount", String.valueOf(credit - 1));
        editor.commit();*/
    }
}
