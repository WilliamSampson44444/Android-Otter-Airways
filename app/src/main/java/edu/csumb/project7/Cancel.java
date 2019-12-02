package edu.csumb.project7;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class Cancel extends AppCompatActivity{
    private Boolean secondTry;
    private MyLog reservation;
    private String name;
    private Flight chosen;
    private TextView status;
    private List<MyLog> reservation_list;
    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("InstructorView", "onCreate called");
        setContentView(R.layout.cancel);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        status = findViewById(R.id.textView2);

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new ReservationAdapter();
        rv.setAdapter(reservationAdapter);

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
                chooseDialog(null);
            }
        });

    }

    private class ReservationAdapter extends RecyclerView.Adapter<Cancel.ItemHolder> {

        public ReservationAdapter() { loginDialog(null); }

        @Override
        public Cancel.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(Cancel.this);
            return new Cancel.ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(Cancel.ItemHolder holder, int position) {
            MyLog log = reservation_list.get(position);
            holder.bind( "Reservation #" + log.getReservation()+"\n"+ log.getMisc() + "\n");
        }

        @Override
        public int getItemCount() {
            return reservation_list==null ? 0 : reservation_list.size();
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

    private void chooseDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(message != null){
            builder.setTitle(message);
            secondTry = true;
        }else{
            builder.setTitle("Select Reservation");
            secondTry = false;
        }

        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_choose1, null);
        builder.setView(v);
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                EditText rID = v.findViewById(R.id.id);
                int ID = Integer.parseInt(rID.getText().toString());
                reservation = FlightRoom.getFlightRoom(Cancel.this).dao().getReservation(ID);
                chosen = FlightRoom.getFlightRoom(Cancel.this).dao().getFlight(reservation.getFlightID());
                if(reservation == null){
                    chooseDialog("Invalid Reservation ID");
                    return;
                }else{
                    confirmDialog(null);
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

    private void confirmDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Cancel this reservation?");

        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(v);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                chosen.setAvailableSeats(chosen.getAvailableSeats() + reservation.getTickets());
                FlightRoom.getFlightRoom(Cancel.this).dao().logTransaction(new MyLog("Cancellation",
                        name, new Date().toString(), reservation.getFlightID(), reservation.getReservation(),
                        reservation.getMisc(), reservation.getTickets()));
                FlightRoom.getFlightRoom(Cancel.this).dao().deleteReservation(reservation);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                errorDialog("Failed to cancel reservation.");
            }
        });
        AlertDialog dialog = builder.create();

        TextView info = v.findViewById(R.id.info);

        info.setText("Reservation #" + reservation.getReservation() + "\n" +
                reservation.getMisc() + "\n");

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
                String username = tName.getText().toString();
                EditText tPass = v.findViewById(R.id.pass);
                String pass = tPass.getText().toString();

                List<User> user_list = FlightRoom.getFlightRoom(Cancel.this).dao().getAllUsers();
                for(int i = 0; i < user_list.size(); i++){
                    if(username.equals(user_list.get(i).getUsername()) && pass.equals(user_list.get(i).getPassword())){
                        name = username;
                        reservation_list = FlightRoom.getFlightRoom(Cancel.this).dao().searchReservations(name);
                        reservationAdapter.notifyDataSetChanged();
                        if(reservation_list.size() == 0){
                            errorDialog("User has no reservations.");
                        }
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