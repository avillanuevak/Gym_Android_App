package edu.ub.pis2425.projectejady0.domain;

import java.util.ArrayList;
import java.util.List;

public class GymClass implements CalendarItem {
    private GymClassId id;
    private String name;
    private String instructor;
    private String location;
    private int currentCapacity;
    private int maxCapacity;
    private List<String> schedule;
    private List<String> registeredUsers;

    public GymClass() {
        this.schedule = new ArrayList<>();
        this.registeredUsers = new ArrayList<>();
    }

    public GymClass(GymClassId id, String name, String instructor, String location, int currentCapacity, int maxCapacity, List<String> schedule, List<String> registeredUsers) {
        this.id = id;
        this.name = name;
        this.instructor = instructor;
        this.location = location;
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.schedule = schedule != null ? schedule : new ArrayList<>();
        this.registeredUsers = registeredUsers != null ? registeredUsers : new ArrayList<>();
    }


    public GymClassId getId() {
        return id;
    }

    public void setId(GymClassId id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;

    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public List<String> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<String> schedule) {
        this.schedule = schedule;
    }

    public List<String> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<String> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }
}