import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math.*;

//Класс описания модели поведения
public class GSMPlannerModel {
    final static double EARTH_RADIUS = 6371.0; //Примерный радиус Земли
    final int SECTOR_CAPACITY = 100;
    final double SECTOR_PRICE = 1000;

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

    /*private double getX(double latitude, double longitude) {
        return GSMPlannerModel.R * Math.cos(latitude) * Math.cos(longitude);
    }

    public double getY(double latitude, double longitude) {
        return GSMPlannerModel.R * Math.cos(latitude) * Math.sin(longitude);
    }

    public double getZ(double latitude) {
        return GSMPlannerModel.R * Math.sin(latitude);
    }

    public double getLatitude(double z) {
        return Math.asin(z / R);
    }

    public double getLongitude (double x, double y) {
        return Math.atan2(y, x);
    }*/

    // Получает в градусах, считает в радианах, возвращает расстояние в КМ
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60.0 * 1.853159616;
        return (dist);
    }

    public void placeSectors() {
        // В масштабах Земли размерами города можно пренебречь. Поэтому мы высчитываем длину градуса широты и долготы один раз
        // Перемещаемся вверх увеличивая широту опорной точки соты на 5 км
        double latitude5KmLength = getLatitudeKilometerLength(workMap.getBottomLatitude()) * 5.0; // Вычисляем значение протяженности 5км в градусах по широте
        double longitude5KmLength = getLongitudeKilometerLength(workMap.getLeftLongitude()) * 5.0; // Вычисляем значение протяженности 5км в градусах по долготе

        // начиная с нижней широты, мы поднимаемся к верхней с шагом в 5 км в градусах
        for (double latitude = workMap.getBottomLatitude(); latitude < workMap.getTopLatitude(); latitude = latitude + longitude5KmLength) {
            // начиная с левой долготы, двигаемся вправо с шагом 5 км в градусах
            for (double longitude = workMap.getLeftLongitude(); longitude < workMap.getRightLongitude(); longitude = longitude + latitude5KmLength) {

                // поиск оптимальной установки сектора только после подтверждения наличия домов в квадрате
                if(isHousesInArea(latitude, longitude, latitude5KmLength, longitude5KmLength)) {
                    // устанавливаем сектор 4-мя разными способами, определяя оптимальный по покрытию
                    // ориентир установки - номер четверти круга (как в сетке декартовых координат)
                    int maxCustomers = 0; // переменная, с помощью которой выясним самую оптимальную четверть
                    int bestQuarter = 0;

                    for (int quarter = 1; quarter <= 4; quarter++) {
                        int tmpCustomers = getPossibleSectorCustomersInArea(latitude, longitude,latitude5KmLength,longitude5KmLength,quarter);
                        if (tmpCustomers > maxCustomers){
                            maxCustomers = tmpCustomers;
                            bestQuarter = quarter;
                        }
                    }

                    // установка в карту лучшего решения с указанием четверти
                    workMap.addSector(new Sector(latitude, longitude, bestQuarter, SECTOR_CAPACITY, SECTOR_PRICE));

                    // определение необходимости установки зеркального сектора
                    int customersInArea = getCustomersInArea(latitude, longitude, latitude5KmLength, longitude5KmLength);
                    int customersInSector = getPossibleSectorCustomersInArea(latitude, longitude, latitude5KmLength, longitude5KmLength, bestQuarter);
                    if (customersInArea > customersInSector) {
                        // требуется установка противолежащего сектора для покрытия всего квадрата
                        // выбор координат и направления для зеркального сектора
                        int quarter = 0;
                        switch (bestQuarter){
                            case 1:
                                latitude = latitude + longitude5KmLength * 5;
                                longitude = longitude + latitude5KmLength * 5;
                                quarter = 3;
                                break;
                            case 2:
                                latitude = latitude - longitude5KmLength * 5;
                                longitude = longitude + latitude5KmLength * 5;
                                quarter = 4;
                                break;
                            case 3:
                                latitude = latitude - longitude5KmLength * 5;
                                longitude = longitude - latitude5KmLength * 5;
                                quarter = 1;
                                break;
                            case 4:
                                latitude = latitude + longitude5KmLength * 5;
                                longitude = longitude - latitude5KmLength * 5;
                                quarter = 2;
                                break;
                        }
                        workMap.addSector(new Sector(latitude, longitude, quarter, SECTOR_CAPACITY, SECTOR_PRICE));
                    }
                }
            }
        }
    }

    private void placeConnectionStations() {
        //TODO: написать фунццию установки станций связи
    }

    private void placeCellularStations() {
        //TODO: написать функцию установки сотовых станций
    }

    private void placeBaseStations() {
        //TODO: написать функцию установки базовых станцию
    }

    private void generateMap() {
        //TODO: попробовать сгенерировать карту с нанесенными на ней станциямим и секторами
    }

    //TODO:Проверить правильность вычисления LatitudeKilometerLength
    private double getLatitudeKilometerLength (double latitude) {
        /*double rlat = deg2rad(latitude); // Переводим широту в радианы
        double metersInDegree = 111132.92 - 559.82*Math.cos(2 * rlat) + 1.175*Math.cos(4*rlat); // Вычисляем количество метров в градусе широты
        return 1000/metersInDegree; // Вычисляем значение 1000 метров в градусах*/
        //Из-за неточности в вичислениях по формулам длину одного градуса меридиана возьмем const = 111.3
        return 1/111.3;
    }

    //TODO: проверить правильность вычисления longitudeKilometerLength
    private double getLongitudeKilometerLength (double longitude) {
        double rlon = deg2rad(longitude);
        //double metersInDegree = 111412.84 * Math.cos(rlon) - 93.5 * Math.cos(3 * rlon); // Вычисляем количество метров в градусе долготы
        //return 1000/metersInDegree; // Вычисляем значение 1000 метров в градусах
        return 1/111.3*Math.cos(rlon);
    }

    boolean isHousesInArea(double latitude, double longitude, double latitude5KmLength, double longitude5KmLength) {
        // перебираем дома с целью найти дом в заданом квадарате
        for (House house : workMap.getHouses()) {
            if (isInsideArea(latitude, longitude, latitude5KmLength, longitude5KmLength, house.getLatitude(), house.getLongitude()))
                return true;
        }
        return false;
    }

    private boolean isInsideArea(double latitude, double longitude, double latitudeKilometerLength, double longitudeKilometerLength, double objectLatitude, double objectLongitude) {
        return (objectLatitude >= latitude) &&
                (objectLatitude <= (latitude + (longitudeKilometerLength * 5))) &&
                (objectLongitude >= longitude) &&
                (objectLongitude <= (longitude + (latitudeKilometerLength * 5)));
    }


    public double getPlaneX(double latitude, double longitude) {
        return EARTH_RADIUS * Math.cos(latitude) * Math.cos(longitude);
    }

    public double getPlaneY(double latitude, double longitude) {
        return EARTH_RADIUS * Math.cos(latitude) * Math.sin(longitude);
    }

    private int getCustomersInArea(
            double latitude,
            double longitude,
            double latitudeKilometerLength,
            double longitudeKilometerLength) {
        int customersCount = 0;
        //перебираем дома в секторе
        for (House house : workMap.getHouses()) {
            if (isInsideArea(latitude, longitude, latitudeKilometerLength, longitudeKilometerLength, house.getLatitude(), house.getLongitude()))
                customersCount = customersCount + house.getPopulation();
        }
        return customersCount;
    }

    private int getPossibleSectorCustomersInArea(
            double sectorLatitude,
            double sectorLongitude,
            double latitude5KmLength,
            double longitude5KmLength,
            int direction) {
        double sectorStartLatitude;
        double sectorStartLongitude;
        double sectorEndLatitude;
        double sectorEndLongitude;
        int customersCount = 0;

        switch (direction) {
            case 1: sectorStartLongitude = sectorLongitude + longitude5KmLength * 1;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * 0;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * 0;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 1;
                    break;

            case 2: sectorStartLongitude = sectorLongitude + longitude5KmLength * 0;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * 1;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * -1;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 0;
                    break;

            case 3: sectorStartLongitude = sectorLongitude + longitude5KmLength * 5 * -1;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * 5 * 0;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * 5 * 0;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 5 * 1;
                    break;

            case 4: sectorStartLongitude = sectorLongitude + longitude5KmLength * 5 * 0;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * 5 * -1;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * 5 * 1;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 5 * 0;
                    break;

            default:
                return -1;
        }
        //перебираем дома в секторе
        for (House house : workMap.getHouses()) {
            /*boolean isInside = isInsideSector(
                    deg2rad(house.getLatitude()), deg2rad(house.getLongitude()),
                    deg2rad(sectorLatitude), deg2rad(sectorLongitude),
                    deg2rad(sectorStartLatitude), deg2rad(sectorStartLongitude),
                    deg2rad(sectorEndLatitude), deg2rad(sectorEndLongitude),
                    Math.max(deg2rad(latitudeKilometerLength), deg2rad(longitudeKilometerLength)));*/
            boolean isInside = isInsideSector(
                    house.getLatitude(), house.getLongitude(),
                    sectorLatitude, sectorLongitude,
                    sectorStartLatitude, sectorStartLongitude,
                    sectorEndLatitude,sectorEndLongitude,
                    Math.max(latitude5KmLength,longitude5KmLength));
            if (isInside)
                    customersCount = customersCount + house.getPopulation();
        }
        return customersCount;
    }

    private boolean isInsideSector(double objectLatitude, double objectLongitude,
                                   double sectorLatitude, double sectorLongitude,
                                   double sectorStartLatitude, double sectorStartLongitude,
                                   double sectorEndLatitude, double sectorEndLongitude,
                                   double radius) {

        if ((objectLatitude == sectorLatitude) && (objectLongitude == sectorLongitude))
            return true;

        double relPointLatitude = objectLatitude - sectorLatitude;
        double relPointLongitude = objectLongitude - sectorLongitude;

//        return !areClockwise(sectorStartLongitude, sectorStartLatitude, objectLongitude, objectLatitude) &&
//                areClockwise(sectorEndLongitude, sectorEndLatitude, objectLongitude, objectLatitude) &&
//                isWithinRadius(relPointLongitude, relPointLatitude, radius*radius);
        boolean startClockwise =areClockwise(sectorStartLatitude, sectorStartLongitude, relPointLatitude, relPointLongitude);
        boolean endClockwise = areClockwise(sectorEndLatitude, sectorEndLongitude, relPointLatitude, relPointLongitude);
        boolean withinRadius = isInCircle(objectLatitude, objectLongitude, sectorLatitude, sectorLongitude, radius);

        return !startClockwise && endClockwise && withinRadius;
        /*return !areClockwise(sectorStartLatitude, sectorStartLongitude, relPointLatitude, relPointLongitude) &&
                areClockwise(sectorEndLatitude, sectorEndLongitude, relPointLatitude, relPointLongitude) &&
                isWithinRadius(relPointLatitude, relPointLongitude, radius*radius);*/
    }

    private boolean isInCircle(double objLatitude, double objLongitude,
                               double sectorLatitude, double sectorLongitude,
                               double radius) {
        return (Math.pow((objLatitude - sectorLatitude),2) - Math.pow((objLongitude - sectorLongitude),2) < Math.pow(radius,2));
    }


    /*private boolean isInsideSector(double objectLatitude, double objectLongitude,
                                   double sectorLatitude, double sectorLongitude,
                                   double sectorStartLatitude, double sectorStartLongitude,
                                   double sectorEndLatitude, double sectorEndLongitude,
                                   double radius) {

        if ((objectLatitude == sectorLatitude) && (objectLongitude == sectorLongitude))
            return true;

        double relPointLatitude = objectLatitude - sectorLatitude;
        double relPointLongitude = objectLongitude - sectorLongitude;

        return !areClockwise(sectorStartLongitude, sectorStartLatitude, relPointLongitude, relPointLatitude) &&
                areClockwise(sectorEndLongitude, sectorEndLatitude, relPointLongitude, relPointLatitude) &&
                isWithinRadius(relPointLongitude, relPointLatitude, radius*radius);

        *//*return !areClockwise(sectorStartLatitude, sectorStartLongitude, relPointLatitude, relPointLongitude) &&
                areClockwise(sectorEndLatitude, sectorEndLongitude, relPointLatitude, relPointLongitude) &&
                isWithinRadius(relPointLatitude, relPointLongitude, radius*radius);*//*
    }*/

    private boolean isWithinRadius(double vx, double vy, double radius) {
        double radiusSquared = radius*radius;
        return (vx*vx + vy*vy) <= radiusSquared;
    }

    private boolean areClockwise(double v1x, double v1y, double v2x, double v2y) {
        return ((- v1x)*v2y + v1y*v2x) > 0;
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
