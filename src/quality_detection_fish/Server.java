package quality_detection_fish;


import arduino.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

    StorageBox box;

    private char output = '0';
    private String input = "0";
    private String auto = "0";
    
    Arduino arduino;

    public Server(StorageBox storage) {
        this.box = storage;
    }

    @Override
    public void run() {
       try{
        arduino = new Arduino("COM3", 345600); //enter the port name here, and ensure that Arduino is connected, otherwise exception will be thrown.
        arduino.openConnection();
       }
       catch (NullPointerException ex) {
                Logger.getLogger(ImageData.class.getName()).log(Level.SEVERE, null, ex);
            }
        while (!box.getShutdown()) {
            while (box.getBlitsFlag()) {

                arduino.serialWrite(output);
                box.setBlitsFlag(false);
                System.out.println("Server print");

            }
            input = arduino.serialRead();
            if (input.contains("ok")) {
                box.setArduinoAutput("ok");
                auto = "ok";
            } else {
                auto = "0";
            }
        }
        
        System.out.println("Server shutdown");

    }

    /**
     * Sets the output parameter to be sent to the arduino client.
     * @param input
     */
    public void setOutput(char input) {
        output = input;
    }

    /**
     * Returns the parameter read from the arduino client.
     * @return
     */
    public String getInput() {
        return auto;
    }

}
