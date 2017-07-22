/**
 * Created by David Turk on 7/21/17.
 */
public class VehicleInfo {
    private int vin;
    private double odometer;
    private double consumption;
    private double odometerForLastOilChange;
    private double engineSize;

    public VehicleInfo() {
    }

    public int getVin() {
        return vin;
    }

    public void setVin(int vin) {
        this.vin = vin;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getOdometerForLastOilChange() {
        return odometerForLastOilChange;
    }

    public void setOdometerForLastOilChange(double odometerForLastOilChange) {
        this.odometerForLastOilChange = odometerForLastOilChange;
    }

    public double getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(double engineSize) {
        this.engineSize = engineSize;
    }

    @Override
    public String toString() {
        return "Vehicle VIN: " + getVin() + "\n" +
                "Current Odometer: " + getOdometer() + "\n" +
                "Total Fuel Consumed: " + getConsumption() + "\n" +
                "Last Oil Change at " + getOdometerForLastOilChange() + " miles\n" +
                "Engine size: " + getEngineSize() + "L";
    }
}
