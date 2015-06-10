/**
 * Класс объекта Дом. Включает в себя широту, долготу, количество жителей
 */
public class House {
    private double latitude;
    private double longitude;
    private int population;

    public House(double latitude, double longitude, int population) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public int getPopulation(){
        return this.population;
    }
}
