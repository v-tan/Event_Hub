package com.vectortangent.android.eventhub;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.icu.util.TimeZone;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "AddEventActivity";
    private EditText eventNameEditText;
    private Button timeButton;
    private Button dateButton;
    private EditText eventPlaceEditText;
    private EditText eventCostEditText;
    private EditText eventCapacityEditText;
    private Spinner currencySpinner;
    private String costOfATicket = "";
    private Button createEventButton;
    private DatabaseReference eventsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setToolbar();

        eventsDatabaseReference = FirebaseDatabase.getInstance().getReference("events");

        eventNameEditText = findViewById(R.id.input_event_name);
        timeButton = findViewById(R.id.input_time);
        dateButton = findViewById(R.id.input_date);
        eventPlaceEditText = findViewById(R.id.input_place);
        eventCostEditText = findViewById(R.id.input_cost);
        eventCapacityEditText = findViewById(R.id.input_capacity);
        currencySpinner = findViewById(R.id.currencySelector);
        createEventButton = findViewById(R.id.createEventButton);

        timeButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
        createEventButton.setOnClickListener(this);

        setSpinner();
//        eventCostEditText.setOn
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.currency, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(spinnerAdapter);
        currencySpinner.setSelected(true);
        currencySpinner.setSelection(0);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String currencyCode = (String) parent.getItemAtPosition(position);
//                Currency currency = Currency.getInstance(currencyCode);
//                costOfATicket = currency.getSymbol();
                parent.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        final Calendar cal = Calendar.getInstance();
        if (v == timeButton){
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, this,
                    hour, minute, false);
            timePickerDialog.show();
        } else if (v == dateButton){
            int year = cal.get(Calendar.YEAR);
            int month  = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                    year, month, day);
            datePickerDialog.show();
        } else if (v == createEventButton){
            if (validate()){
                createEvent();
            }
        }
    }

    private void createEvent() {
        createEventButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(AddEventActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Event...");
        progressDialog.show();

        Event newEvent = new Event();
        newEvent.setEventName(eventNameEditText.getText().toString());
        String[] dayAndDate = dateButton.getText().toString().split(", ", 2);
        if (dayAndDate.length != 0){
            newEvent.setEventDay(dayAndDate[0]);
            newEvent.setEventDate(dayAndDate[1]);
        } else {
            Toast.makeText(this, "creation of event failed...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        newEvent.setEventTime(timeButton.getText().toString());
        newEvent.setPlace(eventPlaceEditText.getText().toString());
        newEvent.setTicketCost(Float.parseFloat(eventCostEditText.getText().toString()));
        newEvent.setEventCapacity(Long.parseLong(eventCapacityEditText.getText().toString()));

        String id = eventsDatabaseReference.push().getKey();
        eventsDatabaseReference.child(id).setValue(newEvent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddEventActivity.this,
                                "Event successfully created", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEventActivity.this,
                                "online creation of event failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void failEventCreation() {
//        Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
//    }

    private boolean validate() {
        boolean valid = true;

        String eventName = eventNameEditText.getText().toString();
        String eventPlace = eventPlaceEditText.getText().toString();
        String eventTime = timeButton.getText().toString();
        String eventDate = dateButton.getText().toString();

        if (eventName.isEmpty()) {
            eventNameEditText.setError("Enter an Event Name");
            valid = false;
        } else {
            eventNameEditText.setError(null);
        }

        if (eventPlace.isEmpty()) {
            eventPlaceEditText.setError("Enter a Venue");
            valid = false;
        } else {
            eventPlaceEditText.setError(null);
        }

        if (eventTime.isEmpty() || eventTime.equalsIgnoreCase("time")){
            Toast.makeText(this, "Please enter a suitable time", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (eventDate.isEmpty() || eventDate.equalsIgnoreCase("date")){
            Toast.makeText(this, "Please enter a suitable date", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        String capacityString = eventCapacityEditText.getText().toString();
        if (capacityString.isEmpty()){
            eventCapacityEditText.setError("Please enter a number represnting capacity");
            valid = false;
        } else {
            Long eventCapacity = Long.parseLong(capacityString);
            if (eventCapacity == 0){
                eventCapacityEditText.setError("Capacity can't be 0");
                valid = false;
            } else if (eventCapacity > 75000){
                eventCapacityEditText.setError("Capacity must be less than 75000");
                valid = false;
            } else {
                eventCapacityEditText.setError(null);
            }
        }

        String costString = eventCostEditText.getText().toString();
        if (costString.isEmpty()){
            eventCostEditText.setError("Is the event FREE? If YES, enter 0.0");
            valid = false;
        } else {
            Float eventTicketCost = Float.parseFloat(costString);
            if (eventTicketCost > 50000.0){
                eventCostEditText.setError("ticket cost must be less than 50000 units");
                valid = false;
            } else {
                eventCostEditText.setError(null);
            }
        }


        return valid;
    }

    private void setToolbar() {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd mm yyyy", Locale.getDefault());
        String dateToFormat = dayOfMonth + " " + (month) + " " + year;
        Log.d(TAG, dateToFormat);
        try {
            dateButton.setText("" + new SimpleDateFormat("EEEE, dd MMM, yyyy")
                    .format(dateFormat.parse(dateToFormat)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String timeToFormat = hourOfDay + ":" + minute;
        try {
            timeButton.setText("" + new SimpleDateFormat("hh:mm aa")
                    .format(timeFormat.parse(timeToFormat)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
