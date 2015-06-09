import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Класс описания модели поведения
public class GSMPlannerModel {
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
        return workMap.getLeftLongitude();
    }

    public double getWorkMapLeftBottomY() {
        return workMap.getBottomLatitude();
    }

    public double getWorkMapRightTopX(){
        return workMap.getRightLongitude();
    }

    public double getWorkMapRightTopY(){
        return workMap.getTopLatitude();
    }

    public void setWorkMapLeftBottomX(double leftBottomX) {
        workMap.setLeftLongitude(leftBottomX);
    }

    public void setWorkMapLeftBottomY(double leftBottomY) {
        workMap.setBottomLatitude(leftBottomY);
    }

    public void setWorkMapRightTopX(double rightTopX){
        workMap.setRightLongitude(rightTopX);
    }

    public void setWorkMapRightTopY(double rightTopY){
        workMap.setTopLatitude(rightTopY);
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
            double bottomLatitude = houses.get(0).getLatitude();
            double topLatitude = houses.get(0).getLatitude();
            double leftLongitude = houses.get(0).getLongitude();
            double rightLongitude = houses.get(0).getLongitude();

            for (House house : houses) {
                this.addHouseToWorkMap(house);
                //Нахождение крайних координат
                if (house.getLatitude() < bottomLatitude)
                    bottomLatitude = house.getLatitude();
                else if (house.getLatitude() > topLatitude)
                    topLatitude = house.getLatitude();
                if (house.getLongitude() < leftLongitude)
                    leftLongitude = house.getLongitude();
                else if (house.getLongitude() > rightLongitude)
                    rightLongitude = house.getLongitude();
            }
            // Установка границ карты по крайним домам
            workMap.setBottomLatitude(bottomLatitude);
            workMap.setLeftLongitude(leftLongitude);
            workMap.setTopLatitude(topLatitude);
            workMap.setRightLongitude(rightLongitude);
        }
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60.0 * 1.853159616;
        return (dist);
    }

    private void placeSectors() {
        //Перемещаемся вверх увеличивая широту опорной точки соты на 5 км
        //for (double lat = workMap.)
        //Перемещаемся вверх увеличивая широту опорной точки соты на 5 км
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
