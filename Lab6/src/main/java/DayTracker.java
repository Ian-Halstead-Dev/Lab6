//This is the class to track what day it is.
public class DayTracker {
    private static int currentDay = 1;

    public static int getCurrentDay() {
        return currentDay;
    }

    public static void nextDay() {
        currentDay++;
    }

    public static void resetDay() {
        currentDay = 1;
    }
}