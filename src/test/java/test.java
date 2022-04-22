import java.time.ZoneId;
import java.util.Iterator;
import java.util.Set;

public class test {
    public static void main(String[] args) {
        Set<String> zones = ZoneId.getAvailableZoneIds();
        Iterator<String> zonesI = zones.iterator();
        while (zonesI.hasNext()){
            String a = zonesI.next();
            if(a.contains("Asia")){
                System.out.println(a);
            }

        }
    }
}
