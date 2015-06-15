public class Sector extends Equipment {
    private ConnectionStation connectionStation;
    private int direction;
    private int customers;
    public Sector(double latitude, double longitude, int direction, int capacity, double price, int customers) {
        super(latitude, longitude, capacity, price);
        this.direction = direction;
        this.customers = customers;
    }

    public int getDirection(){
        return this.direction;
    }

    public int getCustomers() {
        return customers;
    }

    public ConnectionStation getConnectionStation() {
        return connectionStation;
    }

    public void setConnectionStation(ConnectionStation connectionStation) {
        this.connectionStation = connectionStation;
    }
}
