/*
 * The LiveStream class grabs video frames from the camera and displays 
 * them in the GUI.
 */
package quality_detection_fish;

import org.opencv.core.Mat;

/**
 *
 * @author Kristian Kvam
 */
public class LiveStream extends Thread{
    
    StorageBox box;
    GUI gui;
    Camera cam;
    
    private Mat videoFrame = new Mat();
    
    public LiveStream(StorageBox storage, Camera camera, GUI gu){
        this.box = storage;
        this.cam = camera;
        this.gui = gu;
    }
    
    @Override
    public void run(){
        while(!box.getShutdown()){
            while(box.getStreamFlag()){
               videoFrame = box.getVideoFrame();
               gui.setStreamFrame(videoFrame);
                
            }
        }
    }
    
}
