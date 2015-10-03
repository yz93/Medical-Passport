/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medpassport;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import java.awt.Toolkit;
/**
 *
 * @author Joseph
 */
public class SavingScreen extends JWindow {
    private ImageIcon splashImage = new ImageIcon(getClass().getResource("/medpassport/resources/Progress.gif"));
    private int width, height;
    public JLabel jlblImage = new JLabel();
    public JProgressBar jProgressBar1 = new JProgressBar();

	public SavingScreen() {
		super();
		try  {
			jbInit();

			// Center the screen
			Toolkit tk = this.getToolkit();

			width = 300;  // image is 500 wide
			height = 185; // image is 330 tall. Add a bit for the progress bar
			int x = (tk.getScreenSize().width - width) / 2;
			int y = (tk.getScreenSize().height - height) / 2;
			this.setLocation(x, y);
			this.setSize(width, height);
            this.setVisible(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    private void jbInit() throws Exception {
        //jlblImage.setText("jLabel1");
        jlblImage.setIcon(splashImage);
        jProgressBar1.setStringPainted(true);
        getContentPane().add(jlblImage, BorderLayout.CENTER);
        getContentPane().add(jProgressBar1, BorderLayout.SOUTH);
    }


    /**
     * Sets the text of the progress bar and its value
     *
     * @param msg The message to be displayed in the progress bar
     * @param theVal An integer value from 0 to 100
     */
    public void setStatus(String msg, int value) {
        jProgressBar1.setString(msg);
        jProgressBar1.setValue(value);
    }
}
