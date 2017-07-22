import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Scanner;

/**
 * Created by David Turk on 7/21/17.
 */
public class Main {
    public static void main(String[] args) {
        //Get the information for the VehicleInfo from the command line (i.e. Scanner).
        // Do not write code for error handling the input, just the green path (i.e. type in the correct stuff).
        Scanner scanner = new Scanner(System.in);
        
        //Get the VIN # (Integer)
        System.out.println("What is the VIN for your vehicle? (Integer)");
        int vin = Integer.parseInt( scanner.nextLine() );
        
        //Get odometer reading (Double)
        System.out.println("What is the current odometer reading? (Double)");
        double odometer = Double.parseDouble( scanner.nextLine() );
        
        //Get the gas consumption in gallons (Double)
        System.out.println("How many gallons of gas have been consumed since the last report? (Double)");
        double consumption = Double.parseDouble( scanner.nextLine() );
        
        //Get odometer reading at last oil change
        System.out.println("What was the odometer reading at the last oil change? (Double)");
        double odometerForLastOilChange = Double.parseDouble( scanner.nextLine() );
        
        //Get engine size
        System.out.println("What is the engine size of the vehicle? (Double)");
        double engineSize = Double.parseDouble( scanner.nextLine() );

        VehicleInfo data = new VehicleInfo();
        data.setVin(vin);
        data.setOdometer(odometer);
        data.setConsumption(consumption);
        data.setOdometerForLastOilChange(odometerForLastOilChange);
        data.setEngineSize(engineSize);
        //Once all the info for a VehicleInfo has been entered and a VehicleInfo object has been created
        // the report(vehicleInfo) method in the TelematicsService should be called.
        try {
            TelematicsService.report(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
