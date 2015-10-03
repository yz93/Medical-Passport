/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medpassport;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;

/**
 *
 * @author Joseph
 */
public class ExcelAdapter1  implements ActionListener {
    private Clipboard system;
    private JTable jTable1 ;
    /**
     * The Excel Adapter is constructed with a
     * JTable on which it enables Copy-Paste and acts
     * as a Clipboard listener.
     */

    public ExcelAdapter1(JTable myJTable) {
        jTable1 = myJTable;
        KeyStroke backspace = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        // Identifying the Paste KeyStroke user can modify this
        //to copy on some other Key combination.
        jTable1.registerKeyboardAction(this, "Backspace", backspace, JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this, "Delete", delete, JComponent.WHEN_FOCUSED);
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Public Accessor methods for the Table on which this adapter acts.
     */
    public JTable getJTable() {return jTable1;}
    public void setJTable(JTable jTable1) {this.jTable1 = jTable1;}
    /**
     * This method is activated on the Keystrokes we are listening to
     * in this implementation. Here it listens for Copy and Paste ActionCommands.
     * Selections comprising non-adjacent cells result in invalid selection and
     * then copy action cannot be performed.
     * Paste is done by aligning the upper left corner of the selection with the
     * 1st element in the current selection of the JTable.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo("Backspace") == 0) {
            int numcols = jTable1.getSelectedColumnCount();
            int numrows = jTable1.getSelectedRowCount();
            int startRow = (jTable1.getSelectedRows())[0];
            int startCol = (jTable1.getSelectedColumns())[0];

            try {
                for (int i = startRow; i < startRow + numrows; i++) {
                    for (int j = startCol; j < startCol + numcols; j++) {
                        jTable1.setValueAt(null, i, j);
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getActionCommand().compareTo("Delete") == 0) {
            int numcols = jTable1.getSelectedColumnCount();
            int numrows = jTable1.getSelectedRowCount();
            int startRow = (jTable1.getSelectedRows())[0];
            int startCol = (jTable1.getSelectedColumns())[0];

            try {
                for (int i = startRow; i < startRow + numrows; i++) {
                    for (int j = startCol; j < startCol + numcols; j++) {
                        jTable1.setValueAt(null, i, j);
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
   }
}