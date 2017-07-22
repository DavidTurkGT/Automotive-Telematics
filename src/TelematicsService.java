import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
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
        System.out.print("Updating dashboard.html...");
        String[] dashboardHTML = createDashboard(reports);
        System.out.println("Updated!");
        System.out.print("Saving dashboard.html...");
        File dashboard = saveDashboard(dashboardHTML);
        System.out.println("Saved!");
        System.out.println("Report can be viewed at: " + dashboard.getAbsolutePath() );
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
        //Cut off the last three closing tags (to be added back later)
        html.remove(19);
        html.remove(18);
        html.remove(17);
        //Create table rows for every report
        DecimalFormat df = new DecimalFormat("0.#");
        for(int i =0; i < reports.length; i++){
            html.add("<tr>");
            html.add("<td align=\"center\">"+ df.format( reports[i].getVin() ) + "</td>" +
                    "<td align=\"center\">" + df.format( reports[i].getOdometer() ) + "</td>" +
                    "<td align=\"center\">" + df.format( reports[i].getConsumption() ) + "</td>" +
                    "<td align=\"center\">" + df.format( reports[i].getOdometerForLastOilChange() ) + "</td>" +
                    "<td align=\"center\">" + df.format( reports[i].getEngineSize() ) + "</td>");
            html.add("</tr>");
        }
        //Add back the last three closing tags
        html.add("</table>");
        html.add("</body>");
        html.add("</hmtl>");

        String[] dashboard = html.toArray(new String[0]);
        dashboard = updateDashboard(dashboard, reports);
        return dashboard;
    }

    private static String[] updateDashboard(String[] dashboard, VehicleInfo[] reports){
        //Update Averages
        DecimalFormat df = new DecimalFormat("0.#");
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
        dashboard[9] = dashboard[9].replaceFirst("#", df.format(totalOdometer/reports.length) );
        dashboard[9] = dashboard[9].replaceFirst("#",df.format( totalConsumption/reports.length) );
        dashboard[9] = dashboard[9].replaceFirst("#",df.format(totalLastOilChange/reports.length) );
        dashboard[9] = dashboard[9].replaceFirst("#",df.format(totalEngineSize/reports.length) );
        return dashboard;
    }

    private static File saveDashboard(String[] html){
        File file = new File("dashboard.html");
        try{
            FileWriter fileWriter = new FileWriter(file);
            for(String line : html){
                fileWriter.write(line + "\n");
            }
            fileWriter.close();
        } catch (IOException e){
            System.out.println("Error in writing dashboard.html. Exiting...");
            e.printStackTrace();
        }
        return file;
    }

}