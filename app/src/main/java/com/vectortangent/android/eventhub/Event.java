package com.vectortangent.android.eventhub;

public class Event {
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventDay;
    private String place;
    private float ticketCost;
    private long eventCapacity;

    public Event(){

    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String startDate) {
        this.eventDate = startDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public float getTicketCost() {
        return ticketCost;
    }

    public void setTicketCost(float ticketCost) {
        this.ticketCost = ticketCost;
    }

    public long getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(long eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public String getEventDay() {
        return eventDay;
    }

    public void setEventDay(String eventDay) {
        this.eventDay = eventDay;
    }
}
