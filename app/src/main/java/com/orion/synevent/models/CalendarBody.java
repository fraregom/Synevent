package com.orion.synevent.models;

import java.util.List;

public class CalendarBody {

    private List<DayBody> monday = null;
    private List<DayBody> tuesday = null;
    private List<DayBody> wednesday = null;
    private List<DayBody> thursday = null;
    private List<DayBody>  friday = null;
    private List<DayBody>  saturday = null;
    private List<DayBody>  sunday = null;

    public List<DayBody>  getMonday() {
        return monday;
    }

    public void setMonday(List<DayBody>  monday) {
        this.monday = monday;
    }

    public List<DayBody> getTuesday() {
        return tuesday;
    }

    public void setTuesday(List<DayBody>  tuesday) {
        this.tuesday = tuesday;
    }

    public List<DayBody>  getWednesday() {
        return wednesday;
    }

    public void setWednesday(List<DayBody>  wednesday) {
        this.wednesday = wednesday;
    }

    public List<DayBody>  getThursday() {
        return thursday;
    }

    public void setThursday(List<DayBody>  thursday) {
        this.thursday = thursday;
    }

    public List<DayBody>  getFriday() {
        return friday;
    }

    public void setFriday(List<DayBody>  friday) {
        this.friday = friday;
    }

    public List<DayBody>  getSaturday() {
        return saturday;
    }

    public void setSaturday(List<DayBody>  saturday) {
        this.saturday = saturday;
    }

    public List<DayBody>  getSunday() {
        return sunday;
    }

    public void setSunday(List<DayBody>  sunday) {
        this.sunday = sunday;
    }

}
