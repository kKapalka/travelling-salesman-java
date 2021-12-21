package pl.kkapalka.salesman;

public abstract class TravelRouteMouseEventHandler implements javafx.event.EventHandler<javafx.scene.input.MouseEvent> {
    public int index;

    public TravelRouteMouseEventHandler(int index) {
        this.index = index;
    }
}
