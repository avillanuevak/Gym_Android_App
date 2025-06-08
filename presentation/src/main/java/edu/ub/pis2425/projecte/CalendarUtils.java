package edu.ub.pis2425.projecte;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ub.pis2425.projectejady0.domain.CalendarItem;
import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.Routine;

/**
 * Utility class to help organize and sort calendar items (gym classes and routines)
 * by the days of the week in a consistent and reusable way.
 */
public class CalendarUtils {

    // Groups a list of CalendarItemWithSchedule objects by their specific schedule (day of the week).
    public static Map<String, List<CalendarFragment.CalendarItemWithSchedule>> sortItemsByDay(List<CalendarFragment.CalendarItemWithSchedule> items) {
        Map<String, List<CalendarFragment.CalendarItemWithSchedule>> itemsByDay = new TreeMap<>(CalendarUtils::compareDays);

        for (CalendarFragment.CalendarItemWithSchedule itemWithSchedule : items) {
            String day = capitalizeFirstLetter(itemWithSchedule.getSpecificSchedule());
            itemsByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(itemWithSchedule);
        }

        return itemsByDay;
    }

    // Capitalizes the first letter of the input string and converts the rest to lowercase.
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // Compares two days of the week and sorts them in the order: Monday, Tuesday, ..., Sunday.
    public static int compareDays(String day1, String day2) {
        List<String> daysOrder = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        int index1 = daysOrder.indexOf(day1);
        int index2 = daysOrder.indexOf(day2);
        if (index1 == -1 || index2 == -1) {
            return day1.compareTo(day2);
        }
        return Integer.compare(index1, index2);
    }
}
