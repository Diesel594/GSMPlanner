public class CellularStation extends Equipment {
    private BaseStation baseStation;

    public CellularStation(double posX, double posY, int capacity, double price) {
        super(posX, posY, capacity, price);
    }

    public BaseStation getBaseStation() {
        return baseStation;
    }

    public void setBaseStation(BaseStation baseStation) {
        this.baseStation = baseStation;
    }
}
