public class ConnectionStation extends Equipment
{
    private  CellularStation cellularStation;

    public ConnectionStation(double posX, double posY, int capacity, double price) {
        super(posX, posY, capacity, price);
    }

    public CellularStation getCellularStation() {
        return cellularStation;
    }

    public void setCellularStation(CellularStation cellularStation) {
        this.cellularStation = cellularStation;
    }

}
