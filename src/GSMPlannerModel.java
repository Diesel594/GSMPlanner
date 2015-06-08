import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Класс описания модели поведения
public class GSMPlannerModel {
    private WorkMap workMap;

    public GSMPlannerModel() {
       workMap = new WorkMap();
    }

    public void addHouseToWorkMap (House house) {
        workMap.addHouse(house);
    }

    public void addSectorToWorkMap (Sector sector) {
        workMap.addSector(sector);
    }

    public void addConnectionStationToWorkMap (ConnectionStation connectionStation) {
        workMap.addConnectionStation(connectionStation);
    }

    public void addCellularStationToWorkMap (CellularStation cellularStation){
        workMap.addCellularStation(cellularStation);
    }

    public void addBaseStationToWorkMap (BaseStation baseStation) {
        workMap.addBaseStation(baseStation);
    }

    public double getWorkMapLeftBottomX (){
        return workMap.getLeftBottomX();
    }

    public double getWorkMapLeftBottomY() {
        return workMap.getLeftBottomY();
    }

    public double getWorkMapRightTopX(){
        return workMap.getRightTopX();
    }

    public double getWorkMapRightTopY(){
        return workMap.getRightTopY();
    }

    public void setWorkMapLeftBottomX(double leftBottomX) {
        workMap.setLeftBottomX(leftBottomX);
    }

    public void setWorkMapLeftBottomY(double leftBottomY) {
        workMap.setLeftBottomY(leftBottomY);
    }

    public void setWorkMapRightTopX(double rightTopX){
        workMap.setRightTopX(rightTopX);
    }

    public void setWorkMapRightTopY(double rightTopY){
        workMap.setRightTopY(rightTopY);
    }

    public List<House> parseDataFile(String filePath) {
        List<House> houseList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String dataLine;
            while ((dataLine = bufferedReader.readLine())!= null) {
                //Попытаться распарсить данные
                if (dataLine.contains(" ")) {
                    String[] dataLineParts = dataLine.split(" ");
                    try {
                        double latitude = Double.parseDouble(dataLineParts[0]);
                        double longitude = Double.parseDouble(dataLineParts[1]);
                        int population = Integer.parseInt(dataLineParts[2]);
                        House newHouse = new House(latitude, longitude, population);
                        houseList.add(newHouse);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return houseList;
    }

    public void fillUpMapWithHouses (List<House> houses){
        if (houses.size() > 0) {
            double mapLeftBottomX = houses.get(0).getPosX();
            double mapLeftBottomY = houses.get(0).getPosY();
            double mapRightTopX = houses.get(0).getPosX();;
            double mapRightTopY = houses.get(0).getPosY();

            for (Iterator<House> houseListIterator = houses.iterator(); houseListIterator.hasNext(); ) {
                House house = houseListIterator.next();
                this.addHouseToWorkMap(house);
                //Нахождение крайних координат
                if (house.getPosX() < workMap.getLeftBottomX())
                    workMap.setLeftBottomX(house.getPosX());
                else if (house.getPosX() > workMap.getRightTopX())
                    workMap.setRightTopX(house.getPosX());
                if (house.getPosY() < workMap.getLeftBottomY())
                    workMap.setLeftBottomY(house.getPosY());
                else if (house.getPosY() > workMap.getRightTopY())
                    workMap.setRightTopY(house.getPosY());
            }
        }
    }
}
