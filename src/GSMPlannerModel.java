import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Класс описания модели поведения
public class GSMPlannerModel {
    final static double EARTH_RADIUS = 6371.0; //Примерный радиус Земли
    final double WIRE_PRICE = 100;
    final double WIREKESS_PRICE = 10;
    final int SECTOR_CAPACITY = 100;
    final double SECTOR_PRICE = 1000;
    final int CONNECTION_STATION_CAPACITY = 1000;
    final double CONNECTION_STATION_PRICE = 10000;
    final int CELLULAR_STATION_CAPACITY = 5000;
    final double CELLULAR_STATION_PRICE = 100000;


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
                    int connectedCustomers = 0;
                    while (connectedCustomers < maxCustomers) {
                        int tmpCustomers = 0;
                        if (maxCustomers - connectedCustomers > SECTOR_CAPACITY)
                            tmpCustomers = SECTOR_CAPACITY;
                        else
                            tmpCustomers = maxCustomers - connectedCustomers;
                        workMap.addSector(new Sector(longitude, latitude, bestQuarter, SECTOR_CAPACITY, SECTOR_PRICE, tmpCustomers));
                        connectedCustomers = connectedCustomers + tmpCustomers;
                    }

                    // определение необходимости установки дополнительного сектора


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
                        int sectorCustomers = getPossibleSectorCustomersInArea(latitude, longitude, latitude5KmLength, longitude5KmLength, quarter);
                        int servedCustomers = 0;
                        while (sectorCustomers >= 100) {
                            if (sectorCustomers >= 100)
                                servedCustomers = 100;
                            else
                                servedCustomers = sectorCustomers % 100;
                            sectorCustomers = servedCustomers - servedCustomers;
                            workMap.addSector(new Sector(latitude, longitude, quarter, SECTOR_CAPACITY, SECTOR_PRICE, servedCustomers));
                        }
                    }
                }
            }
        }
    }

    public void placeConnectionStations() {
        // Счстаем, возможно-ли окучить всех клиентов одной станцией и сколько это будет стоить
        // Емкость 1000 соединений
        // переменные для вычисления средней точки и цены подключения к ней
        double sumX = 0;
        double sumY = 0;
        double stationX = 0;
        double stationY = 0;
        int sectorCount = 0;
        double totalCost = 0;
        int customers = 0;
        // перебираем все секторы и считаем количество подключений на секторе
        for (Sector sector: workMap.getSectors()){
            sumX = sumX + sector.getLongitude();
            sumY = sumY + sector.getLatitude();
            customers = customers + sector.getCustomers();
            sectorCount++;
        }
        totalCost = 0;
        // складываем все координаты и делим на количество домов. Получаем среднюю точку для установки станции
        stationX = sumX / sectorCount;
        stationY = sumY / sectorCount;
        // Перебираем секторы и считаем стоимость подключения к одной станции
        //totalCost = totalCost + CONNECTION_STATION_PRICE;
        for (Sector sector : workMap.getSectors()) {
            double distanceConnStationSector = getDistance(stationY, stationX, sector.getLatitude(), sector.getLongitude());
            totalCost = totalCost + distanceConnStationSector * WIRE_PRICE;
        }
        //Считаем стоимость станций, нобходимых для установки
        totalCost = totalCost + (customers / (int)CONNECTION_STATION_CAPACITY) * CONNECTION_STATION_PRICE;
        if (customers % CONNECTION_STATION_CAPACITY != 0) totalCost = totalCost + CONNECTION_STATION_PRICE;

        //посчитать стоимость альтернативной установки
        int secondTotalCost = 0;
        // разбиваем на два треугольника
        // считаем стоимость установки двух станций.
        double priceTriangle1 = getPriceConnectionStationInTriangle(
                workMap.getLeftLongitude(), workMap.getBottomLatitude(),
                workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude());
        double priceTriangle2 = getPriceConnectionStationInTriangle(
                workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude());

        // В случае, если стоймость установки одной станции связи меньше чем двух, то ставить одну
        if (totalCost < priceTriangle1 + priceTriangle2){
            ConnectionStation connectionStation = new ConnectionStation(stationX, stationY, CONNECTION_STATION_CAPACITY, CONNECTION_STATION_PRICE);
            workMap.addConnectionStation(connectionStation);
            return;
        }

        // Ставим две станции
        placeSingleTriangleConnectionStation(workMap.getLeftLongitude(), workMap.getBottomLatitude(),
                workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude(), priceTriangle1);
        placeSingleTriangleConnectionStation(workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude(), priceTriangle2);


    }

    private void placeSingleTriangleConnectionStation(double x1, double y1, double x2, double y2, double x3, double y3, double currentPrice) {
        // решить, может две станции обойдутся дешевле
        if (getDoubleTriangleConnectionStationPrice(x1, y1, x2, y2, x3, y3) < currentPrice) {
             placeDoubleTriangleConnectionStations(x1, y1, x2, y2, x3, y3, getDoubleTriangleConnectionStationPrice(x1, y1, x2, y2, x3, y3));
        } else {

            // установка станции
            double middleX = (x1 + x2 + x3) / 3;
            double middleY = (y1 + y2 + y3) / 3;
            int sectorCustomers = getPossibleSectorCustomersInTrianlge(x1, y1, x2, y2, x3, y3);
            int connectedCustomers = 0;

            // необходимо опосредовано, через секторы, подключить всех пользователей в треугольнике
            while (connectedCustomers < sectorCustomers) {
                ConnectionStation connectionStation = new ConnectionStation(middleX, middleY, CONNECTION_STATION_CAPACITY, CONNECTION_STATION_PRICE);
                //перебираем секторы, которые планируем подключить
                for (Sector sector : workMap.getSectors()) {
                    // сектор находится в треугольнике
                    if (isInTriangle(x1, y1, x2, y2, x3, y3, sector.getLongitude(), sector.getLatitude())) {
                        //сектор ни к кому не подключен
                        if (sector.getConnectionStation() == null) {
                            sector.setConnectionStation(connectionStation);
                            connectedCustomers = connectedCustomers + sector.getCustomers();
                        }
                    }
                }
                workMap.addConnectionStation(connectionStation);
            }
        }
    }

    private void placeDoubleTriangleConnectionStations(double x1, double y1, double x2, double y2, double x3, double y3, double currentPrice){
        // делим существующий треугольник  по длинной стороне
        if (getDistance(x1, y1, x2, y2) > getDistance(x1, y1, x3, y3) && getDistance(x1, y1, x2, y2) > getDistance(x2, y2, x3, y3)) {
            // делим сторону x1y1-x2y2
            placeSingleTriangleConnectionStation(x1, y1, (x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x3, y3, currentPrice);
            placeSingleTriangleConnectionStation((x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x2, y2, x3, y3, currentPrice);
        }else if (getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x2, y2) && getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x3, y3)) {
            // делим сторону x2y2-x3y3
            placeSingleTriangleConnectionStation(x1, y1, x2, y2, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2), currentPrice);
            placeSingleTriangleConnectionStation(x1, y1, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2), x3, y3, currentPrice);
        }else{
            // деллим сторону x1y1-x3y3
            placeSingleTriangleConnectionStation(x1, y1, x2, y2, (x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2), currentPrice);
            placeSingleTriangleConnectionStation((x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2), x2, y2, x3, y3, currentPrice);
        }
    }

    private void placeDoubleTriangleCellularStations(double x1, double y1, double x2, double y2, double x3, double y3, double currentPrice){
        // делим существующий треугольник  по длинной стороне
        if (getDistance(x1, y1, x2, y2) > getDistance(x1, y1, x3, y3) && getDistance(x1, y1, x2, y2) > getDistance(x2, y2, x3, y3)) {
            // делим сторону x1y1-x2y2
            placeSingleTriangleCellularStation(x1, y1, (x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x3, y3, currentPrice);
            placeSingleTriangleCellularStation((x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x2, y2, x3, y3, currentPrice);
        }else if (getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x2, y2) && getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x3, y3)) {
            // делим сторону x2y2-x3y3
            placeSingleTriangleCellularStation(x1, y1, x2, y2, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2), currentPrice);
            placeSingleTriangleCellularStation(x1, y1, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2), x3, y3, currentPrice);
        }else{
            // деллим сторону x1y1-x3y3
            placeSingleTriangleCellularStation(x1, y1, x2, y2, (x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2), currentPrice);
            placeSingleTriangleCellularStation((x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2), x2, y2, x3, y3, currentPrice);
        }
    }

    private void placeSingleTriangleCellularStation(double x1, double y1, double x2, double y2, double x3, double y3, double currentPrice) {
        // решить, может две станции обойдутся дешевле
        if (getDoubleTriangleCellularStationPrice(x1, y1, x2, y2, x3, y3) < currentPrice) {
            placeDoubleTriangleCellularStations(x1, y1, x2, y2, x3, y3, getDoubleTriangleCellularStationPrice(x1, y1, x2, y2, x3, y3));
        } else {

            // установка станции
            double middleX = (x1 + x2 + x3) / 3;
            double middleY = (y1 + y2 + y3) / 3;
            int connectionCustomers = getPossibleConnectionCustomersInTrianlge(x1, y1, x2, y2, x3, y3);
            int connectedCustomers = 0;

            // необходимо опосредовано, через секторы, подключить всех пользователей в треугольнике
            while (connectedCustomers < connectionCustomers) {
                CellularStation cellularStation = new CellularStation(middleX, middleY, CELLULAR_STATION_CAPACITY, CELLULAR_STATION_PRICE);
                //перебираем станции связи, которые планируем подключить
                for (ConnectionStation connectionStation : workMap.getConnectionStations()) {
                    // станция связи находится в треугольнике
                    if (isInTriangle(x1, y1, x2, y2, x3, y3, connectionStation.getLongitude(), connectionStation.getLatitude())) {
                        // станция связи ни к кому не подключена
                        if (connectionStation.getCellularStation()== null) {
                            connectionStation.setCellularStation(cellularStation);
                            for (Sector sector : workMap.getSectors()) {
                                if (sector.getConnectionStation() == connectionStation)
                                    connectedCustomers = connectedCustomers + sector.getCustomers();
                            }
                        }
                    }
                }
                workMap.addCellularStation(cellularStation);
            }
        }
    }

    // подсчет стоимости установки в треугольнике
    double getPriceConnectionStationInTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        double resultPrice = 0;
        double sumX = 0;
        double sumY = 0;
        double sectorCount = 0;
        double stationX = 0;
        double stationY = 0;

        //вычисление средней точки
        for (Sector sector: workMap.getSectors()){
            if (isInTriangle(x1,y1,x2,y2,x3,y3,sector.getLongitude(),sector.getLatitude())) {
                sumX = sumX + sector.getLongitude();
                sumY = sumY + sector.getLatitude();
                sectorCount++;
            }
        }
        stationX = sumX / sectorCount;
        stationY = sumY / sectorCount;

        //подсчет количества абонентов
        int customers = 0;
        for (Sector sector: workMap.getSectors()){
            if (isInTriangle(x1, y1, x2, y2, x3, y3, sector.getLongitude(), sector.getLatitude())) {
                customers = customers + sector.getCustomers();
            }
        }
        //подключение всех абонентов в треугольнике
        int connectedCustomers = 0;
        //подсчет стоимости всех станций на данной точке
        while (connectedCustomers < customers) {
            resultPrice = resultPrice + CONNECTION_STATION_PRICE;
            connectedCustomers = connectedCustomers + CONNECTION_STATION_CAPACITY;
        }
        //подсчет стоимости подключений секторов к станциям
        for (Sector sector: workMap.getSectors()){
            if (isInTriangle(x1, y1, x2, y2, x3, y3, stationX, stationY)) {
                resultPrice = resultPrice + getDistance(stationX, stationY, sector.getLongitude(), sector.getLatitude());
            }
        }

        return resultPrice;
    }

    // подсчет стоимости двухзонной установки
    double getDoubleTriangleConnectionStationPrice(double x1, double y1, double x2, double y2, double x3, double y3) {
        double resultPrice = 0;
        // делим существующий треугольник  по длинной стороне
        if (getDistance(x1, y1, x2, y2) > getDistance(x1, y1, x3, y3) && getDistance(x1, y1, x2, y2) > getDistance(x2, y2, x3, y3)) {
            // делим сторону x1y1-x2y2
            double priceTriangle1 = getPriceConnectionStationInTriangle(x1, y1, (x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x3, y3);
            double priceTriangle2 = getPriceConnectionStationInTriangle((x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x2, y2, x3, y3);
            resultPrice = priceTriangle1 + priceTriangle2;
        }else if (getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x2, y2) && getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x3, y3)) {
            // делим сторону x2y2-x3y3
            double priceTriangle1 = getPriceConnectionStationInTriangle(x1, y1, x2, y2, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2));
            double priceTriangle2 = getPriceConnectionStationInTriangle(x1, y1, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2), x3, y3);
            resultPrice = priceTriangle1 + priceTriangle2;
        }else{
            // деллим сторону x1y1-x3y3
            double priceTriangle1 = getPriceConnectionStationInTriangle(x1, y1, x2, y2, (x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2));
            double priceTriangle2 = getPriceConnectionStationInTriangle((x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2), x2, y2, x3, y3);
            resultPrice = priceTriangle1 + priceTriangle2;
        }

        return resultPrice;
    }

    // подсчет стоимости установки в треугольнике
    double getPriceCellularStationInTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        double resultPrice = 0;
        double sumX = 0;
        double sumY = 0;
        double connectionStationCount = 0;
        double stationX = 0;
        double stationY = 0;

        //вычисление средней точки
        for (ConnectionStation connectionStation: workMap.getConnectionStations()){
            if (isInTriangle(x1,y1,x2,y2,x3,y3,connectionStation.getLongitude(),connectionStation.getLatitude())) {
                sumX = sumX + connectionStation.getLongitude();
                sumY = sumY + connectionStation.getLatitude();
                connectionStationCount++;
            }
        }
        stationX = sumX / connectionStationCount;
        stationY = sumY / connectionStationCount;

        //подсчет количества абонентов
        int customers = 0;
        for (ConnectionStation connectionStation: workMap.getConnectionStations()){
            if (isInTriangle(x1, y1, x2, y2, x3, y3, connectionStation.getLongitude(), connectionStation.getLatitude())) {
                for (Sector sector: workMap.getSectors()){
                    if (sector.getConnectionStation() == connectionStation)
                        customers = customers + sector.getCustomers();
                }
            }
        }
        //подключение всех абонентов в треугольнике
        int connectedCustomers = 0;
        //подсчет стоимости всех станций на данной точке
        resultPrice = resultPrice + (customers / (int)CELLULAR_STATION_CAPACITY) * CELLULAR_STATION_PRICE;
        if (customers % CELLULAR_STATION_CAPACITY != 0) resultPrice =resultPrice+ CELLULAR_STATION_PRICE;
        //подсчет стоимости подключений секторов к станциям
        for (Sector sector: workMap.getSectors()){
            if (isInTriangle(x1, y1, x2, y2, x3, y3, stationX, stationY)) {
                resultPrice = resultPrice + getDistance(stationX, stationY, sector.getLongitude(), sector.getLatitude());
            }
        }

        return resultPrice;
    }

    // подсчет стоимости двухзонной установки
    double getDoubleTriangleCellularStationPrice(double x1, double y1, double x2, double y2, double x3, double y3) {
        double resultPrice = 0;
        // делим существующий треугольник  по длинной стороне
        if (getDistance(x1, y1, x2, y2) > getDistance(x1, y1, x3, y3) && getDistance(x1, y1, x2, y2) > getDistance(x2, y2, x3, y3)) {
            // делим сторону x1y1-x2y2
            double priceTriangle1 = getPriceCellularStationInTriangle(x1, y1, (x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x3, y3);
            double priceTriangle2 = getPriceCellularStationInTriangle((x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2), x2, y2, x3, y3);
            resultPrice = priceTriangle1 + priceTriangle2;
        }else if (getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x2, y2) && getDistance(x2, y2, x3, y3) > getDistance(x1, y1, x3, y3)) {
            // делим сторону x2y2-x3y3
            double priceTriangle1 = getPriceCellularStationInTriangle(x1, y1, x2, y2, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2));
            double priceTriangle2 = getPriceCellularStationInTriangle(x1, y1, (x2 + (x3 - x2) / 2), (y2 + (y3 - y2) / 2), x3, y3);
            resultPrice = priceTriangle1 + priceTriangle2;
        }else{
            // деллим сторону x1y1-x3y3
            double priceTriangle1 = getPriceCellularStationInTriangle(x1, y1, x2, y2, (x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2));
            double priceTriangle2 = getPriceCellularStationInTriangle((x1 + (x3 - x1) / 2), (y1 + (y3 - y1) / 2), x2, y2, x3, y3);
            resultPrice = priceTriangle1 + priceTriangle2;
        }

        return resultPrice;
    }

    public void placeCellularStations() {
        double sumX = 0;
        double sumY = 0;
        double stationX = 0;
        double stationY = 0;
        int connectionStationCount = 0;
        double totalCost = 0;
        int customers = 0;
        // перебираем все секторы и считаем количество подключений на секторе
        for (ConnectionStation connectionStation: workMap.getConnectionStations()){
            sumX = sumX + connectionStation.getLongitude();
            sumY = sumY + connectionStation.getLatitude();
            for (Sector sector : workMap.getSectors())
                if (sector.getConnectionStation() == connectionStation)
                    customers = customers + sector.getCustomers();
            connectionStationCount++;
        }
        totalCost = 0;
        // складываем все координаты и делим на количество станций. Получаем среднюю точку для установки сотовой станции
        stationX = sumX / connectionStationCount;
        stationY = sumY / connectionStationCount;
        // Перебираем станции связи и считаем стоимость подключения к одной сотовой станции
        for (ConnectionStation connectionStation : workMap.getConnectionStations()) {
            double distanceCellularStationSector = getDistance(stationY, stationX, connectionStation.getLatitude(), connectionStation.getLongitude());
            totalCost = totalCost + distanceCellularStationSector * WIRE_PRICE;
        }
        //Считаем стоимость станций, нобходимых для установки
        totalCost = totalCost + (customers / (int)CELLULAR_STATION_CAPACITY) * CELLULAR_STATION_PRICE;
        if (customers % CELLULAR_STATION_CAPACITY != 0) totalCost = totalCost + CELLULAR_STATION_PRICE;

        //посчитать стоимость альтернативной установки
        int secondTotalCost = 0;
        // разбиваем на два треугольника
        // считаем стоимость установки двух станций.
        double priceTriangle1 = getPriceCellularStationInTriangle(
                workMap.getLeftLongitude(), workMap.getBottomLatitude(),
                workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude());
        double priceTriangle2 = getPriceCellularStationInTriangle(
                workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude());

        // В случае, если стоймость установки одной станции связи меньше чем двух, то ставить одну
        if (totalCost < priceTriangle1 + priceTriangle2){
            CellularStation cellularStation= new CellularStation(stationX, stationY, CELLULAR_STATION_CAPACITY, CELLULAR_STATION_PRICE);
            workMap.addCellularStation(cellularStation);
            return;
        }

        // Ставим две станции
        placeSingleTriangleCellularStation(workMap.getLeftLongitude(), workMap.getBottomLatitude(),
                workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude(), priceTriangle1);
        placeSingleTriangleCellularStation(workMap.getLeftLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getTopLatitude(),
                workMap.getRightLongitude(), workMap.getBottomLatitude(), priceTriangle2);



    }

    public void placeBaseStations() {
        //TODO: написать функцию установки базовых станцию
    }

    private void generateMap() {
        //TODO: попробовать сгенерировать карту с нанесенными на ней станциямим и секторами
    }

    private double getLatitudeKilometerLength (double latitude) {
        return 1/111.3;
    }

    private double getLongitudeKilometerLength (double longitude) {
        double rlon = deg2rad(longitude);
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

    private boolean isInsideArea(double latitude, double longitude, double latitude5KmLength, double longitude5KmLength, double objectLatitude, double objectLongitude) {
        return (objectLatitude >= latitude) &&
                (objectLatitude <= (latitude + (longitude5KmLength))) &&
                (objectLongitude >= longitude) &&
                (objectLongitude <= (longitude + (latitude5KmLength)));
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
            case 1: sectorStartLongitude = sectorLongitude + latitude5KmLength * 1;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * 0;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * 0;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 1;
                    break;

            case 2: sectorStartLongitude = sectorLongitude + latitude5KmLength * 0;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * 1;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * -1;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 0;
                    break;

            case 3: sectorStartLongitude = sectorLongitude + latitude5KmLength * -1;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * 0;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * 0;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 1;
                    break;

            case 4: sectorStartLongitude = sectorLongitude + latitude5KmLength * 0;
                    sectorStartLatitude = sectorLatitude + longitude5KmLength * -1;
                    sectorEndLongitude = sectorLongitude + latitude5KmLength * 1;
                    sectorEndLatitude = sectorLatitude + longitude5KmLength * 0;
                    break;

            default:
                return -1;
        }
        //перебираем дома в секторе
        for (House house : workMap.getHouses()) {
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

    /*private boolean isInsideSector(double objectLatitude, double objectLongitude,
                                   double sectorLatitude, double sectorLongitude,
                                   double sectorStartLatitude, double sectorStartLongitude,
                                   double sectorEndLatitude, double sectorEndLongitude,
                                   double radius) {

        if ((objectLatitude == sectorLatitude) && (objectLongitude == sectorLongitude))
            return true;

        double relPointLatitude = objectLatitude - sectorLatitude;
        double relPointLongitude = objectLongitude - sectorLongitude;

        boolean startClockwise = areClockwise(sectorStartLongitude, sectorStartLatitude, relPointLongitude, relPointLatitude);
        boolean endClockwise = areClockwise(sectorEndLongitude, sectorEndLatitude, relPointLongitude, relPointLatitude);
        boolean withinRadius = isWithinRadius(objectLongitude, objectLatitude, radius);

        return (!startClockwise) && endClockwise && withinRadius;
    }*/
    private boolean isInsideSector(double objectLatitude, double objectLongitude,
                                   double sectorLatitude, double sectorLongitude,
                                   double sectorStartLatitude, double sectorStartLongitude,
                                   double sectorEndLatitude, double sectorEndLongitude,
                                   double radius) {

        return isInTriangle(sectorLongitude, sectorLatitude, sectorStartLongitude, sectorStartLatitude, sectorEndLongitude, sectorEndLatitude,objectLongitude, objectLatitude);
    }


    private int getPossibleSectorCustomersInTrianlge(double x1, double y1, double x2, double y2, double x3, double y3) {
        // перебираем все дома и проверяем их пренадлежность к треугольнику. Считаем количество клиентов.
        int customersCount = 0;
        for (Sector sector : workMap.getSectors()) {
            if (isInTriangle(x1, y1, x2, y2, x3, y3, sector.getLongitude(), sector.getLatitude()))
                customersCount = customersCount + sector.getCustomers();
        }
        return customersCount;
    }

    private int getPossibleConnectionCustomersInTrianlge(double x1, double y1, double x2, double y2, double x3, double y3) {
        // перебираем все дома и проверяем их пренадлежность к треугольнику. Считаем количество клиентов.
        int customersCount = 0;
        for (ConnectionStation connectionStation : workMap.getConnectionStations()) {
            if (isInTriangle(x1, y1, x2, y2, x3, y3, connectionStation.getLongitude(), connectionStation.getLatitude()))
                for (Sector sector : workMap.getSectors()) {
                    if (sector.getConnectionStation() == connectionStation)
                        customersCount = customersCount + sector.getCustomers();
                }

        }
        return customersCount;
    }

    private boolean isInCircle(double objLatitude, double objLongitude,
                               double sectorLatitude, double sectorLongitude,
                               double radius) {
        return (Math.pow((objLatitude - sectorLatitude),2) - Math.pow((objLongitude - sectorLongitude),2) < Math.pow(radius,2));
    }

    private boolean isWithinRadius(double vx, double vy, double radius) {
        double radiusSquared = radius*radius;
        return (vx*vx + vy*vy) <= radiusSquared;
    }

    private boolean areClockwise(double v1x, double v1y, double v2x, double v2y) {
        return (((v1y*v2x) - (v1x*v2y)) > 0);
    }

    private boolean isInTriangle(double x1, double y1, double x2, double y2, double x3, double y3, double x0, double y0) {
        double a = (x1 - x0) * (y2 - y1) - (x2 - x1) * (y1 - y0);
        double b = (x2 - x0) * (y3 - y2) - (x3 - x2) * (y2 - y0);
        double c = (x3 - x0) * (y1 - y3) - (x1 - x3) * (y3 - y0);

        return ((a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0));
    }
}
