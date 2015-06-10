import java.util.ArrayList;

public class WorkMap {
    private ArrayList<House> houses; // Дома на карте
    private ArrayList<Sector> sectors; // Секторы на карте
    private ArrayList<CellularStation> cellularStations; // Сотовые станции на карте
    private ArrayList<ConnectionStation> connectionStations; // Станции связи на карте
    private ArrayList<BaseStation> baseStations; //Базовые станции на карте
    private double leftLongitude; //Левая граница
    private double bottomLatitude; //Нижняя граница
    private double rightLongitude; //Правая граница
    private double topLatitude; //Верхняя грацниа

    public WorkMap() {
        houses = new ArrayList<>();
        sectors = new ArrayList<>();
        cellularStations = new ArrayList<>();
        connectionStations = new ArrayList<>();
        baseStations = new ArrayList<>();
        leftLongitude = bottomLatitude = rightLongitude = topLatitude = 0.0;
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

    public double getLeftLongitude() {
        return leftLongitude;
    }

    public void setLeftLongitude(double leftLongitude) {
        this.leftLongitude = leftLongitude;
    }

    public double getBottomLatitude() {
        return bottomLatitude;
    }

    public void setBottomLatitude(double bottomLatitude) {
        this.bottomLatitude = bottomLatitude;
    }

    public double getRightLongitude() {
        return rightLongitude;
    }

    public void setRightLongitude(double rightLongitude) {
        this.rightLongitude = rightLongitude;
    }

    public double getTopLatitude() {
        return topLatitude;
    }

    public void setTopLatitude(double topLatitude) {
        this.topLatitude = topLatitude;
    }
}
