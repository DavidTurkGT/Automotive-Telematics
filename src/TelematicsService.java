import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David Turk on 7/21/17.
 */
public class TelematicsService {

    static void report(VehicleInfo vehicleInfo) throws JsonProcessingException {

        //Write the VehicleInfo to a file as json using the VIN as the name of the file and a "json" extension
        // (e.g. "234235435.json"). The file will overwrite any existing files for the same VIN.

        //This writes a Java object to a json
        try{
            saveVehicleInfoToJson(vehicleInfo);
        } catch(JsonProcessingException jpe){
            System.out.println("Error in saving to JSON");
            jpe.printStackTrace();
        }


        //Find all the files that end with ".json" and convert back to a VehicleInfo object.
        System.out.println("Finding all vehicle info reports...");
        System.out.print("\tFinding all JSON files...");
        File[] jsonFiles = findAllJsonFiles();
        System.out.println("Found!");
        System.out.print("\tExtracting JSON strings...");
        String[] jsonStrings = extractJsonStrings(jsonFiles);
        System.out.println("Extracted!");
        VehicleInfo[] reports = createVehicleInfoReports(jsonStrings);
        for(VehicleInfo report : reports){
            System.out.println("\tFound report for vehicle VIN " + report.getVin() );
        }
        System.out.println("Done!");

        //Update an dashboard.html
        System.out.println("Updating dashboard.html...");
        String[] dashboardHTML = createDashboard(reports);
        dashboardHTML[3] = dashboardHTML[3].replace("#",reports.length+"");
        System.out.println("Read in the following HTML");
        for(int i = 0; i < dashboardHTML.length; i++){
            System.out.print("Index " + i + ": ");
            System.out.println(dashboardHTML[i]);
        }
        saveDashboard(dashboardHTML);
    }

    private static void saveVehicleInfoToJson(VehicleInfo vi) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(vi);
        System.out.println("Saving the vehicle info...");
        try {
            File file = new File(vi.getVin()+".json");
            System.out.println("\tCreated file: " + file.getName() );
            System.out.println("\tWriting to file...");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.close();
            System.out.println("\tWrite: Success!");
        } catch (IOException e){
            System.out.println("\tWrite: Failure! IOException printed below");
            e.printStackTrace();
        }
        System.out.println("Save: Success!");
    }

    private static File[] findAllJsonFiles(){
        ArrayList<File> jsonFiles = new ArrayList<>();
        File file = new File(".");
        for (File f : file.listFiles()) {
            if (f.getName().endsWith(".json")) {
                jsonFiles.add(f);
            }
        }
        return jsonFiles.toArray(new File[0]);
    }

    private static String[] extractJsonStrings(File[] jsonFiles){
        ArrayList<String> jsonStrings = new ArrayList<>();
        Scanner fileScanner;
        for(File f : jsonFiles){
            try {
                fileScanner = new Scanner(f);
                ArrayList<String> fileContents = new ArrayList<>();
                while( fileScanner.hasNext() ){
                    fileContents.add( fileScanner.nextLine() );
                }
                jsonStrings.add(fileContents.toArray(new String[0])[0]);
            } catch (FileNotFoundException e) {
                System.out.println("ERROR! File " + f.getPath() + " not found");
                e.printStackTrace();
            }
        }
        return jsonStrings.toArray(new String[0]);
    }

    private static VehicleInfo[] createVehicleInfoReports(String[] jsonStrings){
        ArrayList<VehicleInfo> reports = new ArrayList<>();
        for(String json : jsonStrings){
            try{
                ObjectMapper mapper = new ObjectMapper();
                VehicleInfo vi = mapper.readValue(json, VehicleInfo.class);
                reports.add(vi);
            } catch (IOException e) {
                System.out.println("\t\tCreating vehicle info: Failure!");
                e.printStackTrace();
            }
        }
        return reports.toArray(new VehicleInfo[0]);
    }

    private static String[] createDashboard(VehicleInfo[] reports){
        ArrayList<String> html = new ArrayList<>();
        try{
            Scanner fileScanner = new Scanner(new File("dashboard-template.html"));
            while( fileScanner.hasNext() ){
                html.add( fileScanner.nextLine() );
            }
        } catch (FileNotFoundException e){
            System.out.println("ERROR! Cannot find Dashboard Template");
            e.printStackTrace();
        }
        String[] dashboard = html.toArray(new String[0]);
        dashboard = updateDashboard(dashboard, reports);
        return dashboard;
    }

    private static String[] updateDashboard(String[] dashboard, VehicleInfo[] reports){
        dashboard[3] = dashboard[3].replace("#",reports.length+"");
        double totalOdometer = 0;
        double totalConsumption = 0;
        double totalLastOilChange = 0;
        double totalEngineSize = 0;
        for(VehicleInfo report : reports){
            totalOdometer += report.getOdometer();
            totalConsumption += report.getConsumption();
            totalLastOilChange += report.getOdometerForLastOilChange();
            totalEngineSize += report.getEngineSize();
        }
        dashboard[9] = dashboard[9].replaceFirst("#", (totalOdometer/reports.length)+"");
        dashboard[9] = dashboard[9].replaceFirst("#",(totalConsumption/reports.length)+"");
        dashboard[9] = dashboard[9].replaceFirst("#",(totalLastOilChange/reports.length)+"");
        dashboard[9] = dashboard[9].replaceFirst("#",(totalEngineSize/reports.length)+"");
        return dashboard;
    }

    private static void saveDashboard(String[] html){
        //TODO
        return;
    }

}