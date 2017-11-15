import org.junit.Assert;
import org.junit.Test;
import ru.cracker.Model.Model;
import ru.cracker.Model.Observable;
import ru.cracker.Model.SlaveMarketModel;
import ru.cracker.Model.database.MerchDb;
import ru.cracker.Model.merchandises.Slave;
import ru.cracker.exceptions.AlreadyBoughtException;
import ru.cracker.view.Observer;

import java.util.ArrayList;


public class Tests {
    MerchDb db = new MerchDb();
    ArrayList<Slave> slaves = new ArrayList<>();

    {
        db.addMerchandise(new Slave(140, 35, 12, "male", 0, "Pete", 100));
        db.addMerchandise(new Slave(174, 44, 16, "female", 1, "Diana", 200));
        db.addMerchandise(new Slave(185, 85, 20, "male", 2, "Luise", 300));

        slaves.add(new Slave(140, 35, 12, "male", 0, "Pete", 100));
        slaves.add(new Slave(174, 44, 16, "female", 1, "Diana", 200));
        slaves.add(new Slave(185, 85, 20, "male", 2, "Luise", 300));
    }

    @Test
    public void searchMerchandiseTest() {
        Assert.assertEquals(slaves.get(2), db.searchMerchandise("gender=male and name=Luise").get(0));
    }

    @Test
    public void searchMerchandiseEuqalsAndGreaterTest() {
        slaves.remove(1);
        slaves.remove(1);
        Assert.assertEquals(slaves, db.searchMerchandise("id<=0"));
    }

    @Test
    public void removeMerchandiseTest() {
        db.removeMerchandise(new Slave(174, 44, 16, "female", 1, "Diana", 200));
        slaves.remove(1);
        ArrayList<Slave> list = new ArrayList<>();
        list.add(new Slave(140, 35, 12, "male", 0, "Pete", 100));
        list.add(new Slave(185, 85, 20, "male", 1, "Luise", 300));
        Assert.assertEquals(list, db.searchMerchandise(""));
    }

    @Test
    public void getMerchantByIdTest() {
        Assert.assertEquals(
                new Slave(185, 85, 20, "male", 2, "Luise", 300),
                db.getMerchantById(2));
    }

    @Test
    public void removeMerchandiseByIdTest() {
        db.removeMerchandise(1);
        db.removeMerchandise(0);
        ArrayList<Slave> list = new ArrayList<>();
        list.add(new Slave(185, 85, 20, "male", 1, "Luise", 300));
        Assert.assertEquals(list, db.searchMerchandise(""));
    }

    @Test
    public void slaveEqualsTest() {
        Assert.assertEquals(true, slaves.get(0).equals(new Slave(140, 35, 12, "male", 1, "Pete", 100)));
    }

    @Test
    public void addSlaveTest() {
        Slave slave = new Slave(1, 2, 3, "male", "David", 320);
        db.addMerchandise(slave);
        slaves.add(new Slave(1, 2, 3, "male", 3, "David", 320));
        Assert.assertEquals(slaves, db.searchMerchandise(""));
    }

    @Test
    public void observerTests() throws AlreadyBoughtException {
        System.out.println("\u001B[32mObserver mechanism\u001B[0m testBlock started{");
        Model model = new SlaveMarketModel();
        Observer observer = new Observer() {

            int assertId = 1;

            {
                ((Observable) model).addObserver(this);
            }

            @Override
            public void update() {
                System.out.println("some data was updated");
            }

            @Override
            public void deleted(int id) {
                System.out.println("item with id=" + id + " was deleted");
                Assert.assertEquals(assertId, id);
            }

            @Override
            public void changed(int id) {
                System.out.println("item with id=" + id + " was changed");
                Assert.assertEquals(assertId, id);
            }
        };
        slaves.forEach(model::addMerchandise);
        model.removeMerchandise(1);
        model.buyMerchandise(1);
        System.out.println("\u001B[32m}\u001B[0m");
    }

    @Test
    public void queryTest() throws AlreadyBoughtException {
        System.out.println("\u001B[32mQuery test\u001B[0m testBlock started{");
        Model model = new SlaveMarketModel();
        slaves.forEach(model::addMerchandise);
        model.buyMerchandise(1);
        System.out.println(db.searchMerchandise("id>=0 and id!=1"));
        System.out.println("\u001B[32m}\u001B[0m");
    }
}
