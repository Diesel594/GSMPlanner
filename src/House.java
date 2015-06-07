/**
 * Created by Gerz on 07.06.2015.
 */
public class House {
    private double posX;
    private double posY;
    private int population;

    public House(double posX, double posY, int population) {
        this.posX = posX;
        this.posY = posY;
        this.population = population;
    }

    public double getPosX(){
        return this.posX;
    }

    public double getPosY(){
        return this.posY;
    }

    public int getPopulation(){
        return this.population;
    }
}
