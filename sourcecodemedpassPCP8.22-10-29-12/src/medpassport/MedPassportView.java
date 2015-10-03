/*
 * MedPassportView.java
 */

package medpassport;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import com.enterprisedt.net.ftp.FTPMessageCollector;
import com.enterprisedt.net.ftp.FTPTransferType;
import java.awt.Cursor;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultEditorKit;

/**
 * The application's main frame.
 */
public class MedPassportView extends FrameView implements ActionListener{
    private final Dimension preferredFrameSize = new Dimension(750, 700);
    private final Dimension preferredChartFrameSize = new Dimension(513, 255);
    private final String passwd = "bebedoc2009";
    private int driveCounts = 0, checkImageUpload = 0, errorCount = 0, copyCount = 0;
    private char[] letters = new char[23];
    private String tempImage = null;
    private String[] tempList = new String[1000];
    private String[] tempList1 = new String[1000];
    private String[] tempImg = new String[1000];
    private String[] tempImg1 = new String[1000];
    private String[] tempCopy = new String[1000];
    private String[] tempCopy1 = new String[1000];
    private String host, port;
    private int tempCount = 0, tempICount = 0, tempCount1 = 0, tempICount1 = 0;
    private boolean READ_ONLY = false;
    private SavingScreen ss;
    boolean working = false;

    private final String[] driveLetters = {"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private final String[] growthColNames = {"Months","0","2","4","6","9","12","15","18","24","36"};
    private final String[] bmiColNames = {"Years","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
    private final String[] schedColNames = {"Vaccine\\Age","Birth","1m","2m","4m","6m","12m","15m","18m","2y","4-6y","11-12y","13-18y","18-40y","40-60y","60-100y"};
    
    private Thread colorChanger;

    //used by the theme updater calls:
    //making these and many of the design components public is bad programming practice
    //there should eventually be a getter/setter for each component's background color property
    public String gender = "None";
    public String prevGender = "None";

    private Object[] row1_male = {"",new Double(8), new Double(12), new Double(15), new Double(17), new Double(20), new Double(23), new Double(24.5), new Double(26), new Double(28), new Double(32)};
    private Object[] row3_male = {"",new Double(18.75), new Double(23), new Double(25), new Double(26.25), new Double(28), new Double(29.75), new Double(31), new Double(32.25), new Double(34.25), new Double(38)};
    private Object[] row5_male = {"",new Double(14),new Double(15.75), new Double(16.25), new Double(17.25), new Double(17.75), new Double(18.25), new Double(18.5), new Double(18.75), new Double(19.25), new Double(19.75)};
    private Object[] bmiRow_male = {"",new Double(16.5), new Double(16), new Double(15.5), new Double(15.2), new Double(15), new Double(15.2), new Double(15.5), new Double(16), new Double(16.5), new Double(17), new Double(18), new Double(18.5), new Double(19), new Double(19.5), new Double(20.5), new Double(21), new Double(21.5), new Double(22.5), new Double(23)};

    private Object[] row1_female = {"",new Double(7), new Double(11), new Double(13.5), new Double(16), new Double(19), new Double(21), new Double(23), new Double(24), new Double(26.5), new Double(30.5)};
    private Object[] row3_female = {"",new Double(18.75), new Double(24), new Double(24.5), new Double(24.75), new Double(27), new Double(29), new Double(31.5), new Double(31.5), new Double(34), new Double(37.5)};
    private Object[] row5_female = {"",new Double(13.75),new Double(15.25), new Double(16.25), new Double(16.75), new Double(17.25), new Double(17.75), new Double(18), new Double(18.25), new Double(18.75), new Double(19.25)};
    private Object[] bmiRow_female = {"",new Double(16.5), new Double(15.5), new Double(15), new Double(15), new Double(15), new Double(15.5), new Double(16), new Double(16.5), new Double(17), new Double(17.5), new Double(18), new Double(18.5), new Double(19.5), new Double(20), new Double(20.5), new Double(21), new Double(21.5), new Double(21.5), new Double(22)};

    private Double[] standard_LVW_xAxis = {18.0, 20.0, 22.0, 24.0, 26.0, 28.0, 30.0, 32.0, 36.0, 40.0};
    private Double[] standard_M_LVW_05 =  { 4.0,  6.5,  9.0, 12.0, 14.5, 17.0, 19.0, 21.5, 26.0, 31.0};
    private Double[] standard_M_LVW_50 =  { 5.0,  8.0, 10.5, 13.5, 16.0, 19.0, 22.0, 24.0, 29.0, 35.0};
    private Double[] standard_M_LVW_95 =  { 6.5,  9.5, 12.5, 16.0, 19.0, 22.0, 25.0, 28.0, 33.5, 40.0};
    private Double[] standard_F_LVW_05 =  { 4.0,  6.5,  9.0, 11.5, 14.0, 16.5, 19.0, 21.0, 25.5, 30.5};
    private Double[] standard_F_LVW_50 =  { 5.0,  8.0, 10.5, 13.5, 16.0, 19.0, 21.5, 24.0, 29.0, 34.5};
    private Double[] standard_F_LVW_95 =  { 7.0,  9.5, 12.2, 16.0, 19.0, 22.0, 24.5, 27.5, 33.0, 41.0};

    private Double[] standard_M_length_05 = { 18.0, 21.5, 23.5, 25.0, 26.5, 28.0, 29.0, 30.0, 32.0, 35.5};
    private Double[] standard_M_length_95 = { 21.5, 24.5, 26.5, 28.0, 30.0, 32.0, 33.0, 34.5, 37.0, 40.5};
    private Double[] standard_F_length_05 = { 18.0, 21.0, 22.5, 24.0, 26.0, 27.0, 28.5, 29.5, 31.5, 35.0};
    private Double[] standard_F_length_95 = { 21.0, 24.0, 26.0, 27.5, 29.5, 31.0, 32.5, 34.0, 36.0, 40.0};

    private Double[] standard_M_weight_05 = {  6.0,  9.0, 12.0, 14.0, 17.0, 19.0, 20.5, 22.0, 23.5, 26.5};
    private Double[] standard_M_weight_95 = { 10.0, 14.0, 18.0, 21.0, 25.0, 27.0, 29.5, 31.0, 33.5, 38.0};
    private Double[] standard_F_weight_05 = {  6.0,  8.5, 11.0, 13.0, 15.5, 17.5, 19.0, 20.5, 22.5, 25.5};
    private Double[] standard_F_weight_95 = {  9.0, 13.0, 16.5, 19.0, 22.5, 25.0, 27.0, 29.0, 32.0, 38.0};

    private Double[] standard_M_hc_05 = { 12.75, 14.75, 15.75, 16.5, 17.0, 17.5, 17.75, 18.0, 18.25, 18.5};
    private Double[] standard_M_hc_95 = { 15.25, 16.75, 17.5, 18.0, 18.75, 19.0, 19.5, 19.75, 20.0, 20.5};
    private Double[] standard_F_hc_05 = { 12.75, 14.5, 15.25, 16.0, 16.5, 17.0, 17.25, 17.5, 17.75, 18.0};
    private Double[] standard_F_hc_95 = { 15.0, 16.25, 17.0, 17.5, 18.25, 18.5, 19.0, 19.25, 19.5, 20.15};

    private Double[] standard_M_bmi_05 = { 14.8, 14.3, 14.0, 13.8, 13.7, 13.7, 13.8, 14.0, 14.2, 14.5, 15.0, 15.5, 16.0, 16.5, 17.0, 17.5, 18.1, 18.7, 19.0};
    private Double[] standard_M_bmi_95 = { 19.0, 18.1, 17.9, 18.0, 18.5, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 27.5, 28.1, 29.0, 29.5, 30.5};
    private Double[] standard_F_bmi_05 = { 14.4, 14.0, 13.8, 13.5, 13.4, 13.4, 13.5, 13.8, 14.0, 14.5, 15.0, 15.4, 15.9, 16.3, 16.8, 17.2, 17.5, 17.8, 17.9};
    private Double[] standard_F_bmi_95 = { 19.1, 18.3, 18.0, 18.3, 19.0, 19.5, 20.5, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 29.5, 30.5, 31.0, 32.0};

    public final String currentDirectory = ".";
    public String selectedPatient = "";
    public Entry selectedEntry;
    public ImagePanel patientImage;
    public ImagePanel growthImage;
    public String growthImageLoc;
    
    public DatabaseList patients = new DatabaseList(currentDirectory);
    public ListSelectionModel listSelectionModel1;
    public boolean isNull = false;
    public boolean imageUpdated = false;    
    private javax.swing.JTextPane currentZoomedField = null;
    private int currentZoomedFlag = 0;
    private JFrame mainFrame = MedPassportApp.getApplication().getMainFrame();
    //the above variable is never used but need to be place to make setDefaultCloseOperation() function to work

    //public String d = ((new SimpleDateFormat("yyyy-MM-dd")).format(new Date())).toString();

    public MedPassportView(SingleFrameApplication app) {
        super(app);
        patients.populatePatients();

        getFrame().setTitle("MedPassport");
        initComponents();

        new FileDrop(imagePanel, new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   
                try{
                int option = 0, option1 = 0, reval = 0, c, index = 0, k = 0, l = 0;
                String imageF = null;
                File outputFile = null;
                File tempDir = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                File outputDir = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
                String list[] = tempDir.list();
                String list1[] = outputDir.list();
                
                if(files[files.length-1].getName().toLowerCase().endsWith(".jpg") || files[files.length-1].getName().toLowerCase().endsWith(".jpeg") || files[files.length-1].getName().toLowerCase().endsWith(".png") || files[files.length-1].getName().toLowerCase().endsWith(".bmp") || files[files.length-1].isDirectory())
                {  
                  int reply = JOptionPane.showConfirmDialog(null, "Are you sure to add "+files[files.length-1].getName(), "Add Picture", JOptionPane.YES_NO_OPTION);
                  
                  if(reply == JOptionPane.YES_OPTION)
                  {
                      File inputFile = new File(files[files.length-1].getCanonicalPath());
                      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile), 4096);
                      imageF = files[files.length-1].getName();
                      tempImage = imageF;

                      if (list != null) 
                      {
                        for (int i = 0; i < list.length; i++) {
                            if (list[i].equals(tempImage)) {
                                JOptionPane.showMessageDialog(null, "You already have the same image for this patient", "Same Image Error", JOptionPane.ERROR_MESSAGE);
                                return;
                               }
                           }
                      }
                      else if (list1 != null) {
                        for (int i = 0; i < list1.length; i++) {
                            if (list1[i].equals(tempImage)) {
                            JOptionPane.showMessageDialog(null, "You already have the same image for this patient", "Same Image Error", JOptionPane.ERROR_MESSAGE);
                            return;
                            }
                           }
                        }

            if (selectedEntry == null) {
                File Dir = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                Dir.mkdir();
                outputFile = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp" + File.separator + files[files.length-1].getName());
            }
            else
                outputFile = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator +files[files.length-1].getName());
            
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile), 4096);
            
            if (outputFile.exists()) {
                if (!outputFile.canWrite())
                    option = JOptionPane.showConfirmDialog(null, "FileCopy: destination file is unwriteable: " + outputFile.getName(), "Write Error", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.NO_OPTION)
                    JOptionPane.showMessageDialog(null, "FileCopy: existing file was not overwritten.", "Write Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                String parent = outputFile.getParent();

                if (parent == null)
                    parent = System.getProperty("user.dir");
                File dir = new File(parent);

                if (!dir.exists())
                    JOptionPane.showMessageDialog(null, "FileCopy: destination directory doesn't exist: " + parent, "Write Error", JOptionPane.ERROR_MESSAGE);
                if (dir.isFile())
                    JOptionPane.showMessageDialog(null, "FileCopy: destination is not a directory: " + parent, "Write Error", JOptionPane.ERROR_MESSAGE);
                if (!dir.canWrite())
                    JOptionPane.showMessageDialog(null, "FileCopy: destination directory is unwriteable: " + parent, "Write Error", JOptionPane.ERROR_MESSAGE);
            }

            while ((c = bis.read()) != -1)
                bos.write(c);

            bis.close();
            bos.close();
                  }
                  else if (reply == JOptionPane.NO_OPTION) 
                  {
                     //do nothing 
                  }
                          if (selectedEntry == null)
            selectedPatient = "temp";
        else if (selectedEntry != null && !selectedPatient.equals("temp")) {
           
            
            for (int i = 0; i < selectedEntry.images.length; i++) {
                tempImg[i] = selectedEntry.images.imageNames[i];
                tempList[i] = selectedEntry.images.labels[i];
            }
            
            if (checkImageUpload == 0) {
                for (int i = 0; i < selectedEntry.images.length; i++) {
                    tempCopy[i] = tempImg[i];
                    tempCopy1[i] = tempList[i];
                }
                copyCount = selectedEntry.images.length;
            }
            
            tempCount1 = selectedEntry.images.length;
            tempICount1 = selectedEntry.images.length;

            for (int i = 0; i < selectedEntry.images.length; i++)
                if (selectedEntry.images.imageNames[i].equals(patientImage.getImageName()))
                    l = i;
            tempList[l] = image_labelField.getText();

            //JOptionPane.showMessageDialog(null, tempImg[0], "Label", JOptionPane.ERROR_MESSAGE);
            selectedEntry.images = new ImageList(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            for (int i = 0; i < tempCount1; i++) {
                selectedEntry.images.imageNames[i] = tempImg[i];
                selectedEntry.images.labels[i] = tempList[i];
            }
            //
            //JOptionPane.showMessageDialog(null, tempCount1 + " " + this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
            //this.selectedEntry.images.setLabels(this.selectedEntry.images.imageNames, this.selectedEntry.images.labels);
            
            if (selectedEntry.images != null && patientImage != null)
                imagePanel.remove(patientImage);
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label1", JOptionPane.ERROR_MESSAGE);
            patientImage = new ImagePanel(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + imageF);
            imagePanel.add(patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
            imagePanel.repaint();
            patientImage.repaint();
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
            image_labelField.setText("No Description.");
            selectedEntry.images.add(imageF, image_labelField.getText());
            checkImageUpload++;
            tempImg[tempICount1++] = imageF;
            tempList[tempCount1++] = image_labelField.getText();
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length + " " + this.selectedEntry.images.imageNames[this.selectedEntry.images.length - 1], "Label", JOptionPane.ERROR_MESSAGE);

        }
        
        if (selectedPatient.equals("temp")) {
            selectedEntry = new Entry(currentDirectory + File.separator + "Entries" + File.separator + "temp" + File.separator, "patientInfo");
            if (selectedEntry.images.length <= 1) {
                patientImage = new ImagePanel(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + imageF);
                imagePanel.add(patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
                imagePanel.repaint();
                patientImage.repaint();
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
                image_labelField.setText("No Description.");
                selectedEntry.images.add(imageF, image_labelField.getText());
                tempImg1[tempICount++] = imageF;
                tempList1[tempCount++] = image_labelField.getText();
            }
            else {
                //JOptionPane.showMessageDialog(null, prevCount + "\n" + this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
                //if ((prevCount % (this.selectedEntry.images.length - 1)) == 0)
                for (int i = 0; i < selectedEntry.images.length; i++)
                    if (selectedEntry.images.imageNames[i].equals(patientImage.getImageName()))
                        k = i;

                tempList1[k] = image_labelField.getText();
                //JOptionPane.showMessageDialog(null, tempCount + " " + tempList1[tempCount - 1] + "\n" + this.selectedEntry.images.imageNames[tempCount - 1], "Label", JOptionPane.ERROR_MESSAGE);
                
                //this.selectedEntry.images = new ImageList(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label2", JOptionPane.ERROR_MESSAGE);
                for (int i = 0; i < selectedEntry.images.length; i++) {
                    selectedEntry.images.imageNames[i] = tempImg1[i];
                    selectedEntry.images.labels[i] = tempList1[i];
                }
                //this.selectedEntry.images.setLabels(this.selectedEntry.images.imageNames, this.selectedEntry.images.labels);
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.labels[0], "Label", JOptionPane.ERROR_MESSAGE);
                if (selectedEntry.images != null && patientImage != null)
                    imagePanel.remove(patientImage);
                
                patientImage = new ImagePanel(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + imageF);
                imagePanel.add(patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
                imagePanel.repaint();
                patientImage.repaint();
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
                image_labelField.setText("No Description.");
                selectedEntry.images.add(imageF, image_labelField.getText());
                tempImg1[tempICount++] = imageF;
                tempList1[tempCount++] = image_labelField.getText();
            }
        }
                }
                else
                {
                   JOptionPane.showMessageDialog(null, "Image file ONLY", "Alert", JOptionPane.ERROR_MESSAGE);
                }
                }
                catch(Exception e)
                {}
            }   // end filesDropped
        }); // end FileDrop.Listener
        
        
        
        //Doing this fixes the weird resize issue caused by memory of previous window size
        this.getFrame().setPreferredSize(this.preferredFrameSize);
        this.getFrame().setResizable(false);
        this.getFrame().setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        //this.getFrame().setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE); 
        this.getFrame().pack();
        this.getFrame().addWindowListener ( new WindowAdapter()
        {
            @Override
            public void windowClosing ( WindowEvent e )
            {
                int result = JOptionPane.showConfirmDialog((java.awt.Component) e.getSource(),"Do you want to exit?", "Warning", JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                  //System.exit(0);
                    if (selectedEntry != null)
                    {
                        statusLabel.setText("");
        
                        File file = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");

                        if (selectedEntry == null) {
                            deleteDir(file);
                            patients.getList().remove(patients.getList().find("temp"));
                            patients.populatePatients();  //re-populate the list
                            jList1.setModel(patients.getList());
                        } else {
                            if (checkImageUpload > 0) {
                                if (selectedEntry.images.length > copyCount) {
                                    for (int i = copyCount; i < selectedEntry.images.length; i++) {
                                        //JOptionPane.showMessageDialog(null, , "Write Error", JOptionPane.ERROR_MESSAGE);
                                        File imgFile = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + selectedEntry.images.imageNames[i]);
                                        deleteDir(imgFile);
                                        selectedEntry.images.imageNames[i] = "";
                                        selectedEntry.images.labels[i] = "";
                                    }
                                }
                            }
                        }
                        hidePatientPane();

                        if (growthTable.isEditing()) {
                            TableCellEditor cellEditor = growthTable.getCellEditor(growthTable.getEditingRow(), growthTable.getEditingColumn());
                            cellEditor.cancelCellEditing();
                        }
                        growthTable.clearSelection();
                        growthTable.removeEditor();

                        if (bmiTable.isEditing()) {
                            TableCellEditor cellEditor1 = bmiTable.getCellEditor(bmiTable.getEditingRow(), bmiTable.getEditingColumn());
                            cellEditor1.cancelCellEditing();
                        }
                        bmiTable.clearSelection();
                        bmiTable.removeEditor();

                        if (schedTable.isEditing()) {
                            TableCellEditor cellEditor2 = schedTable.getCellEditor(schedTable.getEditingRow(), schedTable.getEditingColumn());
                            cellEditor2.cancelCellEditing();
                        }
                        schedTable.clearSelection();
                        schedTable.removeEditor();
                        for (int i = 0; i < 1000; i++) {
                            tempList[i] = "";
                            tempList1[i] = "";
                            tempImg[i] = "";
                            tempImg1[i] = "";
                        }
                        tempCount = 0;
                        tempICount = 0;
                        tempCount1 = 0;
                        tempICount1 = 0;
                        checkImageUpload = 0;
                        copyCount = 0;
                    }
                    else 
                    {
                        File file1 = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                        deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".reg");
                        deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".zip");
                        deleteDir(file1);
                        System.exit(0);
                    }
                } /*else if (result == JOptionPane.NO_OPTION) {
                  //System.out.println("Do nothing");
                    File file1 = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                    deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".reg");
                    deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".zip");
                    deleteDir(file1);
                }*/
            }
        });

        try
        {
            this.introTextPane.setPage(getClass().getResource("/medpassport/resources/intro.html"));
            this.introTextPane.addHyperlinkListener(
            new HyperlinkListener()
            {
                public void hyperlinkUpdate(HyperlinkEvent evt)
                {
                    // if a link was clicked
                    if(evt.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
                    {
                        try
                        {
                            // launch a browser with the appropriate URL
                            BareBonesBrowserLaunch.openURL(evt.getURL().toString());
                        }
                        catch(Exception e)
                        {
                            System.out.println("I/O error launching external browser.");
                        }
                    }
                }
            }
        );

        }
        catch (Exception e) {}

        //this.introTextPane.setText("This is a mobile ‘Medical Passport’ that helps patients carry their own health records at all times. Only Physicians are allowed to add or delete information in the ‘Medical Passport’." +
        //                           "\n\nAny information that looks suspicious to the reader should be reported to the Primary Care Physician. The Primary Care Physicians name and phone number can be found on the page related to the patient’s personal information." +
        //                           "\n\nYou can click on www.bebedoc.com and learn FOR FREE about the most common children’s illnesses encountered at the Pediatrician’s office.");
        initTables();

        entry_mRadio.addActionListener(this);
        entry_fRadio.addActionListener(this);

        //updateTheme();

        listSelectionModel1 = jList1.getSelectionModel();
        listSelectionModel1.addListSelectionListener( new ListSelectionHandler1() );
        listSelectionModel1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        
        //The following codes will allow users to select all texts when a cell in a table is selected!
        //Without the following codes, the user cannot backspace when changes are made!
        //((javax.swing.DefaultCellEditor)schedTable.getDefaultEditor(new Object().getClass())).setClickCountToStart(1);
        schedTable.setCellSelectionEnabled(true);
        schedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                //The following statement see if the user wants to copy text to tables
                if (schedCopyCheck.isSelected() && (!schedCopyTxt.getText().equals(""))) {
                    int row = schedTable.rowAtPoint(e.getPoint());
                    int col = schedTable.columnAtPoint(e.getPoint());
                    schedTable.editCellAt(row, col);
                    java.awt.Component ed = schedTable.getEditorComponent();
                    ed.setSize(30,50);
                    JTextField jf = (JTextField)ed.getComponentAt(col, row);
                    jf.selectAll();
                    jf.requestFocusInWindow();
                    jf.setText(schedCopyTxt.getText());
                    schedTable.getName();
                }
            }               
        });
        
        growthTable.setCellSelectionEnabled(true);
        growthTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = growthTable.rowAtPoint(e.getPoint());
                int col = growthTable.columnAtPoint(e.getPoint());
                growthTable.editCellAt(row, col );
                java.awt.Component ed = growthTable.getEditorComponent();
                JTextField jf = (JTextField)ed.getComponentAt(col, row);
                jf.selectAll();
                jf.requestFocusInWindow();
                //The following statement see if the user wants to copy text to tables
                if (growthCopyCheck.isSelected() && (!growthCopyTxt.getText().equals("")))
                    jf.setText(growthCopyTxt.getText());                
            }               
        });
        
        bmiTable.setCellSelectionEnabled(true);
        bmiTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = bmiTable.rowAtPoint(e.getPoint());
                int col = bmiTable.columnAtPoint(e.getPoint());
                bmiTable.editCellAt(row, col );
                java.awt.Component ed = bmiTable.getEditorComponent();
                JTextField jf = (JTextField)ed.getComponentAt(col, row);
                jf.selectAll();
                jf.requestFocusInWindow();
                //The following statement see if the user wants to copy text to tables
                if (bmiCopyCheck.isSelected() && (!bmiCopyTxt.getText().equals("")))
                    jf.setText(bmiCopyTxt.getText());                
            }               
        });
        
        this.image_labelField.addFocusListener( new java.awt.event.FocusAdapter()
        {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
                String tmpTxt = image_labelField.getText();
                String origTxt = tmpTxt;
                for (int j = 0; j < ILLEGAL_CHARACTERS.length; j++)
                    tmpTxt = removeChar(tmpTxt, ILLEGAL_CHARACTERS[j]);
                
                if (!tmpTxt.equals(origTxt))
                {
                    JOptionPane.showMessageDialog(getFrame(), "You are not allowed to use the following characters in the image caption\n '/', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'", "Illegal Characters", JOptionPane.ERROR_MESSAGE);
                    image_labelField.setText(tmpTxt);
                }
            }
            //The following function removes a character from a string
            public String removeChar(String s, char c) {
                StringBuffer r = new StringBuffer(s);
                String c_str = Character.toString(c);
                while (r.indexOf(c_str) > 0) {
                    StringBuffer r_tmp = r;
                    r = new StringBuffer(r_tmp.length() - 1);
                    int idxChar = r_tmp.indexOf(c_str);
                    r.insert(0, r_tmp.subSequence(0, idxChar));
                    r.insert(idxChar, r_tmp.subSequence(idxChar+1, r_tmp.length()));
                }
                return r.toString();
            }
        });
        
        //Add lose-focus function for the image label textfield        
        this.entry_specialArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_specialArea.getText());
                    currentZoomedField = entry_specialArea;
                    getFrame().setEnabled(false);
            }               
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_primaryCareArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_primaryCareArea.getText());
                    currentZoomedField = entry_primaryCareArea;
                    getFrame().setEnabled(false);
            }
        });
        //Add lose-focus function for the image label textfield
        this.entry_medHistArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_medHistArea.getText());
                    currentZoomedField = entry_medHistArea;
                    getFrame().setEnabled(false);
            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_mentalHealthArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_mentalHealthArea.getText());
                    currentZoomedField = entry_mentalHealthArea;
                    getFrame().setEnabled(false);
            }
        });
        
        this.entry_medExamArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_medExamArea.getText());
                    currentZoomedField = entry_medExamArea;
                    getFrame().setEnabled(false);
            }
        });
        
        this.btn_medExam.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            //currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                           //entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    currentZoomedField = entry_medExamArea;
                    
                    if(currentZoomedField.getText().isEmpty())
                    {
                        zoomedText.setText("Date:"+"\n"+"History:"+"\n"+"PE:"+"\n\n"+"HR   RR   BP          Temp    O2 Saturation"+"\n\n"+"Impression:"+"\n\n"+"Plan:");
                    
                    }
                    else
                    {
                        zoomedText.setText(currentZoomedField.getText()+"\n----------------------------------------------------\nDate:"+"\n"+"History:"+"\n"+"PE:"+"\n\n"+"HR   RR   BP          Temp    O2 Saturation"+"\n\n"+"Impression:"+"\n\n"+"Plan:");
                    }
                    
                    getFrame().setEnabled(false);
            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_socialIssuesArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_socialIssuesArea.getText());
                    currentZoomedField = entry_socialIssuesArea;
                    getFrame().setEnabled(false);
            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_labsArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_labsArea.getText());
                    currentZoomedField = entry_labsArea;
                    getFrame().setEnabled(false);
            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_radiologyArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_radiologyArea.getText());
                    currentZoomedField = entry_radiologyArea;
                    getFrame().setEnabled(false);
            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_medsArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_medsArea.getText());
                    currentZoomedField = entry_medsArea;
                    getFrame().setEnabled(false);
            }
        });        
        
        //Add lose-focus function for the image label textfield
        this.entry_allergyArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_allergyArea.getText());
                    currentZoomedField = entry_allergyArea;
                    getFrame().setEnabled(false);
            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_consultationArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_consultationArea.getText());
                    currentZoomedField = entry_consultationArea;
                    getFrame().setEnabled(false);
            }
        });
        
        /*
        //Add lose-focus function for the image label textfield
        this.entry_consultationArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_consultationArea1.getText());
                    currentZoomedField = entry_consultationArea1;
                    getFrame().setEnabled(false);
            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_consultationArea2.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_consultationArea2.getText());
                    currentZoomedField = entry_consultationArea2;
                    getFrame().setEnabled(false);
            }
        });
         * 
         */
        
        //Add lose-focus function for the image label textfield
        this.entry_skinTestArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_skinTestArea.getText());
                    currentZoomedField = entry_skinTestArea;
                    getFrame().setEnabled(false);
            }
        });
 
        this.btn_TBtest.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            //currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            //entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    currentZoomedField = entry_skinTestArea;
                    if(currentZoomedField.getText().isEmpty())
                    {
                        zoomedText.setText("Date     Given Date     Read     +/- \n");
                    }
                    else
                    {
                        zoomedText.setText(currentZoomedField.getText()+"\n\nDate     Given Date     Read     +/- \n");
                    }
                 
                    getFrame().setEnabled(false);
            }
        });

        
        
        //Add lose-focus function for the image label textfield
        this.entry_familyHistArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_familyHistArea.getText());
                    currentZoomedField = entry_familyHistArea;
                    getFrame().setEnabled(false);
            }
        });
        
        /*
        //Add lose-focus function for the image label textfield
        this.entry_specialistArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_specialistArea.getText());
                    currentZoomedField = entry_specialistArea;
                    getFrame().setEnabled(false);

            }
        });
        
        //Add lose-focus function for the image label textfield
        this.entry_consultantArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                    zoomTextField.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
                    zoomTextField.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent winEvt) {
                            currentZoomedField.setText(zoomedText.getText());
                            getFrame().setEnabled(true);                        
                            //change the focus to something else!!
                            entry_saveButton.requestFocus();
                            zoomTextField.dispose();
                        }
                    });
                    zoomTextField.setVisible(true);
                    zoomTextField.setLocationRelativeTo(null);
                    zoomedText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    zoomTextField.getRootPane().setDefaultButton(zoomOkButton);
                    zoomTextField.setAlwaysOnTop(true);           
                    zoomTextField.pack();
                    zoomedText.setText(entry_consultantArea.getText());
                    currentZoomedField = entry_consultantArea;
                    getFrame().setEnabled(false);

            }
        });
         */
        
        //new ImageIcon("resources/growthChartBackGround.jpg").getImage();

        //this.patientPane.setForegroundAt(0, new Color(255, 255, 255));
        //this.patientPane.setForegroundAt(1, new Color(80, 80, 186));
        //this.patientPane.setForegroundAt(2, new Color(60, 60, 152));
        //this.patientPane.setForegroundAt(3, new Color(40, 40, 118));
        //this.patientPane.setForegroundAt(4, new Color(255, 255, 255));
        //this.patientPane.setForegroundAt(5, new Color(0, 0, 50));
        
        /*
        this.patientPane.addChangeListener(new ChangeListener() {
             // This method is called whenever the selected tab changes
                @Override
                public void stateChanged(ChangeEvent evt) {
                    javax.swing.JTabbedPane pane = (javax.swing.JTabbedPane)evt.getSource();

                    // Get current tab
                    //int sel = pane.getSelectedIndex();
                    switch(pane.getSelectedIndex()) {
                        case 0:     
                            currentZoomedFlag = 1;
                            entry_saveButton.grabFocus();
                            break;
                        case 1:
                            currentZoomedFlag = 1;
                            entry_specialistArea.grabFocus();
                            break;
                        case 4:
                            currentZoomedFlag = 1;
                            entry_specialistArea1.grabFocus();
                            break;
                        case 5:
                            currentZoomedFlag = 1;
                            entry_specialistArea2.grabFocus();
                            break;
                    }
            }
        });
         * 
         */
        
        //this.largePanel.getRootPane().setDefaultButton(jButton1);       
    }
    
    @Action
    public void closeZoomField() {
        currentZoomedField.setText(zoomedText.getText());
        getFrame().setEnabled(true);
        //change the focus to something else!!
        entry_saveButton.requestFocus();        
        zoomTextField.dispose();
        this.currentZoomedFlag = 1;
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = MedPassportApp.getApplication().getMainFrame();
            aboutBox = new MedPassportAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        MedPassportApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        patientLabel = new javax.swing.JLabel();
        patientScrollPane = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        newPatientButton = new javax.swing.JButton();
        modifyButton = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        saveToPassportButton = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        genderGroup = new javax.swing.ButtonGroup();
        introPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        introTextPane = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        intro_docField = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        patientPane = new javax.swing.JTabbedPane();
        entryPanel = new javax.swing.JPanel();
        entry_memberPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        entry_eyeColorField = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        entry_primaryCareArea = new javax.swing.JTextPane();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        entry_familyHistArea = new javax.swing.JTextPane();
        entry_birthPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        entry_birthCityField = new javax.swing.JTextField();
        entry_birthHospitalField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        entry_fatherField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        entry_motherField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        entry_phoneField = new javax.swing.JTextField();
        entry_emailField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        entry_bloodField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        entry_birthDateField = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        entry_emcallField = new javax.swing.JTextField();
        imagePanel = new javax.swing.JPanel();
        image_leftButton = new javax.swing.JButton();
        image_rightButton = new javax.swing.JButton();
        image_labelField = new javax.swing.JTextField();
        entry_basicPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        entry_firstField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        entry_lastField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        entry_mRadio = new javax.swing.JRadioButton();
        entry_fRadio = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        entry_memberIDField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        entry_insuranceField = new javax.swing.JTextField();
        entry_peoplePanel = new javax.swing.JPanel();
        entry_saveButton = new javax.swing.JButton();
        entry_cancelButton = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        entry_medHistArea = new javax.swing.JTextPane();
        jLabel29 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        entry_lastEditArea = new javax.swing.JTextPane();
        jButton8 = new javax.swing.JButton();
        jScrollPane19 = new javax.swing.JScrollPane();
        entry_specialistArea = new javax.swing.JTextPane();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        entry_medExamArea = new javax.swing.JTextPane();
        btn_medExam = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        largePanel = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        entry_medsArea = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        entry_consultationArea = new javax.swing.JTextPane();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        entry_radiologyArea = new javax.swing.JTextPane();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        entry_allergyArea = new javax.swing.JTextPane();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        entry_labsArea = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        entry_specialArea = new javax.swing.JTextPane();
        growthPanel = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        growth_upperScrollPane = new javax.swing.JScrollPane();
        growthTable = new javax.swing.JTable();
        growth_lowerScrollPane = new javax.swing.JScrollPane();
        bmiTable = new javax.swing.JTable();
        growth_buttonPanel = new javax.swing.JPanel();
        growth_chart1Button = new javax.swing.JButton();
        growth_chart2Button = new javax.swing.JButton();
        growth_chart3Button = new javax.swing.JButton();
        growth_chart4Button = new javax.swing.JButton();
        growth_bmiChartButton = new javax.swing.JButton();
        growth_imagePanel = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        growthCopyCheck = new javax.swing.JCheckBox();
        growthCopyTxt = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        bmiCopyCheck = new javax.swing.JCheckBox();
        bmiCopyTxt = new javax.swing.JTextField();
        jButton22 = new javax.swing.JButton();
        schedulePanel = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        schedTable = new javax.swing.JTable();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        schedCopyCheck = new javax.swing.JCheckBox();
        jLabel42 = new javax.swing.JLabel();
        schedCopyTxt = new javax.swing.JTextField();
        jButton21 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        entry_skinTestArea = new javax.swing.JTextPane();
        jLabel17 = new javax.swing.JLabel();
        btn_TBtest = new javax.swing.JButton();
        problemlistPanel = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        entry_socialIssuesArea = new javax.swing.JTextPane();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        entry_mentalHealthArea = new javax.swing.JTextPane();
        jLabel53 = new javax.swing.JLabel();
        riskfactorPanel = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jScrollPane20 = new javax.swing.JScrollPane();
        entry_consultantArea = new javax.swing.JTextPane();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        specialistPanel = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        entry_consultationArea1 = new javax.swing.JTextPane();
        jButton4 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        physicianPanel = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        jScrollPane29 = new javax.swing.JScrollPane();
        entry_consultationArea2 = new javax.swing.JTextPane();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jScrollPane22 = new javax.swing.JScrollPane();
        entry_consultationArea3 = new javax.swing.JTextPane();
        PasswordFrame = new javax.swing.JFrame();
        jLabel33 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        passwdConfirm = new javax.swing.JButton();
        imageFileChooser = new javax.swing.JFileChooser();
        emailFrame = new javax.swing.JFrame();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        senderEmail = new javax.swing.JTextField();
        senderPasswd = new javax.swing.JPasswordField();
        jLabel39 = new javax.swing.JLabel();
        receiverEmail = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        Subject = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        Content = new javax.swing.JScrollPane();
        Text = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        SavingWindow = new javax.swing.JFrame();
        jLabel37 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        PasswordFrame1 = new javax.swing.JFrame();
        jLabel38 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        passwdConfirm1 = new javax.swing.JButton();
        PasswordFrame2 = new javax.swing.JFrame();
        jLabel46 = new javax.swing.JLabel();
        jPasswordField3 = new javax.swing.JPasswordField();
        passwdConfirm2 = new javax.swing.JButton();
        zoomTextField = new javax.swing.JFrame();
        jScrollPane18 = new javax.swing.JScrollPane();
        zoomedText = new javax.swing.JTextPane();
        zoomOkButton = new javax.swing.JButton();
        zoomTextPane = new javax.swing.JFrame();
        jButton18 = new javax.swing.JButton();
        jScrollPane37 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(medpassport.MedPassportApp.class).getContext().getResourceMap(MedPassportView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setMaximumSize(new java.awt.Dimension(740, 700));
        mainPanel.setMinimumSize(new java.awt.Dimension(740, 700));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(740, 700));
        mainPanel.setLayout(null);

        patientLabel.setName("patientLabel"); // NOI18N
        mainPanel.add(patientLabel);
        patientLabel.setBounds(12, 13, 0, 0);

        patientScrollPane.setName("patientScrollPane"); // NOI18N

        jList1.setFont(resourceMap.getFont("jList1.font")); // NOI18N
        jList1.setModel(patients.getList());
        jList1.setName("jList1"); // NOI18N
        patientScrollPane.setViewportView(jList1);

        mainPanel.add(patientScrollPane);
        patientScrollPane.setBounds(12, 20, 172, 567);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(medpassport.MedPassportApp.class).getContext().getActionMap(MedPassportView.class, this);
        newPatientButton.setAction(actionMap.get("showBlankPatientPane")); // NOI18N
        newPatientButton.setFont(resourceMap.getFont("newPatientButton.font")); // NOI18N
        newPatientButton.setText(resourceMap.getString("newPatientButton.text")); // NOI18N
        newPatientButton.setName("newPatientButton"); // NOI18N
        mainPanel.add(newPatientButton);
        newPatientButton.setBounds(213, 20, 160, 29);

        modifyButton.setAction(actionMap.get("showPasswordFrame")); // NOI18N
        modifyButton.setFont(resourceMap.getFont("newPatientButton.font")); // NOI18N
        modifyButton.setText(resourceMap.getString("modifyButton.text")); // NOI18N
        modifyButton.setActionCommand(resourceMap.getString("modifyButton.actionCommand")); // NOI18N
        modifyButton.setName("modifyButton"); // NOI18N
        mainPanel.add(modifyButton);
        modifyButton.setBounds(213, 62, 160, 29);

        jButton5.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton5.setFont(resourceMap.getFont("newPatientButton.font")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        mainPanel.add(jButton5);
        jButton5.setBounds(213, 184, 160, 29);

        deleteButton.setAction(actionMap.get("showDeleteFrame")); // NOI18N
        deleteButton.setFont(resourceMap.getFont("newPatientButton.font")); // NOI18N
        deleteButton.setText(resourceMap.getString("deleteButton.text")); // NOI18N
        deleteButton.setName("deleteButton"); // NOI18N
        mainPanel.add(deleteButton);
        deleteButton.setBounds(213, 104, 160, 29);

        saveToPassportButton.setAction(actionMap.get("showSaveToPassportFrame")); // NOI18N
        saveToPassportButton.setFont(resourceMap.getFont("newPatientButton.font")); // NOI18N
        saveToPassportButton.setText(resourceMap.getString("saveToPassportButton.text")); // NOI18N
        saveToPassportButton.setMaximumSize(new java.awt.Dimension(53, 25));
        saveToPassportButton.setMinimumSize(new java.awt.Dimension(53, 25));
        saveToPassportButton.setName("saveToPassportButton"); // NOI18N
        saveToPassportButton.setPreferredSize(new java.awt.Dimension(53, 25));
        mainPanel.add(saveToPassportButton);
        saveToPassportButton.setBounds(213, 146, 160, 25);
        saveToPassportButton.getAccessibleContext().setAccessibleName(resourceMap.getString("saveToPassportButton.AccessibleContext.accessibleName")); // NOI18N

        jLabel48.setIcon(resourceMap.getIcon("jLabel48.icon")); // NOI18N
        jLabel48.setText(resourceMap.getString("jLabel48.text")); // NOI18N
        jLabel48.setMaximumSize(new java.awt.Dimension(836, 588));
        jLabel48.setMinimumSize(new java.awt.Dimension(836, 588));
        jLabel48.setName("jLabel48"); // NOI18N
        mainPanel.add(jLabel48);
        jLabel48.setBounds(0, 0, 740, 600);

        menuBar.setFont(resourceMap.getFont("menuBar.font")); // NOI18N
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setBackground(resourceMap.getColor("statusPanel.background")); // NOI18N
        statusPanel.setToolTipText(resourceMap.getString("statusPanel.toolTipText")); // NOI18N
        statusPanel.setMaximumSize(new java.awt.Dimension(650, 20));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(620, 21));

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        statusLabel.setText(resourceMap.getString("statusLabel.text")); // NOI18N
        statusLabel.setName("statusLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(statusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(statusMessageLabel))
                    .add(statusPanelLayout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(statusLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 435, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(statusPanelLayout.createSequentialGroup()
                        .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(statusPanelLayout.createSequentialGroup()
                                .add(24, 24, 24)
                                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(statusMessageLabel)
                                    .add(statusAnimationLabel)))
                            .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(statusPanelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(statusLabel)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        introPanel.setBackground(resourceMap.getColor("introPanel.background")); // NOI18N
        introPanel.setMaximumSize(new java.awt.Dimension(740, 600));
        introPanel.setMinimumSize(new java.awt.Dimension(740, 600));
        introPanel.setName("introPanel"); // NOI18N
        introPanel.setPreferredSize(new java.awt.Dimension(740, 600));
        introPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane7.setBorder(null);
        jScrollPane7.setName("jScrollPane7"); // NOI18N

        introTextPane.setBackground(resourceMap.getColor("introTextPane.background")); // NOI18N
        introTextPane.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("introTextPane.border.lineColor"))); // NOI18N
        introTextPane.setEditable(false);
        introTextPane.setFont(resourceMap.getFont("introTextPane.font")); // NOI18N
        introTextPane.setText(resourceMap.getString("introTextPane.text")); // NOI18N
        introTextPane.setName("introTextPane"); // NOI18N
        jScrollPane7.setViewportView(introTextPane);

        introPanel.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 710, 150));

        jButton2.setAction(actionMap.get("userPatient")); // NOI18N
        jButton2.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        introPanel.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 480, 330, -1));

        jButton3.setAction(actionMap.get("userDoctor")); // NOI18N
        jButton3.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        introPanel.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, 340, -1));

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N
        introPanel.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 450, -1, -1));

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/medpassport/resources/bebedoc-logo.png"))); // NOI18N
        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N
        introPanel.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, -1, -1));

        intro_docField.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        intro_docField.setText(resourceMap.getString("intro_docField.text")); // NOI18N
        intro_docField.setName("intro_docField"); // NOI18N
        introPanel.add(intro_docField, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 520, 160, -1));

        jLabel26.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N
        introPanel.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, -1, 20));

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 740, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 110, Short.MAX_VALUE)
        );

        introPanel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 740, 110));

        jLabel47.setIcon(resourceMap.getIcon("jLabel47.icon")); // NOI18N
        jLabel47.setText(resourceMap.getString("jLabel47.text")); // NOI18N
        jLabel47.setName("jLabel47"); // NOI18N
        introPanel.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 740, 600));

        patientPane.setBackground(resourceMap.getColor("entry_growthCharts_saveButton.background")); // NOI18N
        patientPane.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        patientPane.setFont(resourceMap.getFont("patientPane.font")); // NOI18N
        patientPane.setMinimumSize(new java.awt.Dimension(740, 635));
        patientPane.setName("patientPane"); // NOI18N
        patientPane.setPreferredSize(new java.awt.Dimension(740, 635));

        entryPanel.setBackground(resourceMap.getColor("entryPanel.background")); // NOI18N
        entryPanel.setMaximumSize(new java.awt.Dimension(740, 610));
        entryPanel.setMinimumSize(new java.awt.Dimension(740, 610));
        entryPanel.setName("entryPanel"); // NOI18N
        entryPanel.setPreferredSize(new java.awt.Dimension(740, 610));
        entryPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        entry_memberPanel.setBackground(resourceMap.getColor("entry_memberPanel.background")); // NOI18N
        entry_memberPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        entry_memberPanel.setName("entry_memberPanel"); // NOI18N
        entry_memberPanel.setPreferredSize(new java.awt.Dimension(400, 124));
        entry_memberPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N
        entry_memberPanel.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jLabel14.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        entry_memberPanel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        entry_eyeColorField.setFont(resourceMap.getFont("entry_eyeColorField.font")); // NOI18N
        entry_eyeColorField.setName("entry_eyeColorField"); // NOI18N
        entry_memberPanel.add(entry_eyeColorField, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 250, 25));

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        entry_primaryCareArea.setEditable(false);
        entry_primaryCareArea.setFont(resourceMap.getFont("entry_lastEditArea.font")); // NOI18N
        entry_primaryCareArea.setMaximumSize(new java.awt.Dimension(6, 21));
        entry_primaryCareArea.setName("entry_primaryCareArea"); // NOI18N
        jScrollPane8.setViewportView(entry_primaryCareArea);

        entry_memberPanel.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 320, 30));

        jLabel23.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N
        entry_memberPanel.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        entry_familyHistArea.setEditable(false);
        entry_familyHistArea.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        entry_familyHistArea.setName("entry_familyHistArea"); // NOI18N
        jScrollPane4.setViewportView(entry_familyHistArea);

        entry_memberPanel.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 320, 50));

        entryPanel.add(entry_memberPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 350, 170));

        entry_birthPanel.setBackground(resourceMap.getColor("entry_birthPanel.background")); // NOI18N
        entry_birthPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        entry_birthPanel.setName("entry_birthPanel"); // NOI18N
        entry_birthPanel.setPreferredSize(new java.awt.Dimension(400, 112));

        jLabel10.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        entry_birthCityField.setFont(resourceMap.getFont("entry_bloodField.font")); // NOI18N
        entry_birthCityField.setName("entry_birthCityField"); // NOI18N

        entry_birthHospitalField.setFont(resourceMap.getFont("entry_bloodField.font")); // NOI18N
        entry_birthHospitalField.setName("entry_birthHospitalField"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        entry_fatherField.setFont(resourceMap.getFont("entry_bloodField.font")); // NOI18N
        entry_fatherField.setName("entry_fatherField"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        entry_motherField.setFont(resourceMap.getFont("entry_bloodField.font")); // NOI18N
        entry_motherField.setName("entry_motherField"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        entry_phoneField.setFont(resourceMap.getFont("entry_bloodField.font")); // NOI18N
        entry_phoneField.setName("entry_phoneField"); // NOI18N

        entry_emailField.setFont(resourceMap.getFont("entry_bloodField.font")); // NOI18N
        entry_emailField.setName("entry_emailField"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        entry_bloodField.setBackground(resourceMap.getColor("entry_bloodField.background")); // NOI18N
        entry_bloodField.setFont(resourceMap.getFont("entry_bloodField.font")); // NOI18N
        entry_bloodField.setText(resourceMap.getString("entry_bloodField.text")); // NOI18N
        entry_bloodField.setName("entry_bloodField"); // NOI18N

        jLabel28.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        entry_birthDateField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("M/d/yyyy"))));
        entry_birthDateField.setText(resourceMap.getString("entry_birthDateField.text")); // NOI18N
        entry_birthDateField.setFont(resourceMap.getFont("entry_birthDateField.font")); // NOI18N
        entry_birthDateField.setName("entry_birthDateField"); // NOI18N

        jLabel24.setFont(resourceMap.getFont("jLabel24.font")); // NOI18N
        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        entry_emcallField.setFont(resourceMap.getFont("entry_emcallField.font")); // NOI18N
        entry_emcallField.setName("entry_emcallField"); // NOI18N

        org.jdesktop.layout.GroupLayout entry_birthPanelLayout = new org.jdesktop.layout.GroupLayout(entry_birthPanel);
        entry_birthPanel.setLayout(entry_birthPanelLayout);
        entry_birthPanelLayout.setHorizontalGroup(
            entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(entry_birthPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel10)
                    .add(entry_birthPanelLayout.createSequentialGroup()
                        .add(jLabel11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(entry_birthDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                    .add(entry_birthPanelLayout.createSequentialGroup()
                        .add(jLabel13)
                        .add(18, 18, 18)
                        .add(entry_birthHospitalField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(entry_birthCityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, entry_birthPanelLayout.createSequentialGroup()
                        .add(jLabel28)
                        .add(6, 6, 6)
                        .add(entry_bloodField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
                    .add(entry_birthPanelLayout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(entry_phoneField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(entry_emailField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                    .add(entry_birthPanelLayout.createSequentialGroup()
                        .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel3))
                        .add(6, 6, 6)
                        .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(entry_fatherField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .add(entry_motherField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)))
                    .add(entry_birthPanelLayout.createSequentialGroup()
                        .add(jLabel24)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(entry_emcallField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)))
                .addContainerGap())
        );
        entry_birthPanelLayout.setVerticalGroup(
            entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(entry_birthPanelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(entry_birthDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(entry_birthHospitalField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel13))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel12))
                    .add(entry_birthCityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(entry_bloodField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel28))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(entry_fatherField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(entry_motherField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(entry_phoneField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(entry_emailField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5)
                    .add(jLabel6))
                .add(10, 10, 10)
                .add(entry_birthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel24)
                    .add(entry_emcallField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel11.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel11.AccessibleContext.accessibleName")); // NOI18N

        entryPanel.add(entry_birthPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 350, 260));

        imagePanel.setBackground(resourceMap.getColor("imagePanel.background")); // NOI18N
        imagePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        imagePanel.setMaximumSize(new java.awt.Dimension(274, 166));
        imagePanel.setName("imagePanel"); // NOI18N
        imagePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image_leftButton.setAction(actionMap.get("showPrevImage")); // NOI18N
        image_leftButton.setText(resourceMap.getString("image_leftButton.text")); // NOI18N
        image_leftButton.setName("image_leftButton"); // NOI18N
        imagePanel.add(image_leftButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 205, -1, -1));

        image_rightButton.setAction(actionMap.get("showNextImage")); // NOI18N
        image_rightButton.setText(resourceMap.getString("image_rightButton.text")); // NOI18N
        image_rightButton.setName("image_rightButton"); // NOI18N
        imagePanel.add(image_rightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 205, -1, -1));

        image_labelField.setFont(resourceMap.getFont("image_labelField.font")); // NOI18N
        image_labelField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        image_labelField.setText(resourceMap.getString("image_labelField.text")); // NOI18N
        image_labelField.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        image_labelField.setMaximumSize(new java.awt.Dimension(70, 18));
        image_labelField.setName("image_labelField"); // NOI18N
        imagePanel.add(image_labelField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 207, 250, -1));

        entryPanel.add(imagePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 350, 230));

        entry_basicPanel.setBackground(resourceMap.getColor("entry_basicPanel.background")); // NOI18N
        entry_basicPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        entry_basicPanel.setName("entry_basicPanel"); // NOI18N
        entry_basicPanel.setPreferredSize(new java.awt.Dimension(400, 273));
        entry_basicPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        entry_basicPanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        entry_firstField.setFont(resourceMap.getFont("entry_insuranceField.font")); // NOI18N
        entry_firstField.setText(resourceMap.getString("entry_firstField.text")); // NOI18N
        entry_firstField.setName("entry_firstField"); // NOI18N
        entry_basicPanel.add(entry_firstField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 170, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        entry_basicPanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 20));

        entry_lastField.setFont(resourceMap.getFont("entry_insuranceField.font")); // NOI18N
        entry_lastField.setText(resourceMap.getString("entry_lastField.text")); // NOI18N
        entry_lastField.setName("entry_lastField"); // NOI18N
        entry_basicPanel.add(entry_lastField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 170, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        entry_basicPanel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, -1, -1));

        entry_mRadio.setBackground(resourceMap.getColor("entry_mRadio.background")); // NOI18N
        genderGroup.add(entry_mRadio);
        entry_mRadio.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        entry_mRadio.setText(resourceMap.getString("entry_mRadio.text")); // NOI18N
        entry_mRadio.setName("entry_mRadio"); // NOI18N
        entry_basicPanel.add(entry_mRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, -1, -1));

        entry_fRadio.setBackground(resourceMap.getColor("entry_fRadio.background")); // NOI18N
        genderGroup.add(entry_fRadio);
        entry_fRadio.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        entry_fRadio.setText(resourceMap.getString("entry_fRadio.text")); // NOI18N
        entry_fRadio.setName("entry_fRadio"); // NOI18N
        entry_basicPanel.add(entry_fRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        entry_basicPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, 20));

        entry_memberIDField.setFont(resourceMap.getFont("entry_insuranceField.font")); // NOI18N
        entry_memberIDField.setText(resourceMap.getString("entry_memberIDField.text")); // NOI18N
        entry_memberIDField.setName("entry_memberIDField"); // NOI18N
        entry_basicPanel.add(entry_memberIDField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 240, -1));

        jLabel1.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        entry_basicPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 20));

        entry_insuranceField.setFont(resourceMap.getFont("entry_insuranceField.font")); // NOI18N
        entry_insuranceField.setName("entry_insuranceField"); // NOI18N
        entry_basicPanel.add(entry_insuranceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, 240, -1));

        entryPanel.add(entry_basicPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 350, 140));

        entry_peoplePanel.setBackground(resourceMap.getColor("entry_peoplePanel.background")); // NOI18N
        entry_peoplePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        entry_peoplePanel.setName("entry_peoplePanel"); // NOI18N
        entry_peoplePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        entry_saveButton.setAction(actionMap.get("savePatient")); // NOI18N
        entry_saveButton.setBackground(resourceMap.getColor("jButton8.background")); // NOI18N
        entry_saveButton.setFont(resourceMap.getFont("entry_saveButton.font")); // NOI18N
        entry_saveButton.setForeground(resourceMap.getColor("jButton8.foreground")); // NOI18N
        entry_saveButton.setText(resourceMap.getString("entry_saveButton.text")); // NOI18N
        entry_saveButton.setName("entry_saveButton"); // NOI18N
        entry_peoplePanel.add(entry_saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 100, -1));

        entry_cancelButton.setAction(actionMap.get("cancelButton")); // NOI18N
        entry_cancelButton.setBackground(resourceMap.getColor("jButton8.background")); // NOI18N
        entry_cancelButton.setFont(resourceMap.getFont("entry_saveButton.font")); // NOI18N
        entry_cancelButton.setForeground(resourceMap.getColor("jButton8.foreground")); // NOI18N
        entry_cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
        entry_cancelButton.setName("cancelButton"); // NOI18N
        entry_peoplePanel.add(entry_cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 280, 90, -1));

        jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        entry_peoplePanel.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, 20));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        entry_medHistArea.setEditable(false);
        entry_medHistArea.setFont(resourceMap.getFont("entry_lastEditArea.font")); // NOI18N
        entry_medHistArea.setName("entry_medHistArea"); // NOI18N
        jScrollPane3.setViewportView(entry_medHistArea);

        entry_peoplePanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 330, 30));

        jLabel29.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N
        entry_peoplePanel.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jLabel34.setFont(resourceMap.getFont("entry_saveButton.font")); // NOI18N
        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N
        entry_peoplePanel.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, 20));

        jScrollPane17.setName("jScrollPane17"); // NOI18N
        jScrollPane17.setPreferredSize(new java.awt.Dimension(8, 30));

        entry_lastEditArea.setEditable(false);
        entry_lastEditArea.setFont(resourceMap.getFont("entry_lastEditArea.font")); // NOI18N
        entry_lastEditArea.setName("entry_lastEditArea"); // NOI18N
        jScrollPane17.setViewportView(entry_lastEditArea);

        entry_peoplePanel.add(jScrollPane17, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 240, 260, 30));

        jButton8.setAction(actionMap.get("changeToSecondTab")); // NOI18N
        jButton8.setBackground(resourceMap.getColor("jButton8.background")); // NOI18N
        jButton8.setFont(resourceMap.getFont("entry_saveButton.font")); // NOI18N
        jButton8.setForeground(resourceMap.getColor("jButton8.foreground")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        entry_peoplePanel.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 280, 110, -1));

        jScrollPane19.setName("jScrollPane19"); // NOI18N

        entry_specialistArea.setFont(resourceMap.getFont("entry_specialistArea.font")); // NOI18N
        entry_specialistArea.setName("entry_specialistArea"); // NOI18N
        jScrollPane19.setViewportView(entry_specialistArea);

        entry_peoplePanel.add(jScrollPane19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 330, 70));

        jLabel30.setFont(resourceMap.getFont("jLabel30.font")); // NOI18N
        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N
        entry_peoplePanel.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        entry_medExamArea.setName("entry_medExamArea"); // NOI18N
        jScrollPane9.setViewportView(entry_medExamArea);

        entry_peoplePanel.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 250, 40));

        btn_medExam.setFont(resourceMap.getFont("btn_medExam.font")); // NOI18N
        btn_medExam.setText(resourceMap.getString("btn_medExam.text")); // NOI18N
        btn_medExam.setName("btn_medExam"); // NOI18N
        entry_peoplePanel.add(btn_medExam, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 60, 80, 30));

        entryPanel.add(entry_peoplePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 280, 350, 320));

        jButton9.setAction(actionMap.get("imageUpload")); // NOI18N
        jButton9.setFont(resourceMap.getFont("image_labelField.font")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        entryPanel.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(477, 244, -1, -1));
        jButton9.getAccessibleContext().setAccessibleName(resourceMap.getString("jButton9.AccessibleContext.accessibleName")); // NOI18N

        patientPane.addTab(resourceMap.getString("entryPanel.TabConstraints.tabTitle"), entryPanel); // NOI18N

        largePanel.setBackground(resourceMap.getColor("largePanel.background")); // NOI18N
        largePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        largePanel.setMaximumSize(new java.awt.Dimension(740, 580));
        largePanel.setMinimumSize(new java.awt.Dimension(740, 580));
        largePanel.setName("largePanel"); // NOI18N
        largePanel.setPreferredSize(new java.awt.Dimension(740, 580));
        largePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N
        largePanel.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, -1, -1));

        jLabel16.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        largePanel.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, -1));

        jLabel19.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        largePanel.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, -1, -1));

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        entry_medsArea.setEditable(false);
        entry_medsArea.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        entry_medsArea.setName("entry_medsArea"); // NOI18N
        jScrollPane6.setViewportView(entry_medsArea);

        largePanel.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 700, 40));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        entry_consultationArea.setEditable(false);
        entry_consultationArea.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        entry_consultationArea.setName("entry_consultationArea"); // NOI18N
        jScrollPane1.setViewportView(entry_consultationArea);

        largePanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 700, 120));

        jLabel15.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        largePanel.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        entry_radiologyArea.setEditable(false);
        entry_radiologyArea.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        entry_radiologyArea.setName("entry_radiologyArea"); // NOI18N
        jScrollPane14.setViewportView(entry_radiologyArea);

        largePanel.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 700, 40));

        jLabel31.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N
        largePanel.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        entry_allergyArea.setBackground(resourceMap.getColor("entry_allergyArea.background")); // NOI18N
        entry_allergyArea.setEditable(false);
        entry_allergyArea.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        entry_allergyArea.setName("entry_allergyArea"); // NOI18N
        jScrollPane15.setViewportView(entry_allergyArea);

        largePanel.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 700, 40));

        jLabel32.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N
        largePanel.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        jScrollPane16.setName("jScrollPane16"); // NOI18N

        entry_labsArea.setEditable(false);
        entry_labsArea.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        entry_labsArea.setAlignmentY(0.1F);
        entry_labsArea.setName("entry_labsArea"); // NOI18N
        jScrollPane16.setViewportView(entry_labsArea);

        largePanel.add(jScrollPane16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 700, 40));

        jButton1.setAction(actionMap.get("savePatient")); // NOI18N
        jButton1.setBackground(resourceMap.getColor("entry_otherInfo_exitButton.background")); // NOI18N
        jButton1.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jButton1.setText(resourceMap.getString("entry_otherInfo_saveButton.text")); // NOI18N
        jButton1.setName("entry_otherInfo_saveButton"); // NOI18N
        largePanel.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 600, 100, -1));

        jButton10.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton10.setBackground(resourceMap.getColor("entry_otherInfo_exitButton.background")); // NOI18N
        jButton10.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jButton10.setText(resourceMap.getString("entry_otherInfo_exitButton.text")); // NOI18N
        jButton10.setName("entry_otherInfo_exitButton"); // NOI18N
        largePanel.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 600, 100, -1));

        jButton23.setAction(actionMap.get("changeToThirdTab")); // NOI18N
        jButton23.setBackground(resourceMap.getColor("entry_otherInfo_exitButton.background")); // NOI18N
        jButton23.setFont(resourceMap.getFont("jButton23.font")); // NOI18N
        jButton23.setText(resourceMap.getString("jButton23.text")); // NOI18N
        jButton23.setName("jButton23"); // NOI18N
        largePanel.add(jButton23, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 600, 110, -1));

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        entry_specialArea.setEditable(false);
        entry_specialArea.setFont(resourceMap.getFont("entry_lastEditArea.font")); // NOI18N
        entry_specialArea.setName("entry_specialArea"); // NOI18N
        jScrollPane5.setViewportView(entry_specialArea);

        largePanel.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 700, 120));

        patientPane.addTab(resourceMap.getString("largePanel.TabConstraints.tabTitle"), largePanel); // NOI18N

        growthPanel.setBackground(resourceMap.getColor("growthPanel.background")); // NOI18N
        growthPanel.setMaximumSize(new java.awt.Dimension(740, 580));
        growthPanel.setMinimumSize(new java.awt.Dimension(740, 580));
        growthPanel.setName("growthPanel"); // NOI18N
        growthPanel.setPreferredSize(new java.awt.Dimension(740, 580));
        growthPanel.setLayout(null);

        jLabel50.setIcon(resourceMap.getIcon("jLabel50.icon")); // NOI18N
        jLabel50.setText(resourceMap.getString("jLabel50.text")); // NOI18N
        jLabel50.setMaximumSize(new java.awt.Dimension(516, 258));
        jLabel50.setMinimumSize(new java.awt.Dimension(516, 258));
        jLabel50.setName("jLabel50"); // NOI18N
        jLabel50.setPreferredSize(new java.awt.Dimension(516, 258));
        growthPanel.add(jLabel50);
        jLabel50.setBounds(210, 10, 510, 270);

        growth_upperScrollPane.setBorder(null);
        growth_upperScrollPane.setHorizontalScrollBar(null);
        growth_upperScrollPane.setName("growth_upperScrollPane"); // NOI18N

        growthTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Std. Weight", null, null, null, null, null, null, null, null, null, null},
                {"Actual Weight", null, null, null, null, null, null, null, null, null, null},
                {"Std. Length", null, null, null, null, null, null, null, null, null, null},
                {"Actual Length", null, null, null, null, null, null, null, null, null, null},
                {"Std. HC", null, null, null, null, null, null, null, null, null, null},
                {"Actual HC", null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Month", "0", "2", "4", "6", "9", "12", "15", "18", "24", "36"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        growthTable.setColumnSelectionAllowed(true);
        growthTable.setName("growthTable"); // NOI18N
        growthTable.getTableHeader().setReorderingAllowed(false);
        growth_upperScrollPane.setViewportView(growthTable);
        growthTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        growthTable.getColumnModel().getColumn(0).setResizable(false);
        growthTable.getColumnModel().getColumn(0).setPreferredWidth(150);

        growthPanel.add(growth_upperScrollPane);
        growth_upperScrollPane.setBounds(12, 294, 710, 130);

        growth_lowerScrollPane.setName("growth_lowerScrollPane"); // NOI18N

        bmiTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Std. BMI", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Actual BMI", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Month", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bmiTable.setColumnSelectionAllowed(true);
        bmiTable.setName("bmiTable"); // NOI18N
        bmiTable.getTableHeader().setReorderingAllowed(false);
        growth_lowerScrollPane.setViewportView(bmiTable);
        bmiTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        bmiTable.getColumnModel().getColumn(0).setResizable(false);
        bmiTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        bmiTable.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title11")); // NOI18N
        bmiTable.getColumnModel().getColumn(11).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title12")); // NOI18N
        bmiTable.getColumnModel().getColumn(12).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title13")); // NOI18N
        bmiTable.getColumnModel().getColumn(13).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title14")); // NOI18N
        bmiTable.getColumnModel().getColumn(14).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title15")); // NOI18N
        bmiTable.getColumnModel().getColumn(15).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title16")); // NOI18N
        bmiTable.getColumnModel().getColumn(16).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title17")); // NOI18N
        bmiTable.getColumnModel().getColumn(17).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title18")); // NOI18N
        bmiTable.getColumnModel().getColumn(18).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title19")); // NOI18N
        bmiTable.getColumnModel().getColumn(19).setHeaderValue(resourceMap.getString("bmiTable.columnModel.title20")); // NOI18N

        growthPanel.add(growth_lowerScrollPane);
        growth_lowerScrollPane.setBounds(10, 460, 710, 70);

        growth_buttonPanel.setBackground(resourceMap.getColor("growth_buttonPanel.background")); // NOI18N
        growth_buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        growth_buttonPanel.setName("growth_buttonPanel"); // NOI18N

        growth_chart1Button.setAction(actionMap.get("showChart1")); // NOI18N
        growth_chart1Button.setText(resourceMap.getString("growth_chart1Button.text")); // NOI18N
        growth_chart1Button.setName("growth_chart1Button"); // NOI18N

        growth_chart2Button.setAction(actionMap.get("showChart2")); // NOI18N
        growth_chart2Button.setText(resourceMap.getString("growth_chart2Button.text")); // NOI18N
        growth_chart2Button.setName("growth_chart2Button"); // NOI18N

        growth_chart3Button.setAction(actionMap.get("showChart3")); // NOI18N
        growth_chart3Button.setText(resourceMap.getString("growth_chart3Button.text")); // NOI18N
        growth_chart3Button.setName("growth_chart3Button"); // NOI18N

        growth_chart4Button.setAction(actionMap.get("showChart4")); // NOI18N
        growth_chart4Button.setText(resourceMap.getString("growth_chart4Button.text")); // NOI18N
        growth_chart4Button.setName("growth_chart4Button"); // NOI18N

        growth_bmiChartButton.setAction(actionMap.get("showBMIChart")); // NOI18N
        growth_bmiChartButton.setText(resourceMap.getString("growth_bmiChartButton.text")); // NOI18N
        growth_bmiChartButton.setName("growth_bmiChartButton"); // NOI18N

        org.jdesktop.layout.GroupLayout growth_buttonPanelLayout = new org.jdesktop.layout.GroupLayout(growth_buttonPanel);
        growth_buttonPanel.setLayout(growth_buttonPanelLayout);
        growth_buttonPanelLayout.setHorizontalGroup(
            growth_buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(growth_buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(growth_buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(growth_chart1Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(growth_chart2Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(growth_chart3Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(growth_chart4Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(growth_bmiChartButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                .addContainerGap())
        );
        growth_buttonPanelLayout.setVerticalGroup(
            growth_buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(growth_buttonPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(growth_chart1Button)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(growth_chart2Button)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(growth_chart3Button)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(growth_chart4Button)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 57, Short.MAX_VALUE)
                .add(growth_bmiChartButton)
                .add(34, 34, 34))
        );

        growthPanel.add(growth_buttonPanel);
        growth_buttonPanel.setBounds(12, 13, 190, 270);

        growth_imagePanel.setBackground(resourceMap.getColor("growth_imagePanel.background")); // NOI18N
        growth_imagePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        growth_imagePanel.setMaximumSize(new java.awt.Dimension(516, 258));
        growth_imagePanel.setMinimumSize(new java.awt.Dimension(516, 258));
        growth_imagePanel.setName("growth_imagePanel"); // NOI18N

        org.jdesktop.layout.GroupLayout growth_imagePanelLayout = new org.jdesktop.layout.GroupLayout(growth_imagePanel);
        growth_imagePanel.setLayout(growth_imagePanelLayout);
        growth_imagePanelLayout.setHorizontalGroup(
            growth_imagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 512, Short.MAX_VALUE)
        );
        growth_imagePanelLayout.setVerticalGroup(
            growth_imagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 266, Short.MAX_VALUE)
        );

        growthPanel.add(growth_imagePanel);
        growth_imagePanel.setBounds(209, 13, 516, 270);

        jButton11.setAction(actionMap.get("savePatient")); // NOI18N
        jButton11.setBackground(resourceMap.getColor("entry_growthCharts_saveButton.background")); // NOI18N
        jButton11.setFont(resourceMap.getFont("entry_grwothChar_exitButton.font")); // NOI18N
        jButton11.setText(resourceMap.getString("entry_growthCharts_saveButton.text")); // NOI18N
        jButton11.setName("entry_growthCharts_saveButton"); // NOI18N
        growthPanel.add(jButton11);
        jButton11.setBounds(390, 580, 100, 29);

        jButton12.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton12.setBackground(resourceMap.getColor("entry_growthCharts_saveButton.background")); // NOI18N
        jButton12.setFont(resourceMap.getFont("entry_grwothChar_exitButton.font")); // NOI18N
        jButton12.setText(resourceMap.getString("entry_grwothChar_exitButton.text")); // NOI18N
        jButton12.setName("entry_grwothChar_exitButton"); // NOI18N
        growthPanel.add(jButton12);
        jButton12.setBounds(620, 530, 100, 29);

        jLabel43.setText(resourceMap.getString("jLabel43.text")); // NOI18N
        jLabel43.setName("jLabel43"); // NOI18N
        growthPanel.add(jLabel43);
        jLabel43.setBounds(10, 430, 54, 15);

        growthCopyCheck.setName("growthCopyCheck"); // NOI18N
        growthPanel.add(growthCopyCheck);
        growthCopyCheck.setBounds(80, 430, 21, 21);

        growthCopyTxt.setName("growthCopyTxt"); // NOI18N
        growthPanel.add(growthCopyTxt);
        growthCopyTxt.setBounds(110, 430, 45, 21);

        jLabel45.setText(resourceMap.getString("jLabel45.text")); // NOI18N
        jLabel45.setName("jLabel45"); // NOI18N
        growthPanel.add(jLabel45);
        jLabel45.setBounds(10, 570, 54, 15);

        bmiCopyCheck.setName("bmiCopyCheck"); // NOI18N
        growthPanel.add(bmiCopyCheck);
        bmiCopyCheck.setBounds(80, 570, 21, 21);

        bmiCopyTxt.setName("bmiCopyTxt"); // NOI18N
        growthPanel.add(bmiCopyTxt);
        bmiCopyTxt.setBounds(110, 530, 45, 21);

        jButton22.setAction(actionMap.get("changeToFouthTab")); // NOI18N
        jButton22.setBackground(resourceMap.getColor("entry_growthCharts_saveButton.background")); // NOI18N
        jButton22.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton22.setName("jButton22"); // NOI18N
        growthPanel.add(jButton22);
        jButton22.setBounds(500, 580, 110, 33);

        patientPane.addTab(resourceMap.getString("growthPanel.TabConstraints.tabTitle"), growthPanel); // NOI18N

        schedulePanel.setBackground(resourceMap.getColor("schedulePanel.background")); // NOI18N
        schedulePanel.setMaximumSize(new java.awt.Dimension(740, 580));
        schedulePanel.setMinimumSize(new java.awt.Dimension(740, 580));
        schedulePanel.setName("schedulePanel"); // NOI18N
        schedulePanel.setPreferredSize(new java.awt.Dimension(740, 580));

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        schedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Hepatitis B", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"DTaP", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"HIB", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Polio", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"MMR", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Varicella", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Pneumoccoccal", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Hepatitis A", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Influenza", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Gardasil", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Rotavirus", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Pediarix", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Menactra", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"TDaP", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Zostavax", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "Birth", "1 Mo.", "null", "null", "null", "null", "null", "null", "null", "null", "null", "Title 13", "Title 14", "Title 15", "Title 16"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        schedTable.setName("schedTable"); // NOI18N
        schedTable.setSurrendersFocusOnKeystroke(true);
        schedTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(schedTable);
        schedTable.getColumnModel().getColumn(0).setPreferredWidth(150);

        jButton13.setAction(actionMap.get("savePatient")); // NOI18N
        jButton13.setBackground(resourceMap.getColor("entry_immunSch_exitButton.background")); // NOI18N
        jButton13.setFont(resourceMap.getFont("entry_immunSch_exitButton.font")); // NOI18N
        jButton13.setText(resourceMap.getString("entry_immunSch_saveButton.text")); // NOI18N
        jButton13.setAlignmentX(0.5F);
        jButton13.setName("entry_immunSch_saveButton"); // NOI18N

        jButton14.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton14.setBackground(resourceMap.getColor("entry_immunSch_exitButton.background")); // NOI18N
        jButton14.setFont(resourceMap.getFont("entry_immunSch_exitButton.font")); // NOI18N
        jButton14.setText(resourceMap.getString("entry_immunSch_exitButton.text")); // NOI18N
        jButton14.setName("entry_immunSch_exitButton"); // NOI18N

        schedCopyCheck.setText(resourceMap.getString("chedCopyCheck.text")); // NOI18N
        schedCopyCheck.setName("chedCopyCheck"); // NOI18N

        jLabel42.setText(resourceMap.getString("schedCopyLabel.text")); // NOI18N
        jLabel42.setName("schedCopyLabel"); // NOI18N

        schedCopyTxt.setText(resourceMap.getString("schedCopyTxt.text")); // NOI18N
        schedCopyTxt.setName("schedCopyTxt"); // NOI18N

        jButton21.setAction(actionMap.get("changeToFifthTab")); // NOI18N
        jButton21.setBackground(resourceMap.getColor("entry_immunSch_exitButton.background")); // NOI18N
        jButton21.setFont(resourceMap.getFont("jButton21.font")); // NOI18N
        jButton21.setText(resourceMap.getString("jButton21.text")); // NOI18N
        jButton21.setName("jButton21"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        entry_skinTestArea.setEditable(false);
        entry_skinTestArea.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        entry_skinTestArea.setText(resourceMap.getString("entry_skinTestArea.text")); // NOI18N
        entry_skinTestArea.setName("entry_skinTestArea"); // NOI18N
        jScrollPane2.setViewportView(entry_skinTestArea);

        jLabel17.setFont(resourceMap.getFont("entry_otherInfo_exitButton.font")); // NOI18N
        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        btn_TBtest.setFont(resourceMap.getFont("btn_TBtest.font")); // NOI18N
        btn_TBtest.setText(resourceMap.getString("btn_TBtest.text")); // NOI18N
        btn_TBtest.setName("btn_TBtest"); // NOI18N

        org.jdesktop.layout.GroupLayout schedulePanelLayout = new org.jdesktop.layout.GroupLayout(schedulePanel);
        schedulePanel.setLayout(schedulePanelLayout);
        schedulePanelLayout.setHorizontalGroup(
            schedulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(schedulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(schedulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .add(schedulePanelLayout.createSequentialGroup()
                        .add(schedCopyCheck)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(schedCopyTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 321, Short.MAX_VALUE)
                        .add(jButton13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel42)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 700, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(schedulePanelLayout.createSequentialGroup()
                        .add(jLabel17)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btn_TBtest)))
                .addContainerGap())
        );
        schedulePanelLayout.setVerticalGroup(
            schedulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(schedulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 345, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(schedulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(btn_TBtest))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jLabel42)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(schedulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(schedulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButton21)
                        .add(jButton14)
                        .add(jButton13))
                    .add(schedCopyCheck)
                    .add(schedCopyTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(50, 50, 50))
        );

        patientPane.addTab(resourceMap.getString("schedulePanel.TabConstraints.tabTitle"), schedulePanel); // NOI18N

        problemlistPanel.setName("problemlistPanel"); // NOI18N

        jLabel51.setFont(resourceMap.getFont("jLabel51.font")); // NOI18N
        jLabel51.setText(resourceMap.getString("jLabel51.text")); // NOI18N
        jLabel51.setName("jLabel51"); // NOI18N

        jButton24.setAction(actionMap.get("savePatient")); // NOI18N
        jButton24.setBackground(resourceMap.getColor("jButton24.background")); // NOI18N
        jButton24.setFont(resourceMap.getFont("jButton26.font")); // NOI18N
        jButton24.setText(resourceMap.getString("jButton24.text")); // NOI18N
        jButton24.setAlignmentX(0.5F);
        jButton24.setName("jButton24"); // NOI18N

        jButton25.setAction(actionMap.get("changeToSixthTab")); // NOI18N
        jButton25.setBackground(resourceMap.getColor("jButton25.background")); // NOI18N
        jButton25.setFont(resourceMap.getFont("jButton26.font")); // NOI18N
        jButton25.setText(resourceMap.getString("jButton25.text")); // NOI18N
        jButton25.setName("jButton25"); // NOI18N

        jButton26.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton26.setBackground(resourceMap.getColor("jButton26.background")); // NOI18N
        jButton26.setFont(resourceMap.getFont("jButton26.font")); // NOI18N
        jButton26.setText(resourceMap.getString("jButton26.text")); // NOI18N
        jButton26.setName("jButton26"); // NOI18N

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        entry_socialIssuesArea.setBackground(resourceMap.getColor("entry_socialIssuesArea.background")); // NOI18N
        entry_socialIssuesArea.setEditable(false);
        entry_socialIssuesArea.setFont(resourceMap.getFont("entry_lastEditArea.font")); // NOI18N
        entry_socialIssuesArea.setName("entry_socialIssuesArea"); // NOI18N
        jScrollPane12.setViewportView(entry_socialIssuesArea);

        jLabel25.setFont(resourceMap.getFont("jLabel25.font")); // NOI18N
        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        entry_mentalHealthArea.setBackground(resourceMap.getColor("entry_mentalHealthArea.background")); // NOI18N
        entry_mentalHealthArea.setEditable(false);
        entry_mentalHealthArea.setFont(resourceMap.getFont("entry_lastEditArea.font")); // NOI18N
        entry_mentalHealthArea.setName("entry_mentalHealthArea"); // NOI18N
        jScrollPane13.setViewportView(entry_mentalHealthArea);

        jLabel53.setFont(resourceMap.getFont("jLabel53.font")); // NOI18N
        jLabel53.setText(resourceMap.getString("jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53"); // NOI18N

        org.jdesktop.layout.GroupLayout problemlistPanelLayout = new org.jdesktop.layout.GroupLayout(problemlistPanel);
        problemlistPanel.setLayout(problemlistPanelLayout);
        problemlistPanelLayout.setHorizontalGroup(
            problemlistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, problemlistPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(problemlistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel25)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, problemlistPanelLayout.createSequentialGroup()
                        .add(problemlistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(problemlistPanelLayout.createSequentialGroup()
                                .add(jLabel51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 385, Short.MAX_VALUE))
                            .add(problemlistPanelLayout.createSequentialGroup()
                                .add(jButton24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(jButton25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel53))
                .addContainerGap())
        );
        problemlistPanelLayout.setVerticalGroup(
            problemlistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(problemlistPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel51)
                .add(54, 54, 54)
                .add(jLabel25)
                .add(9, 9, 9)
                .add(jScrollPane12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jLabel53)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(problemlistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton26)
                    .add(jButton25)
                    .add(jButton24))
                .addContainerGap())
        );

        patientPane.addTab(resourceMap.getString("problemlistPanel.TabConstraints.tabTitle"), problemlistPanel); // NOI18N

        riskfactorPanel.setName("riskfactorPanel"); // NOI18N

        jLabel52.setFont(resourceMap.getFont("jLabel52.font")); // NOI18N
        jLabel52.setText(resourceMap.getString("jLabel52.text")); // NOI18N
        jLabel52.setName("jLabel52"); // NOI18N

        jScrollPane20.setName("jScrollPane20"); // NOI18N

        entry_consultantArea.setFont(resourceMap.getFont("entry_consultantArea.font")); // NOI18N
        entry_consultantArea.setName("entry_consultantArea"); // NOI18N
        jScrollPane20.setViewportView(entry_consultantArea);

        jButton27.setAction(actionMap.get("savePatient")); // NOI18N
        jButton27.setBackground(resourceMap.getColor("jButton27.background")); // NOI18N
        jButton27.setFont(resourceMap.getFont("jButton29.font")); // NOI18N
        jButton27.setText(resourceMap.getString("jButton27.text")); // NOI18N
        jButton27.setAlignmentX(0.5F);
        jButton27.setName("jButton27"); // NOI18N

        jButton28.setAction(actionMap.get("changeToSeventhTab")); // NOI18N
        jButton28.setBackground(resourceMap.getColor("jButton28.background")); // NOI18N
        jButton28.setFont(resourceMap.getFont("jButton29.font")); // NOI18N
        jButton28.setText(resourceMap.getString("jButton28.text")); // NOI18N
        jButton28.setName("jButton28"); // NOI18N

        jButton29.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton29.setBackground(resourceMap.getColor("jButton29.background")); // NOI18N
        jButton29.setFont(resourceMap.getFont("jButton29.font")); // NOI18N
        jButton29.setText(resourceMap.getString("jButton29.text")); // NOI18N
        jButton29.setName("jButton29"); // NOI18N

        org.jdesktop.layout.GroupLayout riskfactorPanelLayout = new org.jdesktop.layout.GroupLayout(riskfactorPanel);
        riskfactorPanel.setLayout(riskfactorPanelLayout);
        riskfactorPanelLayout.setHorizontalGroup(
            riskfactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(riskfactorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(riskfactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .add(jLabel52)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, riskfactorPanelLayout.createSequentialGroup()
                        .add(jButton27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        riskfactorPanelLayout.setVerticalGroup(
            riskfactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(riskfactorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel52)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(riskfactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton28)
                    .add(jButton29)
                    .add(jButton27))
                .addContainerGap())
        );

        patientPane.addTab(resourceMap.getString("riskfactorPanel.TabConstraints.tabTitle"), riskfactorPanel); // NOI18N

        specialistPanel.setBackground(resourceMap.getColor("specialistPanel.background")); // NOI18N
        specialistPanel.setMaximumSize(new java.awt.Dimension(740, 580));
        specialistPanel.setMinimumSize(new java.awt.Dimension(740, 580));
        specialistPanel.setName("specialistPanel"); // NOI18N
        specialistPanel.setPreferredSize(new java.awt.Dimension(740, 580));
        specialistPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel49.setFont(resourceMap.getFont("jLabel52.font")); // NOI18N
        jLabel49.setText(resourceMap.getString("jLabel49.text")); // NOI18N
        jLabel49.setName("jLabel49"); // NOI18N
        specialistPanel.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));
        jLabel49.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel49.AccessibleContext.accessibleName")); // NOI18N

        jScrollPane21.setName("jScrollPane21"); // NOI18N

        entry_consultationArea1.setFont(resourceMap.getFont("jLabel52.font")); // NOI18N
        entry_consultationArea1.setName("entry_consultationArea1"); // NOI18N
        jScrollPane21.setViewportView(entry_consultationArea1);

        specialistPanel.add(jScrollPane21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 700, 480));

        jButton4.setAction(actionMap.get("savePatient")); // NOI18N
        jButton4.setBackground(resourceMap.getColor("jButton15.background")); // NOI18N
        jButton4.setFont(resourceMap.getFont("jLabel52.font")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton4.setName("jButton4"); // NOI18N
        specialistPanel.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 530, 100, -1));

        jButton15.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton15.setBackground(resourceMap.getColor("jButton15.background")); // NOI18N
        jButton15.setFont(resourceMap.getFont("jLabel52.font")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setName("jButton15"); // NOI18N
        specialistPanel.add(jButton15, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 530, 100, -1));

        jButton20.setAction(actionMap.get("changeToEighthTab")); // NOI18N
        jButton20.setBackground(resourceMap.getColor("jButton15.background")); // NOI18N
        jButton20.setFont(resourceMap.getFont("jButton20.font")); // NOI18N
        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton20.setName("jButton20"); // NOI18N
        specialistPanel.add(jButton20, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 530, 110, -1));

        patientPane.addTab(resourceMap.getString("specialistPanel.TabConstraints.tabTitle"), specialistPanel); // NOI18N

        physicianPanel.setBackground(resourceMap.getColor("physicianPanel.background")); // NOI18N
        physicianPanel.setMaximumSize(new java.awt.Dimension(740, 580));
        physicianPanel.setMinimumSize(new java.awt.Dimension(740, 580));
        physicianPanel.setName("physicianPanel"); // NOI18N
        physicianPanel.setPreferredSize(new java.awt.Dimension(740, 580));
        physicianPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel57.setFont(resourceMap.getFont("jLabel61.font")); // NOI18N
        jLabel57.setText(resourceMap.getString("jLabel57.text")); // NOI18N
        jLabel57.setName("jLabel57"); // NOI18N
        physicianPanel.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jScrollPane29.setName("jScrollPane29"); // NOI18N

        entry_consultationArea2.setFont(resourceMap.getFont("jLabel61.font")); // NOI18N
        entry_consultationArea2.setName("entry_consultationArea2"); // NOI18N
        jScrollPane29.setViewportView(entry_consultationArea2);

        physicianPanel.add(jScrollPane29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 700, 480));

        jButton16.setAction(actionMap.get("savePatient")); // NOI18N
        jButton16.setBackground(resourceMap.getColor("jButton19.background")); // NOI18N
        jButton16.setFont(resourceMap.getFont("jButton16.font")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setName("jButton16"); // NOI18N
        physicianPanel.add(jButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 530, 100, 30));

        jButton17.setAction(actionMap.get("cancelButton")); // NOI18N
        jButton17.setBackground(resourceMap.getColor("jButton19.background")); // NOI18N
        jButton17.setFont(resourceMap.getFont("jButton16.font")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N
        physicianPanel.add(jButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 530, 100, -1));

        jButton19.setAction(actionMap.get("changeToFirstTab")); // NOI18N
        jButton19.setBackground(resourceMap.getColor("jButton19.background")); // NOI18N
        jButton19.setFont(resourceMap.getFont("jButton19.font")); // NOI18N
        jButton19.setText(resourceMap.getString("jButton19.text")); // NOI18N
        jButton19.setName("jButton19"); // NOI18N
        physicianPanel.add(jButton19, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 530, 110, -1));

        jScrollPane22.setName("jScrollPane22"); // NOI18N

        entry_consultationArea3.setEditable(false);
        entry_consultationArea3.setFont(resourceMap.getFont("entry_consultationArea3.font")); // NOI18N
        entry_consultationArea3.setName("entry_consultationArea3"); // NOI18N
        jScrollPane22.setViewportView(entry_consultationArea3);

        physicianPanel.add(jScrollPane22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 700, 480));

        patientPane.addTab(resourceMap.getString("physicianPanel.TabConstraints.tabTitle"), physicianPanel); // NOI18N

        patientPane.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        PasswordFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        PasswordFrame.setTitle(resourceMap.getString("PasswordFrame.title")); // NOI18N
        PasswordFrame.setMinimumSize(new java.awt.Dimension(320, 117));
        PasswordFrame.setName("PasswordFrame"); // NOI18N
        PasswordFrame.setResizable(false);

        jLabel33.setFont(resourceMap.getFont("passwdConfirm.font")); // NOI18N
        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        jPasswordField1.setFont(resourceMap.getFont("passwdConfirm.font")); // NOI18N
        jPasswordField1.setText(resourceMap.getString("jPasswordField1.text")); // NOI18N
        jPasswordField1.setName("jPasswordField1"); // NOI18N

        passwdConfirm.setAction(actionMap.get("confirmPassword")); // NOI18N
        passwdConfirm.setFont(resourceMap.getFont("passwdConfirm.font")); // NOI18N
        passwdConfirm.setText(resourceMap.getString("passwdConfirm.text")); // NOI18N
        passwdConfirm.setName("passwdConfirm"); // NOI18N

        org.jdesktop.layout.GroupLayout PasswordFrameLayout = new org.jdesktop.layout.GroupLayout(PasswordFrame.getContentPane());
        PasswordFrame.getContentPane().setLayout(PasswordFrameLayout);
        PasswordFrameLayout.setHorizontalGroup(
            PasswordFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PasswordFrameLayout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jLabel33)
                .add(18, 18, 18)
                .add(jPasswordField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, PasswordFrameLayout.createSequentialGroup()
                .addContainerGap(126, Short.MAX_VALUE)
                .add(passwdConfirm)
                .add(124, 124, 124))
        );
        PasswordFrameLayout.setVerticalGroup(
            PasswordFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PasswordFrameLayout.createSequentialGroup()
                .add(22, 22, 22)
                .add(PasswordFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel33)
                    .add(jPasswordField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(passwdConfirm)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        imageFileChooser.setAcceptAllFileFilterUsed(false);
        imageFileChooser.setCurrentDirectory(new java.io.File("C:\\Program Files\\NetBeans 7.0.1"));
        imageFileChooser.setMinimumSize(new java.awt.Dimension(560, 397));
        imageFileChooser.setName("imageFileChooser"); // NOI18N

        emailFrame.setTitle(resourceMap.getString("emailFrame.title")); // NOI18N
        emailFrame.setName("emailFrame"); // NOI18N

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N

        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N

        senderEmail.setText(resourceMap.getString("senderEmail.text")); // NOI18N
        senderEmail.setName("senderEmail"); // NOI18N

        senderPasswd.setText(resourceMap.getString("senderPasswd.text")); // NOI18N
        senderPasswd.setName("senderPasswd"); // NOI18N

        jLabel39.setText(resourceMap.getString("jLabel39.text")); // NOI18N
        jLabel39.setName("jLabel39"); // NOI18N

        receiverEmail.setName("receiverEmail"); // NOI18N

        jLabel40.setText(resourceMap.getString("jLabel40.text")); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N

        Subject.setText(resourceMap.getString("Subject.text")); // NOI18N
        Subject.setName("Subject"); // NOI18N

        jLabel41.setText(resourceMap.getString("jLabel41.text")); // NOI18N
        jLabel41.setName("jLabel41"); // NOI18N

        Content.setName("Content"); // NOI18N

        Text.setColumns(20);
        Text.setRows(5);
        Text.setName("Text"); // NOI18N
        Content.setViewportView(Text);

        jButton6.setAction(actionMap.get("checkEmailEntries")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N

        jButton7.setAction(actionMap.get("cancelEmail")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N

        clearBtn.setAction(actionMap.get("clearEmail")); // NOI18N
        clearBtn.setText(resourceMap.getString("clearBtn.text")); // NOI18N
        clearBtn.setName("clearBtn"); // NOI18N

        jLabel44.setText(resourceMap.getString("jLabel44.text")); // NOI18N
        jLabel44.setName("jLabel44"); // NOI18N

        org.jdesktop.layout.GroupLayout emailFrameLayout = new org.jdesktop.layout.GroupLayout(emailFrame.getContentPane());
        emailFrame.getContentPane().setLayout(emailFrameLayout);
        emailFrameLayout.setHorizontalGroup(
            emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(emailFrameLayout.createSequentialGroup()
                .addContainerGap()
                .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(emailFrameLayout.createSequentialGroup()
                        .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel36)
                            .add(jLabel35)
                            .add(jLabel40)
                            .add(jLabel39)
                            .add(jLabel41))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(Content, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                            .add(Subject, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                            .add(receiverEmail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(senderEmail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(emailFrameLayout.createSequentialGroup()
                                .add(senderPasswd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jLabel44)
                                .add(44, 44, 44)))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, emailFrameLayout.createSequentialGroup()
                        .add(jButton6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jButton7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(clearBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(161, 161, 161))))
        );
        emailFrameLayout.setVerticalGroup(
            emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(emailFrameLayout.createSequentialGroup()
                .addContainerGap()
                .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel35)
                    .add(senderEmail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel36)
                    .add(senderPasswd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel44))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel40)
                    .add(receiverEmail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel39)
                    .add(Subject, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel41)
                    .add(Content, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 187, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 21, Short.MAX_VALUE)
                .add(emailFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton6)
                    .add(jButton7)
                    .add(clearBtn))
                .add(19, 19, 19))
        );

        SavingWindow.setMinimumSize(new java.awt.Dimension(300, 205));
        SavingWindow.setName("SavingWindow"); // NOI18N
        SavingWindow.setResizable(false);

        jLabel37.setIcon(resourceMap.getIcon("jLabel37.icon")); // NOI18N
        jLabel37.setText(resourceMap.getString("jLabel37.text")); // NOI18N
        jLabel37.setName("jLabel37"); // NOI18N

        jProgressBar1.setMaximumSize(new java.awt.Dimension(32767, 20));
        jProgressBar1.setMinimumSize(new java.awt.Dimension(300, 20));
        jProgressBar1.setName("jProgressBar1"); // NOI18N
        jProgressBar1.setPreferredSize(new java.awt.Dimension(300, 20));

        org.jdesktop.layout.GroupLayout SavingWindowLayout = new org.jdesktop.layout.GroupLayout(SavingWindow.getContentPane());
        SavingWindow.getContentPane().setLayout(SavingWindowLayout);
        SavingWindowLayout.setHorizontalGroup(
            SavingWindowLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel37)
            .add(jProgressBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        SavingWindowLayout.setVerticalGroup(
            SavingWindowLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SavingWindowLayout.createSequentialGroup()
                .add(jLabel37)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        PasswordFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        PasswordFrame1.setTitle(resourceMap.getString("PasswordFrame1.title")); // NOI18N
        PasswordFrame1.setMinimumSize(new java.awt.Dimension(320, 117));
        PasswordFrame1.setName("PasswordFrame1"); // NOI18N
        PasswordFrame1.setResizable(false);

        jLabel38.setFont(resourceMap.getFont("jPasswordField2.font")); // NOI18N
        jLabel38.setText(resourceMap.getString("jLabel38.text")); // NOI18N
        jLabel38.setName("jLabel38"); // NOI18N

        jPasswordField2.setFont(resourceMap.getFont("jPasswordField2.font")); // NOI18N
        jPasswordField2.setName("jPasswordField2"); // NOI18N

        passwdConfirm1.setAction(actionMap.get("deletePatient")); // NOI18N
        passwdConfirm1.setFont(resourceMap.getFont("jPasswordField2.font")); // NOI18N
        passwdConfirm1.setText(resourceMap.getString("passwdConfirm1.text")); // NOI18N
        passwdConfirm1.setName("passwdConfirm1"); // NOI18N

        org.jdesktop.layout.GroupLayout PasswordFrame1Layout = new org.jdesktop.layout.GroupLayout(PasswordFrame1.getContentPane());
        PasswordFrame1.getContentPane().setLayout(PasswordFrame1Layout);
        PasswordFrame1Layout.setHorizontalGroup(
            PasswordFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PasswordFrame1Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jLabel38)
                .add(18, 18, 18)
                .add(jPasswordField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, PasswordFrame1Layout.createSequentialGroup()
                .addContainerGap(125, Short.MAX_VALUE)
                .add(passwdConfirm1)
                .add(125, 125, 125))
        );
        PasswordFrame1Layout.setVerticalGroup(
            PasswordFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PasswordFrame1Layout.createSequentialGroup()
                .add(22, 22, 22)
                .add(PasswordFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel38)
                    .add(jPasswordField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(passwdConfirm1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PasswordFrame2.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        PasswordFrame2.setTitle(resourceMap.getString("PasswordFrame2.title")); // NOI18N
        PasswordFrame2.setMinimumSize(new java.awt.Dimension(320, 117));
        PasswordFrame2.setName("PasswordFrame2"); // NOI18N
        PasswordFrame2.setResizable(false);

        jLabel46.setFont(resourceMap.getFont("jLabel46.font")); // NOI18N
        jLabel46.setText(resourceMap.getString("jLabel46.text")); // NOI18N
        jLabel46.setName("jLabel46"); // NOI18N

        jPasswordField3.setFont(resourceMap.getFont("jLabel46.font")); // NOI18N
        jPasswordField3.setName("jPasswordField3"); // NOI18N

        passwdConfirm2.setAction(actionMap.get("saveToPassport")); // NOI18N
        passwdConfirm2.setFont(resourceMap.getFont("jLabel46.font")); // NOI18N
        passwdConfirm2.setText(resourceMap.getString("passwdConfirm2.text")); // NOI18N
        passwdConfirm2.setName("passwdConfirm2"); // NOI18N

        org.jdesktop.layout.GroupLayout PasswordFrame2Layout = new org.jdesktop.layout.GroupLayout(PasswordFrame2.getContentPane());
        PasswordFrame2.getContentPane().setLayout(PasswordFrame2Layout);
        PasswordFrame2Layout.setHorizontalGroup(
            PasswordFrame2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 339, Short.MAX_VALUE)
            .add(PasswordFrame2Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jLabel46)
                .add(18, 18, 18)
                .add(jPasswordField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, PasswordFrame2Layout.createSequentialGroup()
                .addContainerGap(132, Short.MAX_VALUE)
                .add(passwdConfirm2)
                .add(118, 118, 118))
        );
        PasswordFrame2Layout.setVerticalGroup(
            PasswordFrame2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
            .add(PasswordFrame2Layout.createSequentialGroup()
                .add(22, 22, 22)
                .add(PasswordFrame2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel46)
                    .add(jPasswordField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(passwdConfirm2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        zoomTextField.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        zoomTextField.setTitle(resourceMap.getString("zoomTextField.title")); // NOI18N
        zoomTextField.setName("zoomTextField"); // NOI18N
        zoomTextField.setResizable(false);

        jScrollPane18.setName("jScrollPane18"); // NOI18N

        zoomedText.setFont(resourceMap.getFont("zoomedText.font")); // NOI18N
        zoomedText.setName("zoomedText"); // NOI18N
        jScrollPane18.setViewportView(zoomedText);

        zoomOkButton.setAction(actionMap.get("closeZoomField")); // NOI18N
        zoomOkButton.setFont(resourceMap.getFont("zoomOkButton.font")); // NOI18N
        zoomOkButton.setText(resourceMap.getString("zoomOkButton.text")); // NOI18N
        zoomOkButton.setName("zoomOkButton"); // NOI18N

        org.jdesktop.layout.GroupLayout zoomTextFieldLayout = new org.jdesktop.layout.GroupLayout(zoomTextField.getContentPane());
        zoomTextField.getContentPane().setLayout(zoomTextFieldLayout);
        zoomTextFieldLayout.setHorizontalGroup(
            zoomTextFieldLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(zoomTextFieldLayout.createSequentialGroup()
                .addContainerGap()
                .add(zoomTextFieldLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(zoomTextFieldLayout.createSequentialGroup()
                        .add(jScrollPane18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, zoomTextFieldLayout.createSequentialGroup()
                        .add(zoomOkButton)
                        .add(174, 174, 174))))
        );
        zoomTextFieldLayout.setVerticalGroup(
            zoomTextFieldLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(zoomTextFieldLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 216, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(zoomOkButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        zoomTextPane.setName("zoomTextPane"); // NOI18N

        jButton18.setFont(resourceMap.getFont("jButton18.font")); // NOI18N
        jButton18.setText(resourceMap.getString("jButton18.text")); // NOI18N
        jButton18.setName("jButton18"); // NOI18N

        jScrollPane37.setName("jScrollPane37"); // NOI18N

        jTextPane1.setName("jTextPane1"); // NOI18N
        jScrollPane37.setViewportView(jTextPane1);

        org.jdesktop.layout.GroupLayout zoomTextPaneLayout = new org.jdesktop.layout.GroupLayout(zoomTextPane.getContentPane());
        zoomTextPane.getContentPane().setLayout(zoomTextPaneLayout);
        zoomTextPaneLayout.setHorizontalGroup(
            zoomTextPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(zoomTextPaneLayout.createSequentialGroup()
                .add(zoomTextPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(zoomTextPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane37, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                    .add(zoomTextPaneLayout.createSequentialGroup()
                        .add(176, 176, 176)
                        .add(jButton18)))
                .addContainerGap())
        );
        zoomTextPaneLayout.setVerticalGroup(
            zoomTextPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, zoomTextPaneLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane37, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton18)
                .addContainerGap())
        );

        setComponent(introPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    //this is for the female radio button
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane Content;
    private javax.swing.JFrame PasswordFrame;
    private javax.swing.JFrame PasswordFrame1;
    private javax.swing.JFrame PasswordFrame2;
    private javax.swing.JFrame SavingWindow;
    private javax.swing.JTextField Subject;
    private javax.swing.JTextArea Text;
    private javax.swing.JCheckBox bmiCopyCheck;
    private javax.swing.JTextField bmiCopyTxt;
    private javax.swing.JTable bmiTable;
    private javax.swing.JButton btn_TBtest;
    private javax.swing.JButton btn_medExam;
    private javax.swing.JButton clearBtn;
    private javax.swing.JButton deleteButton;
    private javax.swing.JFrame emailFrame;
    public volatile javax.swing.JPanel entryPanel;
    private javax.swing.JTextPane entry_allergyArea;
    public volatile javax.swing.JPanel entry_basicPanel;
    private javax.swing.JTextField entry_birthCityField;
    private javax.swing.JFormattedTextField entry_birthDateField;
    private javax.swing.JTextField entry_birthHospitalField;
    public volatile javax.swing.JPanel entry_birthPanel;
    private javax.swing.JTextField entry_bloodField;
    private javax.swing.JButton entry_cancelButton;
    private javax.swing.JTextPane entry_consultantArea;
    private javax.swing.JTextPane entry_consultationArea;
    private javax.swing.JTextPane entry_consultationArea1;
    private javax.swing.JTextPane entry_consultationArea2;
    private javax.swing.JTextPane entry_consultationArea3;
    private javax.swing.JTextField entry_emailField;
    private javax.swing.JTextField entry_emcallField;
    private javax.swing.JTextField entry_eyeColorField;
    public volatile javax.swing.JRadioButton entry_fRadio;
    private javax.swing.JTextPane entry_familyHistArea;
    private javax.swing.JTextField entry_fatherField;
    private javax.swing.JTextField entry_firstField;
    private javax.swing.JTextField entry_insuranceField;
    private javax.swing.JTextPane entry_labsArea;
    private javax.swing.JTextPane entry_lastEditArea;
    private javax.swing.JTextField entry_lastField;
    public volatile javax.swing.JRadioButton entry_mRadio;
    private javax.swing.JTextPane entry_medExamArea;
    private javax.swing.JTextPane entry_medHistArea;
    private javax.swing.JTextPane entry_medsArea;
    private javax.swing.JTextField entry_memberIDField;
    public volatile javax.swing.JPanel entry_memberPanel;
    private javax.swing.JTextPane entry_mentalHealthArea;
    private javax.swing.JTextField entry_motherField;
    private javax.swing.JPanel entry_peoplePanel;
    private javax.swing.JTextField entry_phoneField;
    private javax.swing.JTextPane entry_primaryCareArea;
    private javax.swing.JTextPane entry_radiologyArea;
    private javax.swing.JButton entry_saveButton;
    private javax.swing.JTextPane entry_skinTestArea;
    private javax.swing.JTextPane entry_socialIssuesArea;
    private javax.swing.JTextPane entry_specialArea;
    private javax.swing.JTextPane entry_specialistArea;
    private javax.swing.ButtonGroup genderGroup;
    private javax.swing.JCheckBox growthCopyCheck;
    private javax.swing.JTextField growthCopyTxt;
    public volatile javax.swing.JPanel growthPanel;
    private javax.swing.JTable growthTable;
    private javax.swing.JButton growth_bmiChartButton;
    public volatile javax.swing.JPanel growth_buttonPanel;
    private javax.swing.JButton growth_chart1Button;
    private javax.swing.JButton growth_chart2Button;
    private javax.swing.JButton growth_chart3Button;
    private javax.swing.JButton growth_chart4Button;
    private javax.swing.JPanel growth_imagePanel;
    public volatile javax.swing.JScrollPane growth_lowerScrollPane;
    public volatile javax.swing.JScrollPane growth_upperScrollPane;
    private javax.swing.JFileChooser imageFileChooser;
    public volatile javax.swing.JPanel imagePanel;
    private javax.swing.JTextField image_labelField;
    private javax.swing.JButton image_leftButton;
    private javax.swing.JButton image_rightButton;
    private javax.swing.JPanel introPanel;
    private javax.swing.JTextPane introTextPane;
    private javax.swing.JTextField intro_docField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPasswordField jPasswordField3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane29;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane37;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextPane jTextPane1;
    public volatile javax.swing.JPanel largePanel;
    public volatile javax.swing.JPanel mainPanel;
    public volatile javax.swing.JMenuBar menuBar;
    private javax.swing.JButton modifyButton;
    private javax.swing.JButton newPatientButton;
    private javax.swing.JButton passwdConfirm;
    private javax.swing.JButton passwdConfirm1;
    private javax.swing.JButton passwdConfirm2;
    private javax.swing.JLabel patientLabel;
    private javax.swing.JTabbedPane patientPane;
    public volatile javax.swing.JScrollPane patientScrollPane;
    public volatile javax.swing.JPanel physicianPanel;
    private javax.swing.JPanel problemlistPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField receiverEmail;
    private javax.swing.JPanel riskfactorPanel;
    private javax.swing.JButton saveToPassportButton;
    private javax.swing.JCheckBox schedCopyCheck;
    private javax.swing.JTextField schedCopyTxt;
    private javax.swing.JTable schedTable;
    private javax.swing.JPanel schedulePanel;
    private javax.swing.JTextField senderEmail;
    private javax.swing.JPasswordField senderPasswd;
    public volatile javax.swing.JPanel specialistPanel;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusMessageLabel;
    public volatile javax.swing.JPanel statusPanel;
    private javax.swing.JButton zoomOkButton;
    private javax.swing.JFrame zoomTextField;
    private javax.swing.JFrame zoomTextPane;
    private javax.swing.JTextPane zoomedText;
    // End of variables declaration//GEN-END:variables

    
    
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    class ListSelectionHandler1 implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            int selectionIndex = lsm.getAnchorSelectionIndex();
            String patientName = patients.getList().getElementAt(selectionIndex);
            selectedPatient = patientName;
            modifyButton.setEnabled(true);
		}
	}
    
    //gender changed
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("F") && this.entry_fRadio.isSelected())
        {
            this.prevGender = this.gender;
            this.gender = "F";
            updateTheme();
        }
        else if (e.getActionCommand().equals("M") && this.entry_mRadio.isSelected())
        {
            this.prevGender = this.gender;
            this.gender = "M";
            updateTheme();
        }
        fillStandardGrowthTableValues();
    }

    /*
     * Updates the GUI based on patient's gender
     *
     */
    public void updateTheme()
    {
        try
        {
            //wait until the color changer is done changing the previous color
            if (this.colorChanger != null)
                this.colorChanger.join();

            this.colorChanger = new ColorChangingThread();
            this.colorChanger.start();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public class ColorChangingThread extends Thread
    {
        private final Color femaleMainColor = new Color(255,225,225);
        private final Color maleMainColor = new Color(204,255,255);
        private final Color neutralMainColor = new Color(240,240,240);

        private final Color femaleDarker1 = new Color(255,234,224);
        private final Color maleDarker1 = new Color(230,255,255);
        private final Color neutralDarker1 = new Color(240,240,240);

        private final Color femaleDarker2 = new Color(255,214,204);
        private final Color maleDarker2 = new Color(214,255,255);
        private final Color neutralDarker2 = new Color(225,225,225);

        private final Color femaleLighter1 = new Color(255,220,220);
        private final Color maleLighter1 = new Color(220,255,255);
        private final Color neutralLighter1 = new Color(240,240,240);

        private final int SLEEP_TIME_MS = 10; //the larger this number, the longer the transition takes

        @Override
        public void run()
        {
            Color mainColor;
            Color darker1;
            Color darker2;
            Color lighter1;

            int divisions = 40; //larger number means smoother transition (>255 makes no sense)

            for (int i=1; i <= divisions; i++)
            {
                Color prevMainColor;
                Color prevDarker1;
                Color prevDarker2;
                Color prevLighter1;

                Color nextMainColor;
                Color nextDarker1;
                Color nextDarker2;
                Color nextLighter1;

                if (gender.equals(prevGender))
                    return;

                if (gender.equals("None"))
                {
                    nextMainColor = neutralMainColor;
                    nextDarker1 = neutralDarker1;
                    nextDarker2 = neutralDarker2;
                    nextLighter1 = neutralLighter1;
                }
                else if (gender.equals("F"))
                {
                    nextMainColor = femaleMainColor;
                    nextDarker1 = femaleDarker1;
                    nextDarker2 = femaleDarker2;
                    nextLighter1 = femaleLighter1;
                }
                else
                {
                    nextMainColor = maleMainColor;
                    nextDarker1 = maleDarker1;
                    nextDarker2 = maleDarker2;
                    nextLighter1 = maleLighter1;
                }

                if (prevGender.equals("None"))
                {
                    prevMainColor = neutralMainColor;
                    prevDarker1 = neutralDarker1;
                    prevDarker2 = neutralDarker2;
                    prevLighter1 = neutralLighter1;
                }
                else if (prevGender.equals("F"))
                {
                    prevMainColor = femaleMainColor;
                    prevDarker1 = femaleDarker1;
                    prevDarker2 = femaleDarker2;
                    prevLighter1 = femaleLighter1;
                }
                else
                {
                    prevMainColor = maleMainColor;
                    prevDarker1 = maleDarker1;
                    prevDarker2 = maleDarker2;
                    prevLighter1 = maleLighter1;
                }

                mainColor = graduateRGB(i,divisions,prevMainColor,nextMainColor);
                darker1 = graduateRGB(i,divisions,prevDarker1,nextDarker1);
                darker2 = graduateRGB(i,divisions,prevDarker2,nextDarker2);
                lighter1 = graduateRGB(i,divisions,prevLighter1,nextLighter1);

                //BG Color
                mainPanel.setBackground(mainColor);
                entryPanel.setBackground(mainColor);
                largePanel.setBackground(mainColor);
                specialistPanel.setBackground(mainColor);
                physicianPanel.setBackground(mainColor);
                schedulePanel.setBackground(mainColor);
                growthPanel.setBackground(mainColor);
                problemlistPanel.setBackground(mainColor);
                riskfactorPanel.setBackground(mainColor);
                growth_upperScrollPane.setBackground(growthPanel.getBackground());
                growth_lowerScrollPane.setBackground(growthPanel.getBackground());                                 

                //Darker1 Color Set
                growth_buttonPanel.setBackground(darker1);
                growth_imagePanel.setBackground(darker1);
                    growth_imagePanel.removeAll();
                entry_basicPanel.setBackground(darker1);
                entry_mRadio.setBackground(entry_basicPanel.getBackground());
                entry_fRadio.setBackground(entry_basicPanel.getBackground());
                entry_birthPanel.setBackground(darker1);
                entry_memberPanel.setBackground(darker1);
                entry_peoplePanel.setBackground(darker1);
                imagePanel.setBackground(darker1);

                //Darker2 Color Set
                statusPanel.setBackground(darker2);

                //Lighter1 Color Set
                patientScrollPane.setBackground(lighter1);

                try
                {
                    Thread.sleep(SLEEP_TIME_MS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    return;
                }

                getComponent().invalidate();
            }
        }

        private Color graduateRGB(int index, int divisions, Color from, Color to)
        {
            return new Color(from.getRed()+(int)(((double)(to.getRed()-from.getRed())/divisions)*index),
                    from.getGreen()+(int)(((double)(to.getGreen()-from.getGreen())/divisions)*index),
                    from.getBlue()+(int)(((double)(to.getBlue()-from.getBlue())/divisions)*index));
        }
    }

    //fills entryPanel fields with selectedEntry params
    public void fillPatientPane()
    {
        //this.patientPane.setEnabledAt(2, true); //enable growth chart for existing patients
        //this.patientPane.setEnabledAt(3, true); //enable sched
        this.growth_bmiChartButton.setEnabled(true);
        this.growth_chart1Button.setEnabled(true);
        this.growth_chart2Button.setEnabled(true);
        this.growth_chart3Button.setEnabled(true);
        this.growth_chart4Button.setEnabled(true);
        
        if (this.patientImage != null)
            this.imagePanel.remove(this.patientImage); //removes the image from the image panel
        
        String[] firstImage = this.selectedEntry.images.getFirst();
        if (!firstImage[0].equals(""))
        {
            this.image_labelField.setText(firstImage[1]);
            String patientDir = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator;
            this.patientImage = new ImagePanel(patientDir + firstImage[0]);
            //setRGB( dst, 0, 0, width, height, inPixels );
            this.imagePanel.add(patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
            this.imagePanel.validate();
        }

        this.growth_imagePanel.removeAll();
        this.jLabel50.setVisible(true);
/*
            this.growth_imagePanel.removeAll();
            //String imageOutputLocation = this.currentDirectory + File.separator + "medpassport" + File.separator + "resources" + File.separator + "growthChartBackGround.jpg";
            //ImagePanel initImage = new ImagePanel(getClass().getResource("resources\\about.png").getFile());       
            ImagePanel initImage = new ImagePanel(getClass().getResource("resources" + File.separator + "about.png").toString());
            initImage.setAllSizes(preferredChartFrameSize);
            this.growth_imagePanel.add(initImage);
            this.growth_imagePanel.validate();

            this.growth_imagePanel.repaint();

       */         
        

        this.entry_firstField.setText(selectedEntry.first);
        this.entry_lastField.setText(selectedEntry.last);
        this.entry_firstField.setEnabled(false);
        this.entry_lastField.setEnabled(false);
        if (selectedEntry.gender.equals("M"))
        {
            this.entry_mRadio.setSelected(true);
            this.prevGender = this.gender;
            this.gender = "M";
            updateTheme();
        }
        else if (selectedEntry.gender.equals("F"))
        {
            this.entry_fRadio.setSelected(true);
            this.prevGender = this.gender;
            this.gender = "F";
            updateTheme();
        }
        else
        {
            if (this.genderGroup.getSelection() != null)
                this.genderGroup.getSelection().setSelected(false); //java 1.5
            //this.genderGroup.clearSelection(); //java 1.6
            this.prevGender = this.gender;
            this.gender = "None";
            updateTheme();
        }

        //this.growthMenuItem.setEnabled(true);

        this.entry_birthDateField.setText(selectedEntry.birthDate);
        this.entry_birthCityField.setText(selectedEntry.birthCity);
        this.entry_birthHospitalField.setText(selectedEntry.birthHospital);

        this.entry_insuranceField.setText(selectedEntry.insuranceProvider);
        this.entry_memberIDField.setText(selectedEntry.memberID);
        this.entry_fatherField.setText(selectedEntry.fatherName);
        this.entry_motherField.setText(selectedEntry.motherName);
        this.entry_phoneField.setText(selectedEntry.phone);
        this.entry_emcallField.setText(selectedEntry.emcall);
        this.entry_emailField.setText(selectedEntry.email);
        this.entry_eyeColorField.setText(selectedEntry.eyeColor);
        this.entry_bloodField.setText(selectedEntry.cordBlood);
        this.entry_labsArea.setText(selectedEntry.labs);
        this.entry_radiologyArea.setText(selectedEntry.radiology);  
        this.entry_medsArea.setText(selectedEntry.meds);
        this.entry_allergyArea.setText(selectedEntry.allergies);
        this.entry_consultationArea.setText(selectedEntry.consultationNote);
        this.entry_skinTestArea.setText(selectedEntry.TBSkinTest);
        this.entry_medHistArea.setText(selectedEntry.medicalHistory);
        this.entry_mentalHealthArea.setText(selectedEntry.mentalHealth);
        this.entry_socialIssuesArea.setText(selectedEntry.socialIssues);
        this.entry_familyHistArea.setText(selectedEntry.familyHistory);
        this.entry_specialArea.setText(selectedEntry.specialFeatures);
        this.entry_primaryCareArea.setText(selectedEntry.primaryCare);
        this.entry_specialistArea.setText(selectedEntry.specialists);
        this.entry_consultantArea.setText(selectedEntry.consultants);
        this.entry_consultationArea1.setText(selectedEntry.consultationNote_s);
        this.entry_consultationArea2.setText(selectedEntry.consultationNote_er);

        this.entry_lastEditArea.setText(selectedEntry.lastEdit);

        this.fillStandardGrowthTableValues();

        this.growthTable.clearSelection();
        ExcelAdapter1 myAd = new ExcelAdapter1(this.growthTable);
        javax.swing.table.TableModel model = this.growthTable.getModel();
        for (int i=0; i < this.growthTable.getRowCount(); i++)
        {
            for (int j=1; j < this.growthTable.getColumnCount(); j++)
            {
                if (i == 1)
                    model.setValueAt(this.selectedEntry.growth1[j-1], i, j);
                else if (i == 3)
                    model.setValueAt(this.selectedEntry.growth2[j-1], i, j);
                else if (i == 5)
                    model.setValueAt(this.selectedEntry.growth3[j-1], i, j);
            }
        }
        this.growthTable.setCellSelectionEnabled(true);

        this.bmiTable.clearSelection();
        ExcelAdapter1 myAd1 = new ExcelAdapter1(this.bmiTable);
        model = this.bmiTable.getModel();
        for (int j=1; j < this.bmiTable.getColumnCount(); j++)
        {
            model.setValueAt(this.selectedEntry.bmi[j-1], 1, j);
        }
        this.bmiTable.setCellSelectionEnabled(true);

        this.schedTable.clearSelection();
        ExcelAdapter myAd2 = new ExcelAdapter(this.schedTable);
        model = this.schedTable.getModel();
        for (int i=0; i < this.schedTable.getRowCount(); i++)
        {
            for (int j=0; j < this.schedTable.getColumnCount(); j++)
            {
                model.setValueAt(this.selectedEntry.sched[i][j], i, j);
            }
        }
        this.schedTable.setCellSelectionEnabled(true);
    }

    //saves all entered information into the selectedEntry class params
    //and then calls its createXMLEntry method to create the file.
    @Action
    public void savePatient() throws FileNotFoundException, IOException, FTPException, InterruptedException {
        //try {
        //JOptionPane.showMessageDialog(null, "0 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        /*
        this.entry_saveButton.setEnabled(false);
        Runnable runA = new Runnable() {
            public void run() {
                //showSavingWindow();
                ss = new SavingScreen();
                ss.setAlwaysOnTop(true);
                //ss.toFront();
            }
        };

        final Thread threadA = new Thread(runA);
        threadA.start();
         * 
         */

        //JOptionPane.showMessageDialog(null, "1 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        Runnable runB = new Runnable() {
            public void run() {

                try {
                    //JOptionPane.showMessageDialog(null, "4 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    //while (true) {
                    savePatientPane();   
                    AutoDismiss a = new AutoDismiss(new JDialog());
                    a.showMessageDialog(null, "Saving Completed..");
                    //working = true;
                    //JOptionPane.showMessageDialog(null, "5 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    //ss.setVisible(false);
                    //threadA.interrupt();
                    entry_saveButton.setEnabled(true);
                    //JOptionPane.showMessageDialog(null, "6 " + threadA.isAlive(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    //JOptionPane.showMessageDialog(null, "7 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FTPException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread threadB = new Thread(runB);
        threadB.start();
        this.entry_saveButton.setEnabled(true);
        //JOptionPane.showMessageDialog(null, "2 " + Thread.activeCount() , "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        //threadA.interrupt();
        //JOptionPane.showMessageDialog(null, "3 " + Thread.activeCount() , "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        /*
        if (working) {
            //threadA.interrupt();
            this.SavingWindow.setVisible(false);
        }*/
    //} catch (IOException x) {
        //x.printStackTrace();
    //}
    //JOptionPane.showMessageDialog(null, Thread.activeCount() , "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        /*
        if (!selectedEntry.motherName.equals(""))
            hidePatientPane();
        for (int i = 0; i < 1000; i++) {
            tempList[i] = "";
            tempList1[i] = "";
            tempImg[i] = "";
            tempImg1[i] = "";
        }
        tempCount = 0;
        tempICount = 0;
        tempCount1 = 0;
        tempICount1 = 0;
        checkImageUpload = 0;
        copyCount = 0;
        errorCount = 0;*/        
    }

    @Action
    public void savePatientEmail() throws FileNotFoundException, IOException, FTPException, InterruptedException {
        //savePatientPane();
        //JOptionPane.showMessageDialog(null, "The patient information files will be compressed in a .zip file", "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        if (!selectedEntry.motherName.equals(""))
            showEmailFrame();
    }

    public void savePatientPane() throws FileNotFoundException, IOException, FTPException, InterruptedException {
        int k = 0, yz = 0;
        //JOptionPane.showMessageDialog(null, tempImg1[0] + "\n" + this.selectedEntry.images.imageNames[0], "Label", JOptionPane.ERROR_MESSAGE);
        if (selectedEntry == null) //if this is a new patient
        {
            if ((this.entry_birthDateField.getText().equals(""))
                || (this.entry_memberIDField.getText().equals(""))
                || (this.entry_firstField.getText().equals(""))
                || (this.entry_lastField.getText().equals("")) )
            {
                this.statusLabel.setText("Please fill out member ID, birth date, first, and last name.");

                Color redField = new Color(255,190,190);

                if (this.entry_birthDateField.getText().equals(""))
                    this.entry_birthDateField.setBackground(redField);
                else
                    this.entry_birthDateField.setBackground(Color.WHITE);

                if (this.entry_memberIDField.getText().equals(""))
                    this.entry_memberIDField.setBackground(redField);
                else
                    this.entry_memberIDField.setBackground(Color.WHITE);

                if (this.entry_firstField.getText().equals(""))
                    this.entry_firstField.setBackground(redField);
                else
                    this.entry_firstField.setBackground(Color.WHITE);

                if (this.entry_lastField.getText().equals(""))
                    this.entry_lastField.setBackground(redField);
                else
                    this.entry_lastField.setBackground(Color.WHITE);

                return;
            }

            String newPatient = this.entry_firstField.getText() + " " + this.entry_lastField.getText();
            File patientDir = new File(this.currentDirectory + File.separator + "Entries" + File.separator + newPatient);

            if (patientDir.exists() && patientDir.isDirectory()) {
                JOptionPane.showMessageDialog(null, "Patient exists!\nPlease press Cancel Button.", "Error", JOptionPane.ERROR_MESSAGE);
                //statusLabel.setText("Patient exists");
                return; //don't let the user go forward if there is a duplicate name
            }
            else {
                try
                {
                    if (patientDir.mkdir())
                    {
                        //statusLabel.setText("Patient created.");
                        patients.populatePatients();  //re-populate the list
                        jList1.setModel(patients.getList());  //redraw the list
                    }
                    else
                    {
                        //statusLabel.setText("Can't create patient database.");
                        JOptionPane.showMessageDialog(null, "Can't create patient database.\nPlease press Cancel Button.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                catch (SecurityException e)
                {
                    //statusLabel.setText("Can't create patient: security exception.");
                    JOptionPane.showMessageDialog(null, "Can't create patient: security exception.\nPlease press Cancel Button.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            selectedEntry = new Entry(currentDirectory + File.separator + "Entries" +
                               File.separator + newPatient + File.separator, "patientInfo");

            selectedPatient = newPatient;
            //temporary folder for image(in case of cancelling the creation of new patient)
            if (tempImage != null) {
                File file = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp" + File.separator + tempImage);
                File file1 = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                File dir = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);

                boolean success = file.renameTo(new File(dir, file.getName()));

                if (success)
                    deleteDir(file1);
            }
            /*
            if (tempImage != null) {
                patients.getList().remove(patients.getList().find("temp"));
                patients.populatePatients();  //re-populate the list
                jList1.setModel(patients.getList());
            }*/
        }

        if ((this.selectedEntry.images != null) && (this.patientImage != null))
            this.selectedEntry.images.setCurrentLabel(this.image_labelField.getText()); //save current label

        if (this.entry_fRadio.isSelected())
                selectedEntry.gender = "F";
        else //if (this.entry_mRadio.isSelected())
                selectedEntry.gender = "M";

        selectedEntry.first = this.entry_firstField.getText();
        selectedEntry.last = this.entry_lastField.getText();

        selectedEntry.birthDate = this.entry_birthDateField.getText();
        selectedEntry.birthCity = this.entry_birthCityField.getText();
        selectedEntry.birthHospital = this.entry_birthHospitalField.getText();

        selectedEntry.fatherName = this.entry_fatherField.getText();
        selectedEntry.motherName = this.entry_motherField.getText();
        selectedEntry.memberID = this.entry_memberIDField.getText();
        selectedEntry.eyeColor = this.entry_eyeColorField.getText();
        selectedEntry.cordBlood = this.entry_bloodField.getText();
        selectedEntry.phone = this.entry_phoneField.getText();
        selectedEntry.emcall=this.entry_emcallField.getText();
        selectedEntry.email = this.entry_emailField.getText();
        selectedEntry.insuranceProvider = this.entry_insuranceField.getText();
        this.entry_labsArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.labs = this.entry_labsArea.getText();
        this.entry_radiologyArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.radiology = this.entry_radiologyArea.getText();
        
        this.entry_primaryCareArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.primaryCare = this.entry_primaryCareArea.getText();
        this.entry_specialistArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.specialists = this.entry_specialistArea.getText();
        this.entry_consultantArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.consultants = this.entry_consultantArea.getText();

        this.entry_medsArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.meds = this.entry_medsArea.getText();
        this.entry_allergyArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.allergies = this.entry_allergyArea.getText();
        this.entry_consultationArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.consultationNote = this.entry_consultationArea.getText();
        this.entry_skinTestArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.TBSkinTest = this.entry_skinTestArea.getText();
        this.entry_medHistArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.medicalHistory = this.entry_medHistArea.getText();
        this.entry_socialIssuesArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.socialIssues = this.entry_socialIssuesArea.getText();
        this.entry_mentalHealthArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.mentalHealth = this.entry_mentalHealthArea.getText();
        this.entry_familyHistArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.familyHistory = this.entry_familyHistArea.getText();
        this.entry_specialArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.specialFeatures = this.entry_specialArea.getText();
        
        this.entry_consultationArea1.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.consultationNote_s = this.entry_consultationArea1.getText();
        
        this.entry_consultationArea2.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        selectedEntry.consultationNote_er = this.entry_consultationArea2.getText();

        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String currentEditor = this.intro_docField.getText().replace(';', ',') + " - " + dateFormat.format(new Date());
        selectedEntry.lastEdit = currentEditor + ";" + selectedEntry.lastEdit;

        this.saveGrowthTables();

        //this.selectedEntry.bmi[j-1] = (Double)model.getValueAt(1, j);
        if (this.schedTable.isEditing())
            this.schedTable.getCellEditor().stopCellEditing();
        javax.swing.table.TableModel model = this.schedTable.getModel();
        for (int i=0; i < this.schedTable.getRowCount(); i++)
        {
            for (int j=0; j < this.schedTable.getColumnCount(); j++)
            {
                String cell = ((String)model.getValueAt(i, j));
                if (cell == null)
                    this.selectedEntry.sched[i][j] = cell;
                else
                    this.selectedEntry.sched[i][j] = cell.replace(',', ' ');
            }
        }
        
        if (this.selectedPatient.equals("temp")) {
            //tempList1[tempCount++] = this.image_labelField.getText();
            for (int i = 0; i < this.selectedEntry.images.length; i++)
                if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName()))
                    k = i;

            tempList1[k] = this.image_labelField.getText();

            for (int i = 0; i < this.selectedEntry.images.length; i++) {
                this.selectedEntry.images.imageNames[i] = tempImg1[i];
                this.selectedEntry.images.labels[i] = tempList1[i];
            }

            //JOptionPane.showMessageDialog(null, tempCount + " " + tempList1[tempCount - 1] + "\n" + this.selectedEntry.images.imageNames[tempCount - 1], "Label", JOptionPane.ERROR_MESSAGE);
            /*
            this.selectedEntry.images = new ImageList(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label2", JOptionPane.ERROR_MESSAGE);
            for (int i = 0; i < this.selectedEntry.images.length - 1; i++) {
                this.selectedEntry.images.imageNames[i] = tempImg1[i];
                this.selectedEntry.images.labels[i] = tempList1[i];
            }
            this.selectedEntry.images.add(tempImg1[tempCount - 1], tempList1[tempCount - 1]);*/
        }
        else {
            if (checkImageUpload > 0) {
                for (int i = 0; i < this.selectedEntry.images.length; i++)
                    if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName()))
                        yz = i;

                tempList[yz] = this.image_labelField.getText();

                for (int i = 0; i < this.selectedEntry.images.length; i++) {
                    this.selectedEntry.images.imageNames[i] = tempImg[i];
                    this.selectedEntry.images.labels[i] = tempList[i];
                }
            }
            else {
                for (int i = 0; i < this.selectedEntry.images.length; i++)
                    if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName()))
                        yz = i;

                tempList[yz] = this.image_labelField.getText();
            }
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length + " " + tempCount1 + " " + tempImg[yz] + " " + tempList[tempCount1 - 1] + "\n" + this.selectedEntry.images.imageNames[tempCount1 - 1], "Label", JOptionPane.ERROR_MESSAGE);
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length + " " + tempCount1, "Label", JOptionPane.ERROR_MESSAGE);
            /*
            this.selectedEntry.images = new ImageList(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label2", JOptionPane.ERROR_MESSAGE);
            for (int i = 0; i < this.selectedEntry.images.length - 1; i++) {
                this.selectedEntry.images.imageNames[i] = tempImg[i];
                this.selectedEntry.images.labels[i] = tempList[i];
            }
            this.selectedEntry.images.add(tempImg[tempCount1 - 1], tempList[tempCount1 - 1]);*/
        }
        
        if (selectedEntry.motherName.length() == 0) {
            this.entry_motherField.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(null, "Please fill out mother's name", "Field required", JOptionPane.ERROR_MESSAGE);
            errorCount++;
            return;
        }
        else {/*
            this.selectedEntry.images = new ImageList(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            JOptionPane.showMessageDialog(null, tempImg1[0], "Label2", JOptionPane.ERROR_MESSAGE);
            for (int i = 0; i < this.selectedEntry.images.length - 1; i++) {
                this.selectedEntry.images.imageNames[i] = tempImg1[i];
                this.selectedEntry.images.labels[i] = tempList1[i];
            }
            this.selectedEntry.images.add(tempImg1[tempCount - 1], tempList1[tempCount - 1]);*/
            selectedEntry.createXMLEntry();
            this.statusLabel.setText("Patient info saved.");
        }

        if (this.selectedPatient.equals("temp")) {
            String newPatient = this.entry_firstField.getText() + " " + this.entry_lastField.getText();
            File patientDir = new File(this.currentDirectory + File.separator + "Entries" + File.separator + newPatient);
            File tempDir = new File(this.currentDirectory + File.separator + "Entries" + File.separator + "temp");
            if (patientDir.mkdir()) {
                patients.getList().addItem(newPatient);
                patients.populatePatients();  //re-populate the list
                jList1.setModel(patients.getList());  //redraw the list
            }
            else {
                statusLabel.setText("Can't create patient database.");
                return;
            }
            copyFiles(tempDir, patientDir);
            selectedPatient = newPatient;
        }

        File tempDir = new File(this.currentDirectory + File.separator + "Entries" + File.separator + "temp");
        if (tempDir.exists())
            deleteDir(tempDir);

        detectUSB();
        //ss.setStatus("Saving...", 50);
        
        File f = new File(".");
        String drive = f.getAbsolutePath();
        File fromPath, toPath, toTempArray, fromTemp1, toPaths[] = new File[driveCounts], toTempArrays[] = new File[driveCounts], exe;
        int option = 0;

        //C Drive to USB
        if (drive.charAt(0) == 'C') {/*
            if (driveCounts == 1) {
                fromPath = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
                toPath = new File(letters[0] + ":" + File.separator + "Entries"  + File.separator + selectedPatient);

                copyFiles(fromPath, toPath);

                File fromTemp = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                if (fromTemp.exists())
                    deleteDir(fromTemp);
                File toTemp = new File(letters[0] + ":" + File.separator + "Entries"  + File.separator + "temp");
                if (toTemp.exists())
                    deleteDir(toTemp);

                exe = new File(letters[0] + ":" + File.separator + "MedPassport.exe");
                if (!exe.exists())
                     Runtime.getRuntime().exec("xcopy " + currentDirectory + File.separator + "MedPassport.exe " + letters[0] + ":" + File.separator + " /C");
                //Thread.sleep(1000);
            }
            else if (driveCounts == 0 && !letters[0].equals("")) {
                fromPath = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
                toPath = new File(letters[0] + ":" + File.separator + "Entries"  + File.separator + selectedPatient);
                //toPath = new File("F:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + selectedPatient);
                copyFiles(fromPath, toPath);

                File fromTemp = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                if (fromTemp.exists())
                    deleteDir(fromTemp);
                File toTemp = new File(letters[0] + ":" + File.separator + "Entries"  + File.separator + "temp");
                if (toTemp.exists())
                    deleteDir(toTemp);

                exe = new File(letters[0] + ":" + File.separator + "MedPassport.exe");
                if (!exe.exists())
                     Runtime.getRuntime().exec("xcopy " + currentDirectory + File.separator + "MedPassport.exe " + letters[0] + ":" + File.separator + " /C");
            }
            else if (driveCounts > 1) {
                option = JOptionPane.showConfirmDialog(null, "More than one USB Flash Drive Found. Would you like to procees copy?", "Multiple Destination Found", JOptionPane.YES_NO_OPTION);
                fromPath = new File(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient);

                if (option == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < letters.length; i++) {
                        toPaths[i] = new File(letters[i] + ":" + File.separator + "Entries"  + File.separator + selectedPatient);
                        copyFiles(fromPath, toPaths[i]);


                        toTempArray[i] = new File(letters[i] + ":" + File.separator + "Entries"  + File.separator + "temp");
                        if (toTempArray[i].exists())
                            deleteDir(toTempArray[i]);

                        exe = new File(letters[i] + ":" + File.separator + "MedPassport.exe");
                        if (!exe.exists())
                            Runtime.getRuntime().exec("xcopy " + currentDirectory + File.separator + "MedPassport.exe " + letters[i] + ":" + File.separator + " /C");
                    }
                }

                File fromTemp = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                if (fromTemp.exists())
                    deleteDir(fromTemp);
            }*/
            if (driveCounts > 0) {
                //if (driveNum == driveCounts) {
                    fromPath = new File(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient);

                    for (int i = 0; i < driveCounts; i++) {
                        toPath = new File(letters[i] + ":" + File.separator + "Entries"  + File.separator + selectedPatient);
                        copyFiles(fromPath, toPath);
                        //JOptionPane.showMessageDialog(null, letters[i], "Field required", JOptionPane.ERROR_MESSAGE);

                        toTempArray = new File(letters[i] + ":" + File.separator + "Entries"  + File.separator + "temp");
                        if (toTempArray.exists())
                            deleteDir(toTempArray);

                        exe = new File(letters[i] + ":" + File.separator + "MedPassport.exe");
                        if (!exe.exists())
                            Runtime.getRuntime().exec("xcopy " + currentDirectory + File.separator + "MedPassport.exe " + letters[i] + ":" + File.separator + " /C");
                    }

                    fromTemp1 = new File(currentDirectory + File.separator + "Entries"  + File.separator + "temp");
                    if (fromTemp1.exists()) {
                        //JOptionPane.showMessageDialog(null, "It exists.", "Field required", JOptionPane.ERROR_MESSAGE);
                        deleteDir(fromTemp1);
                    }
                //}
            }
        }//USB to C Drive
        else {
            fromPath = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            toPath = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + selectedPatient);
            copyFiles(fromPath, toPath);

            /*
            if (!toPath.exists()) {
                //JOptionPane.showMessageDialog(null, "Couldn't copy", "Field required", JOptionPane.ERROR_MESSAGE);
                copyFiles(fromPath, toPath);
            }*/

            File fromTemp = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
            if (fromTemp.exists())
                deleteDir(fromTemp);
            File toTemp = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + "temp");
            if (toTemp.exists())
                deleteDir(toTemp);
        }

        fromPath = new File(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient);
        //toPath = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + selectedPatient);

        String[] list = null;
        if (fromPath.isDirectory())
            list = fromPath.list();

        String[] localfileList = new String[list.length];
        String[] dirs, root, medpass;

        int dirCount = 0, rootCount = 0, medCount = 0;
        int ascii = 0;

        for (int i = 0; i < list.length; i++)
            if (list[i].equals("patientInfo"))
                ascii = i;

        //copy all the files in the list.
        for (int i = 0; i < list.length; i++)
            localfileList[i] = currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + list[i];

        //ss.setStatus("Saving...", 70);
        
        /*
        FTPClient ftp;
        ftp = new FTPClient();
        ftp.setRemoteHost("ftp.bebedoc.com");

        FTPMessageCollector listener = new FTPMessageCollector();
        ftp.setMessageListener(listener);
        ftp.connect();
        ftp.login("rwatchi", "Bebedoc901");
        ftp.setConnectMode(FTPConnectMode.PASV);

        ftp.setType(FTPTransferType.BINARY);
        ///////////////////////////////////////
        root = ftp.dir(".", false);
        for (int i = 0; i < root.length; i++)
            if (root[i].equals("MedPassport"))
                rootCount++;

        if (rootCount == 0) {
            ftp.mkdir("MedPassport");
            ftp.chdir("MedPassport");
        }
        else
            ftp.chdir("MedPassport");

        medpass = ftp.dir(".", false);
        for (int i = 0; i < medpass.length; i++)
            if (medpass[i].equals("Entries"))
                medCount++;

        if (medCount == 0) {
            ftp.mkdir("Entries");
            ftp.chdir("Entries");
        }
        else
            ftp.chdir("Entries");

        //ftp.chdir("/MedPassport/Entries/");
        dirs = ftp.dir(".", false);
        //JOptionPane.showMessageDialog(null, dirs[1], "Field required", JOptionPane.ERROR_MESSAGE);

        for (int i = 0; i < dirs.length; i++)
            if (dirs[i].equals(selectedPatient))
                dirCount++;

        if (dirCount == 0) {
            ftp.mkdir(selectedPatient);
            ftp.chdir(selectedPatient);
        }
        else
            ftp.chdir(selectedPatient);

        ss.setStatus("Saving...", 80);
        
        for (int i = 0; i < ascii; i++)
            ftp.put(localfileList[i], list[i], false);

        for (int i = ascii + 1; i < list.length; i++)
            ftp.put(localfileList[i], list[i], false);

        ftp.setType(FTPTransferType.ASCII);
        ftp.put(localfileList[ascii], list[ascii], false);

        ftp.quit();
        ss.setStatus("Saving...", 90);
        */

        patients.getList().remove(patients.getList().find("temp"));
        patients.populatePatients();  //re-populate the list
        jList1.setModel(patients.getList());

        /* We remove the following code not to allow users to go back to the 
         * patient list panel after saving
         * 
         if (!selectedEntry.motherName.equals(""))
            hidePatientPane();
         */
        for (int i = 0; i < 1000; i++) {
            tempList[i] = "";
            tempList1[i] = "";
            tempImg[i] = "";
            tempImg1[i] = "";
        }
        tempCount = 0;
        tempICount = 0;
        tempCount1 = 0;
        tempICount1 = 0;
        checkImageUpload = 0;
        copyCount = 0;
        errorCount = 0;
        //ss.setStatus("Saving...", 100);
        
    }

    public void showSavingWindow() {
        this.SavingWindow.setVisible(true);
        this.SavingWindow.setLocationRelativeTo(null);
        this.jProgressBar1.setValue(0);
    }
    
    @Action
    public void showSaveToPassportFrame() {
        if (jList1.getSelectedValue() == null) {
           JOptionPane.showMessageDialog(null, "Please select a patient", "Selection Error", JOptionPane.ERROR_MESSAGE);
           return;
        }
        else {
            this.PasswordFrame2.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
            this.PasswordFrame2.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent winEvt) {
                    jPasswordField3.setText("");
                }
            });
            this.PasswordFrame2.setVisible(true);
            this.PasswordFrame2.setLocationRelativeTo(null);
            jPasswordField3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            this.PasswordFrame2.getRootPane().setDefaultButton(passwdConfirm2);
            this.PasswordFrame2.pack();
        }
    }
    
    @Action
    public void saveToPassport() throws FileNotFoundException, IOException, FTPException {
        String words = "", selected = jList1.getSelectedValue().toString();
        int commaIndex[] = new int[2000];
        char buffer[] = new char[10000];
        int index1 = 0, i = 0;

        FileInputStream fis = new FileInputStream(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient +  File.separator + "patientInfo");
        int n = 0;
        while ((n = fis.read()) != -1){
            char c = (char)n;
            buffer[i] = c;
            if (c == ',') {
                commaIndex[index1++] = i;
            }
            i++;
        }

        fis.close();
        
        Entry tempEntry = new Entry(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient +  File.separator, "patientInfo");
        tempEntry.readXMLEntry();
        words = tempEntry.motherName;
        tempEntry = null;

        //for (int j = commaIndex[6] + 1; j < commaIndex[7]; j++)
        //    words += String.valueOf(buffer[j]);

        String clearPassword = new String(jPasswordField3.getPassword());

        if (clearPassword.equals(passwd) || clearPassword.equals(words)) {
            jPasswordField3.setText("");
            jPasswordField3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            this.PasswordFrame2.setVisible(false);
            this.PasswordFrame2.dispose();
            saveToPassportSelected(selected);
        }
        else {
            JOptionPane.showMessageDialog(null, "Please enter the right password!", "Wrong Password", JOptionPane.ERROR_MESSAGE);
            passwdConfirm2.setFocusable(false);
            jPasswordField3.setText("");
            jPasswordField2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }
    
    public void saveToPassportSelected(final String selectedPatient) throws FileNotFoundException, IOException, FTPException {
        /*
        Runnable runA = new Runnable() {
            public void run() {
                ss = new SavingScreen();
                ss.setAlwaysOnTop(true);
                //ss.toFront();
            }
        };

        final Thread threadA = new Thread(runA);
        threadA.start();
         * 
         */

        //JOptionPane.showMessageDialog(null, "1 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        Runnable runB = new Runnable() {
            public void run() {
                try {
                    //JOptionPane.showMessageDialog(null, "4 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    saveToPassportPane(selectedPatient);
                    AutoDismiss a = new AutoDismiss(new JDialog());
                    a.showMessageDialog(null, "Saving Completed..");
                    //working = true;
                    //JOptionPane.showMessageDialog(null, "5 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    //ss.setVisible(false);
                    //threadA.interrupt();
                    //JOptionPane.showMessageDialog(null, "6 " + threadA.isAlive(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    //JOptionPane.showMessageDialog(null, "7 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FTPException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread threadB = new Thread(runB);
        threadB.start();
    }
    
    public void saveToPassportPane(String selectedPatient) throws IOException, FTPException {
        detectUSB();
        //ss.setStatus("Saving...", 50);
        
        File f = new File(".");
        String drive = f.getAbsolutePath();
        File fromPath, toPath, toTempArray, fromTemp1, toPaths[] = new File[driveCounts], toTempArrays[] = new File[driveCounts], exe;
        int option = 0;

        //C Drive to USB
        if (drive.charAt(0) == 'C') {
            if (driveCounts > 0) {
                //if (driveNum == driveCounts) {
                    fromPath = new File(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient);

                    for (int i = 0; i < driveCounts; i++) {
                        toPath = new File(letters[i] + ":" + File.separator + "Entries"  + File.separator + selectedPatient);
                        copyFiles(fromPath, toPath);
                        //JOptionPane.showMessageDialog(null, letters[i], "Field required", JOptionPane.ERROR_MESSAGE);

                        toTempArray = new File(letters[i] + ":" + File.separator + "Entries"  + File.separator + "temp");
                        if (toTempArray.exists())
                            deleteDir(toTempArray);

                        exe = new File(letters[i] + ":" + File.separator + "MedPassport.exe");
                        if (!exe.exists())
                            Runtime.getRuntime().exec("xcopy " + currentDirectory + File.separator + "MedPassport.exe " + letters[i] + ":" + File.separator + " /C");
                    }

                    fromTemp1 = new File(currentDirectory + File.separator + "Entries"  + File.separator + "temp");
                    if (fromTemp1.exists()) {
                        //JOptionPane.showMessageDialog(null, "It exists.", "Field required", JOptionPane.ERROR_MESSAGE);
                        deleteDir(fromTemp1);
                    }
                //}
            }
        }//USB to C Drive
        else {
            fromPath = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            toPath = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + selectedPatient);
            copyFiles(fromPath, toPath);

            /*
            if (!toPath.exists()) {
                //JOptionPane.showMessageDialog(null, "Couldn't copy", "Field required", JOptionPane.ERROR_MESSAGE);
                copyFiles(fromPath, toPath);
            }*/

            File fromTemp = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
            if (fromTemp.exists())
                deleteDir(fromTemp);
            File toTemp = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + "temp");
            if (toTemp.exists())
                deleteDir(toTemp);
        }

        fromPath = new File(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient);
        //toPath = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + selectedPatient);

        String[] list = null;
        if (fromPath.isDirectory())
            list = fromPath.list();

        String[] localfileList = new String[list.length];
        String[] dirs, root, medpass;

        int dirCount = 0, rootCount = 0, medCount = 0;
        int ascii = 0;

        for (int i = 0; i < list.length; i++)
            if (list[i].equals("patientInfo"))
                ascii = i;

        //copy all the files in the list.
        for (int i = 0; i < list.length; i++)
            localfileList[i] = currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + list[i];

        //ss.setStatus("Saving...", 70);
        
        /*
        FTPClient ftp;
        ftp = new FTPClient();
        ftp.setRemoteHost("ftp.bebedoc.com");

        FTPMessageCollector listener = new FTPMessageCollector();
        ftp.setMessageListener(listener);
        ftp.connect();
        ftp.login("rwatchi", "Bebedoc901");
        ftp.setConnectMode(FTPConnectMode.PASV);

        ftp.setType(FTPTransferType.BINARY);
        ///////////////////////////////////////
        root = ftp.dir(".", false);
        for (int i = 0; i < root.length; i++)
            if (root[i].equals("MedPassport"))
                rootCount++;

        if (rootCount == 0) {
            ftp.mkdir("MedPassport");
            ftp.chdir("MedPassport");
        }
        else
            ftp.chdir("MedPassport");

        medpass = ftp.dir(".", false);
        for (int i = 0; i < medpass.length; i++)
            if (medpass[i].equals("Entries"))
                medCount++;

        if (medCount == 0) {
            ftp.mkdir("Entries");
            ftp.chdir("Entries");
        }
        else
            ftp.chdir("Entries");

        //ftp.chdir("/MedPassport/Entries/");
        dirs = ftp.dir(".", false);
        //JOptionPane.showMessageDialog(null, dirs[1], "Field required", JOptionPane.ERROR_MESSAGE);

        for (int i = 0; i < dirs.length; i++)
            if (dirs[i].equals(selectedPatient))
                dirCount++;

        if (dirCount == 0) {
            ftp.mkdir(selectedPatient);
            ftp.chdir(selectedPatient);
        }
        else
            ftp.chdir(selectedPatient);

        ss.setStatus("Saving...", 80);
        
        for (int i = 0; i < ascii; i++)
            ftp.put(localfileList[i], list[i], false);

        for (int i = ascii + 1; i < list.length; i++)
            ftp.put(localfileList[i], list[i], false);

        ftp.setType(FTPTransferType.ASCII);
        ftp.put(localfileList[ascii], list[ascii], false);

        ftp.quit();
        ss.setStatus("Saving...", 90);
        */

        patients.getList().remove(patients.getList().find("temp"));
        patients.populatePatients();  //re-populate the list
        jList1.setModel(patients.getList());

        /* We remove the following code not to allow users to go back to the 
         * patient list panel after saving
         * 
         if (!selectedEntry.motherName.equals(""))
            hidePatientPane();
         */
        for (int i = 0; i < 1000; i++) {
            tempList[i] = "";
            tempList1[i] = "";
            tempImg[i] = "";
            tempImg1[i] = "";
        }
        tempCount = 0;
        tempICount = 0;
        tempCount1 = 0;
        tempICount1 = 0;
        checkImageUpload = 0;
        copyCount = 0;
        errorCount = 0;
        //ss.setStatus("Saving...", 100);
    }

    @Action
    public void showDeleteFrame() {
        if (jList1.getSelectedValue() == null) {
           JOptionPane.showMessageDialog(null, "Please select a patient", "Selection Error", JOptionPane.ERROR_MESSAGE);
           return;
        }
        else {
            this.PasswordFrame1.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
            this.PasswordFrame1.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent winEvt) {
                    jPasswordField2.setText("");
                }
            });
            this.PasswordFrame1.setVisible(true);
            this.PasswordFrame1.setLocationRelativeTo(null);
            jPasswordField2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            this.PasswordFrame1.getRootPane().setDefaultButton(passwdConfirm1);
            this.PasswordFrame1.pack();
        }
    }
        
    @Action
    public void deletePatient() throws FileNotFoundException, IOException, FTPException {
        String words = "", selected = jList1.getSelectedValue().toString();
        int commaIndex[] = new int[2000];
        char buffer[] = new char[10000];
        int index1 = 0, i = 0;

        FileInputStream fis = new FileInputStream(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient +  File.separator + "patientInfo");
        int n = 0;
        while ((n = fis.read()) != -1){
            char c = (char)n;
            buffer[i] = c;
            if (c == ',') {
                commaIndex[index1++] = i;
            }
            i++;
        }

        fis.close();

        for (int j = commaIndex[6] + 1; j < commaIndex[7]; j++)
            words += String.valueOf(buffer[j]);

        String clearPassword = new String(jPasswordField2.getPassword());

        if (clearPassword.equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter the right password!", "Wrong Password", JOptionPane.ERROR_MESSAGE);
            passwdConfirm1.setFocusable(false);
            jPasswordField2.setText("");
            jPasswordField2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
        else if (clearPassword.equals(passwd)) {
            jPasswordField2.setText("");
            jPasswordField2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            this.PasswordFrame1.setVisible(false);
            this.PasswordFrame1.dispose();
            deleteSelected(selected);
        }
        else if (clearPassword.equals(words)) {
            jPasswordField2.setText("");
            jPasswordField2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            this.PasswordFrame1.setVisible(false);
            this.PasswordFrame1.dispose();
            deleteSelected(selected);
        }
        else {
            JOptionPane.showMessageDialog(null, "Please enter the right password!", "Wrong Password", JOptionPane.ERROR_MESSAGE);
            passwdConfirm1.setFocusable(false);
            jPasswordField2.setText("");
            jPasswordField2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }

    public void deleteSelected(final String selectedPatient) throws FileNotFoundException, IOException, FTPException {
        Runnable runA = new Runnable() {
            public void run() {
                ss = new SavingScreen();
                ss.setAlwaysOnTop(true);
                //ss.toFront();
            }
        };

        final Thread threadA = new Thread(runA);
        threadA.start();

        //JOptionPane.showMessageDialog(null, "1 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
        Runnable runB = new Runnable() {
            public void run() {
                try {
                    //JOptionPane.showMessageDialog(null, "4 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    deletePatientPane(selectedPatient);
                    working = true;
                    //JOptionPane.showMessageDialog(null, "5 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    ss.setVisible(false);
                    threadA.interrupt();
                    //JOptionPane.showMessageDialog(null, "6 " + threadA.isAlive(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                    //JOptionPane.showMessageDialog(null, "7 " + Thread.activeCount(), "Compress Information", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FTPException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread threadB = new Thread(runB);
        threadB.start();
    }

    public void deletePatientPane(String selectedPatient) throws IOException, FTPException {
        detectUSB();

        File f = new File(".");
        String drive = f.getAbsolutePath();
        File fromPath, toPath;

        if (drive.charAt(0) == 'C') {
            fromPath = new File(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient);
            deleteDir(fromPath);

            if (driveCounts > 0) {
                for (int i = 0; i < driveCounts; i++) {
                    toPath = new File(letters[i] + ":" + File.separator + "Entries"  + File.separator + selectedPatient);
                    deleteDir(toPath);
                }
            }
        }
        else {
            fromPath = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            toPath = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries"  + File.separator + selectedPatient);
            deleteDir(fromPath);
            deleteDir(toPath);
        }
        ss.setStatus("Deleting...", 50);
        fromPath = new File(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient);
        String[] list;
        String[] dirs, root, medpass;

        int dirCount = 0, rootCount = 0, medCount = 0;
        
        FTPClient ftp;
        ftp = new FTPClient();
        ftp.setRemoteHost("ftp.bebedoc.com");

        FTPMessageCollector listener = new FTPMessageCollector();
        ftp.setMessageListener(listener);
        ftp.connect();
        ftp.login("rwatchi", "Bebedoc901");
        ftp.setConnectMode(FTPConnectMode.PASV);

        ftp.setType(FTPTransferType.BINARY);
        ss.setStatus("Deleting...", 60);
        ///////////////////////////////////////
        root = ftp.dir(".", false);
        for (int i = 0; i < root.length; i++)
            if (root[i].equals("MedPassport"))
                rootCount++;

        if (rootCount == 0)
            return;
        else
            ftp.chdir("MedPassport");

        medpass = ftp.dir(".", false);
        for (int i = 0; i < medpass.length; i++)
            if (medpass[i].equals("Entries"))
                medCount++;

        if (medCount == 0)
            return;
        else
            ftp.chdir("Entries");

        //ftp.chdir("/MedPassport/Entries/");
        dirs = ftp.dir(".", false);
        //JOptionPane.showMessageDialog(null, dirs[1], "Field required", JOptionPane.ERROR_MESSAGE);

        for (int i = 0; i < dirs.length; i++)
            if (dirs[i].equals(selectedPatient))
                dirCount++;

        if (dirCount == 0)
            return;
        else
            ftp.chdir(selectedPatient);
            
        ss.setStatus("Deleting...", 70);

        list = ftp.dir(".", false);
        
        for (int i = 2; i < list.length; i++)
            ftp.delete(list[i]);
        
        ss.setStatus("Deleting...", 80);
        ftp.cdup();

        ftp.rmdir(selectedPatient);

        ftp.quit();
        ss.setStatus("Deleting...", 90);

        patients.getList().remove(patients.getList().find(selectedPatient));
        patients.populatePatients();  //re-populate the list
        jList1.setModel(patients.getList());
        ss.setStatus("Deleting...", 100);
    }

    @Action
    public void showPasswordFrame() {
        if (jList1.getSelectedValue() == null) {
           JOptionPane.showMessageDialog(null, "Please select a patient", "Selection Error", JOptionPane.ERROR_MESSAGE);
           return;
        }
        else {
            this.PasswordFrame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
            this.PasswordFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent winEvt) {
                    jPasswordField1.setText("");
                }
            });
            this.PasswordFrame.setVisible(true);
            this.PasswordFrame.setLocationRelativeTo(null);
            jPasswordField1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            this.PasswordFrame.getRootPane().setDefaultButton(passwdConfirm);
            this.PasswordFrame.pack();
        }
    }
    
    public void showPassword() {
       this.PasswordFrame.setVisible(true);
       this.PasswordFrame.setLocationRelativeTo(null);
       jPasswordField1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }
/*
    public void showEmailButtonFrame() {
        this.emailButtonFrame.setVisible(true);
        this.emailButtonFrame.setLocationRelativeTo(null);
    }
*/
    public String readFileAsString(String filePath) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            fileData.append(buf, 0, numRead);
        }
        reader.close();
        return fileData.toString();
    }

    @Action
    public void confirmPassword() throws IOException {
        //following codes read the mother's name to set it as a second password.
        Entry tempEntry = new Entry(currentDirectory + File.separator + "Entries"  + File.separator + selectedPatient +  File.separator, "patientInfo");
        tempEntry.readXMLEntry();
        String words = tempEntry.motherName;
        tempEntry = null;

        String clearPassword = new String(jPasswordField1.getPassword());

        if (clearPassword.equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter the right password!", "Wrong Password", JOptionPane.ERROR_MESSAGE);
            passwdConfirm.setFocusable(false);
            jPasswordField1.setText("");
            jPasswordField1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
        else if ((clearPassword.equals(passwd)) || (clearPassword.equals(words))) {
                       
            jPasswordField1.setText("");
            jPasswordField1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            this.PasswordFrame.setVisible(false);
            this.PasswordFrame.dispose();
            selectedEntry = new Entry(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator, "patientInfo");
            selectedEntry.readXMLEntry();
            
            //Now checking the patient information in the MedPassport
            detectUSB();

            File f = new File(".");
            String drive = f.getAbsolutePath();
            File toPath, toPaths[] = new File[driveCounts], toTempArrays[] = new File[driveCounts], exe;
            
            //C Drive to USB
            if (drive.charAt(0) == 'C') 
            {
                Entry selectedEntryMedPassport = null;
                if (driveCounts > 0) {
                    for (int k = 0; k < driveCounts; k++) {
                        toPath = new File(letters[k] + ":" + File.separator + "Entries"  + File.separator + selectedPatient);
                        if (toPath.exists())
                        {
                            //compare the two files!!
                            selectedEntryMedPassport = new Entry(toPath + File.separator, "patientInfo");
                            selectedEntryMedPassport.readXMLEntry();
                            break;
                        }
                    }
                }
                if (selectedEntryMedPassport != null) { //The patient is found in USB
                    if (!selectedEntry.lastEdit.equals(selectedEntryMedPassport.lastEdit)) //the database is modified
                    {
                        String tmp_str = findDiffEntries(selectedEntry, selectedEntryMedPassport);
                        if (!tmp_str.isEmpty())
                        {
                            int result = JOptionPane.showConfirmDialog(this.getFrame(), "The database of " + selectedEntry.first + " " + selectedEntry.last + " in local hard drive and the MedPassport are not identical.\nThe following items have been modified:\n" + tmp_str + "\nDo you want to MERGE?", "Database Modified", JOptionPane.YES_NO_CANCEL_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                JOptionPane optionPane = new JOptionPane("Merging...");
                                JDialog dialog = optionPane.createDialog("Notification");
                                selectedEntry = mergeEntries(selectedEntry, selectedEntryMedPassport);
                                dialog.dispose();
                                fillPatientPane();
                                this.statusLabel.setText("Modifying existing patient.");
                                showPatientPane();
                            }
                            else if (result == JOptionPane.NO_OPTION) {
                                fillPatientPane();
                                this.statusLabel.setText("Modifying existing patient.");
                                showPatientPane();
                            }
                        }
                    }
                    else { //found the patient in USB but database is identical
                        fillPatientPane();
                        this.statusLabel.setText("Modifying existing patient.");
                        showPatientPane();
                    }
                }
                else { //The patient is NOT found in USB
                    fillPatientPane();
                    this.statusLabel.setText("Modifying existing patient.");
                    showPatientPane();
                }
                    
            }
            else { //Opened this program from USB
                fillPatientPane();
                this.statusLabel.setText("Modifying existing patient.");
                showPatientPane();
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Please enter the right password!", "Wrong Password", JOptionPane.ERROR_MESSAGE);
            passwdConfirm.setFocusable(false);
            jPasswordField1.setText("");
            jPasswordField1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }
    
    FilenameFilter jpegFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return (!name.toLowerCase().endsWith("chart.png"))
                    && (name.toLowerCase().endsWith(".jpg")
                        || name.toLowerCase().endsWith(".jpeg")
                        || name.toLowerCase().endsWith(".png")
                        || name.toLowerCase().endsWith(".bmp")); }
    };
    
    public String findDiffEntries(Entry origEntry, Entry usbEntry)
    {
        //This function compares the two entries, and return what items has been modified.
        String return_str = "";
        
        if (!origEntry.specialFeatures.equals(usbEntry.specialFeatures))
            return_str += "Special Features, ";
        if (!origEntry.mentalHealth.equals(usbEntry.mentalHealth))
            return_str += "Mental Health, ";
        if (!origEntry.socialIssues.equals(usbEntry.socialIssues))
            return_str += "Social Issues, ";
        if (!origEntry.primaryCare.equals(usbEntry.primaryCare))
            return_str += "Primary Care Physician, ";
        if (!origEntry.medicalHistory.equals(usbEntry.medicalHistory))
            return_str += "Medical History, ";
        
        if (!origEntry.labs.equals(usbEntry.labs))
            return_str += "Labs, ";
        if (!origEntry.radiology.equals(usbEntry.radiology))
            return_str += "Radiology, ";
        if (!origEntry.meds.equals(usbEntry.meds))
            return_str += "Meds, ";
        if (!origEntry.allergies.equals(usbEntry.allergies))
            return_str += "Allergies, ";
        if (!origEntry.consultationNote.equals(usbEntry.consultationNote))
            return_str += "Consulation notes of primary physician, ";
        if (!origEntry.TBSkinTest.equals(usbEntry.TBSkinTest))
            return_str += "TB Skin Test, ";
        if (!origEntry.familyHistory.equals(usbEntry.familyHistory))
            return_str += "Family History, ";
        if (!origEntry.specialists.equals(usbEntry.specialists))
            return_str += "Problem Lists, ";
        if (!origEntry.consultants.equals(usbEntry.consultants))
            return_str += "Risk Factors, ";
        if (!origEntry.consultationNote_s.equals(usbEntry.consultationNote_s))
            return_str += "Consultation notes of specialists, ";
        if (!origEntry.consultationNote_er.equals(usbEntry.consultationNote_er))
            return_str += "Consultation notes of E.R. physicians, ";
        
        //removing ", " at the end, if the string is not empty
        if (!return_str.isEmpty())
            return_str = (String)return_str.subSequence(0, return_str.length()-2);
        
        return return_str;
    }
    
    public Entry mergeEntries(Entry origEntry, Entry usbEntry)
    {
        //This function compares the two entries, and return what items has been modified.
        String return_str = "";
        
        if (!origEntry.specialFeatures.equals(usbEntry.specialFeatures))
        {
            if(usbEntry.specialFeatures.contains(origEntry.specialFeatures))
                origEntry.specialFeatures = usbEntry.specialFeatures;
            else if(!origEntry.specialFeatures.contains(usbEntry.specialFeatures))
            {
                String[] orig_string = origEntry.specialFeatures.split("\r\n");
                String[] usb_string = usbEntry.specialFeatures.split("\r\n");                
                origEntry.specialFeatures = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        
        if (!origEntry.mentalHealth.equals(usbEntry.mentalHealth))
        {
            if(usbEntry.mentalHealth.contains(origEntry.mentalHealth))
                origEntry.mentalHealth = usbEntry.mentalHealth;
            else if(!origEntry.mentalHealth.contains(usbEntry.mentalHealth))
            {
                String[] orig_string = origEntry.mentalHealth.split("\r\n");
                String[] usb_string = usbEntry.mentalHealth.split("\r\n");                
                origEntry.mentalHealth = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.socialIssues.equals(usbEntry.socialIssues))
        {
            if(usbEntry.socialIssues.contains(origEntry.socialIssues))
                origEntry.socialIssues = usbEntry.socialIssues;
            else if(!origEntry.socialIssues.contains(usbEntry.socialIssues))
            {
                String[] orig_string = origEntry.socialIssues.split("\r\n");
                String[] usb_string = usbEntry.socialIssues.split("\r\n");                
                origEntry.socialIssues = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.primaryCare.equals(usbEntry.primaryCare))
        {
            if(usbEntry.primaryCare.contains(origEntry.primaryCare))
                origEntry.primaryCare = usbEntry.primaryCare;
            else if(!origEntry.primaryCare.contains(usbEntry.primaryCare))
            {
                String[] orig_string = origEntry.primaryCare.split("\r\n");
                String[] usb_string = usbEntry.primaryCare.split("\r\n");                
                origEntry.primaryCare = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.medicalHistory.equals(usbEntry.medicalHistory))
        {
            if(usbEntry.medicalHistory.contains(origEntry.medicalHistory))
                origEntry.medicalHistory = usbEntry.medicalHistory;
            else if(!origEntry.medicalHistory.contains(usbEntry.medicalHistory))
            {
                String[] orig_string = origEntry.medicalHistory.split("\r\n");
                String[] usb_string = usbEntry.medicalHistory.split("\r\n");                
                origEntry.medicalHistory = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        
        if (!origEntry.labs.equals(usbEntry.labs))
        {
            if(usbEntry.labs.contains(origEntry.labs))
                origEntry.labs = usbEntry.labs;
            else if(!origEntry.labs.contains(usbEntry.labs))
            {
                String[] orig_string = origEntry.labs.split("\r\n");
                String[] usb_string = usbEntry.labs.split("\r\n");                
                origEntry.labs = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.radiology.equals(usbEntry.radiology))
        {
            if(usbEntry.radiology.contains(origEntry.radiology))
                origEntry.radiology = usbEntry.radiology;
            else if(!origEntry.radiology.contains(usbEntry.radiology))
            {
                String[] orig_string = origEntry.radiology.split("\r\n");
                String[] usb_string = usbEntry.radiology.split("\r\n");                
                origEntry.radiology = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.meds.equals(usbEntry.meds))
        {
            if(usbEntry.meds.contains(origEntry.meds))
                origEntry.meds = usbEntry.meds;
            else if(!origEntry.meds.contains(usbEntry.meds))
            {
                String[] orig_string = origEntry.meds.split("\r\n");
                String[] usb_string = usbEntry.meds.split("\r\n");                
                origEntry.meds = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.allergies.equals(usbEntry.allergies))
        {
            if(usbEntry.allergies.contains(origEntry.allergies))
                origEntry.allergies = usbEntry.allergies;
            else if(!origEntry.allergies.contains(usbEntry.allergies))
            {
                String[] orig_string = origEntry.allergies.split("\r\n");
                String[] usb_string = usbEntry.allergies.split("\r\n");                
                origEntry.allergies = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.consultationNote.equals(usbEntry.consultationNote))
        {
            if(usbEntry.consultationNote.contains(origEntry.consultationNote))
                origEntry.consultationNote = usbEntry.consultationNote;
            else if(!origEntry.consultationNote.contains(usbEntry.consultationNote))
            {
                String[] orig_string = origEntry.consultationNote.split("\r\n");
                String[] usb_string = usbEntry.consultationNote.split("\r\n");                
                origEntry.consultationNote = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.TBSkinTest.equals(usbEntry.TBSkinTest))
        {
            if(usbEntry.TBSkinTest.contains(origEntry.TBSkinTest))
                origEntry.TBSkinTest = usbEntry.TBSkinTest;
            else if(!origEntry.TBSkinTest.contains(usbEntry.TBSkinTest))
            {
                String[] orig_string = origEntry.TBSkinTest.split("\r\n");
                String[] usb_string = usbEntry.TBSkinTest.split("\r\n");                
                origEntry.TBSkinTest = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.familyHistory.equals(usbEntry.familyHistory))
        {
            if(usbEntry.familyHistory.contains(origEntry.familyHistory))
                origEntry.familyHistory = usbEntry.familyHistory;
            else if(!origEntry.familyHistory.contains(usbEntry.familyHistory))
            {
                String[] orig_string = origEntry.familyHistory.split("\r\n");
                String[] usb_string = usbEntry.familyHistory.split("\r\n");                
                origEntry.familyHistory = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.specialists.equals(usbEntry.specialists))
        {
            if(usbEntry.specialists.contains(origEntry.specialists))
                origEntry.specialists = usbEntry.specialists;
            else if(!origEntry.specialists.contains(usbEntry.specialists))
            {
                String[] orig_string = origEntry.specialists.split("\r\n");
                String[] usb_string = usbEntry.specialists.split("\r\n");                
                origEntry.specialists = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.consultants.equals(usbEntry.consultants))
        {
            if(usbEntry.consultants.contains(origEntry.consultants))
                origEntry.consultants = usbEntry.consultants;
            else if(!origEntry.consultants.contains(usbEntry.consultants))
            {
                String[] orig_string = origEntry.consultants.split("\r\n");
                String[] usb_string = usbEntry.consultants.split("\r\n");                
                origEntry.consultants = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.consultationNote_s.equals(usbEntry.consultationNote_s))
        {
            if(usbEntry.consultationNote_s.contains(origEntry.consultationNote_s))
                origEntry.consultationNote_s = usbEntry.consultationNote_s;
            else if(!origEntry.consultationNote_s.contains(usbEntry.consultationNote_s))
            {
                String[] orig_string = origEntry.consultationNote_s.split("\r\n");
                String[] usb_string = usbEntry.consultationNote_s.split("\r\n");                
                origEntry.consultationNote_s = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }
        if (!origEntry.consultationNote_er.equals(usbEntry.consultationNote_er))
        {
            if(usbEntry.consultationNote_er.contains(origEntry.consultationNote_er))
                origEntry.consultationNote_er = usbEntry.consultationNote_er;
            else if(!origEntry.consultationNote_er.contains(usbEntry.consultationNote_er))
            {
                String[] orig_string = origEntry.consultationNote_er.split("\r\n");
                String[] usb_string = usbEntry.consultationNote_er.split("\r\n");                
                origEntry.consultationNote_er = this.mergeTwoStrings(orig_string, usb_string);                
            }            
        }        
        return origEntry;
    }
    
    private String mergeTwoStrings(String[] orig_string, String[] usb_string)
    {
        for (int i = 0; i < orig_string.length; i++)
        {
            for (int j = 0; j < usb_string.length; j++)
            {
                if (orig_string[i].contains(usb_string[j]))
                    usb_string[j] = "";
            }
        }

        //String[] result_string = new String[Math.max(orig_string.length, usb_string.length)];
        String result_string = "";
        for (int i = 0; i < Math.max(orig_string.length, usb_string.length); i++)
        {
            if ((i < orig_string.length) && (orig_string[i] != ""))
                result_string += (orig_string[i] + "\r\n");
            if ((i < usb_string.length) && (usb_string[i] != ""))
                result_string += (usb_string[i] + "\r\n");
        }        
        return result_string;
    }
    
    public void deleteFiles( String d, String e ) {
        ExtensionFilter filter = new ExtensionFilter(e);
        File dir = new File(d);

        String[] list = dir.list(filter);
        File file;

        if (list.length == 0)
            return;

        for (int i = 0; i < list.length; i++) {
            file = new File(d + list[i]);
            boolean isdeleted = file.delete();
        }
    }

    class ExtensionFilter implements FilenameFilter {
        private String extension;

        public ExtensionFilter( String extension ) {
            this.extension = extension;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(extension));
        }
    }

    @Action
    public void imageUpload() throws FileNotFoundException, IOException {
        int option = 0, option1 = 0, reval = 0, c, index = 0, k = 0, l = 0;
        String imageF = null;
        File outputFile = null;
        File tempDir = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
        File outputDir = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
        String list[] = tempDir.list();
        String list1[] = outputDir.list();

        //prevCount = 0;

        this.imageFileChooser.setCurrentDirectory(new java.io.File("."));
        this.imageFileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg") || f.getName().toLowerCase().endsWith(".png") || f.getName().toLowerCase().endsWith(".bmp") || f.isDirectory();
            }

            public String getDescription() {
                return "image files(*.jpg, *jpeg, *png, *bmp)";
            }
        });
        reval = this.imageFileChooser.showOpenDialog(null);

        if (reval == JFileChooser.APPROVE_OPTION) {
            File inputFile = new File(this.imageFileChooser.getCurrentDirectory() + File.separator + this.imageFileChooser.getSelectedFile().getName());
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile), 4096);
            imageF = this.imageFileChooser.getSelectedFile().getName();
            tempImage = imageF;

            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].equals(tempImage)) {
                        JOptionPane.showMessageDialog(null, "You already have the same image for this patient", "Same Image Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            else if (list1 != null) {
                for (int i = 0; i < list1.length; i++) {
                    if (list1[i].equals(tempImage)) {
                        JOptionPane.showMessageDialog(null, "You already have the same image for this patient", "Same Image Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (selectedEntry == null) {
                File Dir = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                Dir.mkdir();
                outputFile = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp" + File.separator + this.imageFileChooser.getSelectedFile().getName());
            }
            else
                outputFile = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + this.imageFileChooser.getSelectedFile().getName());
            
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile), 4096);
            
            if (outputFile.exists()) {
                if (!outputFile.canWrite())
                    option = JOptionPane.showConfirmDialog(null, "FileCopy: destination file is unwriteable: " + outputFile.getName(), "Write Error", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.NO_OPTION)
                    JOptionPane.showMessageDialog(null, "FileCopy: existing file was not overwritten.", "Write Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                String parent = outputFile.getParent();

                if (parent == null)
                    parent = System.getProperty("user.dir");
                File dir = new File(parent);

                if (!dir.exists())
                    JOptionPane.showMessageDialog(null, "FileCopy: destination directory doesn't exist: " + parent, "Write Error", JOptionPane.ERROR_MESSAGE);
                if (dir.isFile())
                    JOptionPane.showMessageDialog(null, "FileCopy: destination is not a directory: " + parent, "Write Error", JOptionPane.ERROR_MESSAGE);
                if (!dir.canWrite())
                    JOptionPane.showMessageDialog(null, "FileCopy: destination directory is unwriteable: " + parent, "Write Error", JOptionPane.ERROR_MESSAGE);
            }

            while ((c = bis.read()) != -1)
                bos.write(c);

            bis.close();
            bos.close();
        }
        else if (reval == JFileChooser.CANCEL_OPTION) {
            if (selectedEntry != null) {/*
                String[] first = this.selectedEntry.images.getFirst();
                String tempFirst = first[1];
                //JOptionPane.showMessageDialog(null, first[0] + "\n" + first[1], "Label", JOptionPane.ERROR_MESSAGE);
                this.imagePanel.remove(this.patientImage);
                this.patientImage = new ImagePanel(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + first[0]);
                this.imagePanel.add(this.patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
                this.imagePanel.repaint();
                this.patientImage.repaint();*/
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
                if (this.selectedEntry.images.length > 0) {
                    for (int i = 0; i < this.selectedEntry.images.length; i++)
                        if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName()))
                            k = i;
                    tempList1[k] = this.image_labelField.getText();
                    //JOptionPane.showMessageDialog(null, tempImg1[k] + "\n" + tempList1[k], "Label", JOptionPane.ERROR_MESSAGE);
                }
                //this.image_labelField.setText(tempFirst);
                
                return;
            }
            else {
                return;
            }
        }

        if (selectedEntry == null)
            selectedPatient = "temp";
        else if (selectedEntry != null && !selectedPatient.equals("temp")) {
            //JOptionPane.showMessageDialog(null, "Yeah", "Label", JOptionPane.ERROR_MESSAGE);
            /*if (checkImageUpload > 0) {
                tempList[tempCount1] = this.image_labelField.getText();
                this.selectedEntry.images.labels[tempCount1] = tempList[tempCount1];
                tempCount1++;
                //JOptionPane.showMessageDialog(null, tempCount1 + "\n" + tempICount1 + "\n" + this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
            }*/
            
            for (int i = 0; i < this.selectedEntry.images.length; i++) {
                tempImg[i] = this.selectedEntry.images.imageNames[i];
                tempList[i] = this.selectedEntry.images.labels[i];
            }
            
            if (checkImageUpload == 0) {
                for (int i = 0; i < this.selectedEntry.images.length; i++) {
                    tempCopy[i] = tempImg[i];
                    tempCopy1[i] = tempList[i];
                }
                copyCount = this.selectedEntry.images.length;
            }
            
            tempCount1 = this.selectedEntry.images.length;
            tempICount1 = this.selectedEntry.images.length;

            for (int i = 0; i < this.selectedEntry.images.length; i++)
                if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName()))
                    l = i;
            tempList[l] = this.image_labelField.getText();

            //JOptionPane.showMessageDialog(null, tempImg[0], "Label", JOptionPane.ERROR_MESSAGE);
            this.selectedEntry.images = new ImageList(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
            for (int i = 0; i < tempCount1; i++) {
                this.selectedEntry.images.imageNames[i] = tempImg[i];
                this.selectedEntry.images.labels[i] = tempList[i];
            }
            //
            //JOptionPane.showMessageDialog(null, tempCount1 + " " + this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
            //this.selectedEntry.images.setLabels(this.selectedEntry.images.imageNames, this.selectedEntry.images.labels);
            
            if (this.selectedEntry.images != null && this.patientImage != null)
                this.imagePanel.remove(this.patientImage);
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label1", JOptionPane.ERROR_MESSAGE);
            this.patientImage = new ImagePanel(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + imageF);
            this.imagePanel.add(this.patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
            this.imagePanel.repaint();
            this.patientImage.repaint();
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
            this.image_labelField.setText("No Description.");
            this.selectedEntry.images.add(imageF, this.image_labelField.getText());
            checkImageUpload++;
            tempImg[tempICount1++] = imageF;
            tempList[tempCount1++] = this.image_labelField.getText();
            //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length + " " + this.selectedEntry.images.imageNames[this.selectedEntry.images.length - 1], "Label", JOptionPane.ERROR_MESSAGE);

        }
        
        if (selectedPatient.equals("temp")) {
            selectedEntry = new Entry(currentDirectory + File.separator + "Entries" + File.separator + "temp" + File.separator, "patientInfo");
            if (this.selectedEntry.images.length <= 1) {
                this.patientImage = new ImagePanel(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + imageF);
                this.imagePanel.add(this.patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
                this.imagePanel.repaint();
                this.patientImage.repaint();
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
                this.image_labelField.setText("No Description.");
                this.selectedEntry.images.add(imageF, this.image_labelField.getText());
                tempImg1[tempICount++] = imageF;
                tempList1[tempCount++] = this.image_labelField.getText();
            }
            else {
                //JOptionPane.showMessageDialog(null, prevCount + "\n" + this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
                //if ((prevCount % (this.selectedEntry.images.length - 1)) == 0)
                for (int i = 0; i < this.selectedEntry.images.length; i++)
                    if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName()))
                        k = i;

                tempList1[k] = this.image_labelField.getText();
                //JOptionPane.showMessageDialog(null, tempCount + " " + tempList1[tempCount - 1] + "\n" + this.selectedEntry.images.imageNames[tempCount - 1], "Label", JOptionPane.ERROR_MESSAGE);
                
                //this.selectedEntry.images = new ImageList(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label2", JOptionPane.ERROR_MESSAGE);
                for (int i = 0; i < this.selectedEntry.images.length; i++) {
                    this.selectedEntry.images.imageNames[i] = tempImg1[i];
                    this.selectedEntry.images.labels[i] = tempList1[i];
                }
                //this.selectedEntry.images.setLabels(this.selectedEntry.images.imageNames, this.selectedEntry.images.labels);
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.labels[0], "Label", JOptionPane.ERROR_MESSAGE);
                if (this.selectedEntry.images != null && this.patientImage != null)
                    this.imagePanel.remove(this.patientImage);
                
                this.patientImage = new ImagePanel(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + File.separator + imageF);
                this.imagePanel.add(this.patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
                this.imagePanel.repaint();
                this.patientImage.repaint();
                //JOptionPane.showMessageDialog(null, this.selectedEntry.images.length, "Label", JOptionPane.ERROR_MESSAGE);
                this.image_labelField.setText("No Description.");
                this.selectedEntry.images.add(imageF, this.image_labelField.getText());
                tempImg1[tempICount++] = imageF;
                tempList1[tempCount++] = this.image_labelField.getText();
            }
        }
    }
    
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    @Action
    public void exitProgram() {
        File file1 = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
		deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".reg");
        deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".zip");
        deleteDir(file1);
        System.exit(0);
    }
    /*
    public void detectUSB() {
        File[] drives = new File[driveLetters.length];
        //boolean[] isDrive = new boolean[letters.length];
        int i, icount = 0, nullCount = 0;
        index = new int[23];
        int readable[] = new int[23];
        String temp = "";
        driveCounts = 0;
        driveIndex = 0;
        letterIndex = 0;
        
        for (i = 0; i < driveLetters.length; i++)
            letters[i] = "";
        
        for ( i = 0; i < driveLetters.length; i++ )
        {
            drives[i] = new File(driveLetters[i] + ":" + File.separator + "Entries");
            if (drives[i].exists() && drives[i].isDirectory()) {
                driveCounts++;
                index[driveIndex++] = i;
                letters[letterIndex++] = driveLetters[i];
            }
        }
        //JOptionPane.showMessageDialog(null, "letterIndex = " + letterIndex, "Input Error", JOptionPane.ERROR_MESSAGE);
        if (driveCounts == 0) {
            for ( i = 0; i < driveLetters.length; i++ )
            {
                drives[i] = new File(driveLetters[i] + ":");
                if (!drives[i - 1].canWrite() && drives[i].canWrite())  {
                    readable[icount++] = i;
                    nullCount++;
                }
            }
            if (nullCount > 0)
                letters[letterIndex++] = driveLetters[readable[icount - 1]];
            JOptionPane.showMessageDialog(null, letters[letterIndex - 1], "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    public void detectUSB() {
        File[] drives = new File[driveLetters.length];
        //boolean[] isDrive = new boolean[letters.length];
        int i, icount = 0, nullCount = 0, odd = 0, readCount = 0;
        index = new int[23];
        int readable[] = new int[23];
        int read[] = new int[23];
        String temp = "";
        driveCounts = 0;
        driveNum = 0;
        driveIndex = 0;
        letterIndex = 0;

        for (i = 0; i < driveLetters.length; i++) {
            letters[i] = "";
            status[i] = false;
        }

        for ( i = 0; i < driveLetters.length; i++ )
        {
            drives[i] = new File(driveLetters[i] + ":" + File.separator + "Entries");
            if (drives[i].exists() && drives[i].isDirectory()) {
                index[driveIndex++] = i;
                //letters[letterIndex++] = driveLetters[i];
            }
        }

        for ( i = 0; i < driveLetters.length; i++ )
        {
            drives[i] = new File(driveLetters[i] + ":");
            if (drives[i].canRead()) 
                read[readCount++] = i;
        }
        //JOptionPane.showMessageDialog(null, readCount + " " + read[readCount - 1], "Input Error", JOptionPane.ERROR_MESSAGE);
        if (readCount > 0) {
            for ( i = 0; i < read[readCount - 1] + 1; i++ )
            {
                drives[i] = new File(driveLetters[i] + ":");
                if (drives[i].canWrite()) {
                    status[i] = true;
                    readable[icount++] = i;
                }
                else
                    status[i] = false;
            }
        //JOptionPane.showMessageDialog(null, readable[icount - 1], "Input Error", JOptionPane.ERROR_MESSAGE);
        
            for ( i = readable[icount - 1]; i >= 1; i-- )
            {
                if (status[i] == true && status[i - 1] == false) {
                    odd = i;
                    break;
                }
                else
                    odd = 0;
            }
            //JOptionPane.showMessageDialog(null, odd, "Input Error", JOptionPane.ERROR_MESSAGE);
            if (odd > 0) {
                driveNum = readable[icount - 1] - odd;
                for (i = 0; i < driveIndex; i++)
                    if (index[i] >= odd)
                        driveCounts++;
                for (i = odd; i < readable[icount - 1] + 1; i++)
                    letters[letterIndex++] = driveLetters[i];
                //JOptionPane.showMessageDialog(null, driveNum + " " + driveCounts + " " + letters[0], "Input Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                if (readable[icount - 1] >= (readCount - 1)) {
                    driveNum = 1;
                    for (i = 0; i < driveIndex; i++)
                        if (index[i] == readable[icount - 1])
                            driveCounts++;
                    letters[letterIndex++] = driveLetters[readable[icount - 1]];
                    //JOptionPane.showMessageDialog(null, "No odd " + driveCounts + " " + letters[0] + " " + letterIndex, "Input Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    driveNum = 0;
                    //JOptionPane.showMessageDialog(null, "driveNum = 0", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    */

    public void detectUSB() {
        //File absolute = new File(".");
        File f = new File(".");
        //@SuppressWarnings("static-access")
        File[] roots = f.listRoots();
        //File[] roots = File.listRoots();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        driveCounts = 0;

        for (int i = 0; i < driveLetters.length; i++)
            letters[i] = '\u0000';

        
        for (File root : roots) {
            if ("Removable Disk".equals(fsv.getSystemTypeDescription(root))) {
            //if ("이동식 디스크".equals(fsv.getSystemTypeDescription(root))) {
                File cd = root;

                if (cd.exists()) {
                    letters[driveCounts++] = cd.toString().charAt(0);
                    //JOptionPane.showMessageDialog(null, driveCounts + " " + cd.toString().charAt(0), "Input Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    public void showEmailFrame() throws IOException {
        this.emailFrame.setVisible(true);
        this.emailFrame.setLocationRelativeTo(null);
        senderEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        File f = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient);
        //boolean directory = f.isDirectory(); // Is it a file or directory?
        zipDirectory(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient,  currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + ".zip");
    }

    public boolean checkEntries() {
        if (this.senderEmail.getText().equals("") || this.receiverEmail.getText().equals("") || this.Subject.getText().equals("") || this.Text.getText().equals("") ) {
            JOptionPane.showMessageDialog(null, "Please fill out all the entries", "Input Error", JOptionPane.ERROR_MESSAGE);
            clearEmailForms();
            return false;
        }

        String clearPassword = new String(senderPasswd.getPassword());

        if (clearPassword.equals("")) {
            JOptionPane.showMessageDialog(null, "Please fill out all the entries", "Input Error", JOptionPane.ERROR_MESSAGE);
            clearEmailForms();
            return false;
        }

        if (!isValidEmailAddress(this.senderEmail.getText())) {
            JOptionPane.showMessageDialog(null, "Please enter your email address in a right format. EX) someone@some.com", "Email Address Error", JOptionPane.ERROR_MESSAGE);
            clearEmailForms();
            return false;
        }
        else if (!isValidEmailAddress(this.receiverEmail.getText())) {
            JOptionPane.showMessageDialog(null, "Please enter your email address in a right format. EX) someone@some.com", "Email Address Error", JOptionPane.ERROR_MESSAGE);
            clearEmailForms();
            return false;
        }

        return true;
    }

    @Action
    public void sendEmail() throws FileNotFoundException, IOException, InterruptedException, FTPException {
        Runnable runA = new Runnable() {
            public void run() {
                ss = new SavingScreen();
                ss.setAlwaysOnTop(true);
            }
        };

        final Thread threadA = new Thread(runA);
        threadA.start();

        Runnable runB = new Runnable() {
            public void run() {
                try {
                    savePatientPane();
                    emailClient();
                    ss.setVisible(false);
                    threadA.interrupt();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FTPException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MedPassportView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        Thread threadB = new Thread(runB);
        threadB.start();
    }
    
    public void emailClient() throws FileNotFoundException, IOException, InterruptedException {
        String patientName;
        
        if (selectedPatient == null) {
            patientName = this.entry_firstField.getText() + " " + this.entry_lastField.getText();
            zipDirectory(currentDirectory + File.separator + "Entries" + File.separator + patientName,  currentDirectory + File.separator + "Entries" + File.separator + patientName + ".zip");
        }
        else
            zipDirectory(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient,  currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + ".zip");
        Runtime.getRuntime().exec("regedit /E " + currentDirectory + File.separator + "Entries" + File.separator + "mail.reg" + " \"HKEY_CLASSES_ROOT" + File.separator + "mailto" + File.separator + "shell" + File.separator + "open" + File.separator + "command\"");
        Thread.sleep(1000);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(currentDirectory + File.separator + "Entries" + File.separator + "mail.reg"));
        int c, count = 0, ncount = 0, bcount = 0, cccount = 0;
        int ref[] = new int[100];
        String newline = System.getProperty("line.separator");
        char buffer[] = new char[3000];
        char buffer1[] = new char[3000];
        int ccount[] = new int[100];
        while ((c = isr.read()) != -1) {
           buffer[count++] = (char)c;
        }

        for (int i = 0; i < count; i++)
            if (buffer[i] == newline.charAt(0))
                ref[ncount++] = i;
            
        
        for (int i = ref[2]; i < ref[3]; i++) 
            if (buffer[i] == 'C' || buffer[i] == 'c') 
                ccount[cccount++] = i;
        
        for (int i = 0; i < ref[3] - ccount[0] + 1; i++)
            buffer1[i] = buffer[ccount[0] + 2*i];
        
        String str = new String(buffer1);
        bcount = str.lastIndexOf("EXE");

        if (bcount == -1)
            bcount = str.lastIndexOf("exe");
        
        isr.close();
        /*
        if (!str.contains("EXE") && !str.contains("exe"))
            showEmailButtonFrame();
        else {
            String finalle = str.copyValueOf(buffer1, 0, bcount + 3);
            Runtime.getRuntime().exec(finalle);
        }*/

        String finalle = str.copyValueOf(buffer1, 0, bcount + 3);
        Runtime.getRuntime().exec(finalle);
        
        File file = new File(currentDirectory + File.separator + "Entries" + File.separator + "mail.reg");
        if (file.exists())
            file.delete();
    }
/*
    @Action
    public void gmail() throws IOException {
        host = "smtp.gmail.com";
        port = "465";
        this.emailButtonFrame.setVisible(false);
        this.emailButtonFrame.dispose();
        showEmailFrame();
    }

    @Action
    public void yahoo() throws IOException {
        host = "smtp.mail.yahoo.com";
        port = "25";
        this.emailButtonFrame.setVisible(false);
        this.emailButtonFrame.dispose();
        showEmailFrame();
    }

    @Action
    public void aol() throws IOException {
        host = "smtp.aol.com";
        port = "25";
        this.emailButtonFrame.setVisible(false);
        this.emailButtonFrame.dispose();
        showEmailFrame();
    }

    @Action
    public void lycos() throws IOException {
        host = "smtp.mail.lycos.com";
        port = "25";
        this.emailButtonFrame.setVisible(false);
        this.emailButtonFrame.dispose();
        showEmailFrame();
    }
*/
    @Action
    public void checkEmailEntries() {
        if (checkEntries())
            emailPatientInfo();
    }

    public void emailPatientInfo() {       
        Properties props = new Properties();
        props.put("mail.smtp.user", this.senderEmail.getText());
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        SecurityManager security = System.getSecurityManager();

        try
        {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            //session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);
            //msg.setText(this.Text.getText());
            msg.setSubject(this.Subject.getText());
            msg.setFrom(new InternetAddress(this.senderEmail.getText()));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(this.receiverEmail.getText()));

            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(this.Text.getText());

            MimeBodyPart mbp2 = new MimeBodyPart();

            // attach the file to the message
            FileDataSource fds = new FileDataSource(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + ".zip");
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName(fds.getName());

            // create the Multipart and add its parts to it
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);

            // add the Multipart to the message
            msg.setContent(mp);

            // set the Date: header
            msg.setSentDate(new Date());

            Transport.send(msg);
        }
        catch (Exception mex)
        {
            JOptionPane.showMessageDialog(null, "Send failed, exception: " + mex, "Email Send Error", JOptionPane.ERROR_MESSAGE);
        }
        
        clearEmailForms();
        this.emailFrame.setVisible(false);
        this.emailFrame.dispose();
        host = "";
        port = "";
        //File file = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + ".zip");
        //if (file.exists())
            //file.delete();
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator
    {
        String clearPassword = new String(senderPasswd.getPassword());

        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(senderEmail.getText(), clearPassword);
        }
    }

    public static final boolean isInteger(String s) {
        boolean flag = false;
        
        for (int x = 0; x < s.length(); x++) {
            char c = s.charAt(x);
            
            if (x == 0 && (c == '-')) 
                continue;  // negative
            if ((c >= '0') && (c <= '9')) {
                flag=true; 
                continue;
            }  // 0 - 9
            
            return false; // invalid
        }
  
        return flag; // valid
    }

    public static boolean isValidEmailAddress(String emailAddress) {
        // a null string is invalid
        if ( emailAddress == null )
            return false;

        // a string without a "@" is an invalid email address
        if ( emailAddress.indexOf("@") < 0 )
            return false;

        // a string without a "."  is an invalid email address
        if ( emailAddress.indexOf(".") < 0 )
            return false;

        if ( lastEmailFieldTwoCharsOrMore(emailAddress) == false )
            return false;

        try
        {
            InternetAddress internetAddress = new InternetAddress(emailAddress);
            return true;
        }
        catch (AddressException ae)
        {
            // log exception
            return false;
        }
    }


    /**
    * Returns true if the last email field (i.e., the country code, or something
    * like .com, .biz, .cc, etc.) is two chars or more in length, which it really
    * must be to be legal.
    */
    private static boolean lastEmailFieldTwoCharsOrMore(String emailAddress)
    {
        if (emailAddress == null) return false;
        StringTokenizer st = new StringTokenizer(emailAddress,".");
        String lastToken = null;
        while ( st.hasMoreTokens() )
        {
            lastToken = st.nextToken();
        }

        if ( lastToken.length() >= 2 )
        {
            return true;
        }
        else
        {
        return false;
        }
    }
    
    public void hidePatientPane()
    {
        if (this.patientImage != null)
            this.imagePanel.remove(this.patientImage); //removes the image from the image panel

        this.selectedEntry = null; //we're now done with this, so null it

        this.prevGender = this.gender;
        this.gender = "None";
        this.updateTheme();

        this.menuBar.setVisible(true);
        this.statusPanel.setVisible(true);

        this.patientPane.setVisible(false);
        this.setComponent(mainPanel);
        this.mainPanel.setVisible(true);
    }

    @Action
    public void cancelButton()
    {
        int result = JOptionPane.showConfirmDialog(this.getFrame(), "Do you want to exit?", "Warning", JOptionPane.YES_NO_OPTION);
                
        if (result == JOptionPane.YES_OPTION) {
          //System.exit(0);
            if (selectedEntry != null)
            {
                this.statusLabel.setText("");

                File file = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");

                if (selectedEntry == null) {
                    deleteDir(file);
                    patients.getList().remove(patients.getList().find("temp"));
                    patients.populatePatients();  //re-populate the list
                    jList1.setModel(patients.getList());
                } else {
                    if (checkImageUpload > 0) {
                        if (this.selectedEntry.images.length > copyCount) {
                            for (int i = copyCount; i < this.selectedEntry.images.length; i++) {
                                //JOptionPane.showMessageDialog(null, , "Write Error", JOptionPane.ERROR_MESSAGE);
                                File imgFile = new File(currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator + this.selectedEntry.images.imageNames[i]);
                                deleteDir(imgFile);
                                this.selectedEntry.images.imageNames[i] = "";
                                this.selectedEntry.images.labels[i] = "";
                            }
                        }
                    }
                }
                this.hidePatientPane();

                if (this.growthTable.isEditing()) {
                    TableCellEditor cellEditor = this.growthTable.getCellEditor(this.growthTable.getEditingRow(), this.growthTable.getEditingColumn());
                    cellEditor.cancelCellEditing();
                }
                this.growthTable.clearSelection();
                this.growthTable.removeEditor();

                if (this.bmiTable.isEditing()) {
                    TableCellEditor cellEditor1 = this.bmiTable.getCellEditor(this.bmiTable.getEditingRow(), this.bmiTable.getEditingColumn());
                    cellEditor1.cancelCellEditing();
                }
                this.bmiTable.clearSelection();
                this.bmiTable.removeEditor();

                if (this.schedTable.isEditing()) {
                   TableCellEditor cellEditor2 = this.schedTable.getCellEditor(this.schedTable.getEditingRow(), this.schedTable.getEditingColumn());
                    cellEditor2.cancelCellEditing();
                }
                this.schedTable.clearSelection();
                this.schedTable.removeEditor();

                /*
                if (file.exists()) {
                    deleteDir(file);
                    patients.getList().remove(patients.getList().getSize() - 2);
                    patients.populatePatients();  //re-populate the list
                }*/
                //if (errorCount == 0) {
                    for (int i = 0; i < 1000; i++) {
                        tempList[i] = "";
                        tempList1[i] = "";
                        tempImg[i] = "";
                        tempImg1[i] = "";
                    }
                    tempCount = 0;
                    tempICount = 0;
                    tempCount1 = 0;
                    tempICount1 = 0;
                    checkImageUpload = 0;
                    copyCount = 0;
                //}
            }
            else 
            {
                File file1 = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
                deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".reg");
                deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".zip");
                deleteDir(file1);
                System.exit(0);
            }
        } else if (result == JOptionPane.NO_OPTION) {
          //System.out.println("Do nothing");
            File file1 = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
            deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".reg");
            deleteFiles(currentDirectory + File.separator + "Entries" + File.separator, ".zip");
            deleteDir(file1);
        }   
    }

    @Action
    public void cancelEmail() {
        this.emailFrame.setVisible(false);
        this.emailFrame.dispose();
        senderEmail.setText("");
        senderPasswd.setText("");
        receiverEmail.setText("");
        Subject.setText("");
        Text.setText("");
        //File file = new File(currentDirectory + File.separator + "Entries" + File.separator + selectedPatient + ".zip");
        //if (file.exists())
            //file.delete();
    }


    public void clearEmailForms() {
        senderEmail.setText("");
        senderPasswd.setText("");
        receiverEmail.setText("");
        Subject.setText("");
        Text.setText("");
        clearBtn.setFocusable(false);
        senderEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    @Action
    public void clearEmail() {
        senderEmail.setText("");
        senderPasswd.setText("");
        receiverEmail.setText("");
        Subject.setText("");
        Text.setText("");
        clearBtn.setFocusable(false);
        senderEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    @Action
    public void userPatient()
    {
        this.intro_docField.setText("Patient");

        this.READ_ONLY = true;

        //this.entry_saveButton.setEnabled(false);
        this.newPatientButton.setEnabled(!READ_ONLY);
        this.deleteButton.setEnabled(!READ_ONLY);

        //this.modifyButton.setText("View");

        this.entry_firstField.setEnabled(!READ_ONLY);
        this.entry_lastField.setEnabled(!READ_ONLY);
        this.entry_memberIDField.setEnabled(!READ_ONLY);
        this.entry_insuranceField.setEnabled(!READ_ONLY);
        this.entry_fRadio.setEnabled(!READ_ONLY);
        this.entry_mRadio.setEnabled(!READ_ONLY);

        this.entry_birthCityField.setEnabled(!READ_ONLY);
        this.entry_birthDateField.setEnabled(!READ_ONLY);
        this.entry_birthHospitalField.setEnabled(!READ_ONLY);
        this.entry_bloodField.setEnabled(!READ_ONLY);
        this.entry_fatherField.setEnabled(!READ_ONLY);
        this.entry_motherField.setEnabled(!READ_ONLY);
        //this.entry_phoneField.setEnabled(!READ_ONLY);
        //this.entry_emailField.setEnabled(!READ_ONLY);

        this.entry_eyeColorField.setEnabled(!READ_ONLY);
        this.entry_specialArea.setEnabled(!READ_ONLY);
        this.entry_primaryCareArea.setEnabled(!READ_ONLY);
        
        this.image_labelField.setEnabled(!READ_ONLY);
        this.entry_medHistArea.setEnabled(!READ_ONLY);
        this.entry_mentalHealthArea.setEnabled(!READ_ONLY);
        this.entry_socialIssuesArea.setEnabled(!READ_ONLY);

        this.entry_labsArea.setEnabled(!READ_ONLY);
        this.entry_radiologyArea.setEnabled(!READ_ONLY);
        this.entry_medsArea.setEnabled(!READ_ONLY);
        this.entry_allergyArea.setEnabled(!READ_ONLY);
        this.entry_consultationArea.setEnabled(!READ_ONLY);
        this.entry_skinTestArea.setEnabled(!READ_ONLY);
        this.entry_familyHistArea.setEnabled(!READ_ONLY);
        this.entry_specialistArea.setEnabled(!READ_ONLY);
        this.entry_consultantArea.setEnabled(!READ_ONLY);        
        this.entry_consultationArea1.setEnabled(!READ_ONLY);        
        this.entry_consultationArea2.setEnabled(!READ_ONLY);

        this.bmiTable.setEnabled(!READ_ONLY);
        this.growthTable.setEnabled(!READ_ONLY);
        this.schedTable.setEnabled(!READ_ONLY);

        showMainPanel();
    }

    @Action
    public void userDoctor() throws IOException
    {
        File f = new File(".");
        String drive = f.getAbsolutePath();
        File fromPath, toPath;

        if (drive.charAt(0) != 'C') {
            fromPath = new File(currentDirectory + File.separator + "Entries");
            toPath = new File("C:" + File.separator + "MedPassport" + File.separator + "Entries");
            copyFiles(fromPath, toPath);
        }

        if (this.intro_docField.getText().equals(""))
        {
            this.intro_docField.setBackground(new Color(255,190,190));
            return;
        }

        this.READ_ONLY = false;
        showMainPanel();
    }

    public void showMainPanel()
    {
        this.setStatusBar(statusPanel);
        this.introPanel.setVisible(false);
        this.setComponent(mainPanel);
        this.mainPanel.setVisible(true);
    }

    public void showPatientPane()
    {
        this.menuBar.setVisible(false);
        this.statusPanel.setVisible(false);
        
        this.entry_memberIDField.setBackground(Color.WHITE);
        this.entry_firstField.setBackground(Color.WHITE);
        this.entry_lastField.setBackground(Color.WHITE);
        this.entry_birthDateField.setBackground(Color.WHITE);

        /* Scroll all TextPanes back up: */
        this.entry_specialArea.setCaretPosition(0);
        this.entry_primaryCareArea.setCaretPosition(0);
        this.entry_medHistArea.setCaretPosition(0);
        this.entry_mentalHealthArea.setCaretPosition(0);
        this.entry_socialIssuesArea.setCaretPosition(0);
        this.entry_lastEditArea.setCaretPosition(0);
        this.entry_labsArea.setCaretPosition(0);
        this.entry_radiologyArea.setCaretPosition(0);
        this.entry_medsArea.setCaretPosition(0);
        this.entry_allergyArea.setCaretPosition(0);
        this.entry_consultationArea.setCaretPosition(0);
        this.entry_skinTestArea.setCaretPosition(0);
        this.entry_familyHistArea.setCaretPosition(0);
        this.entry_specialistArea.setCaretPosition(0);
        this.entry_consultantArea.setCaretPosition(0);
        this.entry_consultationArea1.setCaretPosition(0);
        this.entry_consultationArea2.setCaretPosition(0);

        this.mainPanel.setVisible(false);
        this.setComponent(patientPane);
        this.patientPane.setVisible(true);
    }

    @Action
    public void showBlankPatientPane()
    {       
        //this.patientPane.setEnabledAt(2, false); //disable growth chart for new patients
        //this.patientPane.setEnabledAt(3, false); //disable sched for new patients

        //Charts can't be displayed on new patients because the charts are read
        //from the patient's file, which doesn't exist yet.
        this.growth_bmiChartButton.setEnabled(false);
        this.growth_chart1Button.setEnabled(false);
        this.growth_chart2Button.setEnabled(false);
        this.growth_chart3Button.setEnabled(false);
        this.growth_chart4Button.setEnabled(false);

        this.growth_imagePanel.removeAll();

        this.entry_firstField.setText("");
        this.entry_lastField.setText("");

        this.entry_birthDateField.setText("");
        this.entry_birthCityField.setText("");
        this.entry_birthHospitalField.setText("");

        /*if (this.genderGroup.getSelection() != null)
            this.genderGroup.getSelection().setSelected(false); //java 1.5*/
        this.genderGroup.clearSelection(); //java 1.6

        this.image_labelField.setText("");
        this.entry_bloodField.setText("");
        this.entry_fatherField.setText("");
        this.entry_motherField.setText("");
        this.entry_memberIDField.setText("");
        this.entry_eyeColorField.setText("");
        this.entry_phoneField.setText("");
        this.entry_emailField.setText("");
        this.entry_insuranceField.setText("");
        this.entry_labsArea.setText("");

        this.entry_lastEditArea.setText("");
        
        this.entry_medsArea.setText("");
        this.entry_consultationArea.setText("");
        this.entry_skinTestArea.setText("");
        this.entry_medHistArea.setText("");
        this.entry_mentalHealthArea.setText("");
        this.entry_socialIssuesArea.setText("");
        this.entry_radiologyArea.setText("");
        this.entry_allergyArea.setText("");
        this.entry_familyHistArea.setText("");
        this.entry_specialArea.setText("");
        this.entry_consultantArea.setText("");
        this.entry_specialistArea.setText("");
        this.entry_primaryCareArea.setText("");
        
        /*
        this.patientImage = new ImagePanel();
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(ClassLoader.getSystemResourceAsStream("medpassport/resources/imagePanelBackGround.JPG"));
        }
        catch (Exception e){
        
        }
        if (image != null) {
            patientImage.setImage(image);
            this.imagePanel.add(patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
            //this.imagePanel.validate();
        }
        patientImage = null;
         * 
         */
        
        this.fillStandardGrowthTableValues();

        javax.swing.table.TableModel model = this.growthTable.getModel();
        this.growthTable.removeEditor();
        for (int i = 0; i < this.growthTable.getRowCount(); i++)
        {
            for (int j = 1; j < this.growthTable.getColumnCount(); j++)
            {
                if (i == 1)
                    model.setValueAt(null, i, j);
                else if (i == 3)
                    model.setValueAt(null, i, j);
                else if (i == 5)
                    model.setValueAt(null, i, j);
            }
        }

        this.growthTable.clearSelection();
        this.growthTable.setCellSelectionEnabled(true);
        ExcelAdapter1 myAd = new ExcelAdapter1(this.growthTable);


        model = this.bmiTable.getModel();
        this.bmiTable.removeEditor();
        for (int j=1; j < this.bmiTable.getColumnCount(); j++)
        {
            model.setValueAt(null, 1, j);
        }
        this.bmiTable.clearSelection();
        this.bmiTable.setCellSelectionEnabled(true);
        ExcelAdapter1 myAd1 = new ExcelAdapter1(this.bmiTable);

        model = this.schedTable.getModel();

        this.schedTable.removeEditor();
        for (int i=0; i < this.schedTable.getRowCount(); i++)
            for (int j=1; j < this.schedTable.getColumnCount(); j++)
                model.setValueAt(null, i, j);
        this.schedTable.clearSelection();
        this.schedTable.setCellSelectionEnabled(true);
        ExcelAdapter myAd2 = new ExcelAdapter(this.schedTable);

        this.entry_firstField.setEnabled(true);
        this.entry_lastField.setEnabled(true);
        this.entry_firstField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        //TODO: check on this things status, is it fine if someone can see growth menu item always?
        //this.growthMenuItem.setEnabled(false); //growth table is dependent on gender

        this.selectedPatient = ""; //don't use the selected patient
        this.selectedEntry = null;
        this.statusLabel.setText("Creating new patient.");
        showPatientPane();
    }

    @Action
    public void modifyExistingPatient()
    {
        selectedEntry = new Entry(currentDirectory + File.separator + "Entries" +
                        File.separator + selectedPatient + File.separator, "patientInfo");

        selectedEntry.readXMLEntry();

        fillPatientPane();
        this.statusLabel.setText("Modifying existing patient.");
        showPatientPane();
        return;
    }

    public void fillStandardGrowthTableValues()
    {
        Object[] row1;
        Object[] row3;
        Object[] row5;

        Object[] bmiRow;

        if (this.gender.equals("F"))
        {
            row1 = this.row1_female;
            row3 = this.row3_female;
            row5 = this.row5_female;
            bmiRow = this.bmiRow_female;
        }
        else
        {
            row1 = this.row1_male;
            row3 = this.row3_male;
            row5 = this.row5_male;
            bmiRow = this.bmiRow_male;
        }

        javax.swing.table.TableModel model = this.growthTable.getModel();

        for (int i=0; i < this.growthTable.getRowCount(); i++)
        {
            for (int j=1; j < this.growthTable.getColumnCount(); j++)
            {
                if (i == 0)
                {
                    model.setValueAt(row1[j], i, j);
                }
                else if (i == 2)
                {
                    model.setValueAt(row3[j], i, j);
                }
                else if (i == 4)
                {
                    model.setValueAt(row5[j], i, j);
                }
            }
        }

        model = this.bmiTable.getModel();

        for (int j=1; j < this.bmiTable.getColumnCount(); j++)
        {
            model.setValueAt(bmiRow[j], 0, j);
        }
    }

    public void initTables()
    {
        //setting the column names, because NetBeans can't seem to remember what they are in the settings
        TableColumn column = null;
        for (int i = 0; i < this.growthTable.getColumnCount(); i++)
        {
            column = growthTable.getColumnModel().getColumn(i);
            if (i == 0)
            {
                column.setPreferredWidth(150);
                column.setHeaderValue(this.growthColNames[i]);
            } 
            else
            {
                column.setHeaderValue(this.growthColNames[i]);
            }
        }

        column = null;
        for (int i = 0; i < this.bmiTable.getColumnCount(); i++)
        {
            column = bmiTable.getColumnModel().getColumn(i);
            if (i == 0)
            {
                column.setPreferredWidth(220);
                column.setHeaderValue(this.bmiColNames[i]);
            }
            else
            {
                column.setHeaderValue(this.bmiColNames[i]);
            }
        }

        column = null;
        for (int i = 0; i < this.schedTable.getColumnCount(); i++)
        {
            column = schedTable.getColumnModel().getColumn(i);
            if (i == 0)
            {
                column.setPreferredWidth(150);
                column.setHeaderValue(this.schedColNames[i]);
            }
            else if (i == (this.schedTable.getColumnCount()-1))
            {
                column.setPreferredWidth(90);
                column.setHeaderValue(this.schedColNames[i]);
            }
            else
            {
                column.setHeaderValue(this.schedColNames[i]);
            }
        }
    }

    @Action
    public void showNextImage() throws FileNotFoundException, IOException
    {
        int k = 0;
        if ((this.selectedEntry == null) || (this.selectedEntry.images == null) || (this.patientImage == null))
            return;

        for (int i = 0; i < this.selectedEntry.images.length; i++) 
            if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName())) 
                k = i;

        if (this.selectedPatient.equals("temp"))
            tempList1[k] = this.image_labelField.getText();
        else
            tempList[k] =  this.image_labelField.getText();
        this.selectedEntry.images.labels[k] = this.image_labelField.getText();

        //JOptionPane.showMessageDialog(null, "k = 0\n" + this.selectedEntry.images.imageNames[0] + "\n" + this.tempList1[0], "Write Error", JOptionPane.ERROR_MESSAGE);
        //JOptionPane.showMessageDialog(null, "k = " + k + "\n" + this.selectedEntry.images.imageNames[k] + "\n" + this.tempList1[k], "Write Error", JOptionPane.ERROR_MESSAGE);
        String[] next = this.selectedEntry.images.getNext();
        this.image_labelField.setText(next[1]);
        //JOptionPane.showMessageDialog(null, next[0] + "\n" + next[1], "Write Error", JOptionPane.ERROR_MESSAGE);
        String patientDir = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator;
        this.imagePanel.remove(this.patientImage);
        this.patientImage = new ImagePanel(patientDir + next[0]);
        //JOptionPane.showMessageDialog(null, next[1], "Write Error", JOptionPane.ERROR_MESSAGE);
        this.imagePanel.add(this.patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        this.imagePanel.repaint();
        this.patientImage.repaint();
    }

    @Action
    public void showPrevImage() throws FileNotFoundException, IOException
    {
        int k = 0;
        if ((this.selectedEntry == null) || (this.selectedEntry.images == null) || (this.patientImage == null))
            return;
        
        for (int i = 0; i < this.selectedEntry.images.length; i++)
            if (this.selectedEntry.images.imageNames[i].equals(this.patientImage.getImageName()))
                k = i;

        //if (this.selectedEntry.images.length <= 1) {
        this.selectedEntry.images.labels[k] = this.image_labelField.getText();
        //this.selectedEntry.images.add(this.patientImage.getImageName(), this.image_labelField.getText(), k);
        if (this.selectedPatient.equals("temp"))
            tempList1[k] = this.image_labelField.getText();
        else
            tempList[k] =  this.image_labelField.getText();
        /*}
        else {
            if (this.selectedEntry.images.labels[k].equals("No Description")) {
                this.selectedEntry.images.labels[k] = this.image_labelField.getText();
                this.selectedEntry.images.add(this.patientImage.getImageName(), this.image_labelField.getText(), k);
            }
        }*/

        String[] prev = this.selectedEntry.images.getPrev();
        this.image_labelField.setText(prev[1]);
        
        String patientDir = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator;
        this.imagePanel.remove(this.patientImage);
        this.patientImage = new ImagePanel(patientDir + prev[0]);
        this.imagePanel.add(patientImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        this.imagePanel.repaint();
        this.patientImage.repaint();
        //prevCount++;
    }

    public void saveImageLabel()
    {
        if ((this.selectedEntry == null) || (this.selectedEntry.images == null) || (this.patientImage == null))
            return;
        
        this.selectedEntry.images.setCurrentLabel(this.image_labelField.getText());
    }

    public void saveGrowthTables()
    {
        if (this.selectedEntry == null)
            return;

        if (this.growthTable.isEditing())
            this.growthTable.getCellEditor().stopCellEditing();
        javax.swing.table.TableModel model = this.growthTable.getModel();
        for (int i=0; i < this.growthTable.getRowCount(); i++)
        {
            for (int j=1; j < this.growthTable.getColumnCount(); j++)//get std value
            {
                if (i == 1)
                    this.selectedEntry.growth1[j-1] = (Double)model.getValueAt(i, j);
                else if (i == 3)
                    this.selectedEntry.growth2[j-1] = (Double)model.getValueAt(i, j);
                else if (i == 5)
                    this.selectedEntry.growth3[j-1] = (Double)model.getValueAt(i, j);
            }
        }

        if (this.bmiTable.isEditing())
            this.bmiTable.getCellEditor().stopCellEditing();
        model = this.bmiTable.getModel();
        for (int j=1; j < this.bmiTable.getColumnCount(); j++)
        {
            this.selectedEntry.bmi[j-1] = (Double)model.getValueAt(1, j);
        }
        
    }

    @Action //Weight Growth Chart
    public void showChart1()
    {
        if (this.selectedEntry == null)
            return;

        this.saveGrowthTables();

        Object[] standard_weight_50;
        Double[] standard_weight_05;
        Double[] standard_weight_95;
        if (this.gender.equals("F"))
        {
            standard_weight_50 = this.row1_female;
            standard_weight_05 = this.standard_F_weight_05;
            standard_weight_95 = this.standard_F_weight_95;
        }
        else
        {
            standard_weight_50 = this.row1_male;
            standard_weight_05 = this.standard_M_weight_05;
            standard_weight_95 = this.standard_M_weight_95;
        }

        //TODO: instead of reading from selectedEntry, read from the actual table on screen
        XYDataset dataset = createDataset(this.growthColNames, this.selectedEntry.growth1, standard_weight_50, standard_weight_05, standard_weight_95);
        JFreeChart chart = createChart(dataset,"Weight Chart","Months","Weight (lbs)");
        
        String imageOutputLocation = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator + "weightChart.png";
        try{
            File image = new File(imageOutputLocation);
            if (image.exists())
                image.delete();
            FileOutputStream out = new FileOutputStream(image) ;
            ChartUtilities.writeBufferedImageAsPNG(out,chart.createBufferedImage(preferredChartFrameSize.width, preferredChartFrameSize.height));
            out.close();
            this.growthImageLoc = imageOutputLocation;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reloadGrowthImage();
    }

    @Action //Length Growth Chart
    public void showChart2()
    {
        if (this.selectedEntry == null)
            return;

        this.saveGrowthTables();

        Object[] standard_length_50;
        Double[] standard_length_05;
        Double[] standard_length_95;
        if (this.gender.equals("F"))
        {
            standard_length_50 = this.row3_female;
            standard_length_05 = this.standard_F_length_05;
            standard_length_95 = this.standard_F_length_95;
        }
        else
        {
            standard_length_50 = this.row3_male;
            standard_length_05 = this.standard_M_length_05;
            standard_length_95 = this.standard_M_length_95;
        }

        //TODO: instead of reading from selectedEntry, read from the actual table on screen
        XYDataset dataset = createDataset(this.growthColNames, this.selectedEntry.growth2, standard_length_50, standard_length_05, standard_length_95);
        JFreeChart chart = createChart(dataset,"Length Chart","Months","Length (inches)");
        
        String imageOutputLocation = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator + "lengthChart.png";
        try{
            File image = new File(imageOutputLocation);
            if (image.exists())
                image.delete();
            FileOutputStream out = new FileOutputStream(image) ;
            ChartUtilities.writeBufferedImageAsPNG(out,chart.createBufferedImage(preferredChartFrameSize.width, preferredChartFrameSize.height));
            out.close();
            this.growthImageLoc = imageOutputLocation;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reloadGrowthImage();
    }

    @Action //HC Growth Chart
    public void showChart3()
    {
        if (this.selectedEntry == null)
            return;

        this.saveGrowthTables();

        Object[] standard_hc_50;
        Double[] standard_hc_05;
        Double[] standard_hc_95;
        if (this.gender.equals("F"))
        {
            standard_hc_50 = this.row5_female;
            standard_hc_05 = this.standard_F_hc_05;
            standard_hc_95 = this.standard_F_hc_95;
        }
        else
        {
            standard_hc_50 = this.row5_male;
            standard_hc_05 = this.standard_M_hc_05;
            standard_hc_95 = this.standard_M_hc_95;
        }

        //TODO: instead of reading from selectedEntry, read from the actual table on screen
        XYDataset dataset = createDataset(this.growthColNames, this.selectedEntry.growth3, standard_hc_50, standard_hc_05, standard_hc_95);
        JFreeChart chart = createChart(dataset,"HC Chart","Age (in months)","HC (inches)");

        String imageOutputLocation = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator + "hcChart.png";
        try{
            File image = new File(imageOutputLocation);
            if (image.exists())
                image.delete();
            FileOutputStream out = new FileOutputStream(image) ;
            ChartUtilities.writeBufferedImageAsPNG(out,chart.createBufferedImage(preferredChartFrameSize.width, preferredChartFrameSize.height));
            out.close();
            this.growthImageLoc = imageOutputLocation;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reloadGrowthImage();
    }

    @Action //Length vs Weight Chart
    public void showChart4()
    {
        if (this.selectedEntry == null)
            return;

        this.saveGrowthTables();

        Double[] standard_LVW_50;
        Double[] standard_LVW_05;
        Double[] standard_LVW_95;
        if (this.gender.equals("F"))
        {
            standard_LVW_50 = this.standard_F_LVW_50;
            standard_LVW_05 = this.standard_F_LVW_05;
            standard_LVW_95 = this.standard_F_LVW_95;
        }
        else
        {
            standard_LVW_50 = this.standard_M_LVW_50;
            standard_LVW_05 = this.standard_M_LVW_05;
            standard_LVW_95 = this.standard_M_LVW_95;
        }

        //TODO: instead of reading from selectedEntry, read from the actual table on screen
        XYDataset dataset = createDataset_vs(this.selectedEntry.growth2, this.selectedEntry.growth1, this.standard_LVW_xAxis, standard_LVW_50, standard_LVW_05, standard_LVW_95);
        JFreeChart chart = createChart(dataset,"Length vs Weight","Length (inches)","Weight (lbs)");

        String imageOutputLocation = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator + "lengthWeightChart.png";
        try{
            File image = new File(imageOutputLocation);
            if (image.exists())
                image.delete();
            FileOutputStream out = new FileOutputStream(image) ;
            ChartUtilities.writeBufferedImageAsPNG(out,chart.createBufferedImage(preferredChartFrameSize.width, preferredChartFrameSize.height));
            out.close();
            this.growthImageLoc = imageOutputLocation;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reloadGrowthImage();
    }

    private static XYDataset createDataset_vs(Double[] xAxis_actual, Double[] yAxis_actual,
                                              Double[] xAxis_standard,
                                              Double[] yAxis_50, Double[] yAxis_05, Double[] yAxis_95) 
    {
        XYSeries s1 = new XYSeries("Actual");
        for (int i=0; i < yAxis_actual.length; i++)
        {
            if (xAxis_actual[i] != null && yAxis_actual[i] != null)
                s1.add(xAxis_actual[i],yAxis_actual[i]);
        }


        //s1.add(1, 181.8);
        //s1.add(2, 167.3);
        //s1.add(2, 153.8);

        XYSeries s2 = new XYSeries("50th Percentile");
        for (int i=0; i < xAxis_standard.length; i++)
            s2.add(xAxis_standard[i], yAxis_50[i]);

        XYSeries s3 = new XYSeries("5th Percentile");
        for (int i=0; i < xAxis_standard.length; i++)
            s3.add(xAxis_standard[i], yAxis_05[i]);

        XYSeries s4 = new XYSeries("95th Percentile");
        for (int i=0; i < xAxis_standard.length; i++)
            s4.add(xAxis_standard[i], yAxis_95[i]);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);
        dataset.addSeries(s4);

        return dataset;
    }

    @Action //BMI Growth Chart
    public void showBMIChart()
    {
        if (this.selectedEntry == null)
            return;

        this.saveGrowthTables();

        Object[] standard_bmi_50;
        Double[] standard_bmi_05;
        Double[] standard_bmi_95;
        if (this.gender.equals("F"))
        {
            standard_bmi_50 = this.bmiRow_female;
            standard_bmi_05 = this.standard_F_bmi_05;
            standard_bmi_95 = this.standard_F_bmi_95;
        }
        else
        {
            standard_bmi_50 = this.bmiRow_male;
            standard_bmi_05 = this.standard_M_bmi_05;
            standard_bmi_95 = this.standard_M_bmi_95;
        }

        //TODO: instead of reading from selectedEntry, read from the actual table on screen
        XYDataset dataset = createDataset(this.bmiColNames, this.selectedEntry.bmi, standard_bmi_50, standard_bmi_05, standard_bmi_95);
        JFreeChart chart = createChart(dataset,"BMI Chart","Age (in years)","BMI");

        String imageOutputLocation = this.currentDirectory + File.separator + "Entries" + File.separator + this.selectedPatient + File.separator + "bmiChart.png";
        try{
            File image = new File(imageOutputLocation);
            if (image.exists())
                image.delete();
            FileOutputStream out = new FileOutputStream(image) ;
            ChartUtilities.writeBufferedImageAsPNG(out,chart.createBufferedImage(preferredChartFrameSize.width, preferredChartFrameSize.height));
            out.close();
            this.growthImageLoc = imageOutputLocation;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reloadGrowthImage();
    }

    public void reloadGrowthImage()
    {   
        this.jLabel50.setVisible(false);
        this.growth_imagePanel.removeAll();
        this.growthImage = new ImagePanel(this.growthImageLoc);
        this.growthImage.setAllSizes(preferredChartFrameSize);
        this.growth_imagePanel.add(this.growthImage);
        this.growth_imagePanel.validate();

        this.growth_imagePanel.repaint();
        this.growthImage.repaint();
    }

    private JFreeChart createChart(XYDataset dataset, String title, String xAxis, String yAxis) {

        JFreeChart chart = ChartFactory.createXYLineChart(
            null,              // title
            xAxis,              // x-axis label
            yAxis,              // y-axis label
            dataset,            // data
            PlotOrientation.VERTICAL,
            true,               // create legend?
            false,              // generate tooltips?
            false               // generate URLs?
        );

        //static way of setting color (not good programming):
        if (this.gender.equals("F"))
            chart.setBackgroundPaint(new Color(255,225,225));
        else
            chart.setBackgroundPaint(new Color(204,255,255));

        XYPlot plot = (XYPlot) chart.getPlot();
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer)
        {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setSeriesStroke(0, new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
            renderer.setSeriesStroke(1, new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
            renderer.setSeriesStroke(2, new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
            renderer.setSeriesStroke(3, new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

            Color percentileColor_05 = new Color(0,184,245);
            Color percentileColor_95 = new Color(184,0,245);

            renderer.setSeriesPaint(2, percentileColor_05);
            renderer.setSeriesPaint(3, percentileColor_95);
        }

        //TODO [maybe] : fix the following to format the x-axis values shown

        //DateAxis axis = (DateAxis) plot.getDomainAxis()
        //axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

        //TODO [maybe] : move the legend to the right

        LegendTitle leg = chart.getLegend();
        //leg.setleg

        return chart;
    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private static XYDataset createDataset(String[] xAxis, Double[] yAxis_actual,
                                           Object[] yAxis_50, Double[] yAxis_05, Double[] yAxis_95) {

        XYSeries s1 = new XYSeries("Actual");
        for (int i=0; i < yAxis_actual.length; i++)
        {
            s1.add(Integer.parseInt(xAxis[i+1]),yAxis_actual[i]);
        }
        
        XYSeries s2 = new XYSeries("50th Percentile");
        for (int i=1; i < xAxis.length; i++)
        {
            s2.add(Integer.parseInt(xAxis[i]), (Double)yAxis_50[i]);
        }

        XYSeries s3 = new XYSeries("5th Percentile");
        for (int i=1; i < xAxis.length; i++)
        {
            s3.add(Integer.parseInt(xAxis[i]), yAxis_05[i-1]);
        }

        XYSeries s4 = new XYSeries("95th Percentile");
        for (int i=1; i < xAxis.length; i++)
        {
            s4.add(Integer.parseInt(xAxis[i]), yAxis_95[i-1]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);
        dataset.addSeries(s4);

        return dataset;
    }
    
    @Action
    public void changeToFirstTab()
    {
        this.patientPane.setSelectedIndex(0);
    }

    @Action
    public void changeToSecondTab()
    {
        this.patientPane.setSelectedIndex(1);
    }
    
    @Action
    public void changeToThirdTab()
    {
        this.patientPane.setSelectedIndex(2);
    }
    
    @Action
    public void changeToFouthTab()
    {
        this.patientPane.setSelectedIndex(3);
    }
    
    @Action
    public void changeToFifthTab()
    {
        this.patientPane.setSelectedIndex(4);
    }
    
    @Action
    public void changeToSixthTab()
    {
        this.patientPane.setSelectedIndex(5);
    }
    
    @Action
    public void changeToSeventhTab()
    {
        this.patientPane.setSelectedIndex(6);
    }
    
    @Action
    public void changeToEighthTab()
    {
        this.patientPane.setSelectedIndex(7);
    }

    /** Zip the contents of the directory, and save it in the zipfile */
    public void zipDirectory(String dir, String zipfile) throws IOException, IllegalArgumentException {
        // Check that the directory is a directory, and get its contents
        File d = new File(dir);

        if (!d.isDirectory())
            throw new IllegalArgumentException("Not a directory:  " + dir);
        String[] entries = d.list();
        byte[] buffer = new byte[4096]; // Create a buffer for copying
        int bytesRead;

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

        for (int i = 0; i < entries.length; i++) {
            File f = new File(d, entries[i]);

            if (f.isDirectory())
                continue;//Ignore directory
            FileInputStream in = new FileInputStream(f); // Stream to read file
            ZipEntry entry = new ZipEntry(f.getPath()); // Make a ZipEntry
            out.putNextEntry(entry); // Store entry
            while ((bytesRead = in.read(buffer)) != -1)
                out.write(buffer, 0, bytesRead);
            in.close();
        }
        out.close();
    }

    public void copyFiles(File src, File dest) throws IOException {
        //Check to ensure that the source is valid...
        //File temp = new File(currentDirectory + File.separator + "Entries" + File.separator + "temp");
        if (!src.exists()) 
            throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath()+".");
        else if (!src.canRead())  //check to ensure we have rights to the source...
            throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath()+".");
        
        
        //is this a directory copy?
        if (src.isDirectory()) 	{
            if (!dest.exists()) { //does the destination already exist?
                //if not we need to make it exist if possible (note this is mkdirs not mkdir)
                if (!dest.mkdirs()) {
                    throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
                }
            }
            //get a listing of files...
            String list[] = src.list();
            //copy all the files in the list.
            for (int i = 0; i < list.length; i++)
            {
                File dest1 = new File(dest, list[i]);
                File src1 = new File(src, list[i]);
                copyFiles(src1 , dest1);
            }
        } else {
            //This was not a directory, so lets just copy the file
            FileInputStream fin = null;
            FileOutputStream fout = null;
            byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
            int bytesRead;
            try {
                //open the files for input and output
                fin =  new FileInputStream(src);
                fout = new FileOutputStream (dest);
                //while bytesRead indicates a successful read, lets write...
                while ((bytesRead = fin.read(buffer)) >= 0) {
                    fout.write(buffer,0,bytesRead);
                }
            } catch (IOException e) { //Error copying file...
                IOException wrapper = new IOException("copyFiles: Unable to copy file: " +
                            src.getAbsolutePath() + "to" + dest.getAbsolutePath()+".");
                wrapper.initCause(e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } finally { //Ensure that the files are closed (if they were open).
                if (fin != null) { fin.close(); }
                if (fout != null) { fout.close(); }
            }
        }
    }

    public void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        }
        else {
            if (!sourceLocation.exists())
                JOptionPane.showMessageDialog(null, "File or directory does not exist.", "Save Error", JOptionPane.ERROR_MESSAGE);
            else {
                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        }
    }
}

