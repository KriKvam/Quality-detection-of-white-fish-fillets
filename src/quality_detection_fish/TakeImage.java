/*
 * The Take_Image class times the image capture and the blizing of the light 
 * sources.
 */
package quality_detection_fish;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristian Kvam
 */
public class TakeImage extends Thread {
    
    Server serv;
    Camera cam;
    GUI gui;
    StorageBox box;
    ObjectDetection detect;
    
    private String sensor = "ok";
    
    public TakeImage(Server server, Camera camera, StorageBox storage, GUI gu, ObjectDetection detection){
        this.serv  = server;
        this.cam = camera;
        this.box = storage;
        this.gui = gu;
        this.detect = detection;
    }
    
    @Override
    public void run(){
        while(!box.getShutdown()){
            if((serv.getInput().equalsIgnoreCase(sensor) && gui.getStart() == true) || (gui.getStart() == true && detect.getFishDetected())){ 
                
                serv.setOutput('1');
                box.setBlitsFlag(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TakeImage.class.getName()).log(Level.SEVERE, null, ex);
                }
                cam.autoCapture("red");

                
                serv.setOutput('2');
                box.setBlitsFlag(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TakeImage.class.getName()).log(Level.SEVERE, null, ex);
                }
                cam.autoCapture("green");

                
                serv.setOutput('3');
                box.setBlitsFlag(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TakeImage.class.getName()).log(Level.SEVERE, null, ex);
                }
                cam.autoCapture("blue");

            }
        }
        System.out.println("TakeImage shutdown");
    }
}
