package unsw.venues;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

//each room have multiple start date and end date
public class Room {

    private String size;
    private String name;
    private ArrayList<Res_Date> date;
    private  Res_Date rollback_date;

    public Room(String size,String name) {
        this.size = size;
        this.name = name;
        this.date = new ArrayList<Res_Date>();
    }

    public String getsize() {
        return size;
    }

    public String getname() {
        return name;
    }

    public ArrayList<Res_Date> getdate(){
        return date;
    }

    /**
     * 
     * @return check for overlapping of dates to prevent 2 or more reservation occurs at the same time
     */
    public boolean check_valid_date(LocalDate new_start_date,LocalDate new_end_date) {

        //System.out.println("hehe");    
        for (int i = 0;date.size() > i;i++) {

            if (date.get(i).compare_start_date(new_start_date) >= 0 && date.get(i).compare_end_date(new_end_date) <= 0 ) {
                return false;
            }

            if (date.get(i).compare_start_date(new_start_date) <= 0 && date.get(i).compare_end_date(new_end_date) >= 0 ) {
                return false;
            }
            
        }

        //System.out.println("love is the way");  
        return true;
    }

    /**
     * 
     * request for reservation with a specific id and start and end date
     */
    public void request(LocalDate new_start_date,LocalDate new_end_date,String id) {
        Res_Date new_res_date = new Res_Date(new_start_date, new_end_date,id);
        
        date.add(new_res_date);
        
        Collections.sort(date, new SortbyDate()); 

    }

    //remove res_date from room
    /**
     * 
     * @return remove res_date from room
     */
    public int remove_date(LocalDate start ,LocalDate end) {
        //need to check whether the date exist eventhough the id is correct
        
        for (int i = 0;date.size() > i;i++) {
            if (start.equals(date.get(i).get_start_date()) && end.equals(date.get(i).get_end_date())) {
                //we will keep a  copy of previous reservation date and id
                rollback_date = new Res_Date(start, end, date.get(i).getid());
                date.remove(i);
                return 0;
            }


        }


        return -1;
    }

    /**
     * Only the string and class has been checked
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Room r = (Room) obj;
        
        return r.name.equals(name);
    }

    /**
     * 
     * @return the count of reserved date for the room
     */
    public int get_res_date_list_size() {
        return date.size();
    }

    public Res_Date get_rollback() {
        return rollback_date;
    }

    public void set_rollback(String id,LocalDate start,LocalDate end) {
        rollback_date = new Res_Date(start, end, id);
    }

    public String get_rollback_id() {
        return rollback_date.getid();
    }

    public void rollback() {
        date.add(rollback_date);
    }

    public boolean Compare_room_size(String s) {


        return size.equals(s);
    }


    /**
     * 0 means that the cancel is successful else return -1
     * @return
     */
    public int cancel(String res_id) {
        for (Res_Date res_date : date) {
            if (res_date.compare_id(res_id)) {
                return remove_date(res_date.get_start_date(), res_date.get_end_date());
            }
        }

        return 0;
    }


}