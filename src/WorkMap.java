import java.util.ArrayList;

public class WorkMap {
    private ArrayList<House> houses;
    private ArrayList<Sector> sectors;
    private ArrayList<CellularStation> cellularStations;
    private ArrayList<ConnectionStation> connectionStations;
    private ArrayList<BaseStation> baseStations;
    private double leftBottomX;
    private double leftBottomY;
    private double rightTopX;
    private double rightTopY;

    public WorkMap() {
        leftBottomX = leftBottomY = rightTopX = rightTopY = 0.0;
    }

    public ArrayList<House> getHouses() {
        return houses;
    }

    public void addHouse(House house) {
        this.houses.add(house);
    }

    public ArrayList<Sector> getSectors() {
        return sectors;
    }

    public void addSector(Sector sector) {
        this.sectors.add(sector);
    }

    public ArrayList<CellularStation> getCellularStations() {
        return cellularStations;
    }

    public void addCellularStation(CellularStation cellularStation) {
        this.cellularStations.add(cellularStation);
    }

    public ArrayList<ConnectionStation> getConnectionStations() {
        return connectionStations;
    }

    public void addConnectionStation (ConnectionStation connectionStation) {
        this.connectionStations.add(connectionStation);
    }

    public ArrayList<BaseStation> getBaseStations() {
        return baseStations;
    }

    public void addBaseStation(BaseStation baseStation) {
        this.baseStations.add(baseStation);
    }

    public double getLeftBottomX() {
        return leftBottomX;
    }

    public void setLeftBottomX(double leftBottomX) {
        this.leftBottomX = leftBottomX;
    }

    public double getLeftBottomY() {
        return leftBottomY;
    }

    public void setLeftBottomY(double leftBottomY) {
        this.leftBottomY = leftBottomY;
    }

    public double getRightTopX() {
        return rightTopX;
    }

    public void setRightTopX(double rightTopX) {
        this.rightTopX = rightTopX;
    }

    public double getRightTopY() {
        return rightTopY;
    }

    public void setRightTopY(double rightTopY) {
        this.rightTopY = rightTopY;
    }
}
