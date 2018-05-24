/*
 * The ObjectDetection class checks for white objects within an area.
 */
package quality_detection_fish;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 *
 * @author Kristian Kvam
 */
public class ObjectDetection extends Thread{
    
    Camera cam;
    StorageBox box;
    GUI gui;
    
    private int nonZeroLeft = 0;
    private int nonZeroRight = 0;
    private boolean fishDetected = false;
    
    public ObjectDetection(Camera camera, StorageBox storage, GUI gu){
        this.cam = camera;
        this.box = storage;
        this.gui = gu;
    }
    
    @Override
    public void run() {
        
        while(!box.getShutdown()){
         if(box.getAutoDetect() && gui.getStart()){   
        Mat image = cam.getImage();
        Rect roi = new Rect(50, 0, 30, 480);
        Rect roi2 = new Rect(560, 0, 30, 480);
        
        nonZeroLeft = cropAndCount(image, roi);
        nonZeroRight = cropAndCount(image, roi2);
        
        if((nonZeroLeft >= 200) && (nonZeroRight >= 200)){
            fishDetected = true;
            System.out.println("Object detected");
            fishDetected = false;
        }
        
             try {
                 Thread.sleep(4000);
             } catch (InterruptedException ex) {
                 Logger.getLogger(ObjectDetection.class.getName()).log(Level.SEVERE, null, ex);
             }

         }
       }
      }
    
    /**
     * Returns the boolean value fishDetected.
     * This value indicates if a fillet has been detected by the object detection.
     * 
     * @return boolean value indicating if a fillet has been detected
     */
    public boolean getFishDetected(){
        return fishDetected;
    }
    
    /**
     * Crops out a rectangle in an image and returns the amount of nonzero 
     * pixels within the rectangle as an integer.
     * 
     * @param image image from the camera
     * @param rectangle rectangle with specified size.
     * @return amount of nonzero pixels as integer.
     */
    public int cropAndCount(Mat image, Rect rectangle){
        Mat cropped = new Mat(image, rectangle);
        cvtColor(cropped, cropped, COLOR_BGR2GRAY);
        threshold(cropped, cropped, 150, 255, THRESH_BINARY);
        int count = Core.countNonZero(cropped);
        return count;
    }
    
    }


