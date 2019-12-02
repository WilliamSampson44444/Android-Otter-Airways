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

import java.util.List;

import edu.csumb.project7.flightdb.FlightRoom;
import edu.csumb.project7.model.Flight;
import edu.csumb.project7.model.MyLog;
import edu.csumb.project7.model.User;

public class Manage extends AppCompatActivity {
    private Boolean secondTry;
    private int tickets;
    private Flight newFlight;
    private TextView status;
    private List<MyLog> log_list;
    private Manage.LogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("InstructorView", "onCreate called");
        setContentView(R.layout.reserve);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        status = findViewById(R.id.textView2);

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        logAdapter = new Manage.LogAdapter();
        rv.setAdapter(logAdapter);

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
                newFlightDialog(null);
            }
        });

    }

    private class LogAdapter extends RecyclerView.Adapter<Manage.ItemHolder> {

        public LogAdapter() {
            log_list = FlightRoom.getFlightRoom(Manage.this).dao().getAllLogs();
            if(log_list.isEmpty()){
                status.setText("No logs in the system.");
            }
        }

        @Override
        public Manage.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(Manage.this);
            return new Manage.ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(Manage.ItemHolder holder, int position) {
            MyLog log = log_list.get(position);
            holder.bind( "Type: " + log.getType()+"\nUsername: "+log.getUsername()+"\nDate: "+log.getDate()
                    +"\nFlight No: "+log.getFlightID()+"\n Reservation: "+log.getReservation()+"\nInfo: "+log.getMisc() +"\n");
        }

        @Override
        public int getItemCount() {
            return log_list==null ? 0 : log_list.size();
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

    private void newFlightDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(message != null){
            builder.setTitle(message);
            secondTry = true;
        }else{
            builder.setTitle("Create New Flight");
            secondTry = false;
        }
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_new_flight, null);
        builder.setView(v);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                EditText tFlightNo = v.findViewById(R.id.flightNo);
                String flightNo = tFlightNo.getText().toString();
                if(flightNo.isEmpty()){
                    if(!secondTry) {
                        newFlightDialog("Flight Number required");
                        return;
                    }else{
                        errorDialog("Invalid flight information.");
                        finish();
                    }
                }
                EditText tDepart = v.findViewById(R.id.departure);
                String depart = tDepart.getText().toString();
                if(depart.isEmpty()){
                    if(!secondTry) {
                        newFlightDialog("Origin required");
                        return;
                    }else{
                        errorDialog("Invalid flight information.");
                        finish();
                    }
                }
                EditText tArrive = v.findViewById(R.id.arrival);
                String arrive = tArrive.getText().toString();
                if(arrive.isEmpty()){
                    if(!secondTry) {
                        newFlightDialog("Destination required");
                        return;
                    }else{
                        errorDialog("Invalid flight information.");
                        finish();
                    }
                }
                EditText tDepartTime = v.findViewById(R.id.departTime);
                String time = tDepartTime.getText().toString();
                if(time.isEmpty()){
                    if(!secondTry) {
                        newFlightDialog("Departure time required");
                        return;
                    }else{
                        errorDialog("Invalid flight information.");
                        finish();
                    }
                }
                EditText tCapacity = v.findViewById(R.id.capacity);
                int capacity = Integer.parseInt("0" + tCapacity.getText().toString());
                if(capacity == 0){
                    if(!secondTry) {
                        newFlightDialog("0 capacity flight not allowed");
                        return;
                    }else{
                        errorDialog("Invalid flight information.");
                        finish();
                    }
                }
                EditText tPrice = v.findViewById(R.id.price);
                double price = Double.parseDouble("0" + tPrice.getText().toString());
                if(price == 0){
                    if(!secondTry) {
                        newFlightDialog("0 price flight not allowed");
                        return;
                    }else{
                        errorDialog("Invalid flight information.");
                        finish();
                    }
                }

                if(FlightRoom.getFlightRoom(Manage.this).dao().getFlight(flightNo) != null){
                    errorDialog("FlightNo already in use.");
                    return;
                }

                newFlight = new Flight(flightNo, depart, arrive, time, capacity, price);

                confirmDialog(null);
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

    private void confirmDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Create this flight?");

        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(v);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                FlightRoom.getFlightRoom(Manage.this).dao().addFlight(newFlight);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                finish();
            }
        });
        AlertDialog dialog = builder.create();

        TextView info = v.findViewById(R.id.info);

        info.setText("Flight No: " + newFlight.getFlightNo() + "\nDeparture: " + newFlight.getDeparture()
                + "\nArrival: " + newFlight.getArrival() + "\nDeparture time: " + newFlight.getDepartureTime()
                + "\nCapacity: " + newFlight.getCapacity() + "\nPrice: " + newFlight.getPrice());

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