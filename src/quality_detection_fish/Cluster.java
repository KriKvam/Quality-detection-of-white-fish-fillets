/*
 * The Cluster class holds methods for building and training a cluster 
 * using K-Means Clustering.
 */
package quality_detection_fish;

import java.util.Arrays;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author Kristian Kvam
 */
public class Cluster {

    StorageBox box;
    WriteData writer;
    SimpleKMeans model;
    SimpleKMeans finalCluster;
    
    private boolean trainingFlag = false;

    public Cluster(StorageBox storage, WriteData write) {
        this.box = storage;
        this.writer = write;
    }

    /**
     * Retrieves dataset from the input string and builds and trains a K-Means 
     * model. The model has 4 clusters. The cluster is then saved to destination
     * and loaded as the new cluster model.
     * @param input
     * @throws Exception
     */
    public void runTrainingSet(String input) throws Exception {
        String dataset = input;
        // Load dataset
        DataSource source = new DataSource(dataset);
        // get instances object
        Instances data = source.getDataSet();
        // new instance of clusterer
        model = new SimpleKMeans();
        // number of clusters
        model.setNumClusters(4);
        // build the clusterer
        model.buildClusterer(data);
        // Change location to your prefered location.
        SerializationHelper.write("C:\\Users\\krist\\Dropbox\\Høst 2017\\Sanntids data-teknikk\\Java-kode\\Quality_Detection_Fish\\cluster.arff", model);
        trainingFlag  = true;
        loadCluster();
        System.out.println(model);

    }

    /**
     * Loads a cluster model and defines it as the one to be used.
     * @throws Exception
     */
    public void loadCluster() throws Exception {
        // Change the string to your file location.
        String dataset = "C:\\Users\\krist\\Dropbox\\Høst 2017\\Sanntids data-teknikk\\Java-kode\\Quality_Detection_Fish\\cluster.arff";

        finalCluster = (SimpleKMeans) SerializationHelper.read(dataset);
        System.out.println(finalCluster);
    }

    /**
     * Loads an ARFF file containing the data of one object, evaluates the 
     * data with the cluster and stores the cluster evaluation in the StorageBox
     * class.
     * @throws Exception
     */
    public void checkCluster() throws Exception {

        ClusterEvaluation clsEval = new ClusterEvaluation();
        // Change the string to your file location.
        String dataset1 = "C:\\Users\\krist\\Dropbox\\Høst 2017\\Sanntids data-teknikk\\Java-kode\\Quality_Detection_Fish\\currentFillet.arff";
        // load dataset
        DataSource source1 = new DataSource(dataset1);
        // get instances object
        Instances data1 = source1.getDataSet();

        clsEval.setClusterer(finalCluster);
        clsEval.evaluateClusterer(data1);

        box.setFilletClass(Arrays.toString(clsEval.getClusterAssignments()));
        box.setEvalDone(true);
        
        System.out.println(Arrays.toString(clsEval.getClusterAssignments()));
    }
    
    /**
     * Sets a boolean value trainingFlag.
     * This value is used to check if training of the cluster is finished.
     * @param input
     */
    public void setTrainingFlag(boolean input){
        trainingFlag = input;
    }

    /**
     * Returns the value of trainingFlag.
     * This value is used to check if training of the cluster is finished.
     * @return
     */
    public boolean getTrainingFlag(){
        return trainingFlag;
    }
   

}
