package edu.csumb.project7;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import edu.csumb.project7.flightdb.FlightRoom;
import edu.csumb.project7.model.Flight;
import edu.csumb.project7.model.MyLog;
import edu.csumb.project7.model.User;

public class Reserve extends AppCompatActivity{
    private Boolean secondTry;
    private int tickets;
    private Flight chosen;
    private TextView status;
    private List<Flight> flight_list;
    private FlightAdapter flightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("InstructorView", "onCreate called");
        setContentView(R.layout.reserve);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        status = findViewById(R.id.textView2);

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        flightAdapter = new FlightAdapter();
        rv.setAdapter(flightAdapter);

        Button cancel = findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button choose = findViewById(R.id.button);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFlightDialog(null);
            }
        });

    }

    private class FlightAdapter extends RecyclerView.Adapter<Reserve.ItemHolder> {

        public FlightAdapter() {
            searchFlightDialog(null);
        }

        @Override
        public Reserve.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(Reserve.this);
            return new Reserve.ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(Reserve.ItemHolder holder, int position) {
            Flight flight = flight_list.get(position);
            holder.bind( flight.getFlightNo()+" "+ flight.getDeparture()+" "+flight.getDepartureTime()+" "+flight.getDepartureTime()+" "+flight.getPrice());
        }

        @Override
        public int getItemCount() {
            return flight_list==null ? 0 : flight_list.size();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item, parent, false));
        }

        public void bind(String str){
            TextView v = itemView.findViewById(R.id.item_id);
            v.setText( str) ;
        }
    }

    private void searchFlightDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(message != null){
            builder.setTitle(message);
            secondTry = true;
        }else{
            builder.setTitle("Search Flights");
            secondTry = false;
        }
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_search_flights, null);
        builder.setView(v);
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                EditText tDepart = v.findViewById(R.id.departure);
                String depart = tDepart.getText().toString();
                EditText tArrive = v.findViewById(R.id.arrival);
                String arrive = tArrive.getText().toString();
                EditText tTickets = v.findViewById(R.id.tickets);
                tickets = Integer.parseInt(tTickets.getText().toString());

                if(tickets > 7){
                    searchFlightDialog("Maximum tickets is 7");
                    return;
                }

                //Finally searching flights
                flight_list = FlightRoom.getFlightRoom(Reserve.this).dao().searchFlights(depart, arrive, tickets);
                if(flight_list.size() == 0){
                    errorDialog("No compatible flights available.");
                    return;
                }
                status.setText("Compatible flights listed.");
                flightAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void chooseFlightDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(message != null){
            builder.setTitle(message);
            secondTry = true;
        }else{
            builder.setTitle("Choose Flight");
            secondTry = false;
        }
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_choose, null);
        builder.setView(v);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                EditText tChosen = v.findViewById(R.id.id);
                chosen = FlightRoom.getFlightRoom(Reserve.this).dao().getFlight(tChosen.getText().toString());

                if(chosen == null){
                    if(secondTry){
                        errorDialog("Flight does not exist");
                        return;
                    }else{
                        chooseFlightDialog("Flight does not exist");
                        return;
                    }
                }

                loginDialog(null);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmDialog(final String name){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Correct Information?");

        final int reservation = (int)(Math.random() * Math.pow(2, 30));

        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(v);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                FlightRoom.getFlightRoom(Reserve.this).dao().getFlight(chosen.getFlightNo())
                        .setAvailableSeats(FlightRoom.getFlightRoom(Reserve.this).dao().getFlight(
                                chosen.getFlightNo()).getAvailableSeats() - tickets);
                FlightRoom.getFlightRoom(Reserve.this).dao().logTransaction(
                        new MyLog("Reserve Seat", name, new Date().toString(), chosen.getFlightNo(), reservation,
                                chosen.getFlightNo() + "\n" + chosen.getDeparture() + " to " +
                                chosen.getArrival() + "\n " + tickets +" tickets for $" + tickets * chosen.getPrice(), tickets));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();

        TextView info = v.findViewById(R.id.info);

        info.setText("Reservation " + reservation + "\nFlight number: " + chosen.getFlightNo() +
                "\nDeparture: " + chosen.getDeparture() + " " + chosen.getDepartureTime() +
                "\nArrival: " + chosen.getArrival() + "\n" + tickets + " tickets\nTotal: $" + tickets * chosen.getPrice());

        dialog.show();
    }

    private void loginDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(message != null){
            builder.setTitle(message);
            secondTry = true;
        }else{
            builder.setTitle("Login");
            secondTry = false;
        }
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_account, null);
        builder.setView(v);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                EditText tName = v.findViewById(R.id.name);
                String name = tName.getText().toString();
                EditText tPass = v.findViewById(R.id.pass);
                String pass = tPass.getText().toString();

                List<User> user_list = FlightRoom.getFlightRoom(Reserve.this).dao().getAllUsers();
                for(int i = 0; i < user_list.size(); i++){
                    if(name.equals(user_list.get(i).getUsername()) && pass.equals(user_list.get(i).getPassword())){
                        confirmDialog(name);
                        return;
                    }
                }
                if(secondTry){
                    errorDialog("Invalid login");
                    finish();
                }else{
                    loginDialog("Invalid login");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void errorDialog(String message){
        secondTry = false;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(message);
        /*LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(v);*/
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}