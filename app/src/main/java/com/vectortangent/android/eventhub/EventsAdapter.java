package com.vectortangent.android.eventhub;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private ArrayList<Event> events;

    public EventsAdapter(ArrayList<Event> events){
        this.events = events;
    }

    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_layout,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(events.get(i));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView eventImage;
        private TextView monthView;
        private TextView dateView;
        private TextView dayAndTimeView;
        private TextView eventNameView;
        private TextView eventPlaceView;
        private TextView eventCostView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            monthView = itemView.findViewById(R.id.month);
            dayAndTimeView = itemView.findViewById(R.id.dayAndTime);
            dateView = itemView.findViewById(R.id.date);
            eventNameView = itemView.findViewById(R.id.eventName);
            eventPlaceView = itemView.findViewById(R.id.locationName);
            eventCostView = itemView.findViewById(R.id.eventCost);
        }

        public void bind(Event event){
            Picasso.get().load("https://via.placeholder.com/150").into(eventImage);
            eventNameView.setText(event.getEventName());
            eventPlaceView.setText(event.getPlace());
            eventCostView.setText(event.getTicketCost()+"");
            String[] dateStringArray = event.getEventDate().split(" ");
            monthView.setText(dateStringArray[1].substring(0, 3));
            dateView.setText(event.getEventDate().substring(0, 2));
            dayAndTimeView.setText(event.getEventDay().substring(0, 3) + " " + event.getEventTime());
        }
    }

    public void addEvent(Event e){
        events.add(e);

    }
}
