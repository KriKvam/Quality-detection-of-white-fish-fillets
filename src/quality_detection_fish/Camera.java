/*
 * The Camera class holds methods for capturing images
 */
package quality_detection_fish;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author Kristian Kvam
 */
public class Camera extends Thread {

    StorageBox box;
    Server serv;
    
    private Mat img = new Mat(); // Image destination
    
    /* The value of videoCapture chooses between cameras.
       If you only have one camera connected it should stay at "0". */
    VideoCapture camera = new VideoCapture(0);
    

    public Camera(StorageBox storage, Server server) {
        box = storage;
        serv = server;
    }


    @Override
    public void run() {
        while (!box.getShutdown()) {
            camera.read(img);
                box.setVideoFrame(img);
        }
        System.out.println("Camera shutdown");
        camera.release();
    }

    /**
     * Grabs a frame from the VideoCapture and returns it as a matrix.
     * @return an image from the camera.
     * @throws InterruptedException if no camera is connected.
     */
    public Mat manualCapture() throws InterruptedException {

        if (!camera.isOpened()) {
            System.out.println("Camera Error");
        } else {
            System.out.println("Camera OK");
        }

        if (img.empty()) {
            System.out.println("No image found...");
        } else {
            System.out.println("Image loaded: " + img.height() + "*" + img.width());

        }
        Thread.sleep(500);
        return(img);
    }

    /**
     * Stores different images of type Mat in the StorageBox class based on the 
     * input. Valid inputs are: "red", "green" and "blue".
     * @param input string to tell where the images captured are to be saved.
     */
    public void autoCapture(String input) {

        switch (input) {
            case "red": {
                box.setCaptureDone(true);
                box.setRedImage(img);
                box.setOrgImage(img);
            }
            break;
            case "green": {
                box.setGreenImage(img);
            }
            break;
            case "blue": {
                box.setBlueImage(img);
            }
            break;
            default:
                System.out.println("Invalid input");
                break;
        }

    }


    /**
     * Grabs a frame from the VideoCapture and returns it as a matrix.
     * @return an image from the camera.
     */
    public Mat getImage(){
        return img;
    }
    

}
