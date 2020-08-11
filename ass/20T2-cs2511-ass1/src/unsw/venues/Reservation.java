package unsw.venues;

import java.time.LocalDate;
import java.util.ArrayList;


//need to roll back
public class Reservation {
    //private static int id = 0;
    private String id;
    private Venue res_venue;
    private LocalDate start_date;
    private LocalDate end_date;

    public Reservation(String id,LocalDate start_date,LocalDate end_date,Venue venue) {
        this.id = id;
        res_venue = venue;        
        this.start_date = start_date;
        this.end_date = end_date;
    }

    //getter     
    public String getid() {
        return id;
    }

    public LocalDate getstartdate() {
        return start_date;
    }

    public LocalDate getenddate() {
        return end_date;
    }

    public String getvenue_name() {
        return res_venue.getname();
    }

    public Venue getvenue() {
        return res_venue;
    }

    public int get_n_room() {
        return res_venue.get_n_room();
    }

    public ArrayList<Room> get_rooms() {
        return res_venue.getrooms();
    }
    //setter
    public void setname(String id) {
        this.id = id;
    }

    public void setstartdate(LocalDate start_date) {
        this.start_date = start_date;
    }

    public void setenddate(LocalDate end_date) {
        this.end_date = end_date;
    }


    /**
     * @return null if false, if true return the venue that satified the requirement
     */
    public static Venue check_req(int small,int medium,int large,ArrayList<Venue> venues,LocalDate startdate,LocalDate enddate,String id) {


        for (Venue venue:venues) {
            ArrayList<Room> rooms = venue.req_room(startdate, enddate, small, medium, large,id);

            if (rooms != null) {
                
                Venue req_venue = new Venue(venue.getname());
                for (int j = 0;rooms.size() > j;j++) {
                   
                    req_venue.addRoom(rooms.get(j));
                }
                return req_venue;
            }
        }

        return null;
    }



    /**
     * we only check the id
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        String s = (String)obj;

        return s.equals(id);
    }



    

}