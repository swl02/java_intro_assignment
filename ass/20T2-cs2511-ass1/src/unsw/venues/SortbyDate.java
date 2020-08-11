package unsw.venues;
import java.util.Comparator;

//to implement my sorting function for res_date
public class SortbyDate implements Comparator{

    @Override
    public int compare(Object arg0, Object arg1) {
        Res_Date a = (Res_Date) arg0;
        Res_Date b = (Res_Date) arg1;

        return a.get_start_date().compareTo(b.get_start_date());
    }
    
}