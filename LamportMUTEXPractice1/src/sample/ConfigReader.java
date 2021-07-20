package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ConfigReader {
    public static HashMap<Integer, Integer> clocks = new HashMap<>();
    public static HashMap<String, String> getLookup(String f){
        HashMap<String, String> lookup = new HashMap<String, String>();
        try(BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line = null;
            while((line = r.readLine()) != null ){
                String[] parts = line.split(" ");
                String pid = parts[0];
                String id_port = parts[1]+":"+parts[2];
                int clock_incr = 1; // if clock value is absent then the default clock value is 1
                if(parts.length == 4){
                    clock_incr = Integer.parseInt(parts[3]);
                }
                lookup.put(pid, id_port);
                clocks.put(Integer.parseInt(pid), clock_incr);
            }
        }catch(IOException ex){
            System.err.println("cant open file - ERROR MESSAGE: "+ex.getMessage());
        }
        return lookup;
    }
    public static void main(String[] args){
        ConfigReader.getLookup("./src/tmp/foo");
        System.out.println(ConfigReader.clocks);
    }
}
