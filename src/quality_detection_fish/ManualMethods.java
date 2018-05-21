/*
 * The ManualMethods class holds image processing methods used for manual 
 * image controll.
 */
package quality_detection_fish;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static org.opencv.core.Core.bitwise_not;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;
import static org.opencv.imgproc.Imgproc.adaptiveThreshold;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 *
 * @author Kristian Kvam
 */
public class ManualMethods {

    StorageBox box;
    Camera camera;

    private Mat gray = new Mat();
    private Mat org = new Mat();
    private Mat binary = new Mat();
    private Mat bloodBin = new Mat();
    private Mat erode_dst = new Mat();
    private Mat dilate_dst = new Mat();
    
    double filletArea = 0;
    double bloodArea = 0;
    int listNr = 0;
    List<MatOfPoint> draw = new ArrayList<>();

    public ManualMethods(StorageBox storage, Camera cam) {
        this.box = storage;
        this.camera = cam;
    }

    /**
     * Applies Otsu threshold to the input image, erodes and dilates the 
     * image and returns the area of the biggest white blob in the image.
     * 
     * This is used to find the area of the fillet.
     * This is used for manual control in the GUI.
     * 
     * @return
     */
    public double manAreaCheck() {
        try {
            org = camera.manualCapture();
        } catch (InterruptedException ex) {
            Logger.getLogger(ManualMethods.class.getName()).log(Level.SEVERE, null, ex);
        }

        cvtColor(org, gray, COLOR_BGR2GRAY);
        threshold(gray, binary, 0, 255, THRESH_BINARY + THRESH_OTSU);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
        Imgproc.erode(binary, erode_dst, element);
        Imgproc.dilate(erode_dst, dilate_dst, element);

        filletArea = manMaxGridCheck(dilate_dst);

        return filletArea;
    }

    /**
     * Finds the contours of the white blobs in a binary image and returns the
     * area of the biggest blob.
     * @param input
     * @return
     */
    public double manMaxGridCheck(Mat input) {
        Mat out = new Mat();
        double maxArea = 0;
        int counter = 0;

        Imgproc.findContours(input, draw, out, RETR_TREE, CHAIN_APPROX_SIMPLE);

        Iterator<MatOfPoint> iterator = draw.iterator();
        
        while (iterator.hasNext()) {
            counter++;
            MatOfPoint contour = iterator.next();
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                listNr = counter;
            }
        }

        return maxArea;
    }

    /**
     * Finds the contours of the white blobs in a binary image and returns the
     * total area of all the white blobs.
     * @param input
     * @return
     */
    public double manTotalGridCheck(Mat input) {
        Mat out = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        double totalArea = 0;

        Imgproc.findContours(input, contours, out, RETR_TREE, CHAIN_APPROX_SIMPLE);

        Iterator<MatOfPoint> iterator = contours.iterator();

        while (iterator.hasNext()) {

            MatOfPoint contour = iterator.next();
             totalArea += Imgproc.contourArea(contour);
            
        }

        return totalArea;
    }

    /**
     * Applies an adaptive threshold to the input image, complements the image
     * and returns the total area of all the white blobs.
     * 
     * This is used to find the total area of the blood spots on the fillet.
     * This is used for manual control in the GUI.
     * @return
     */
    public double manBloodThreshold() {
        
        cvtColor(org, gray, COLOR_BGR2GRAY);
        adaptiveThreshold(gray, bloodBin, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 45, 50); //15, 40
        
        bitwise_not(bloodBin, bloodBin);

        bloodArea = manTotalGridCheck(bloodBin);

        return bloodArea;
    }

    static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    /**
     * Returns an BufferedImage of the matrix org.
     * 
     * This is the image grabbed from the camera.
     * 
     * @return
     * @throws Exception
     */
    public BufferedImage getOrg() throws Exception {
        BufferedImage orgImage = Mat2BufferedImage(org);
        return orgImage;
    }

    /**
     * Returns an BufferedImage of the matrix dilate_dst.
     * 
     * This is the image grabbed from the camera, with contours drawn on the
     * image to highlight the edges of the fillet.
     * 
     * @return
     * @throws Exception
     */
    public BufferedImage getBinary() throws Exception {
        Mat draw = drawContour(dilate_dst);
        
        BufferedImage binImage = Mat2BufferedImage(draw);

        return binImage;
    }

    /**
     * Returns an BufferedImage of the matrix bloodBin.
     * 
     * This is the image captured by the camera, with contours drawn on the 
     * image to highlight the edges of the blood spots on the fillet.
     * 
     * @return
     * @throws Exception
     */
    public BufferedImage getBlood() throws Exception {
        Mat draw = drawContour(bloodBin);
        BufferedImage bloodImage = Mat2BufferedImage(draw);
        return bloodImage;
    }
    
    /**
     * Converts the input matrix to a binary image using Otsu threshold, erodes
     * and dilates the image, then returns it.
     * 
     * @param input
     * @return
     */
    public Mat convertToBinary(Mat input){
        Mat binImage = input;
        cvtColor(binImage, binImage, COLOR_BGR2GRAY);
        threshold(binImage, binImage, 0, 255, THRESH_BINARY + THRESH_OTSU);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
        Imgproc.erode(binImage, binImage, element);
        Imgproc.dilate(binImage, binImage, element);
        
        return binImage;
    }
    
    /**
     * Converts the input matrix to an binary image using adaptive threshold, 
     * complements the image, then returns it.
     * 
     * @param input
     * @return
     */
    public Mat convertToAdaptiveBinary(Mat input){
        Mat adapImage = input;
        cvtColor(adapImage, adapImage, COLOR_BGR2GRAY);
        adaptiveThreshold(adapImage, adapImage, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 45, 50); //15, 40
        
        bitwise_not(adapImage, adapImage);
        
        return adapImage;
    }
    
    /**
     * Finds contours in the input matrix, draws the contours onto the original
     * image grabbed from the camera and returns the matrix.
     * 
     * @param input
     * @return
     */
    public Mat drawContour(Mat input){
        Mat contourImg = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat image = new Mat();
        input.convertTo(image, CvType.CV_32SC1);
        
        Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_FLOODFILL, Imgproc.CHAIN_APPROX_SIMPLE);
        
        contourImg = org.clone();//new Mat(image.size(),CV_8UC3);
        for(int i = 0; i < contours.size(); i++){
            Imgproc.drawContours(contourImg, contours, i, new Scalar(45, 170, 41), 1);
        }
        return contourImg;
    }

}
