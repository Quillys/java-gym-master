package ru.yandex.practicum.gym;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Timetable {

    // день недели -> (время начала -> занятия, которые начинаются в это время).
    // TreeMap хранит время начала в отсортированном порядке и поддерживает его при добавлении
    private final Map<DayOfWeek, NavigableMap<TimeOfDay, List<TrainingSession>>> timetable = new HashMap<>();

    public Timetable() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            timetable.put(dayOfWeek, new TreeMap<>());
        }
    }

    /**
     * Добавляет занятие в расписание. В одно время может начинаться несколько занятий.
     * Списки занятий хранятся неизменяемыми: при добавлении создаётся новый список.
     * Добавление делается заранее, и по ТЗ его сложность не ограничена, зато методы чтения
     * отдают данные без копирования, и снаружи расписание нельзя случайно изменить.
     */
    public void addNewTrainingSession(TrainingSession trainingSession) {
        NavigableMap<TimeOfDay, List<TrainingSession>> sessionsForDay = timetable.get(trainingSession.getDayOfWeek());
        TimeOfDay timeOfDay = trainingSession.getTimeOfDay();

        List<TrainingSession> sessionsAtTime = new ArrayList<>();
        List<TrainingSession> existingSessions = sessionsForDay.get(timeOfDay);
        if (existingSessions != null) {
            sessionsAtTime.addAll(existingSessions);
        }
        sessionsAtTime.add(trainingSession);
        sessionsForDay.put(timeOfDay, Collections.unmodifiableList(sessionsAtTime));
    }

    /**
     * Возвращает занятия за день недели, упорядоченные по времени начала:
     * ключ — время начала, значение — занятия, которые начинаются в это время.
     * Сложность O(1): поиск дня в HashMap и обёртка только для чтения, без копирования данных.
     */
    public NavigableMap<TimeOfDay, List<TrainingSession>> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        return Collections.unmodifiableNavigableMap(timetable.get(dayOfWeek));
    }

    /**
     * Возвращает занятия, которые начинаются в указанное время указанного дня недели,
     * или пустой список, если таких занятий нет.
     * Сложность O(log n): поиск дня в HashMap — O(1), поиск времени в TreeMap — O(log n).
     */
    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        List<TrainingSession> sessionsAtTime = timetable.get(dayOfWeek).get(timeOfDay);
        if (sessionsAtTime == null) {
            return Collections.emptyList();
        }
        return sessionsAtTime;
    }

    /**
     * Возвращает количество занятий в неделю для каждого тренера.
     * Первыми идут тренеры, которые проводят больше всего занятий.
     */
    public List<CounterOfTrainings> getCountByCoaches() {
        Map<Coach, Integer> countByCoach = new HashMap<>();
        for (NavigableMap<TimeOfDay, List<TrainingSession>> sessionsForDay : timetable.values()) {
            for (List<TrainingSession> sessionsAtTime : sessionsForDay.values()) {
                for (TrainingSession trainingSession : sessionsAtTime) {
                    Coach coach = trainingSession.getCoach();
                    countByCoach.put(coach, countByCoach.getOrDefault(coach, 0) + 1);
                }
            }
        }

        List<CounterOfTrainings> counters = new ArrayList<>();
        for (Map.Entry<Coach, Integer> entry : countByCoach.entrySet()) {
            counters.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));
        }
        Collections.sort(counters);
        return counters;
    }
}
