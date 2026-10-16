package ru.yandex.practicum.gym;

import java.util.Objects;

public class CounterOfTrainings implements Comparable<CounterOfTrainings> {

    // тренер
    private final Coach coach;
    // количество занятий тренера в неделю
    private final int count;

    public CounterOfTrainings(Coach coach, int count) {
        this.coach = coach;
        this.count = count;
    }

    public Coach getCoach() {
        return coach;
    }

    public int getCount() {
        return count;
    }

    /**
     * Сначала идут тренеры с большим количеством занятий, то есть сортировка по убыванию.
     * При равном количестве — по алфавиту (фамилия, имя, отчество), чтобы порядок всегда был одинаковым.
     */
    @Override
    public int compareTo(CounterOfTrainings other) {
        if (count != other.count) {
            return Integer.compare(other.count, count);
        }
        int result = coach.getSurname().compareTo(other.coach.getSurname());
        if (result == 0) {
            result = coach.getName().compareTo(other.coach.getName());
        }
        if (result == 0) {
            result = coach.getMiddleName().compareTo(other.coach.getMiddleName());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CounterOfTrainings that = (CounterOfTrainings) o;
        return count == that.count && Objects.equals(coach, that.coach);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coach, count);
    }

    @Override
    public String toString() {
        return coach.getSurname() + " " + coach.getName() + " " + coach.getMiddleName() + ": " + count;
    }
}
