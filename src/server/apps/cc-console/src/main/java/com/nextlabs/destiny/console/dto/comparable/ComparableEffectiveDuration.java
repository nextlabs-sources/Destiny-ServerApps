package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.PolicyScheduleConfigDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class ComparableEffectiveDuration
        implements Comparable<ComparableEffectiveDuration> {

    private final String startDateTime;
    private final String endDateTime;
    private final String recurrenceStartTime;
    private final String recurrenceEndTime;
    private final String timezone;
    private final Long recurrenceDateOfMonth;
    private final Long recurrenceDayInMonth;
    private final Boolean sunday;
    private final Boolean monday;
    private final Boolean tuesday;
    private final Boolean wednesday;
    private final Boolean thursday;
    private final Boolean friday;
    private final Boolean saturday;

    public ComparableEffectiveDuration(PolicyScheduleConfigDTO policyScheduleConfigDTO) {
        super();
        startDateTime = policyScheduleConfigDTO.getStartDateTime();
        endDateTime = policyScheduleConfigDTO.getEndDateTime();
        recurrenceStartTime = policyScheduleConfigDTO.getRecurrenceStartTime();
        recurrenceEndTime = policyScheduleConfigDTO.getRecurrenceEndTime();
        timezone = policyScheduleConfigDTO.getTimezone();
        recurrenceDateOfMonth = policyScheduleConfigDTO.getRecurrenceDateOfMonth();
        recurrenceDayInMonth = policyScheduleConfigDTO.getRecurrenceDayInMonth();
        sunday = policyScheduleConfigDTO.isSunday();
        monday = policyScheduleConfigDTO.isMonday();
        tuesday = policyScheduleConfigDTO.isTuesday();
        wednesday = policyScheduleConfigDTO.isWednesday();
        thursday = policyScheduleConfigDTO.isThursday();
        friday = policyScheduleConfigDTO.isFriday();
        saturday = policyScheduleConfigDTO.isSaturday();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableEffectiveDuration)) return false;

        ComparableEffectiveDuration that = (ComparableEffectiveDuration) o;

        return new EqualsBuilder()
                .append(startDateTime, that.startDateTime)
                .append(endDateTime, that.endDateTime)
                .append(recurrenceStartTime, that.recurrenceStartTime)
                .append(recurrenceEndTime, that.recurrenceEndTime)
                .append(timezone, that.timezone)
                .append(recurrenceDateOfMonth, that.recurrenceDateOfMonth)
                .append(recurrenceDayInMonth, that.recurrenceDayInMonth)
                .append(sunday, that.sunday)
                .append(monday, that.monday)
                .append(tuesday, that.tuesday)
                .append(wednesday, that.wednesday)
                .append(thursday, that.thursday)
                .append(friday, that.friday)
                .append(saturday, that.saturday)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDateTime, endDateTime, recurrenceStartTime, recurrenceEndTime, timezone,
                recurrenceDateOfMonth, recurrenceDayInMonth, sunday, monday, tuesday, wednesday, thursday,
                friday, saturday);
    }

    @Override
    public int compareTo(ComparableEffectiveDuration comparableEffectiveDuration) {
        if(startDateTime!= null && !startDateTime.equals(comparableEffectiveDuration.startDateTime)) {
            return startDateTime.compareTo(comparableEffectiveDuration.startDateTime);
        }
        if(endDateTime != null && !endDateTime.equals(comparableEffectiveDuration.endDateTime)) {
            return endDateTime.compareTo(comparableEffectiveDuration.endDateTime);
        }
        if(recurrenceStartTime != null && !recurrenceStartTime.equals(comparableEffectiveDuration.recurrenceStartTime)) {
            return recurrenceStartTime.compareTo(comparableEffectiveDuration.recurrenceStartTime);
        }
        if(recurrenceEndTime != null && !recurrenceEndTime.equals(comparableEffectiveDuration.recurrenceEndTime)) {
            return recurrenceEndTime.compareTo(comparableEffectiveDuration.recurrenceEndTime);
        }
        if(timezone != null && !timezone.equals(comparableEffectiveDuration.timezone)) {
            return timezone.compareTo(comparableEffectiveDuration.timezone);
        }
        if(recurrenceDateOfMonth != null && !recurrenceDateOfMonth.equals(comparableEffectiveDuration.recurrenceDateOfMonth)) {
            return recurrenceDateOfMonth.compareTo(comparableEffectiveDuration.recurrenceDateOfMonth);
        }
        if(recurrenceDayInMonth != null && !recurrenceDayInMonth.equals(comparableEffectiveDuration.recurrenceDayInMonth)) {
            return recurrenceDayInMonth.compareTo(comparableEffectiveDuration.recurrenceDayInMonth);
        }
        if(sunday != null && !sunday.equals(comparableEffectiveDuration.sunday)) {
            return sunday.compareTo(comparableEffectiveDuration.sunday);
        }
        if(monday != null && !monday.equals(comparableEffectiveDuration.monday)) {
            return monday.compareTo(comparableEffectiveDuration.monday);
        }
        if(tuesday != null && !tuesday.equals(comparableEffectiveDuration.tuesday)) {
            return tuesday.compareTo(comparableEffectiveDuration.tuesday);
        }
        if(wednesday != null && !wednesday.equals(comparableEffectiveDuration.wednesday)) {
            return wednesday.compareTo(comparableEffectiveDuration.wednesday);
        }
        if(thursday != null && !thursday.equals(comparableEffectiveDuration.thursday)) {
            return thursday.compareTo(comparableEffectiveDuration.thursday);
        }
        if(friday != null && !friday.equals(comparableEffectiveDuration.friday)) {
            return friday.compareTo(comparableEffectiveDuration.friday);
        }
        if(saturday != null && !saturday.equals(comparableEffectiveDuration.saturday)) {
            return saturday.compareTo(comparableEffectiveDuration.saturday);
        }

        return 0;
    }
}
