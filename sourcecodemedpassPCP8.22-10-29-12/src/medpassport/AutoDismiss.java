/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package medpassport;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Sunghoon Ivan Lee
 */
public class AutoDismiss implements ActionListener
{
    private JDialog dialog;

    public AutoDismiss(JDialog dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        dialog.dispose();
    }    

    // EDIT: in response to comment
    static public void showMessageDialog(Component parent, Object message) {
      // run all of this on the EDT
      final JOptionPane optionPane = new JOptionPane(message);
      String title = UIManager.getString("OptionPane.messageDialogTitle");
      //int style = styleFromMessageType(JOptionPane.INFORMATION_MESSAGE);
      //final JDialog dialog = optionPane.createDialog(parent, title, 0);
      final JDialog dialog = optionPane.createDialog(parent, title);
      Timer timer = new Timer(500, new AutoDismiss(dialog));
      timer.setRepeats(false);
      timer.start();
      if (dialog.isDisplayable())
          dialog.setVisible(true);
    }
}