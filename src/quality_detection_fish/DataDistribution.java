/*
 * The dataDistribution class handles and distributes different data.
 */
package quality_detection_fish;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristian Kvam
 */
public class DataDistribution extends Thread {

    StorageBox box;
    Server serv;
    GUI gui;
    Logging logging;
    PlotGraph plotter;
    
    private int good = 0;
    private int medium = 0;
    private int bad = 0;
    private int small = 0;
    

    public DataDistribution(StorageBox storage, Server server, GUI gu, Logging logg, PlotGraph plot) {
        this.box = storage;
        this.serv = server;
        this.gui = gu;
        this.logging = logg;
        this.plotter = plot;
    }

    @Override
    public void run() {
        try {
            getClassificationCount();
            gui.setFilletCount(good, medium, bad, small);
            
            while (!box.getShutdown()) {
                if(box.getEvalFlag()){
                String string = box.getFilletClass();
                
                compareClass(string);
                
                logging.writeClassificationLogg(good, medium, bad, small);
                box.setEvalDone(false);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DataDistribution.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DataDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }
        
          System.out.println("DataDist shutdown");
    }

    /**
     * Checks the input parameter with several cases and executes several
     * methods if the case matches the input string.
     * 
     * Valid inputs are: "[0.0]", "[1.0]", "[2.0]", "[3.0]".
     * 
     * This is used to update parameters displayed in the GUI and send the 
     * classification value to to client.
     * 
     * @param input
     */
    public void compareClass(String input) {
        switch (input) {
            case "[0.0]": {
              serv.setOutput('6');
              box.setBlitsFlag(true);
              gui.setClassificationText("BAD");
              bad++;
              gui.setFilletCount(good, medium, bad, small);
              gui.setFilletSessionCount("bad");
            }
            break;
            case "[1.0]": {
              serv.setOutput('5');
              box.setBlitsFlag(true);
              gui.setClassificationText("MEDIUM");
              medium++;
              gui.setFilletCount(good, medium, bad, small);
              gui.setFilletSessionCount("medium");
            }
            break;
            case "[2.0]": {
              serv.setOutput('7');
              box.setBlitsFlag(true);
              gui.setClassificationText("SMALL");
              small++;
              gui.setFilletCount(good, medium, bad, small);
              gui.setFilletSessionCount("small");
            }
            break;
            case "[3.0]": {
              serv.setOutput('4');
              box.setBlitsFlag(true);
              gui.setClassificationText("GOOD");
              good++;
              gui.setFilletCount(good, medium, bad, small);
              gui.setFilletSessionCount("good");
            }
            break;
            default:
                System.out.println("Not able to classify object");
                serv.setOutput('8');
                box.setBlitsFlag(true);
                gui.setClassificationText("Unknown");
                break;
        }
    }
    
    /**
     * Loads the classification logg and sets the classification parameters in
     * the class.
     * 
     * This is used to display the total amount of fillets checked in the GUI.
     * 
     * @throws IOException
     */
    public void getClassificationCount() throws IOException{
        String[] nextRecord;
        try (
                // Change the location to your file location.
                Reader reader = Files.newBufferedReader(Paths.get("C:\\Users\\krist\\Dropbox\\Høst 2017\\Sanntids data-teknikk\\Java-kode\\Quality_Detection_Fish\\classificationLogg.csv"));
                CSVReader csvReader = new CSVReader(reader);) {
            
            while ((nextRecord = csvReader.readNext()) != null) {
                good = Integer.parseInt(nextRecord[0]);
                medium = Integer.parseInt(nextRecord[1]);
                bad = Integer.parseInt(nextRecord[2]);
                small = Integer.parseInt(nextRecord[3]);   
            }
        }
    }

}
