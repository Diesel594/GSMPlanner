/**
 * Created by Gerz on 07.06.2015.
 */
public class ConnectionStation extends Equipment
{
    private BaseStation baseStation;

    public ConnectionStation(double posX, double posY, int capacity, double price) {
        super(posX, posY, capacity, price);
    }

    public void setBaseStation(BaseStation baseStation) {
        this.baseStation = baseStation;
    }

    public BaseStation getBaseStation(){
        return this.baseStation;
    }
}
