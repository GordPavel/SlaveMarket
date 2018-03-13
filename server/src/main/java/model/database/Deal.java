package model.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import model.merchandises.Merchandise;
import model.merchandises.classes.Alien;
import model.merchandises.classes.Food;
import model.merchandises.classes.Poison;
import model.merchandises.classes.Slave;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "deal")
@XmlAccessorType(XmlAccessType.FIELD)
public class Deal implements Serializable, Comparable<Deal> {
    private LocalDateTime time;
    private DealState state;
    private User user;
    @XmlElements({
            @XmlElement(type = Slave.class, name = "slave"),
            @XmlElement(type = Food.class, name = "food"),
            @XmlElement(type = Poison.class, name = "poison"),
            @XmlElement(type = Alien.class, name = "alien")
    })
    private Merchandise merchandise;
    private int price;
    private int id;

    public Deal() {
    }

    /**
     * Constructor to create {@link Deal} object.
     *
     * @param user        user who performed action.
     * @param merchandise merchandise that was bought or sold.
     * @param price       merchandise's price.
     * @param state       current deal state. watch {@link DealState}
     *                    for more info.
     * @param id          deal's id.
     */
    public Deal(User user, Merchandise merchandise, int price, DealState state, int id) {
        this.user = user;
        this.merchandise = merchandise;
        this.price = price;
        this.state = state;
        this.id = id;
        this.time = LocalDateTime.now();
    }

    public Deal(Deal deal) {
        this.id = deal.id;
        this.merchandise = deal.merchandise;
        this.price = deal.price;
        this.state = deal.state;
        this.time = deal.time;
        this.user = deal.user;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Merchandise getMerchandise() {
        return this.merchandise;
    }

    public void setMerchandise(Merchandise merchandise) {
        this.merchandise = merchandise;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DealState getState() {
        return this.state;
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
        object.add("date",
                new JsonPrimitive(fmt.print(time)));
        object.add("id", new JsonPrimitive(id));
        object.add("state", new JsonPrimitive(state.toString()));
        object.add("price", new JsonPrimitive(price));
        object.add("merchandise", new JsonPrimitive(merchandise.getAllInfo()));
//    return this.time.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))
//            + ", merchandise=" + this.merchandise
//            + ", price=" + this.price
//            + ", state=" + this.state
//            + this.user.getUsername()
//            + ", dealId=" + this.id;
        return object.toString();
    }

    @Override
    public int compareTo(Deal o) {
        return Integer.compare(this.id, o.id);
    }
}
