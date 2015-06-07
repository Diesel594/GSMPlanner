/**
 * Created by Gerz on 07.06.2015.
 */
public abstract class Equipment {
    private double posX;
    private double posY;
    private int capacity;
    private double price;

    public Equipment(double posX, double posY, int capacity, double price){
        this.posX = posX;
        this.posY = posY;
        this.capacity = capacity;
        this.price = price;
    }

    public double getPosX(){
        return this.posX;
    }

    public double getPosY(){
        return this.posY;
    }

    public double getPrice(){
        return this.price;
    }

    public int getCapacity(){
        return this.capacity;
    }
}
