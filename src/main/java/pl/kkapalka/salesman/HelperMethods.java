package pl.kkapalka.salesman;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Function;

public class HelperMethods {

    /**
     * First portion of the city name, from A to Z with a blank as the first element
     */
    private static final String[] NAME_PARTS_FIRST = new String[]{"","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    /**
     * Second portion of the city name, from A to Z
     */
    private static final String[] NAME_PARTS_SECOND = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    /**
     * Method for filtering out elements of the iterable with non-unique single parameter
     * @param keyExtractor unique-checked parameter
     * @param <T> data type
     * @return iterable with unique parameter constraint
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * Method for visual conversion of integer travel cost, to include a single decimal point
     * @param cost cost as integer
     * @return cost divided by 10, with one decimal point, for display purposes
     */
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

    /**
     * Method creating a city name based on its index. Index 0 - name 'A', index 30 - name 'AE' etc
     * @param cityIndex index of the city
     * @return city name
     */
    public static String createCityName(int cityIndex) {
        return NAME_PARTS_FIRST[cityIndex / NAME_PARTS_SECOND.length] + NAME_PARTS_SECOND[cityIndex % NAME_PARTS_SECOND.length];
    }
}
