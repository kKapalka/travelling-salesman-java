package pl.kkapalka.salesman;

public class HelperMethods {

    public static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, Object> keyExtractor)
    {
        java.util.Map<Object, Boolean> map = new java.util.concurrent.ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
