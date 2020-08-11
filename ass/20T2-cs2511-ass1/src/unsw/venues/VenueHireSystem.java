/**
 *
 */
package unsw.venues;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Venue Hire System for COMP2511.
 *
 * A basic prototype to serve as the "back-end" of a venue hire system. Input
 * and output is in JSON format.
 *
 * @author Robert Clifton-Everest
 *
 */
public class VenueHireSystem {

    /**
     * Constructs a venue hire system. Initially, the system contains no venues,
     * rooms, or bookings.
     */
    //contain all venues in the system
    private ArrayList<Venue> venues; 
    private ArrayList<Reservation> customer;

    public VenueHireSystem() {

        //generate empty venue
        venues = new ArrayList<Venue>();
        customer = new  ArrayList<Reservation>();

    }

    //return index for venue
    //-1 if false    
    /**
     * 
     * @param name
     * @return return index for venue ,-1 if false  
     */
    public Venue FindVenue(String name) {
        for (Venue venue : venues) {
            if (venue.equals(name)) {
                return venue;    
            } 
        }

        return null;
    }


    /**
     * search for a reservation index with a specific id
     * @param id
     * @return return the index if true ,return -1 if not found
     */
    public int findReservation_index(String id) {
        for (int i = 0;customer.size() > i;i++) {
            if (customer.get(i).equals(id)) {
                return i;
            }

        }
        return -1;
    }

    public void processCommand(JSONObject json) {
        switch (json.getString("command")) {

        case "room":
            String venue = json.getString("venue");
            String room = json.getString("room");
            String size = json.getString("size");
            addRoom(venue, room, size);
            break;

        case "request":
            String id = json.getString("id");
            LocalDate start = LocalDate.parse(json.getString("start"));
            LocalDate end = LocalDate.parse(json.getString("end"));
            int small = json.getInt("small");
            int medium = json.getInt("medium");
            int large = json.getInt("large");

            JSONObject result = request(id, start, end, small, medium, large);

            System.out.println(result.toString(2));
            break;
        
        case "cancel":
            id = json.getString("id");
            cancel(id);
            break;
        
        case "change":
            id = json.getString("id");
            start = LocalDate.parse(json.getString("start"));
            end = LocalDate.parse(json.getString("end"));
            small = json.getInt("small");
            medium = json.getInt("medium");
            large = json.getInt("large");
            result = change(id, start, end, small, medium, large);
            System.out.println(result.toString(2));        
            break;
        
        case "list":

            JSONArray result_list = list(json.getString("venue"));
            System.out.println(result_list.toString(2));    
        break;
        
        }
    }

    public void addRoom(String venue, String room, String size) {


        //need to know whether the venue exist already or no
        Venue venue_loc = FindVenue(venue); 

        //make an object called room
        Room new_room = new Room(size, room);

        //means venue does not exist 
        //we need to allocate venue
        if (venue_loc == null) {
            Venue new_venue = new Venue(venue);
            //add room 

            //add the room to the venue
            new_venue.addRoom(new_room);

            //add the venue to the system
            venues.add(new_venue);            
        } else {
            //add room 
            venue_loc.addRoom(new_room);

        }

    }

    public JSONObject request(String id, LocalDate start, LocalDate end,
        int small, int medium, int large) {
        JSONObject result = new JSONObject();


        //no duplication of id allowed
        int dup_index = findReservation_index(id);
        if (dup_index != -1) {
            result.put("status", "rejected");
            return result;                
        }

        //need to check whether we have the venue for the request 
        Venue req_venue  = Reservation.check_req(small, medium, large, venues, start, end,id);
        if (req_venue != null ) {
            result.put("status", "success");
            //if it is success we make a reservation
            Reservation req = new Reservation(id, start, end, req_venue);

            //add it to the system
            customer.add(req);

        } else {

            result.put("status", "rejected");
            return result;            
        }
        
        //perform the request by changing n of room in venue and etc

        result.put("venue", req_venue.getname());

        JSONArray rooms = new JSONArray();

        for (Room room:req_venue.getrooms()) {
            rooms.put(room.getname());
        }


        result.put("rooms", rooms);


        return result;
    }

    public JSONObject change(String id, LocalDate start, LocalDate end,
        int small, int medium, int large) {
        JSONObject result = new JSONObject();
        
        //change is just literally cancel follow by request
        
        //but we need previous version for rollback purpose
        //shallow copy would not work
        //work around by introducing new attribute called rollback date
        ArrayList<Reservation> history_customer = new ArrayList<Reservation>(customer); 

        int transaction = cancel(id);
        if (transaction == -1)  {
            result.put("status", "rejected");
        } else {
            //the request might fail.
            result = request(id, start, end, small, medium, large);

            //to prevent unsuccessful transaction, we  need to rollback
            if (result.get("status").equals("rejected")) {


                //need to change both customer and venue
                customer = history_customer;

                String venue_name = new String();
                for (int i = 0;customer.size() > i;i++) {
                    if (customer.get(i).getid().equals(id)) {
                        venue_name = customer.get(i).getvenue_name();
                    }
                }


                int venue_index = search_venue_name(venue_name);

                if (venue_index == -1) {
                    return result;
                }

                venues.get(venue_index).rollback(id);
        
            }


        }


        
        return result;
    }


    /**
     * 
     * @return -1 signify that the cancelation failed 0 for success
     */
    public int cancel(String id) {
        
        //need to check whether the id exist that we can actually cancel the reservation
        int cancel_index = findReservation_index(id);

        //meaning the id does not exist
        if (cancel_index == -1) {
            return -1;
        }

        //need to find the venue

        String name = customer.get(cancel_index).getvenue_name();

        int venue_index = search_venue_name(name);

        if (venue_index == -1) {
            return -1;
        }

        //we need to remove from venue from room date

        // we keep a copy of it first
        // to prevent anything terrible       
        Venue back_up = venues.get(venue_index);
        
        int transaction = venues.get(venue_index).cancel(id);

        if (transaction == -1) {

            //undo everything
            venues.remove(venue_index);
            venues.add(back_up);
            return -1;
        }  
        //remove from the system
        //remove from the reservation 
        customer.remove(cancel_index);

        return 0;
    }



    /**
     * work only for venues
     * @return -1 if we could not find it , the index if true 
     */
    public int search_venue_name(String name) {
        int i = 0;
        while (venues.size() > i) {
            if (venues.get(i).getname().equals(name)) {
                //from the venue we need to remove the date from the room.
                return i;
            }
            i++;
        }
        return -1;
    }

    public JSONArray list(String venue) {
        JSONArray result = new JSONArray();

        Venue found_venue = FindVenue(venue);

        for (int j = 0;found_venue.get_n_room() > j;j++) {
            JSONObject room_object = new JSONObject();
            JSONArray reservations = new JSONArray();               
            Room found_room = found_venue.getrooms().get(j);
            room_object.put("room", found_room.getname());

            for (int k = 0;found_room.get_res_date_list_size() > k;k++) {
                JSONObject obj = new JSONObject();
                obj.put("id", found_room.getdate().get(k).getid());
                obj.put("start",found_room.getdate().get(k).get_start_date());
                obj.put("end",found_room.getdate().get(k).get_end_date());
                reservations.put(obj);

            }

            room_object.put("reservations",reservations);
            result.put(room_object);
        }

        return result;
    }


    public static void main(String[] args) {
        VenueHireSystem system = new VenueHireSystem();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.trim().equals("")) {
                JSONObject command = new JSONObject(line);
                system.processCommand(command);
            }
        }
        sc.close();
    }

}
