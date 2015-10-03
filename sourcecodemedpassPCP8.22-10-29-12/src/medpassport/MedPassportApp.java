/*
 * MedPassportApp.java
 */

package medpassport;

//import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class MedPassportApp extends SingleFrameApplication {
    static SplashScreen ss = new SplashScreen();
    //SplashScreen ss1;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() 
    {
        //ss.setStatus("loading...", 100);
        show(new MedPassportView(this));
        ss.setVisible(false);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override 
    protected void configureWindow(java.awt.Window root) { }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of MedPassportApp
     */
    public static MedPassportApp getApplication() {
        return Application.getInstance(MedPassportApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) throws InterruptedException {
        ss.setStatus("loading...", 0);
        //ss.setStatus("loading...", 0);
        launch(MedPassportApp.class, args);
        ss.setStatus("loading...", 10);
        ss.setStatus("loading...", 20);
        ss.setStatus("loading...", 30);
        ss.setStatus("loading...", 40);
        ss.setStatus("loading...", 50);
        ss.setStatus("loading...", 60);
        ss.setStatus("loading...", 70);
        ss.setStatus("loading...", 80);
        ss.setStatus("loading...", 90);
        ss.setStatus("loading...", 100);
    }
}
