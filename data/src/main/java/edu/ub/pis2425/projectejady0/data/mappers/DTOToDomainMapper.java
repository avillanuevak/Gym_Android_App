package edu.ub.pis2425.projectejady0.data.mappers;

import edu.ub.pis2425.projectejady0.data.dtos.firestore.ClientFirestoreDto;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.ExerciseFirestoreDto;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.GymClassFirestoreDto;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.NutritionPlanFirestoreDto;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.RoutineExerciseFirestoreDto;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.RoutineFirestoreDto;
import edu.ub.pis2425.projectejady0.domain.Client;
import edu.ub.pis2425.projectejady0.domain.ClientId;
import edu.ub.pis2425.projectejady0.domain.Exercise;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;
import edu.ub.pis2425.projectejady0.domain.NutritionPlan;
import edu.ub.pis2425.projectejady0.domain.NutritionPlanId;
import edu.ub.pis2425.projectejady0.domain.Routine;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import edu.ub.pis2425.projectejady0.domain.RoutineExerciseId;
import edu.ub.pis2425.projectejady0.domain.RoutineId;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.List;

public class DTOToDomainMapper extends ModelMapper {
    public DTOToDomainMapper() {
        super();
        super.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        // Client converters
        super.addConverter(new AbstractConverter<ClientFirestoreDto, Client>() {
            @Override
            protected Client convert(ClientFirestoreDto source) {
                return new Client(new ClientId(source.getId()),
                        source.getUsername(),
                        source.getEmail(),
                        source.getPassword(),
                        source.getPhotoUrl());
            }
        });

        super.addConverter(new AbstractConverter<Client, ClientFirestoreDto>() {
            @Override
            protected ClientFirestoreDto convert(Client source) {
                ClientFirestoreDto dto = new ClientFirestoreDto();
                dto.setId(source.getId().getId());
                dto.setUsername(source.getUsername());
                dto.setEmail(source.getEmail());
                dto.setPassword(source.getPassword());
                dto.setPhotoUrl(source.getPhotoUrl());
                return dto;
            }
        });

        // GymClass converters
        super.addConverter(new AbstractConverter<GymClassFirestoreDto, GymClass>() {
            @Override
            protected GymClass convert(GymClassFirestoreDto source) {
                GymClass gymClass = new GymClass();
                gymClass.setName(source.getName());
                gymClass.setInstructor(source.getInstructor());
                gymClass.setLocation(source.getLocation());
                gymClass.setCurrentCapacity(source.getCurrentCapacity());
                gymClass.setMaxCapacity(source.getMaxCapacity());
                gymClass.setSchedule(source.getSchedule());

                // Ensure registeredUsers is properly initialized
                List<String> registeredUsers = source.getRegisteredUsers();
                if (registeredUsers != null) {
                    gymClass.setRegisteredUsers(registeredUsers);
                }

                return gymClass;
            }
        });

        super.addConverter(new AbstractConverter<GymClass, GymClassFirestoreDto>() {
            @Override
            protected GymClassFirestoreDto convert(GymClass source) {
                GymClassFirestoreDto dto = new GymClassFirestoreDto();
                dto.setName(source.getName());
                dto.setInstructor(source.getInstructor());
                dto.setLocation(source.getLocation());
                dto.setCurrentCapacity(source.getCurrentCapacity());
                dto.setMaxCapacity(source.getMaxCapacity());
                dto.setSchedule(source.getSchedule());
                dto.setRegisteredUsers(source.getRegisteredUsers());
                return dto;
            }
        });

        // NutritionPlan converters
        super.addConverter(new AbstractConverter<NutritionPlanFirestoreDto, NutritionPlan>() {
            @Override
            protected NutritionPlan convert(NutritionPlanFirestoreDto source) {
                NutritionPlan plan = new NutritionPlan();
                if (source.getId() != null) {
                    plan.setId(new NutritionPlanId(source.getId()));
                }
                plan.setClientId(source.getClientId());
                plan.setName(source.getName());
                plan.setAge(source.getAge());
                plan.setHeight(source.getHeight());
                plan.setWeight(source.getWeight());
                String sex = source.getSex();
                if (sex != null && (sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female"))) {
                    plan.setSex(sex.toLowerCase());
                } else {
                    // Asignar valor por defecto o loggear
                    plan.setSex("male");  // O "female", según convenga
                    System.out.println("Warning: sex inválido en NutritionPlanFirestoreDto, asignando 'male' por defecto");
                }
                plan.setWeightChangeGoal(source.getWeightChangeGoal());
                plan.setTimeForChange(source.getTimeForChange());
                plan.setMuscleGainFocus(source.isMuscleGainFocus());
                plan.setActivityLevel(source.getActivityLevel());
                plan.setActive(source.isActive());
                plan.setTargetCalories(source.getTargetCalories());
                plan.setProteinGrams(source.getProteinGrams());
                plan.setCarbsGrams(source.getCarbsGrams());
                plan.setFatsGrams(source.getFatsGrams());
                return plan;
            }
        });

        super.addConverter(new AbstractConverter<NutritionPlan, NutritionPlanFirestoreDto>() {
            @Override
            protected NutritionPlanFirestoreDto convert(NutritionPlan source) {
                NutritionPlanFirestoreDto dto = new NutritionPlanFirestoreDto();
                if (source.getId() != null) {
                    dto.setId(source.getId().toString());
                }
                dto.setClientId(source.getClientId());
                dto.setName(source.getName());
                dto.setAge(source.getAge());
                dto.setHeight(source.getHeight());
                dto.setWeight(source.getWeight());
                dto.setSex(source.getSex());
                dto.setWeightChangeGoal(source.getWeightChangeGoal());
                dto.setTimeForChange(source.getTimeForChange());
                dto.setMuscleGainFocus(source.isMuscleGainFocus());
                dto.setActivityLevel(source.getActivityLevel().name());
                dto.setActive(source.isActive());
                dto.setTargetCalories(source.getTargetCalories());
                dto.setProteinGrams(source.getProteinGrams());
                dto.setCarbsGrams(source.getCarbsGrams());
                dto.setFatsGrams(source.getFatsGrams());
                return dto;
            }
        });
        // Add these to your existing DTOToDomainMapper class

// Exercise converter
        super.addConverter(new AbstractConverter<ExerciseFirestoreDto, Exercise>() {
            @Override
            protected Exercise convert(ExerciseFirestoreDto source) {
                return new Exercise(
                        new ExerciseId(source.getId()),
                        source.getNombre(),
                        source.getDescripcion(),
                        source.getImagen()
                );
            }
        });

// RoutineExercise converter
        super.addConverter(new AbstractConverter<RoutineExerciseFirestoreDto, RoutineExercise>() {
            @Override
            protected RoutineExercise convert(RoutineExerciseFirestoreDto source) {
                Exercise exercise = null;
                if (source.getExerciseDetails() != null) {
                    exercise = new Exercise(
                            new ExerciseId(source.getExerciseDetails().getId()),
                            source.getExerciseDetails().getNombre(),
                            source.getExerciseDetails().getDescripcion(),
                            source.getExerciseDetails().getImagen()
                    );
                } else if (source.getExerciseId() != null) {
                    exercise = new Exercise(
                            new ExerciseId(source.getExerciseId()),
                            null, null, null
                    );
                }

                return new RoutineExercise(
                        new RoutineExerciseId(source.getId()),
                        exercise,
                        source.getSets(),
                        source.getRepetitions(),
                        source.getWeight()
                );
            }
        });

// Routine converter
        super.addConverter(new AbstractConverter<RoutineFirestoreDto, Routine>() {
            @Override
            protected Routine convert(RoutineFirestoreDto source) {
                Routine routine = new Routine();
                if (source.getId() != null) {
                    routine.setId(new RoutineId(source.getId()));
                }
                routine.setName(source.getName());
                routine.setSchedule(source.getDays());
                if (source.getClientId() != null) {
                    routine.setClientId(new ClientId(source.getClientId()));
                }

                // Map exercises
                List<RoutineExercise> exercises = new ArrayList<>();
                if (source.getExercises() != null) {
                    for (RoutineExerciseFirestoreDto exerciseDto : source.getExercises()) {
                        Exercise exercise = null;
                        if (exerciseDto.getExerciseDetails() != null) {
                            exercise = new Exercise(
                                    new ExerciseId(exerciseDto.getExerciseDetails().getId()),
                                    exerciseDto.getExerciseDetails().getNombre(),
                                    exerciseDto.getExerciseDetails().getDescripcion(),
                                    exerciseDto.getExerciseDetails().getImagen()
                            );
                        } else if (exerciseDto.getExerciseId() != null) {
                            exercise = new Exercise(
                                    new ExerciseId(exerciseDto.getExerciseId()),
                                    null, null, null
                            );
                        }

                        RoutineExercise routineExercise = new RoutineExercise(
                                new RoutineExerciseId(exerciseDto.getId()),
                                exercise,
                                exerciseDto.getSets(),
                                exerciseDto.getRepetitions(),
                                exerciseDto.getWeight()
                        );
                        exercises.add(routineExercise);
                    }
                }
                routine.setExercises(exercises);

                return routine;
            }
        });

// Reverse converters (domain to DTO)
        super.addConverter(new AbstractConverter<Exercise, ExerciseFirestoreDto>() {
            @Override
            protected ExerciseFirestoreDto convert(Exercise source) {
                return new ExerciseFirestoreDto(
                        source.getId() != null ? source.getId().getId() : null,
                        source.getNombre(),
                        source.getDescripcion(),
                        source.getImagen()
                );
            }
        });

        super.addConverter(new AbstractConverter<RoutineExercise, RoutineExerciseFirestoreDto>() {
            @Override
            protected RoutineExerciseFirestoreDto convert(RoutineExercise source) {
                ExerciseFirestoreDto exerciseDto = null;
                if (source.getExercise() != null) {
                    exerciseDto = new ExerciseFirestoreDto(
                            source.getExercise().getId().getId(),
                            source.getExercise().getNombre(),
                            source.getExercise().getDescripcion(),
                            source.getExercise().getImagen()
                    );
                }

                return new RoutineExerciseFirestoreDto(
                        source.getId() != null ? source.getId().getId() : null,
                        source.getExercise() != null ? source.getExercise().getId().getId() : null,
                        exerciseDto,
                        source.getSets(),
                        source.getRepetitions(),
                        source.getWeight()
                );
            }
        });

        super.addConverter(new AbstractConverter<Routine, RoutineFirestoreDto>() {
            @Override
            protected RoutineFirestoreDto convert(Routine source) {
                List<RoutineExerciseFirestoreDto> exerciseDtos = new ArrayList<>();
                if (source.getExercises() != null) {
                    for (RoutineExercise exercise : source.getExercises()) {
                        ExerciseFirestoreDto exerciseDto = null;
                        if (exercise.getExercise() != null) {
                            exerciseDto = new ExerciseFirestoreDto(
                                    exercise.getExercise().getId().getId(),
                                    exercise.getExercise().getNombre(),
                                    exercise.getExercise().getDescripcion(),
                                    exercise.getExercise().getImagen()
                            );
                        }

                        exerciseDtos.add(new RoutineExerciseFirestoreDto(
                                exercise.getId() != null ? exercise.getId().getId() : null,
                                exercise.getExercise() != null ? exercise.getExercise().getId().getId() : null,
                                exerciseDto,
                                exercise.getSets(),
                                exercise.getRepetitions(),
                                exercise.getWeight()
                        ));
                    }
                }

                return new RoutineFirestoreDto(
                        source.getId() != null ? source.getId().getId() : null,
                        source.getName(),
                        exerciseDtos,
                        source.getSchedule(),
                        source.getClientId() != null ? source.getClientId().getId() : null
                );
            }
        });
    }

    @Override
    public <T> T map(Object source, Class<T> destinationType) {
        return super.map(source, destinationType);
    }
}