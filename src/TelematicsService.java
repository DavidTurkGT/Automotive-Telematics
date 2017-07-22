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
        ObjectMapper mapper;
        String json;

        //Write the VehicleInfo to a file as json using the VIN as the name of the file and a "json" extension
        // (e.g. "234235435.json"). The file will overwrite any existing files for the same VIN.

        //This writes a Java object to a json
        mapper = new ObjectMapper();
        json = mapper.writeValueAsString(vehicleInfo);
        System.out.println("Saving the vehicle info...");
        try {
            File file = new File(vehicleInfo.getVin()+".json");
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


        //Find all the files that end with ".json" and convert back to a VehicleInfo object.
        System.out.println("Finding all vehicle info reports...");
        File file = new File(".");
        for (File f : file.listFiles()) {
            if (f.getName().endsWith(".json")) {
                //Create a VehileInfo object vi from a JSON file
                try{
                    Scanner fileScanner = new Scanner(f);
                    ArrayList<String> fileContents = new ArrayList<>();
                    while( fileScanner.hasNext() ){
                        fileContents.add( fileScanner.nextLine() );
                    }
                    json = fileContents.toArray(new String[0])[0];
                    System.out.println("\tRead the following JSON: " + json);
                    System.out.println("\tCreating a vehicle info report...");
                    VehicleInfo vi = null;
                    try{
                        vi = mapper.readValue(json, VehicleInfo.class);
                    } catch (IOException e) {
                        System.out.println("\t\tCreating vehicle info: Failure!");
                        e.printStackTrace();
                    }
                    if(vi != null){
                        System.out.println("\t\tCreated vehicle info report: \n----------\n" + vi + "\n----------");
                    } else{
                        System.out.println("\t\tError! No vehicle info report created!");
                    }
                    System.out.println("\tCreate: Success!");

                } catch (FileNotFoundException e){
                    System.out.println("Find: Failure! File not found!");
                    e.printStackTrace();
                }
                //Create a dashboard-template.html to display all info
                System.out.println("Creating dashboard.html from dashboard-template.html ...");
                File dashboardFile = new File("dashboard-template.html");
                String [] template = null;
                try{
                    Scanner fileScanner = new Scanner(dashboardFile);
                    ArrayList<String> templateContents = new ArrayList<>();
                    while( fileScanner.hasNext() ){
                        templateContents.add( fileScanner.nextLine() );
                    }
                    template = templateContents.toArray(new String[0]);
                } catch(FileNotFoundException e){
                    System.out.println("Error! dashboard-template.html not found");
                    e.printStackTrace();
                }
                if(template == null){
                    System.out.println("Error! Template never created");
                } else {
                    System.out.println("Template created. Here it is");
                    System.out.println("____________________________");
                    for(int i = 0; i < template.length; i++){
                        System.out.print("Index " + i + ": ");
                        System.out.println(template[i]);
                    }
                    System.out.println("-----------------------------");
                    System.out.println("End of template");
                }

            }
        }
        System.out.println("Find: Success!");


        //Update a dashboard-template.html (only show 1 place after the decimal for values that are doubles).
        // The dashboard-template.html should look something like this (with the '#' replaced with a number)
    }
}