package pl.kkapalka.salesman;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Function;

public class HelperMethods {

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    public static String convertCostToString(Integer cost) {
        String travelCostAsString = cost.toString();
        if(cost == 0) {
            travelCostAsString = "0.0";
        } else if (cost < 10) {
            travelCostAsString = "0."+cost;
        } else {
            travelCostAsString = travelCostAsString.substring(0, travelCostAsString.length() - 1) + "." + travelCostAsString.substring(travelCostAsString.length() - 1);
        }
        return travelCostAsString;
    }
}
