/**
 * Created by Gerz on 07.06.2015.
 */
public class CellularStation extends Equipment {
    private ConnectionStation connectionStation;

    public CellularStation(double posX, double posY, int capacity, double price) {
        super(posX, posY, capacity, price);
    }

    public void setConnectionStation(ConnectionStation connectionStation){
        this.connectionStation = connectionStation;
    }

    public ConnectionStation getConnectionStation(){
        return this.connectionStation;
    }
}
