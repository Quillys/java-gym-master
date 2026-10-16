package ru.yandex.practicum.gym;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

public class TimetableTest {

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник вернулось одно занятие
        Assertions.assertEquals(List.of(singleTrainingSession),
                toOrderedList(timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY)));
        //Проверить, что за вторник не вернулось занятий
        Assertions.assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        TrainingSession mondayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession saturdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        // Проверить, что за понедельник вернулось одно занятие
        Assertions.assertEquals(List.of(mondayChildTrainingSession),
                toOrderedList(timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY)));
        // Проверить, что за четверг вернулось два занятия в правильном порядке: сначала в 13:00, потом в 20:00
        Assertions.assertEquals(List.of(thursdayChildTrainingSession, thursdayAdultTrainingSession),
                toOrderedList(timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY)));
        // Проверить, что за вторник не вернулось занятий
        Assertions.assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник в 13:00 вернулось одно занятие
        Assertions.assertEquals(List.of(singleTrainingSession),
                timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        //Проверить, что за понедельник в 14:00 не вернулось занятий
        Assertions.assertTrue(timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY,
                new TimeOfDay(14, 0)).isEmpty());
    }

    @Test
    void testSeveralTrainingSessionsStartAtSameTime() {
        Timetable timetable = new Timetable();

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        Group groupAdult = new Group("Йога для взрослых", Age.ADULT, 90);
        Coach coachVasiliev = new Coach("Васильев", "Николай", "Сергеевич");
        Coach coachPetrova = new Coach("Петрова", "Анна", "Игоревна");
        TrainingSession childTrainingSession = new TrainingSession(groupChild, coachVasiliev,
                DayOfWeek.FRIDAY, new TimeOfDay(18, 0));
        TrainingSession adultTrainingSession = new TrainingSession(groupAdult, coachPetrova,
                DayOfWeek.FRIDAY, new TimeOfDay(18, 0));

        timetable.addNewTrainingSession(childTrainingSession);
        timetable.addNewTrainingSession(adultTrainingSession);

        // второе занятие на 18:00 не заменило первое — вернулись оба
        Assertions.assertEquals(List.of(childTrainingSession, adultTrainingSession),
                timetable.getTrainingSessionsForDayAndTime(DayOfWeek.FRIDAY, new TimeOfDay(18, 0)));
        // за день — одно время начала, на которое приходится два занятия
        NavigableMap<TimeOfDay, List<TrainingSession>> fridaySessions =
                timetable.getTrainingSessionsForDay(DayOfWeek.FRIDAY);
        Assertions.assertEquals(1, fridaySessions.size());
        Assertions.assertEquals(List.of(childTrainingSession, adultTrainingSession),
                fridaySessions.get(new TimeOfDay(18, 0)));
    }

    @Test
    void testGetTrainingSessionsForDaySortedByTimeRegardlessOfAddingOrder() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для взрослых", Age.ADULT, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession eveningTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(18, 30));
        TrainingSession morningTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(9, 0));
        TrainingSession lateMorningTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(9, 45));
        TrainingSession afternoonTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(12, 15));

        timetable.addNewTrainingSession(eveningTrainingSession);
        timetable.addNewTrainingSession(morningTrainingSession);
        timetable.addNewTrainingSession(afternoonTrainingSession);
        timetable.addNewTrainingSession(lateMorningTrainingSession);

        // порядок задаёт время начала с учётом минут, а не порядок добавления
        Assertions.assertEquals(List.of(new TimeOfDay(9, 0), new TimeOfDay(9, 45),
                        new TimeOfDay(12, 15), new TimeOfDay(18, 30)),
                new ArrayList<>(timetable.getTrainingSessionsForDay(DayOfWeek.WEDNESDAY).navigableKeySet()));
        Assertions.assertEquals(List.of(morningTrainingSession, lateMorningTrainingSession,
                        afternoonTrainingSession, eveningTrainingSession),
                toOrderedList(timetable.getTrainingSessionsForDay(DayOfWeek.WEDNESDAY)));
    }

    @Test
    void testEmptyTimetableReturnsEmptyResults() {
        Timetable timetable = new Timetable();

        // для любого дня возвращается пустой результат, а не null
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            Assertions.assertTrue(timetable.getTrainingSessionsForDay(dayOfWeek).isEmpty());
            Assertions.assertTrue(timetable.getTrainingSessionsForDayAndTime(dayOfWeek,
                    new TimeOfDay(10, 0)).isEmpty());
        }
    }

    @Test
    void testGetTrainingSessionsForDayAndTimeMatchesExactDayAndTime() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession mondayTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession sundayTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.SUNDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(mondayTrainingSession);
        timetable.addNewTrainingSession(sundayTrainingSession);

        // одно и то же время в разные дни — это разные занятия
        Assertions.assertEquals(List.of(mondayTrainingSession),
                timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        Assertions.assertEquals(List.of(sundayTrainingSession),
                timetable.getTrainingSessionsForDayAndTime(DayOfWeek.SUNDAY, new TimeOfDay(13, 0)));
        Assertions.assertTrue(timetable.getTrainingSessionsForDayAndTime(DayOfWeek.TUESDAY,
                new TimeOfDay(13, 0)).isEmpty());
        // время должно совпадать полностью, вместе с минутами
        Assertions.assertTrue(timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY,
                new TimeOfDay(13, 30)).isEmpty());
    }

    @Test
    void testReturnedTimetableCannotBeModified() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession trainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(trainingSession);

        NavigableMap<TimeOfDay, List<TrainingSession>> mondaySessions =
                timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        List<TrainingSession> sessionsAtTime =
                timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        // изменить расписание можно только через addNewTrainingSession
        Assertions.assertThrows(UnsupportedOperationException.class, () -> mondaySessions.clear());
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> mondaySessions.get(new TimeOfDay(13, 0)).clear());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> sessionsAtTime.add(trainingSession));
        Assertions.assertEquals(List.of(trainingSession),
                timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
    }

    @Test
    void testGetCountByCoachesEmptyTimetable() {
        Timetable timetable = new Timetable();

        Assertions.assertTrue(timetable.getCountByCoaches().isEmpty());
    }

    @Test
    void testGetCountByCoachesSortedByCountDescending() {
        Timetable timetable = new Timetable();

        Group acrobatics = new Group("Акробатика для детей 10-12 лет", Age.CHILD, 60);
        Group yoga = new Group("Йога для взрослых", Age.ADULT, 90);
        Coach coachPetrova = new Coach("Петрова", "Анна", "Игоревна");
        Coach coachIzmailov = new Coach("Измайлов", "Константин", "Викторович");
        Coach coachSemenov = new Coach("Семёнов", "Виктор", "Константинович");
        Coach coachVolkova = new Coach("Волкова", "Галина", "Юрьевна");

        // пример из ТЗ: акробатику по понедельникам, средам и субботам ведёт Измайлов,
        // по пятницам и воскресеньям — Семёнов; йогу ведут Петрова (1 раз) и Волкова (4 раза)
        timetable.addNewTrainingSession(new TrainingSession(yoga, coachPetrova,
                DayOfWeek.TUESDAY, new TimeOfDay(19, 0)));
        timetable.addNewTrainingSession(new TrainingSession(acrobatics, coachIzmailov,
                DayOfWeek.MONDAY, new TimeOfDay(18, 0)));
        timetable.addNewTrainingSession(new TrainingSession(acrobatics, coachIzmailov,
                DayOfWeek.WEDNESDAY, new TimeOfDay(18, 0)));
        timetable.addNewTrainingSession(new TrainingSession(acrobatics, coachSemenov,
                DayOfWeek.FRIDAY, new TimeOfDay(18, 0)));
        timetable.addNewTrainingSession(new TrainingSession(acrobatics, coachIzmailov,
                DayOfWeek.SATURDAY, new TimeOfDay(12, 0)));
        timetable.addNewTrainingSession(new TrainingSession(acrobatics, coachSemenov,
                DayOfWeek.SUNDAY, new TimeOfDay(12, 0)));
        timetable.addNewTrainingSession(new TrainingSession(yoga, coachVolkova,
                DayOfWeek.MONDAY, new TimeOfDay(19, 0)));
        timetable.addNewTrainingSession(new TrainingSession(yoga, coachVolkova,
                DayOfWeek.WEDNESDAY, new TimeOfDay(19, 0)));
        timetable.addNewTrainingSession(new TrainingSession(yoga, coachVolkova,
                DayOfWeek.FRIDAY, new TimeOfDay(19, 0)));
        timetable.addNewTrainingSession(new TrainingSession(yoga, coachVolkova,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0)));

        List<CounterOfTrainings> expected = List.of(
                new CounterOfTrainings(coachVolkova, 4),
                new CounterOfTrainings(coachIzmailov, 3),
                new CounterOfTrainings(coachSemenov, 2),
                new CounterOfTrainings(coachPetrova, 1));
        Assertions.assertEquals(expected, timetable.getCountByCoaches());
    }

    @Test
    void testGetCountByCoachesIdentifiesCoachByFullName() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        // разные объекты с одинаковыми Ф. И. О. — это один и тот же тренер
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach sameCoach = new Coach("Васильев", "Николай", "Сергеевич");
        // однофамилец с другим именем — другой тренер
        Coach namesake = new Coach("Васильев", "Пётр", "Сергеевич");

        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, sameCoach,
                DayOfWeek.TUESDAY, new TimeOfDay(13, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, namesake,
                DayOfWeek.WEDNESDAY, new TimeOfDay(13, 0)));

        List<CounterOfTrainings> expected = List.of(
                new CounterOfTrainings(coach, 2),
                new CounterOfTrainings(namesake, 1));
        Assertions.assertEquals(expected, timetable.getCountByCoaches());
    }

    @Test
    void testGetCountByCoachesWithEqualCountsSortedByFullName() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для взрослых", Age.ADULT, 90);
        Coach coachSmirnovNikolay = new Coach("Смирнов", "Николай", "Андреевич");
        Coach coachBelyaev = new Coach("Беляев", "Евгений", "Викторович");
        Coach coachSmirnovAndrey = new Coach("Смирнов", "Андрей", "Петрович");
        Coach coachMironov = new Coach("Миронов", "Юрий", "Борисович");

        timetable.addNewTrainingSession(new TrainingSession(group, coachSmirnovNikolay,
                DayOfWeek.THURSDAY, new TimeOfDay(15, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachBelyaev,
                DayOfWeek.WEDNESDAY, new TimeOfDay(12, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachSmirnovAndrey,
                DayOfWeek.FRIDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachMironov,
                DayOfWeek.FRIDAY, new TimeOfDay(10, 0)));

        // при равном количестве занятий — по алфавиту: сначала фамилия, затем имя
        List<CounterOfTrainings> expected = List.of(
                new CounterOfTrainings(coachBelyaev, 1),
                new CounterOfTrainings(coachMironov, 1),
                new CounterOfTrainings(coachSmirnovAndrey, 1),
                new CounterOfTrainings(coachSmirnovNikolay, 1));
        Assertions.assertEquals(expected, timetable.getCountByCoaches());
    }

    // Собирает занятия за день в один список в порядке времени начала
    private static List<TrainingSession> toOrderedList(NavigableMap<TimeOfDay, List<TrainingSession>> sessionsForDay) {
        List<TrainingSession> result = new ArrayList<>();
        for (TimeOfDay timeOfDay : sessionsForDay.navigableKeySet()) {
            result.addAll(sessionsForDay.get(timeOfDay));
        }
        return result;
    }
}
