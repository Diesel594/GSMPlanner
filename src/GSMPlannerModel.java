import sun.plugin2.message.MarkTaintedMessage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Класс описания модели поведения
public class GSMPlannerModel {
    final double EARTH_RADIUS = 6371.0; //Примерный радиус Земли
    private WorkMap workMap;
    //private ArrayList<String[]> tblArray;

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

    public WorkMap getWorkMap(){
        return workMap;
    }

    public List<House> parseDataFile(String filePath) {
        List<House> houseList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String dataLine;
            while ((dataLine = bufferedReader.readLine())!= null) {
                //Попытаться распарсить данные
                String[] dataLineParts = dataLine.split(";");
                if (dataLineParts.length == 3) {
                    try {
                        double latitude = Double.parseDouble(dataLineParts[0]);
                        double longitude = Double.parseDouble(dataLineParts[1]);
                        int population = Integer.parseInt(dataLineParts[2]);
                        House newHouse = new House(latitude, longitude, population);
                        houseList.add(newHouse);
                    } catch (Exception e) {
                        System.out.println("Неверный формат строки с данными.");
                        System.out.println(e.getMessage());
                    }
                }
            }

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Файл не найден");
        } catch (IOException e) {
            e.printStackTrace();
        }


        return houseList;
    }

    public void fillUpMapWithHouses (List<House> houses){
        if (houses.size() > 0) {
            double mapLeftBottomX = houses.get(0).getPosX();
            double mapLeftBottomY = houses.get(0).getPosY();
            double mapRightTopX = houses.get(0).getPosX();
            double mapRightTopY = houses.get(0).getPosY();

            for (House house : houses) {
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

    public double getDistance(double firstPointX, double firstPointY, double secondPointX, double secondPointY){
        return Math.sqrt(Math.pow((secondPointX - firstPointX),2) + Math.pow((secondPointY - firstPointY), 2));
    }

    public double getPlaneX(double lat, double lon) {
        return EARTH_RADIUS * Math.cos(lat) * Math.cos(lon);
    }

    public double getPlaneY(double lat, double lon) {
        return EARTH_RADIUS * Math.cos(lat) * Math.sin(lon);
    }

    /*public TblModel fillUpTable(){
        //ArrayList<String[]> myData;
        TblModel model; // наша модель таблицы
        int countNewRow = 0;
        String[] columnNames = {"Equipment",
                "Latitude",
                "Longitude",
                "Capacity",
                "Price"};
        tblArray = new ArrayList<String[]>();
        tblArray.add(new String[]{"1", "2", "3", "4", "5"});
        tblArray.add(new String[]{"11", "21", "31", "41", "51"});
        tblArray.add(new String[]{"12", "22", "32", "42", "52"});
        tblArray.add(new String[]{"13", "23", "33", "43", "53"});
        model = new TblModel(tblArray);
        return model;
    }*/
}
