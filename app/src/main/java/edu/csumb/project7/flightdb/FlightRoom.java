package edu.csumb.project7.flightdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import edu.csumb.project7.model.Flight;
import edu.csumb.project7.model.MyLog;
import edu.csumb.project7.model.User;

@Database(entities={User.class, Flight.class, MyLog.class}, version=1)
public abstract class FlightRoom extends RoomDatabase {
    // singleton
    private static FlightRoom instance;

    public abstract FlightDao dao();

    public static FlightRoom getFlightRoom(final Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FlightRoom.class,
                    "FlightDB") // database name
                    .allowMainThreadQueries()  // temporary for now
                    .build();
        }
        return instance;
    }


    public void loadUsers(Context context) {
        FlightDao dao = getFlightRoom(context).dao();

        User alice = new User("A@lice5", "@cSit100");
        User brian = new User("$BriAn7","123aBc##");
        User chris = new User("!chriS12!", "CHrIS12!!");
        dao.addUser(alice);
        dao.addUser(brian);
        dao.addUser(chris);
    }

    public void loadFlights(Context context){
        FlightDao dao = getFlightRoom(context).dao();

        Flight otter101 = new Flight("Otter101", "Monterey", "Los Angeles", "10:30(am)", 10, 150);
        Flight otter102 = new Flight("Otter102", "Los Angeles", "Monterey", "1:00(pm)", 10, 150);
        Flight otter201 = new Flight("Otter201", "Monterey", "Seattle", "11:00(am)", 5, 200.50);
        Flight otter205 = new Flight("Otter205", "Monterey", "Seattle", "3:45(pm)", 15, 150);
        Flight otter202 = new Flight("Otter202", "Seattle", "Monterey", "2:10(pm)", 5, 200.50);
        dao.addFlight(otter101);
        dao.addFlight(otter102);
        dao.addFlight(otter201);
        dao.addFlight(otter205);
        dao.addFlight(otter202);

    }
}
