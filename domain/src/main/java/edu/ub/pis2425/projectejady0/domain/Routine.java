package edu.ub.pis2425.projectejady0.domain;

import java.util.List;

public class Routine implements CalendarItem {
    private RoutineId id;
    private String name;
    private List<RoutineExercise> exercises;
    private List<String> days;
    private ClientId clientId;

    public Routine() {}

    public Routine(RoutineId id, String name, List<RoutineExercise> exercises, List<String> days, ClientId clientId) {
        this.id = id;
        this.name = name;
        this.exercises = exercises;
        this.days = days;
        this.clientId = clientId;
    }

    public RoutineId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RoutineExercise> getExercises() {
        return exercises;
    }

    @Override
    public List<String> getSchedule() {
        return days;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setId(RoutineId id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExercises(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }

    public void setSchedule(List<String> days) {
        this.days = days;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }


}
