package ninja.wooo.ninjacoffee.serializer;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ninja.wooo.ninjacoffee.R;
import ninja.wooo.ninjacoffee.model.CoffeeMachine;

/**
 * Created by fab on 03.06.2015.
 *
 * Provides API to load and store CoffeeMachines.
 */
public class SharedPreferencesStore {
    // we simply store coffee machines as follows in the shared preferences:
    private static final String KEY_MACHINE_LAST_ID = "coffeeMachine.lastId";

    private static final String KEY_MACHINE_ID = "coffeeMachine{0}.id";
    private static final String KEY_MACHINE_NAME = "coffeeMachine{0}.name";
    private static final String KEY_MACHINE_COUNT = "coffeeMachine{0}.count";
    private static final String KEY_MACHINE_LAST = "coffeeMachine{0}.last";
    private static final String KEY_MACHINE_FREE = "coffeeMachine{0}.free";

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesStore(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            throw new IllegalArgumentException("Expected the sharedPreferences to be not null!");
        }

        this.sharedPreferences = sharedPreferences;
    }

    public List<CoffeeMachine> loadAll() {
        List<CoffeeMachine> machines = new ArrayList<>();

        for (int i = 0; ; i++) {
            // get the coffee machine
            long machineId = sharedPreferences.getLong(MessageFormat.format(KEY_MACHINE_ID, i), -1);
            if (machineId == -1) {
                // check if this slot is marked as free -> there must be something at i + 1
                if (sharedPreferences.getBoolean(MessageFormat.format(KEY_MACHINE_FREE, i), false)) {
                    continue;
                }
                break; // we found all the machines
            }

            // get the rest of the data from the store
            String machineName = sharedPreferences.getString(MessageFormat.format(KEY_MACHINE_NAME, i), null);
            int coffeeCount = sharedPreferences.getInt(MessageFormat.format(KEY_MACHINE_COUNT, i), 0);
            long lastCoffee = sharedPreferences.getLong(MessageFormat.format(KEY_MACHINE_LAST, i), 0L);

            // finally create the machine and fill in the data
            CoffeeMachine machine = new CoffeeMachine();
            machine.setId(machineId);
            machine.setName(machineName);
            machine.setCount(coffeeCount);
            machine.setLast(new Date(lastCoffee));
            machines.add(machine);
        }

        return machines;
    }

    /**
     * @return the next free id within the shared preferences
     */
    private long getNextFreeId() {
        int freeId;
        for (freeId = 0; ; freeId++) {
            // get the coffee machine
            if (!sharedPreferences.contains(MessageFormat.format(KEY_MACHINE_ID, freeId))) {
                break;
            }
        }
        // TODO: Assumption: no one will ever create more than Long.MAX_VALUE coffee machines.
        return freeId;
    }

    public void saveOrUpdate(CoffeeMachine coffeeMachine) {
        // get a new id, if it hasn't been saved yet or reuse the one of the coffee machine
        if (coffeeMachine.getId() == null) {
            coffeeMachine.setId(getNextFreeId());
        }
        long id = coffeeMachine.getId();

        // prepare the editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(MessageFormat.format(KEY_MACHINE_ID, id), id);
        editor.putString(MessageFormat.format(KEY_MACHINE_NAME, id), coffeeMachine.getName());
        editor.putInt(MessageFormat.format(KEY_MACHINE_COUNT, id), coffeeMachine.getCount());
        editor.putLong(MessageFormat.format(KEY_MACHINE_LAST, id), coffeeMachine.getLast() != null ? coffeeMachine.getLast().getTime() : 0L);
        // in case someone added at a place of the free token, remove it
        editor.remove(MessageFormat.format(KEY_MACHINE_FREE, coffeeMachine.getId()));
        // commit the changes
        editor.commit();
    }

    public void delete(CoffeeMachine coffeeMachine) {
        if (coffeeMachine.getId() == null) {
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(MessageFormat.format(KEY_MACHINE_ID, coffeeMachine.getId()));
        editor.remove(MessageFormat.format(KEY_MACHINE_NAME, coffeeMachine.getId()));
        editor.remove(MessageFormat.format(KEY_MACHINE_COUNT, coffeeMachine.getId()));
        editor.remove(MessageFormat.format(KEY_MACHINE_LAST, coffeeMachine.getId()));
        // mark this place as free slot: loadAll will find any coffee machines after this indx
        editor.putBoolean(MessageFormat.format(KEY_MACHINE_FREE, coffeeMachine.getId()), true);
        // commit the changes
        editor.commit();
    }

    public void setLastSelectedCoffeeMachine(Long coffeeMachineId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_MACHINE_LAST_ID, coffeeMachineId);
        // commit the changes
        editor.commit();
    }

    public long getLastSelectedCoffeeMachine() {
        return sharedPreferences.getLong(KEY_MACHINE_LAST_ID, -1);
    }
}
