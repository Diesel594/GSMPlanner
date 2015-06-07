/**
 * Created by Gerz on 07.06.2015.
 */
public class Sector extends Equipment {
    private CellularStation cellularStation;
    public Sector(double posX, double posY, int capacity, double price) {
        super(posX, posY, capacity, price);
    }

    public void setCellularStation(CellularStation cellularStation){
        this.cellularStation = cellularStation;
    }

    public CellularStation getCellularStation() {
        return this.cellularStation;
    }
}
