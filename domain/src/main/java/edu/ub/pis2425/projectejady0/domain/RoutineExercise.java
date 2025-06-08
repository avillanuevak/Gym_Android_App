package edu.ub.pis2425.projectejady0.domain;

public class RoutineExercise {

    private RoutineExerciseId id;
    private Exercise exercise;
    private int sets;
    private int repetitions;
    private double weight;
    public RoutineExercise() {}

    public RoutineExercise(RoutineExerciseId id, Exercise exercise, int sets, int repetitions, double weight) {
        this.id = id;
        this.exercise = exercise;
        this.sets = sets;
        this.repetitions = repetitions;
        this.weight = weight;
    }


    public RoutineExerciseId getId() {
        return id;
    }


    public Exercise getExercise() {
        return exercise;
    }


    public int getSets() {
        return sets;
    }


    public int getRepetitions() {
        return repetitions;
    }


    public double getWeight() {
        return weight;
    }

    public void setSets(int sets){
            this.sets = sets;
    }

    public void setRepetitions( int repetitions){
            this.repetitions = repetitions;
    }

    public void setWeight(double weight){
            this.weight = weight;
    }
}
