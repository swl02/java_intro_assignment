package unsw.venues;

import java.time.LocalDate;

public class Res_Date {

    private LocalDate start_date;
    private LocalDate end_date;
    private String id;
    
    public Res_Date(LocalDate start_date,LocalDate end_date,String id) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.id = id;
    }

    //getter function
    public LocalDate get_start_date() {
        return start_date;
    }

    public LocalDate get_end_date() {
        return end_date;
    }

    public void set_start_date(LocalDate start_date) {
        this.start_date = start_date;
    }


    public void set_end_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    /**
     * 
     * @return returns 0 if both the dates are equal. It returns positive value if this date is greater than the otherDate.It returns negative value if this date is less than the otherDate.
     */
    public int compare_start_date(LocalDate res_date) {
        return start_date.compareTo(res_date);
    }

    /**
     * 
     *
     * @return returns 0 if both the dates are equal.  It returns positive value if this date is greater than the otherDate.It returns negative value if this date is less than the otherDate.
     */    
    public int compare_end_date(LocalDate res_date) {
        return end_date.compareTo(res_date);
    }

    public String getid() {
        return id;
    }

    public String get_start_string() {
        return start_date.toString();
    }
    
    public String get_end_string() {
        return end_date.toString();
    }

    public boolean compare_id(String res_id) {


        return id.equals(res_id);
    }

}