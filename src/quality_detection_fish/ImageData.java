/*
 * The Image_Data class handles all the data extracted from the images.
 */
package quality_detection_fish;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static oracle.jrockit.jfr.events.Bits.intValue;
import org.opencv.core.Mat;

/**
 *
 * @author Kristian Kvam
 */
public class ImageData extends  Thread {
    
    StorageBox box;
    ImageMethods met;
    WriteData data;
    Cluster clust;
    GUI gui;
    Logging logger;
    
    private Mat orgImage = new Mat();
    private Mat redImage = new Mat();
    private Mat greenImage = new Mat();
    private Mat blueImage = new Mat();
    
    int filletArea = 0;
    int redArea = 0;
    int greenArea = 0;
    int blueArea = 0;
    
    public ImageData(StorageBox storage, ImageMethods methods, WriteData write, Cluster cluster, GUI gu, Logging logg){
        this.box = storage;
        this.met = methods;
        this.data = write;
        this.clust = cluster;
        this.gui = gu;
        this.logger = logg;
    }
    
    @Override
    public void run(){
        System.out.println();
        while(!box.getShutdown()){
            if(box.getCaptureDone()){
                
            orgImage = box.getOrgImage();
            redImage = box.getRedImage();
            greenImage = box.getGreenImage();
            blueImage = box.getBlueImage();
            
            filletArea = intValue(met.areaCheck(orgImage));
            gui.setMainImage(orgImage);
            
            redArea = intValue(met.bloodThreshold(redImage));
            gui.setRedImage(redImage);

            greenArea = intValue(met.bloodThreshold(greenImage));
            gui.setGreenImage(greenImage);

            blueArea = intValue(met.bloodThreshold(blueImage));
            gui.setBlueImage(blueImage);
            

            try {
                data.writeArff(filletArea, redArea, greenArea, blueArea);
            } catch (IOException ex) {
                Logger.getLogger(ImageData.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImageData.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                clust.checkCluster();
                gui.setFilletValues(filletArea, redArea, greenArea, blueArea);
            } catch (Exception ex) {
                Logger.getLogger(ImageData.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(gui.getLoggFlag() == true){
                logger.writeToLogg(filletArea, redArea, greenArea, blueArea);
            }
            box.setCaptureDone(false);
            }

        }
        System.out.println("ImageData shutdown");
    }
       
}
