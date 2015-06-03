package ninja.wooo.ninjacoffee;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.List;

import ninja.wooo.ninjacoffee.model.CoffeeMachine;

/**
 * Created by fab on 03.06.2015.
 */
public class CoffeeMachineAdapter extends BaseAdapter {

    private Activity activity;
    private List<CoffeeMachine> coffeeMachineList;

    public CoffeeMachineAdapter(Activity activity, List<CoffeeMachine> coffeeMachineList) {
        this.activity = activity;
        this.coffeeMachineList = coffeeMachineList;
    }

    @Override
    public int getCount() {
        return coffeeMachineList.size();
    }

    @Override
    public Object getItem(int i) {
        return coffeeMachineList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return coffeeMachineList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView text = new TextView(activity);
        text.setTextColor(Color.BLACK);
        text.setTextSize(40);
        text.setText(coffeeMachineList.get(i).getName());
        return text;
    }

    public void add(CoffeeMachine coffeeMachine) {
        coffeeMachineList.add(coffeeMachine);
        notifyDataSetChanged();
    }

    public void remove(CoffeeMachine coffeeMachine) {
        coffeeMachineList.remove(coffeeMachine);
        notifyDataSetChanged();
    }

    public void setCoffeeMachineList(List<CoffeeMachine> coffeeMachineList) {
        this.coffeeMachineList = coffeeMachineList;
        notifyDataSetChanged();
    }
}
