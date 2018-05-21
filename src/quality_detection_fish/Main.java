/*
 * The Main class starts all the threads and loads the cluster.
 */
package quality_detection_fish;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristian Kvam
 */
public class Main {

    /**
     * @param args the command line arguments
     *
     */
    public static void main(String[] args) {
        
        ExecutorService executor = Executors.newFixedThreadPool(7);

        System.load("C:\\Users\\krist\\Documents\\opencv\\build\\java\\x64\\opencv_java330.dll");

        StorageBox storage = new StorageBox();

        Server server = new Server(storage);
        
        WriteData write = new WriteData();
        
        Cluster cluster = new Cluster(storage, write);

        ImageMethods methods = new ImageMethods();
        
        Camera camera = new Camera(storage, server);
        
        ManualMethods check = new ManualMethods(storage, camera);
        
        Logging logg = new Logging();
        
        PlotGraph plot = new PlotGraph();

        GUI gu = new GUI(check, server, storage, cluster, executor, plot);
        
        ImageData data = new ImageData(storage, methods, write, cluster, gu, logg);
        
        ObjectDetection detection = new ObjectDetection(camera, storage, gu);
        
        TakeImage auto = new TakeImage(server, camera, storage, gu, detection);
        
        DataDistribution dataDist = new DataDistribution(storage, server, gu, logg, plot);
        
        LiveStream liveStream = new LiveStream(storage, camera, gu);
        

        gu.setVisible(true);
        executor.execute(camera);
        executor.execute(server);
        executor.execute(auto);
        executor.execute(data);
        executor.execute(dataDist);
        executor.execute(detection);
        executor.execute(liveStream);

        // Loads the cluster at startup
        try {
            cluster.loadCluster();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
