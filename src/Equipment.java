/**
 * Created by Gerz on 07.06.2015.
 */
public abstract class Equipment {
    private double longitude;
    private double latitude;
    private int capacity;
    private double price;

    public Equipment(double longitude, double latitude, int capacity, double price){
        this.longitude = longitude;
        this.latitude = latitude;
        this.capacity = capacity;
        this.price = price;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getX() {
        return GSMPlannerModel.EARTH_RADIUS  * Math.cos(latitude) * Math.cos(longitude);
    }

    public double getY() {
        return GSMPlannerModel.EARTH_RADIUS  * Math.cos(latitude) * Math.sin(longitude);
    }

    public double getPrice(){
        return this.price;
    }

    public int getCapacity(){
        return this.capacity;
    }
}
