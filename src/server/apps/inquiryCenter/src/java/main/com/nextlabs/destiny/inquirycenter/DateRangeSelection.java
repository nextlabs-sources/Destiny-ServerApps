/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter;

import java.util.Calendar;

/**
 * @author nnallagatla
 */
public class DateRangeSelection{
	
	
	public static final int CURRENT_WEEK = 0;
	public static final int CURRENT_MONTH = 1;
	public static final int CURRENT_QUARTER = 2;
	public static final int CURRENT_YEAR = 3;
	public static final int PRIOR_WEEK = 4;
	public static final int PRIOR_MONTH = 5;	
	public static final int PRIOR_QUARTER = 6;
	public static final int PRIOR_YEAR = 7;
	public static final int WEEK_TO_DATE = 8;
	public static final int MONTH_TO_DATE = 9;
	public static final int QUARTER_TO_DATE = 10;
	public static final int YEAR_TO_DATE = 11;
	public static final int TODAY = 12;
	public static final int YESTERDAY = 13;
	public static final int LAST_N_DAYS = 14;
	public static final int LAST_N_HOURS = 15;
	public static final int THIS_HOUR = 16;
	public static final int PRIOR_HOUR = 17;
	
	
    //---------------------------------------------------
    // Initial Date
    private Calendar currentDate    = null;

    // Two GregorianCalendar Objects to Return 
    private Calendar beginDate         = null;
	private Calendar endDate           = null;

    private int range = -1;
    private int month = -1;
    private int year = -1;
    private int day = -1;
    private int n = -1;
    
    /**
     * 
     * @param option
     */
    public DateRangeSelection(int option){

        // Initialize the buttonId to the passed in value
        this.range = option;

        currentDate = Calendar.getInstance();
        
        day = currentDate.get(Calendar.DATE);

        // Set the Current Month
        month   = currentDate.get(Calendar.MONTH);

        // Set the Current Year
        year    = currentDate.get(Calendar.YEAR);

        beginDate   = Calendar.getInstance();
        endDate     = Calendar.getInstance();

        // Set the Current Date Selection 
        setCurrentDateRangeSelection();
    }
    
    
    /**
     * To get the start and end dates relative to the given date
     * @param option
     * @param relativeToDate
     */
    public DateRangeSelection (int option, Calendar relativeToDate)
    {
        // Initialize the buttonId to the passed in value
        this.range = option;

        // Initialize the new GregorianCalendar Objects
        currentDate = relativeToDate;
        
        day = currentDate.get(Calendar.DATE);

        // Set the Current Month
        month   = currentDate.get(Calendar.MONTH);

        // Set the Current Year
        year    = currentDate.get(Calendar.YEAR);

        beginDate   = Calendar.getInstance();
        beginDate.setTimeInMillis(currentDate.getTimeInMillis());
        
        endDate     = Calendar.getInstance();
        endDate.setTimeInMillis(currentDate.getTimeInMillis());

        // Set the Current Date Selection 
        setCurrentDateRangeSelection();
    }
    
    
    /**
     * This constructor to be used for last_n_days option
     * @param option
     * @param n
     */
    public DateRangeSelection(int option, int n){

        // Initialize the buttonId to the passed in value
        this.range = option;

        // Initialize the new GregorianCalendar Objects
        currentDate = Calendar.getInstance();

        // Set the Current Month
        month   = currentDate.get(Calendar.MONTH);

        // Set the Current Year
        year    = currentDate.get(Calendar.YEAR);
        day = currentDate.get(Calendar.DATE);

        beginDate   = Calendar.getInstance();
        endDate     = Calendar.getInstance();
        
        this.n = n;
        
        // Set the Current Date Selection 
        setCurrentDateRangeSelection();
    }
    
    /**
     * This constructor to be used for last_n_days option
     * @param option
     * @param n
     */
    public DateRangeSelection(int option, Calendar relativeToDate, int n){

        // Initialize the buttonId to the passed in value
        this.range = option;

        // Initialize the new GregorianCalendar Objects
        currentDate = relativeToDate;

        // Set the Current Month
        month   = currentDate.get(Calendar.MONTH);

        // Set the Current Year
        year    = currentDate.get(Calendar.YEAR);
        day = currentDate.get(Calendar.DATE);

        beginDate   = Calendar.getInstance();
        beginDate.setTimeInMillis(currentDate.getTimeInMillis());
        
        endDate     = Calendar.getInstance();
        endDate.setTimeInMillis(currentDate.getTimeInMillis());
        
        this.n = n;
        
        // Set the Current Date Selection 
        setCurrentDateRangeSelection();
    }
    

    // Constructor : Creates a new Date Selection Object with Two Dates
    private DateRangeSelection(Calendar beginDate, Calendar endDate){

        // Set the Being and End Dates for this object
        this.beginDate  = beginDate;
        this.endDate    = endDate;
    }

    // Sets the Current Date based on the Button Used
    private void setCurrentDateRangeSelection(){
        // Switch on the buttons id
    	
        switch(range){
        	case CURRENT_WEEK:
        		getCurrentWeek();
        		break;
            case CURRENT_MONTH: 
                getCurrentMonth();
                break;
            // Current Quarter
            case CURRENT_QUARTER:
                getCurrentQuarter();
                break;
            // Current Year
            case CURRENT_YEAR: 
                getCurrentYear();
                break;                
            case PRIOR_WEEK:
            	getPriorWeek();
            	break;            	
            case PRIOR_MONTH: 
                getPriorMonth();
                break;
            case PRIOR_QUARTER: 
                getPriorQuarter();
                break;
            case PRIOR_YEAR: 
                getPriorYear();
                break;
            case WEEK_TO_DATE:
            	getWeekToDate();
            	break;
            case MONTH_TO_DATE:
                getMonthToDate();
                break;
            case QUARTER_TO_DATE:
                getQuarterToDate();
                break;
            case YEAR_TO_DATE:
                getYearToDate();
                break;
            case TODAY:
                getToday();
                break;
            case YESTERDAY:
                getYesterday();
                break;
            case LAST_N_DAYS:
                getLastNDays();
                break;
            case LAST_N_HOURS:
                getLastNHours();
                break;
            case THIS_HOUR:
                getThisHour();
                break;
            case PRIOR_HOUR:
                getPriorHour();
                break;
            // Default Case
            default:
                break;
        }
    }

    private void getPriorHour() {
		int currentHour = beginDate.get(Calendar.HOUR_OF_DAY);
		updateToBeginningOfDay();
		beginDate.set(Calendar.HOUR_OF_DAY, currentHour);
		beginDate.add(Calendar.HOUR_OF_DAY, -1);
		
		updateToEndOfDay();
		endDate.set(Calendar.HOUR_OF_DAY, currentHour -1);
	}


	private void getThisHour() {
		int currentHour = beginDate.get(Calendar.HOUR_OF_DAY);
		updateToBeginningOfDay();
		beginDate.set(Calendar.HOUR_OF_DAY, currentHour);
		
		//no need to change end date
	}


	private void getLastNHours() {
		beginDate.add(Calendar.HOUR_OF_DAY, -1 * n);
		//should we set minutes and seconds to 0?
	}

	private DateRangeSelection getLastNDays() {
        
        beginDate.add(Calendar.DAY_OF_YEAR, -1 * n);

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
	}


	private DateRangeSelection getYesterday() {
        // Set the Year, Month and Date for the beginDate
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, month);
        beginDate.set(Calendar.DATE, day);
        
        beginDate.add(Calendar.DATE, -1);
        
        updateToBeginningOfDay();
        
        endDate.setTimeInMillis(beginDate.getTimeInMillis());
        
        updateToEndOfDay();

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
		
	}


	private void updateToBeginningOfDay()
	{
        beginDate.set(Calendar.HOUR_OF_DAY, 0);
        beginDate.set(Calendar.MINUTE, 0);
        beginDate.set(Calendar.SECOND, 0);
        beginDate.set(Calendar.MILLISECOND, 0);
	}
	
	private void updateToEndOfDay()
	{
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);
	}
	
	private DateRangeSelection getToday() {
        // Set the Year, Month and Date for the beginDate
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, month);
        beginDate.set(Calendar.DATE, day);
        updateToBeginningOfDay();

        
        endDate.setTimeInMillis(currentDate.getTimeInMillis());
        updateToEndOfDay();
        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
	}


	private DateRangeSelection getWeekToDate() {

        // Set the Year, Month and Date for the beginDate
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, month);
        beginDate.set(Calendar.DAY_OF_WEEK, currentDate.getFirstDayOfWeek());
        updateToBeginningOfDay();
        
        endDate.setTimeInMillis(currentDate.getTimeInMillis());

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
	}

	// Returns the Current Month Range as a Date Selection Object
    private DateRangeSelection getCurrentWeek(){

        // Set the Year, Month and Date for the beginDate
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, month);
        beginDate.set(Calendar.DAY_OF_WEEK, currentDate.getFirstDayOfWeek());
        updateToBeginningOfDay();
        
        endDate.setTimeInMillis(beginDate.getTimeInMillis());
        endDate.add(Calendar.DATE, 7);
        endDate.add(Calendar.MILLISECOND, -1);
        updateToEndOfDay();
        
        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);

    }
    
    // Returns the Current Month Range as a Date Selection Object
    private DateRangeSelection getCurrentMonth(){

        // Determine the First and Last Day of the Current Month
        int firstOfMonth    = currentDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        int lastOfMonth     = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Set the Year, Month and Date for the beginDate
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, month);
        beginDate.set(Calendar.DAY_OF_MONTH, firstOfMonth);
        updateToBeginningOfDay();
        
        // Set the Year, Month and Date for the endDate
        endDate.set(Calendar.YEAR, year);
        endDate.set(Calendar.MONTH, month);
        endDate.set(Calendar.DAY_OF_MONTH, lastOfMonth);
        updateToEndOfDay();

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);

    }

    // Returns the Current Quarter Range as a DateRangeSelection Object
    private DateRangeSelection getCurrentQuarter(){

        // Determine the Quarter Begin and Quarter End Months
        int quarterBeginMonth = (((month) / 3) * 3);
        int quarterEndMonth = quarterBeginMonth + 2;

        // Set the Month and Year for the Current Quarter
        beginDate.set(Calendar.MONTH, quarterBeginMonth);
        beginDate.set(Calendar.YEAR, year);

        // Determine First Day of the Quarter Begin Month
        int firstDayOfBeginMonth = beginDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        // Set the Day for the Begin Date of the Current Quarter
        beginDate.set(Calendar.DAY_OF_MONTH, firstDayOfBeginMonth);

        updateToBeginningOfDay();
        
        // Set the Current Quarter Ending Month and Year
        endDate.set(Calendar.MONTH, quarterEndMonth);
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        
        endDate.getTime();
        
        // Determine the Last Day of the Quarter End Month
        int lastDayOfEndMonth = endDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Set the endDate Date of Month
        endDate.set(Calendar.DAY_OF_MONTH, lastDayOfEndMonth);
        
        updateToEndOfDay();

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);

    }

    // Returns the Current Year Range as a DateRangeSelection Object
    private DateRangeSelection getCurrentYear(){

        // Determine the int values of the first and last months of the year
        int firstMonth = currentDate.getActualMinimum(Calendar.MONTH);
        int lastMonth = currentDate.getActualMaximum(Calendar.MONTH);

        // Set the Month for beginDate
        beginDate.set(Calendar.MONTH, firstMonth);

        // Determine the int value for the first Day of the Year
        int firstDayOfYear = beginDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        // Set the Day for Begin Date to the first Day of the year
        beginDate.set(Calendar.DAY_OF_MONTH, firstDayOfYear);
        
        updateToBeginningOfDay();
        
        // Set the Month for the endDate to the Last month of the year
        endDate.set(Calendar.MONTH, lastMonth);
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        
        endDate.getTime();
        // Determine the last day of the Year from the given month
        int lastDayOfYear = endDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Set the Day of the endDate to the Last day of the year
        endDate.set(Calendar.DAY_OF_MONTH, lastDayOfYear);

        updateToEndOfDay();
        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
    }

    /**
     * Returns start and end dates of previous calendar week
     * starting from Sunday
     * 
     * @return
     */
    private DateRangeSelection getPriorWeek(){
		int cdow = beginDate.get(Calendar.DAY_OF_WEEK);
		Calendar lastSun = (Calendar) beginDate.clone();
		lastSun.add(Calendar.WEEK_OF_YEAR, -1);
        // first day
        lastSun.set(Calendar.DAY_OF_WEEK, lastSun.getFirstDayOfWeek());
		
	
		Calendar lastSat = (Calendar) beginDate.clone();
		lastSat.set(Calendar.DAY_OF_WEEK, lastSat.getFirstDayOfWeek());
		lastSat.add(Calendar.DAY_OF_MONTH, -1);
		
		beginDate = lastSun;
		updateToBeginningOfDay();
		
		endDate = lastSat;
		updateToEndOfDay();
		
		// Return the new DateRangeSelection Object
		return new DateRangeSelection(beginDate, endDate);
    }
    
    // Returns the Prior Month as a DateRangeSelection Object
    private DateRangeSelection getPriorMonth(){

        // Decrement the Month by 1 to get the prior month
        month -= 1;

        // Allow for underflow to the previous year
        if(month < 0)
        {   
            month = 11;
            year--;
        }

        // Set the Year, Month, and Day
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, month);
        beginDate.set(Calendar.DAY_OF_MONTH, beginDate.getActualMinimum(Calendar.DAY_OF_MONTH));

        updateToBeginningOfDay();

        // Set the Year, Month, and Day
        endDate.set(Calendar.YEAR, year);
        endDate.set(Calendar.MONTH, month);
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        
        endDate.getTime();
        
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        
        updateToEndOfDay();

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
    }

    // Returns the Prior Quarter as a DateRangeSelection Object
    private DateRangeSelection getPriorQuarter(){

        // Determine the int values for the Begin and End Quarter Months
        int quarterBeginMonth = (((month) / 3) * 3);
        int quarterEndMonth = quarterBeginMonth + 2;

        // Subtract 3 from the 
        quarterBeginMonth   -= 3;
        quarterEndMonth     -= 3;

        // Allow for underflow for the previous year
        if(quarterBeginMonth < 0)
        {   
            quarterBeginMonth += 12;
            quarterEndMonth += 12;

            year --;    
        }

        // Set the Begin Date's Month and Year
        beginDate.set(Calendar.MONTH, quarterBeginMonth);
        beginDate.set(Calendar.YEAR, year);

        // Get First Day of Quarter's Begin Month
        int firstDayOfBeginMonth = beginDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        beginDate.set(Calendar.DAY_OF_MONTH, firstDayOfBeginMonth);

        updateToBeginningOfDay();
        
        // Set the End Date's Month and Year
        endDate.set(Calendar.MONTH, quarterEndMonth);
        endDate.set(Calendar.YEAR, year);
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        
        endDate.getTime();

        // Determine the Last Day of the Quarter's End Month
        int lastDayOfEndMonth = endDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Set the End Day for the End Month
        endDate.set(Calendar.DAY_OF_MONTH, lastDayOfEndMonth);
        
        updateToEndOfDay();

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
    }

    // Returns the Prior Year Range as a DateRangeSelection Object
    private DateRangeSelection getPriorYear(){

        // Decrement the Year by 1 to get the Prior Year
        year -= 1;

        // Determine the int values for the Current years first and last months
        int firstMonth = currentDate.getActualMinimum(Calendar.MONTH);
        int lastMonth = currentDate.getActualMaximum(Calendar.MONTH);

        // Set the Begin date's Year and Month
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, firstMonth);

        // Determine the first day of the first month of the previous year
        int firstDayOfYear = beginDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        // Set the beginDate to the first day of the previous year
        beginDate.set(Calendar.DAY_OF_MONTH, firstDayOfYear);

        updateToBeginningOfDay();
        
        // Set the End date's Year and Month
        endDate.set(Calendar.YEAR, year);
        endDate.set(Calendar.MONTH, lastMonth);
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        
        endDate.getTime();

        // Determine the last day of the last month of the previous year
        int lastDayOfYear = endDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Set the endDate to the last day of the previous year
        endDate.set(Calendar.DAY_OF_MONTH, lastDayOfYear);
        
        updateToEndOfDay();

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
    }       

    // Returns the Month to Date as a DateRangeSelection Object
    private DateRangeSelection getMonthToDate(){

        // Determine the int value for the first day of the Month
        int firstOfMonth = currentDate.getActualMinimum(Calendar.DAY_OF_MONTH);

        // Set the Year, Month, and Day for the Begin Date
        beginDate.set(Calendar.YEAR, year);
        beginDate.set(Calendar.MONTH, month);
        beginDate.set(Calendar.DAY_OF_MONTH, firstOfMonth);

        updateToBeginningOfDay();
        
        // Set the endDate to the Current Date
        endDate = currentDate;

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
    }

    // Returns the Quarter to Date as a DateRangeSelection Object
    private DateRangeSelection getQuarterToDate(){

        // Determine the int value for the current Quarter's beginning month
        int quarterBeginMonth = ((month / 3) * 3);

        // Set the Month and Year for the beginMonth
        beginDate.set(Calendar.MONTH, quarterBeginMonth);
        beginDate.set(Calendar.YEAR, year);

        updateToBeginningOfDay();
        
        // Determine the First Day of Quarter's Begin Month
        int firstDayOfBeginMonth = beginDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        // Set the day of the beginDate to the first day of the Quarter's Month
        beginDate.set(Calendar.DAY_OF_MONTH, firstDayOfBeginMonth);

        // Set the end Date to the Current Date
        endDate = currentDate;

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
    }

    // Returns the Year to Date as a DateRangeSelection Object
    private DateRangeSelection getYearToDate(){

        // Determine the first Month of the Year
        int firstMonth = currentDate.getActualMinimum(Calendar.MONTH);

        beginDate.set(Calendar.MONTH, firstMonth);

        int firstDayOfYear = beginDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        beginDate.set(Calendar.DAY_OF_MONTH, firstDayOfYear);

        updateToBeginningOfDay();
        
        // Set the end Date to the Current Date
        endDate = currentDate;

        // Return the new DateRangeSelection Object
        return new DateRangeSelection(beginDate, endDate);
    }

    /**
	 * @return the beginDate
	 */
	public Calendar getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate the beginDate to set
	 */
	private void setBeginDate(Calendar beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return the endDate
	 */
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	private void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
}
