public class Sector extends Equipment {
    private CellularStation cellularStation;
    private int direction;
    public Sector(double latitude, double longitude, int direction, int capacity, double price) {
        super(latitude, longitude, capacity, price);
        this.direction = direction;
    }

    public int getDirection(){
        return this.direction;
    }

    public void setCellularStation(CellularStation cellularStation){
        this.cellularStation = cellularStation;
    }

    public CellularStation getCellularStation() {
        return this.cellularStation;
    }
}
