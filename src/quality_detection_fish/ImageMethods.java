/*
 * The ImageMehtods class holds image processing methods.
 */
package quality_detection_fish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static org.opencv.core.Core.bitwise_not;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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
public class ImageMethods {

    private Mat gray = new Mat();
    private Mat gray2 = new Mat();
    private Mat binary = new Mat();
    private Mat binary2 = new Mat();
    private Mat erode_dst = new Mat();
    private Mat dilate_dst = new Mat();

    private Mat bloodBin = new Mat();
    private Mat erode = new Mat();
    private Mat dilate = new Mat();
    private Mat masked = new Mat();

    double filletArea = 0;
    double bloodArea = 0;

    int listNr = 0;

    List<MatOfPoint> draw = new ArrayList<>();

    public ImageMethods() {

    }

    /**
     * Applies Otsu threshold to the input image, erodes and dilates the image
     * and returns the area of the biggest white blob in the image.
     *
     * This is used to find the area of the fillet.
     *
     * @param input image
     * @return the area of the largest white blob found in the image.
     */
    public double areaCheck(Mat input) {
        Mat org = input;
        cvtColor(org, gray, COLOR_BGR2GRAY);
        threshold(gray, binary, 0, 255, THRESH_BINARY + THRESH_OTSU);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
        Imgproc.erode(binary, erode_dst, element);
        Imgproc.dilate(erode_dst, dilate_dst, element);

        filletArea = maxGridCheck(dilate_dst);

        System.out.println("Area of fillet: " + filletArea);

        return filletArea;
    }

    /**
     * Finds the contours of the white blobs in a binary image and returns the
     * area of the biggest blob.
     *
     * @param input binary image
     * @return the area of the largest white blob in the image.
     */
    public double maxGridCheck(Mat input) {
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
     *
     * @param input binary image
     * @return the total area of all the white blobs found in the image
     */
    public double totalGridCheck(Mat input) {
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
     * Applies several image processing techniques to the image to create a 
     * binary image.
     *
     * This is used to find the total area of the blood spots on the fillet.
     *
     * @param input image
     * @return the total area of all the white blobs found in the image.
     */
    public double bloodThreshold(Mat input) {
        Mat img = input;
        // Convert to gray-scale
        cvtColor(img, gray2, COLOR_BGR2GRAY);
        // Binary threshold (OTSU)
        threshold(gray2, binary2, 0, 255, THRESH_BINARY + THRESH_OTSU);
        // Create structure element
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        // Erode image
        Imgproc.erode(binary2, erode, element);
        // Dilate image
        Imgproc.dilate(erode, dilate, element);
        // Mask image
        gray.copyTo(masked, dilate);
        // Adaptive threshold
        adaptiveThreshold(gray2, bloodBin, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 45, 50); //15, 40
        // Complement the image
        bitwise_not(bloodBin, bloodBin);
        // Flood fill the image
        Mat floodfilled = Mat.zeros(bloodBin.rows() + 2, bloodBin.cols() + 2, CvType.CV_8U);
        Imgproc.floodFill(bloodBin, floodfilled, new Point(200, 100), new Scalar(0));

        bloodArea = totalGridCheck(bloodBin);

        return bloodArea;
    }

}
