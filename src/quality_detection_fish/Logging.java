/*
 * The Logging class holds methods for logging different data.
 */
package quality_detection_fish;

import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristian Kvam
 */
public class Logging {

    private boolean dateFlag = true;

    public Logging() {

    }

    /**
     * Writes the input parameters to an logg file of type ARFF.
     * 
     * This is used to logg the data from each fillet.
     * 
     * @param area
     * @param HB
     * @param MetHB
     * @param OxyHB
     */
    public void writeToLogg(int area, int HB, int MetHB, int OxyHB) {

        String data = (Integer.toString(area) + " " + Integer.toString(HB) + " " + Integer.toString(MetHB) + " " + Integer.toString(OxyHB));

        // Change FileWriter location to your prefererd location.
        try (FileWriter fw = new FileWriter("C:\\Users\\krist\\Dropbox\\Høst 2017\\Sanntids data-teknikk\\Java-kode\\Quality_Detection_Fish\\Logg.arff", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            while (dateFlag == true) {
                String timeStamp = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z").format(Calendar.getInstance().getTime());
                out.println("% Logging started: " + timeStamp);
                dateFlag = false;
            }

            out.println(data);

        } catch (IOException e) {
        }
    }

    /**
     * Writes the input parameters to a logg file of type CSV.
     * 
     * This CSV file is used to have a total count of all the fillets checked.
     * 
     * @param good
     * @param medium
     * @param bad
     * @param small
     */
    public void writeClassificationLogg(int good, int medium, int bad, int small) {

        try (
                // Change the Writer path to your prefered location.
                Writer writer = Files.newBufferedWriter(Paths.get("C:\\Users\\krist\\Dropbox\\Høst 2017\\Sanntids data-teknikk\\Java-kode\\Quality_Detection_Fish\\classificationLogg.csv"));
                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);) {
            csvWriter.writeNext(new String[]{Integer.toString(good), Integer.toString(medium), Integer.toString(bad), Integer.toString(small)});
        } catch (IOException ex) {
            Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
