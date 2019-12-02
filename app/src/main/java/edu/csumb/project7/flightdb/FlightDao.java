package edu.csumb.project7.flightdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

import edu.csumb.project7.model.Flight;
import edu.csumb.project7.model.MyLog;
import edu.csumb.project7.model.User;

@Dao
public interface FlightDao {
    @Query("select * from Flight")
    List<Flight> getAllFlights();

    @Query("select * from Flight where departure=:depart and arrival=:arrive and availableSeats>=:tickets")
    List<Flight> searchFlights(String depart, String arrive, int tickets);

    @Query("select * from MyLog")
    List<MyLog> getAllLogs();

    @Query("select * from MyLog where username=:name and type='Reserve Seat'")
    List<MyLog> searchReservations(String name);

    @Query("select * from MyLog where reservation=:reservation limit 1")
    MyLog getReservation(int reservation);

    @Delete
    void deleteReservation(MyLog reservation);

    @Query("select * from Flight where flightNo=:flight_id")
    Flight getFlight(String flight_id);

    @Query("select * from User")
    List<User> getAllUsers();

    @Query("select * from User where username=:username and password=:password")
    User getUser(String username, String password);

    @Insert
    void addUser(User user);

    @Insert
    void addFlight(Flight flight);

    @Insert
    void logTransaction(MyLog log);

    @Query("delete from User")
    void deleteAllUsers();
}
