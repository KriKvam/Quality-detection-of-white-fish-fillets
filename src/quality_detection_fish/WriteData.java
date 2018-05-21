/*
 * The WriteData class writes and saves ARFF fils.
 */
package quality_detection_fish;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;

/**
 *
 * @author Kristian Kvam
 */
public class WriteData {

    public WriteData() {

    }

    /**
     * Creates a ARFF file using the input parameters.
     * 
     * This is used to write the data from the fillet to an ARFF file.
     * The file will be overwritten when the method is called again.
     * 
     * @param a
     * @param b
     * @param c
     * @param d
     * @throws IOException
     */
    public void writeArff(int a, int b, int c, int d) throws IOException {
        FastVector atts;
        Instances data;
        double[] vals;

        // 1. set up attributes
        atts = new FastVector();
        // - numeric
        atts.addElement(new Attribute("filletArea"));
        // - numeric
        atts.addElement(new Attribute("hbArea"));
        // - numeric
        atts.addElement(new Attribute("metHbArea"));
        // - numeric
        atts.addElement(new Attribute("oxyHbArea"));

        // 2. create Instances object
        data = new Instances("fish", atts, 0);

        // 3. fill with data
        // first instance
        vals = new double[data.numAttributes()];
        // - numeric
        vals[0] = a;
        // - nominal
        vals[1] = b;
        // - string
        vals[2] = c;
        // - date
        vals[3] = d;

        // add
        data.add(new DenseInstance(1.0, vals));

        // 4. output data
        System.out.println(data);

        // Change the location to your prefered file location.
        BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\krist\\Desktop\\currentFillet.arff"));
        writer.write(data.toString());
        writer.flush();
        writer.close();
    }
    

}
