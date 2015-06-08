import java.util.ArrayList;

public class WorkMap {
    private ArrayList<House> houses;
    private ArrayList<Sector> sectors;
    private ArrayList<CellularStation> cellularStations;
    private ArrayList<ConnectionStation> connectionStations;
    private ArrayList<BaseStation> baseStations;
    private double leftDownX;
    private double leftDownY;
    private double upRightX;
    private double upRightY;

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

    public double getLeftDownX() {
        return leftDownX;
    }

    public void setLeftDownX(double leftDownX) {
        this.leftDownX = leftDownX;
    }

    public double getLeftDownY() {
        return leftDownY;
    }

    public void setLeftDownY(double leftDownY) {
        this.leftDownY = leftDownY;
    }

    public double getUpRightX() {
        return upRightX;
    }

    public void setUpRightX(double upRightX) {
        this.upRightX = upRightX;
    }

    public double getUpRightY() {
        return upRightY;
    }

    public void setUpRightY(double upRightY) {
        this.upRightY = upRightY;
    }
}
