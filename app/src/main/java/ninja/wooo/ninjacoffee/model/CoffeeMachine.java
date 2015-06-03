package ninja.wooo.ninjacoffee.model;

import java.util.Date;

/**
 * Created by fab on 03.06.2015.
 *
 * Represents a coffee machine.
 */
public class CoffeeMachine {
    private Long id;
    private String name;
    private int count = 0;
    private Date last;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getLast() {
        return last;
    }

    public void setLast(Date last) {
        this.last = last;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
