package edu.csumb.project7;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {
    private boolean secondTry = false;
    private TextView status;
    private List<User> user_list;
    private List<Flight> flight_list;
    private UserAdapter user_adapter;
    private FlightAdapter flight_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        status = findViewById(R.id.textView2);

        // load database if database is empty
       user_list = FlightRoom.getFlightRoom(MainActivity.this).dao().getAllUsers();
        if (user_list.size() == 0){
            FlightRoom.getFlightRoom(this).loadUsers(this);
        }

        flight_list = FlightRoom.getFlightRoom(MainActivity.this).dao().getAllFlights();
        if (flight_list.size() == 0){
            FlightRoom.getFlightRoom(this).loadFlights(this);
        }

        RecyclerView rv = findViewById(R.id.users);
        rv.setLayoutManager(new LinearLayoutManager(this));

        user_adapter = new UserAdapter();
        rv.setAdapter(user_adapter);

        RecyclerView rv2 = findViewById(R.id.flights);
        rv2.setLayoutManager(new LinearLayoutManager(this));
        flight_adapter = new FlightAdapter();
        rv2.setAdapter(flight_adapter);

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountDialog(null);

            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Reserve.class);
                startActivity(intent);
            }
        });
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Cancel.class);
                startActivity(intent);
            }
        });Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminLoginDialog(null);
            }
        });
        /*Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete all users
                FlightRoom.getFlightRoom(MainActivity.this).dao().deleteAllUsers();
                user_list.clear();
                user_adapter.notifyDataSetChanged();
            }
        });*/

    }

    private class UserAdapter extends RecyclerView.Adapter<ItemHolder> {

        public UserAdapter(){
            user_list = FlightRoom.getFlightRoom(MainActivity.this).dao().getAllUsers();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            User user = user_list.get(position);
            //holder.bind( "User:"+ user.getUsername()+", password:"+user.getPassword());
        }

        @Override
        public int getItemCount() {
            return user_list.size();
        }
    }

    private class FlightAdapter extends RecyclerView.Adapter<ItemHolder> {

       public FlightAdapter() {
            flight_list = FlightRoom.getFlightRoom(MainActivity.this).dao().getAllFlights();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Flight flight = flight_list.get(position);
            //holder.bind( flight.getFlightNo()+" "+ flight.getDeparture()+" "+flight.getDepartureTime()+" "+flight.getDepartureTime()+" "+flight.getPrice());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAccountDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(message != null){
            builder.setTitle(message);
            secondTry = true;
        }else{
            builder.setTitle("Create Account");
            secondTry = false;
        }
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_account, null);
        builder.setView(v);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                EditText tName = v.findViewById(R.id.name);
                String name = tName.getText().toString();
                EditText tPass = v.findViewById(R.id.pass);
                String pass = tPass.getText().toString();

                //Checking for valid username
                boolean failedUsername = false;
                boolean failedPassword = false;
                boolean symbol = false;
                boolean upper = false;
                boolean lower = false;
                boolean digit = false;
                for(int i = 0; i < name.length(); i++){
                    char c = name.charAt(i);
                    if(c == '!' || c == '@' || c == '#' || c == '$')  symbol = true;
                    else if(Character.isUpperCase(c)) upper = true;
                    else if(Character.isLowerCase(c)) lower = true;
                    else if(Character.isDigit(c)) digit = true;
                }
                if(secondTry && !(symbol && upper && lower && digit)){
                    errorDialog("Bad username.");
                    return;
                } else if(!symbol){ createAccountDialog("Username needs special char");
                    failedUsername = true;
                } else if(!upper){ createAccountDialog("Username needs uppercase");
                    failedUsername = true;
                } else if(!lower){ createAccountDialog("Username needs lowercase");
                    failedUsername = true;
                } else if(!digit){ createAccountDialog("Username needs digit");
                    failedUsername = true;
                }

                //Checking for valid password
                symbol = false;
                upper = false;
                lower = false;
                digit = false;
                for(int i = 0; i < pass.length(); i++){
                    char c = pass.charAt(i);
                    if(c == '!' || c == '@' || c == '#' || c == '$') symbol = true;
                    else if(Character.isUpperCase(c)) upper = true;
                    else if(Character.isLowerCase(c)) lower = true;
                    else if(Character.isDigit(c)) digit = true;
                }
                if(secondTry && !(symbol && upper && lower && digit) && !failedUsername){
                    errorDialog("Bad password.");
                    return;
                } else if(!symbol && !failedUsername){
                    createAccountDialog("Password needs special char");
                    failedPassword = true;
                } else if(!upper && !failedUsername){
                    createAccountDialog("Password needs uppercase");
                    failedPassword = true;
                } else if(!lower && !failedUsername){
                    createAccountDialog("Password needs lowercase");
                    failedPassword = true;
                } else if(!digit && !failedUsername){
                    createAccountDialog("Password needs digit");
                    failedPassword = true;
                }
                //Checking special cases
                if(name.equals("!admiM2")){
                    if(secondTry){
                        errorDialog("Cannot take reserved username");
                        return;
                    }else{
                        createAccountDialog("Cannot take reserved username");
                    }
                    failedUsername = true;
                }
                List<User> users = FlightRoom.getFlightRoom(MainActivity.this).dao().getAllUsers();
                for(int i = 0; i < users.size(); i++){
                    if(name.equals(users.get(i).getUsername())) {
                        if(secondTry){
                             errorDialog("Username already taken");
                             return;
                        }else {
                            createAccountDialog("Username already taken");
                        }
                        failedUsername = true;
                        break;
                    }
                }

                //Finally creating user
                if(!failedPassword && !failedUsername) {
                    FlightRoom.getFlightRoom(MainActivity.this).dao().addUser(new User(name, pass));
                    status.setText("New account created.");
                    user_list = FlightRoom.getFlightRoom(MainActivity.this).dao().getAllUsers();
                    //user_adapter.notifyDataSetChanged();
                    FlightRoom.getFlightRoom(MainActivity.this).dao().logTransaction(
                            new MyLog("new account", name, new Date().toString(),
                                    null, null, null, null));
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

    private void adminLoginDialog(String message){
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

                List<User> user_list = FlightRoom.getFlightRoom(MainActivity.this).dao().getAllUsers();
                for(int i = 0; i < user_list.size(); i++){
                    if(name.equals("!admiM2") && pass.equals("!admiM2")){
                        Intent intent = new Intent(MainActivity.this, Manage.class);
                        startActivity(intent);
                        return;
                    }
                }
                if(secondTry){
                    errorDialog("Invalid login");
                    return;
                }else{
                    adminLoginDialog("Invalid login");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                finish();
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

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
