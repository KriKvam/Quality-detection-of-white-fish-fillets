/*
 * The StorageBox class stores data which some are synchronized.
 */
package quality_detection_fish;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Mat;

/**
 *
 * @author Kristian Kvam
 */
public class StorageBox {

    private boolean redAvailable = false;
    private boolean greenAvailable = false;
    private boolean blueAvailable = false;
    private boolean orgAvailable = false;
    
    private boolean fishClassFlag = false;
    private boolean blitsFlag = false;
    private boolean captureFlag = false;
    private boolean evalFlag = false;
    private boolean autoDetect = false;
    private boolean videoStream = false;
    
    private final Object lock = new Object();
    private final Object imageLock = new Object();
    private final Object evalLock = new Object();
    private final Object auto = new Object();
    private final Object stream = new Object();
    private String arduinoOut = "";

    private Mat orgImage = new Mat();
    private Mat redImage = new Mat();
    private Mat greenImage = new Mat();
    private Mat blueImage = new Mat();
    private Mat videoFrame = new Mat();
    
    
    private boolean shutdown = false;
    private String fishClassification;
    
    
    public StorageBox() {

    }

    /**
     * Synchronized method which sets an image of type Mat.
     * 
     * This is the red illuminated image captured of the fillet.
     * 
     * @param imgMat
     */
    public synchronized void setRedImage(Mat imgMat) {
        while (redAvailable == true) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        redImage = imgMat;
        redAvailable = true;
        notify();
    }

    /**
     * Synchronized method which returns an image of type Mat.
     * 
     * This is the red illuminated image captured of the fillet.
     * 
     * @return
     */
    public synchronized Mat getRedImage() {
        while (redAvailable == false) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        redAvailable = false;
        notify();
        return redImage;
    }

    /**
     * Synchronized method which sets an image of type Mat.
     * 
     * This is the green illuminated image captured of the fillet.
     * 
     * @param imgMat
     */
    public synchronized void setGreenImage(Mat imgMat) {
        while (greenAvailable == true) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        greenImage = imgMat;
        greenAvailable = true;
        notify();
    }

    /**
     * Synchronized method which returns an image of type Mat.
     * 
     * This is the green illuminated image captured of the fillet.
     * 
     * @return
     */
    public synchronized Mat getGreenImage() {
        while (greenAvailable == false) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        greenAvailable = false;
        notify();
        return greenImage;
    }

    /**
     * Synchronized method which sets an image of type Mat.
     * 
     * This is the blue illuminated image captured of the fillet.
     * 
     * @param imgMat
     */
    public synchronized void setBlueImage(Mat imgMat) {
        while (blueAvailable == true) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        blueImage = imgMat;
        blueAvailable = true;
        notify();
    }

    /**
     * Synchronized method which returns an image of type Mat.
     * 
     * This is the blue illuminated image captured of the fillet.
     * 
     * @return
     */
    public synchronized Mat getBlueImage() {
        while (blueAvailable == false) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        blueAvailable = false;
        notify();
        return blueImage;
    }
    
    /**
     * Synchronized method which sets an image of type Mat.
     * 
     * This is the red illuminated image captured of the fillet.
     * This is a duplicate of red image which will be used to find the area
     * of the fillet.
     * 
     * @param imgMat
     */
    public synchronized void setOrgImage(Mat imgMat) {
        while (orgAvailable == true) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        orgImage = imgMat;
        orgAvailable = true;
        notify();
    }

    /**
     * Synchronized method which returns an image of type Mat.
     * 
     * This is the red illuminated image captured of the fillet.
     * This is a duplicate of red image which will be used to find the area
     * of the fillet.
     * 
     * @return
     */
    public synchronized Mat getOrgImage() {
        while (orgAvailable == false) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        orgAvailable = false;
        notify();
        return orgImage;
    }
    
    /**
     * Synchronized method which sets the string value fishClassification.
     * 
     * This is the classification value of the fillet.
     * 
     * @param input
     */
    public synchronized void setFilletClass(String input){
         while (fishClassFlag == true) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        fishClassification = input;
        fishClassFlag = true;
        notify();
    }
    
    /**
     * Synchronized method which returns the string value fishClassification.
     * 
     * This is the classification value of the fillet.
     * 
     * @return
     */
    public synchronized String getFilletClass() {
        while (fishClassFlag == false) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(StorageBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        fishClassFlag = false;
        notify();
        return fishClassification;
    }

    /**
     * Sets the value of the boolean parameter blitsFlag.
     * 
     * This value is used to tell the server class to send a value to the client.
     * 
     * @param input
     */
    public void setBlitsFlag(boolean input) {
        synchronized (lock) {
            blitsFlag = input;
        }

    }

    /**
     * Returns the value of the boolean parameter blitsFlag.
     * 
     * This value is used to tell the server class to send a value to the client.
     * 
     * @return
     */
    public boolean getBlitsFlag() {
        synchronized (lock) {
            return blitsFlag;
        }

    }
    
    /**
     * Sets the value of the string arduinoOut.
     * 
     * This string is the value the server will send to the client.
     * 
     * @param input
     */
    public void setArduinoAutput(String input){
        arduinoOut = input;
    }
    
    /**
     * Returns the value of the string arduinoOut.
     * 
     * This string is the value the server will send to the client.
     * 
     * @return
     */
    public String getArduinoOutput(){
        return arduinoOut;
    }

    /**
     * Returns the value of the boolean parameter shutdown.
     * 
     * This parameter is used to shut down all the threads in the application.
     * 
     * @return
     */
    public boolean getShutdown(){
        return shutdown;
    }
    
    /**
     * Sets the value of the boolean parameter shutdown.
     * 
     * This parameter is used to shut down all the threads in the application.
     * 
     * @param input
     */
    public void setShutdown(boolean input){
        shutdown = input;
    }
    
    /**
     * Sets the value of the boolean parameter captureFlag.
     * 
     * This parameter is used to indicate that the camera class is done 
     * capturing images.
     * 
     * @param input
     */
    public void setCaptureDone(boolean input){
        synchronized (imageLock) {
        captureFlag = input;
        }
    }
    
    /**
     * Returns the value of the boolean parameter captureFlag.
     * 
     * This parameter is used to indicate that the camera class is done 
     * capturing images.
     * 
     * @return
     */
    public boolean getCaptureDone(){
        synchronized (imageLock) {
        return captureFlag;
        }
    }
    
    /**
     * Sets the value of the boolean parameter evalFlag.
     * 
     * This value is used to indicate that the Cluster class is done classifying 
     * the fillet.
     * @param input
     */
    public void setEvalDone(boolean input){
        synchronized (evalLock) {
        evalFlag = input;
        }
    }

    /**
     * Returns the value of the boolean parameter evalFlag.
     * 
     * This value is used to indicate that the Cluster class is done classifying 
     * the fillet.
     * 
     * @return
     */
    public boolean getEvalFlag(){
        synchronized (evalLock) {
        return evalFlag;
        }
    }
    
    /**
     * Sets the value of the boolean parameter autoDetect.
     * 
     * This value is used to indicate if object detection is turned on or off.
     * 
     * @param input
     */
    public void setAutoDetect(boolean input){
        synchronized (auto){
        autoDetect = input;
        }
    }

    /**
     * Returns the value of the boolean parameter autoDetect.
     * 
     * This value is used to indicate if object detection is turned on or off.
     * 
     * @return
     */
    public boolean getAutoDetect(){
        synchronized (auto){
        return autoDetect;
        }
    }
    
    /**
     * Sets the value of the boolean parameter videoStream.
     * 
     * This value is used to indicate if live stream is on or off.
     * 
     * @param input
     */
    public void setStreamFlag(boolean input){
        synchronized (stream){
        videoStream = input;
        }
    }
    
    /**
     * Returns the value of the boolean parameter videoStream.
     * 
     * This value is used to indicate if live stream is on or off.
     * 
     * @return
     */
    public boolean getStreamFlag(){
        synchronized (stream){
        return videoStream;
        }
    }
    
    /**
     * Sets the value from the input matrix to the videoFrame matrix.
     * 
     * This is the video frames in the live stream.
     * 
     * @param input
     */
    public void setVideoFrame(Mat input){
        videoFrame = input;
    }

    /**
     * Returns the matrix videoFrame.
     * 
     * This is the video frames in the live stream.
     * 
     * @return
     */
    public Mat getVideoFrame(){
        return videoFrame;
    }
    

}
