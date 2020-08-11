package unsw.venues;

import java.time.LocalDate;
import java.util.ArrayList;

public class Venue {

    private ArrayList<Room> rooms;    
    private String name;
    private int small;
    private int medium;
    private int large;

    public Venue(String name) {
        rooms = new ArrayList<Room>();
        this.name =  name;
    }

    public String getname() {
        return name;
    }

    /**
     * 
     * add new room as well as  increase for counter of either small, medium or large
     */
    public void addRoom(Room new_room) {
        rooms.add(new_room);

        if (new_room.getsize().toLowerCase().equals("small") ) {
            small++;
        } else if (new_room.getsize().toLowerCase().equals("medium")) {
            medium++;
        } else if (new_room.getsize().toLowerCase().equals("large")) {
            large++;
        }

    }

    public int get_n_room() {
        return small+medium+large;
    }

    public int small_room() {
        return small;
    }

    public int medium_room() {
        return medium;
    }

    public int large_room() {
        return large;
    }

    public ArrayList<Room> getrooms() {
        return rooms;
    }




    /**
     * 
     * @return check whether there are enough room that satisfy the request  ,if true i will put the req into the room of the venue,the checking and the request are sequential so rollback is not needed
     */
    public boolean check_req_room(LocalDate startdate,LocalDate enddate,int s, int m,int l,String id) {
        
        //ArrayList<Room> requested = new ArrayList<Room>();

        //sanity check
        if (small < s) {return false;}
        if (medium < m) {return false;}
        if (large < l) {return false;}
        
        int check_s = s;
        int check_m = m;
        int check_l = l;

        // we will do the checking first before we do the cancelation
        for (Room room : rooms) {
            if (room.check_valid_date(startdate, enddate)) {
                if (room.Compare_room_size("small") && check_s != 0) {
                    check_s--;
                } else if (room.Compare_room_size("medium") && check_m != 0) {
                    check_m--;
                } else if (room.Compare_room_size("large") && check_l != 0) {  
                    check_l--;
                }
            }
        }

        if (check_s != 0) {
            return false;
        }

        if (check_m != 0) {
            return false;
        }

        if (check_l != 0) {
            return false;
        }

        return true;

    }
    /**
     * request the room request
     * @return
     */

    //if we reached here means we can do the req_room 
    public ArrayList<Room> apply_req_room(LocalDate startdate,LocalDate enddate,int s, int m,int l,String id) {
        int check_s = s;
        int check_m = m;
        int check_l = l;

        ArrayList<Room> requested = new ArrayList<Room>(); 


        for (Room room : rooms) {
            if (room.check_valid_date(startdate, enddate)) {
                if (room.Compare_room_size("small") && check_s != 0) {
                    room.request(startdate, enddate,id);
                    check_s--;
                    requested.add(room);    
                } else if (room.Compare_room_size("medium") && check_m != 0) {
                    room.request(startdate, enddate,id);
                    requested.add(room);
                    check_m--;    
                } else if (room.Compare_room_size("large") && check_l != 0) {  
                    room.request(startdate, enddate,id);
                    requested.add(room);
                    check_l--;    
                }
            }
        }        


        return requested;

    }

    public ArrayList<Room> req_room(LocalDate startdate,LocalDate enddate,int s, int m,int l,String id) {

        if (check_req_room(startdate,enddate,s, m,l,id)) {
            return apply_req_room(startdate, enddate, s, m, l, id);
        }

        return null;
    }





    /**
     * remove requested  rooom
     * @return return 0 if possible ,return -1 if not possible
     */
    public int remove_req_room(LocalDate start,LocalDate end,Room res_room) {

        //need to check whether the room exist
        //there may be multiple room
        //prevent ghost room from existing
        for (int i = 0;rooms.size() > i;i++) {
            //which means the room exist
            //not ghost room

            if (res_room.getname().equals(rooms.get(i).getname())) {
                int transaction = rooms.get(i).remove_date(start, end);                
                //transaction failed = -1
                if(transaction == -1) {
                    return -1;
                }
            
            } 
        }

        return 0;
    }


    /**
     * 
     * @return null if we cannot find the room , the room if we actually found the room
     */
    public Room findroom(Room room) {
        for (int i = 0;rooms.size() > i;i++) {
            if(rooms.get(i).equals(room)) {
                return room;
            }            

        }

        return null;
    }

    /**
     * rollback the date to prev
     */
    public void rollback(String id) {
        for (int i = 0;rooms.size() > i;i++) {
            String res_id = rooms.get(i).get_rollback_id();
            if (res_id.equals(id)) {
                rooms.get(i).rollback();
            }

        }

    }

    /**
     * we only compare the string to check
     * @return
     */
    @Override
    public  boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        String s = (String)obj;

        return s.equals(name);
    }

     /**
     * canceling with the specific id
     */
    public int cancel(String id) {
        for (Room res_room : rooms) {
            if (res_room.cancel(id) == -1) {
                return -1;
            }
        }

        //means we failed to cancel
        return 0;
    }   


}