package ninja.wooo.ninjacoffee;

import java.util.List;

import ninja.wooo.ninjacoffee.model.CoffeeMachine;

/**
 * Created by fab on 03.06.2015.
 */
public class CoffeeMachineUtils {
    public static int indexOfMachine(List<CoffeeMachine> coffeeMachines, long id) {
        for (int i = 0; i < coffeeMachines.size(); i++) {
            if (coffeeMachines.get(i).getId() != null && coffeeMachines.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
