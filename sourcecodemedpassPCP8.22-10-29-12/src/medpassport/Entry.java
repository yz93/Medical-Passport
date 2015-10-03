/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medpassport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Hagop
 */

public class Entry
{
    String fileName, filePath;
    private final String key = "bebedoc2009"; 
    private final int MAX_GROWTH_COLUMNS = 10;
    private final int MAX_BMI_COLUMNS = 19;
    private final int MAX_SCHED_COLUMNS = 16;
    private final int MAX_SCHED_ROWS = 50;
    
    private final String delim = ","; //delimeter for output file

    //public static String newline_special = System.getProperty("line.separator");
    public static String newline = "\r\n";
    public ImageList images;
    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    String first = "";
    String last = "";
    String gender = "";
    String birthDate = "";
    String birthCity = "";
    String birthHospital = "";
    String cordBlood = "";
    String insuranceProvider = "";
    String memberID = "";
    String fatherName = "";
    String motherName = "";
    String phone = "";
    String emcall="";
    String email = "";
    String eyeColor = "";
    String labs = "";
    String radiology = "";
    String meds = "";
    String allergies = "";
    String consultationNote = "";
    String TBSkinTest = "";
    String medicalHistory = "";
    String familyHistory = "";
    String specialFeatures = "";
    String mentalHealth = "";
    String socialIssues = "";
    String primaryCare = "";
    String specialists = ""; //TODO: remove
    String consultants = ""; //TODO: remove
    String lastEdit = "";
    
    String labs_s = "";
    String radiology_s = "";
    String meds_s = "";
    String allergies_s = "";
    String consultationNote_s = "";
    String TBSkinTest_s = "";
    String familyHistory_s = "";
    String specialists_s = ""; //TODO: remove
    String consultants_s = ""; //TODO: remove
    
    String labs_er = "";
    String radiology_er = "";
    String meds_er = "";
    String allergies_er = "";
    String consultationNote_er = "";
    String TBSkinTest_er = "";
    String familyHistory_er = "";
    String specialists_er = ""; //TODO: remove
    String consultants_er = ""; //TODO: remove
    
    

    Double[] growth1 = new Double[MAX_GROWTH_COLUMNS];
    Double[] growth2 = new Double[MAX_GROWTH_COLUMNS];
    Double[] growth3 = new Double[MAX_GROWTH_COLUMNS];
    Double[] bmi = new Double[MAX_BMI_COLUMNS];
    String[][] sched = new String[MAX_SCHED_ROWS][MAX_SCHED_COLUMNS];

    public Entry(String path, String file_name)
    {
        this.images = new ImageList((new File(path + file_name)).getParent());
        filePath = path;
        fileName = path + file_name;
    }

    public void sanitizeParams()
    {
        first = first.replace(delim.charAt(0), ' ');
        last = last.replace(delim.charAt(0), ' ');
        birthDate = birthDate.replace(delim.charAt(0), ' ');
        birthCity = birthCity.replace(delim.charAt(0), ' ');
        birthHospital = birthHospital.replace(delim.charAt(0), ' ');

        insuranceProvider = insuranceProvider.replace(delim.charAt(0), ' ');
        memberID = memberID.replace(delim.charAt(0), ' ');

        fatherName = fatherName.replace(delim.charAt(0), ' ');
        motherName = motherName.replace(delim.charAt(0), ' ');
        phone = phone.replace(delim.charAt(0), ' ');
        emcall = emcall.replace(delim.charAt(0), ' ');
        email = email.replace(delim.charAt(0), ' ');
        eyeColor = eyeColor.replace(delim.charAt(0), ' ');
        cordBlood = cordBlood.replace(delim.charAt(0), ' ');

        //TODO: these params need further sanitization:

        //birthDate
        //phone
        //email
        //String text1[] = meds.split("\n");
        //for (int x = 0; x < text1.length; x++)
            //meds += (text1 + "@");
        //JOptionPane.showMessageDialog(null, specialFeatures, "specialFeatures", JOptionPane.INFORMATION_MESSAGE);
        //JOptionPane.showMessageDialog(null, specialFeatures, "specialFeatures1", JOptionPane.INFORMATION_MESSAGE);
        specialFeatures = specialFeatures.replaceAll(newline, "@");
        medicalHistory = medicalHistory.replaceAll(newline, "@");
        mentalHealth = mentalHealth.replaceAll(newline, "@");
        socialIssues = socialIssues.replaceAll(newline, "@");
        primaryCare = primaryCare.replace("@", "#$");
        primaryCare = primaryCare.replaceAll(newline, "@");
        
        labs = labs.replaceAll(newline, "@");
        radiology = radiology.replaceAll(newline, "@");
        meds = meds.replaceAll(newline, "@");
        //JOptionPane.showMessageDialog(null, meds, "meds", JOptionPane.INFORMATION_MESSAGE);
        allergies = allergies.replaceAll(newline, "@");
        consultationNote = consultationNote.replaceAll(newline, "@");
        TBSkinTest = TBSkinTest.replaceAll(newline, "@");
        familyHistory = familyHistory.replaceAll(newline, "@");
        specialists = specialists.replaceAll(newline, "@");
        consultants = consultants.replaceAll(newline, "@");
        
        labs_s = labs_s.replaceAll(newline, "@");
        radiology_s = radiology_s.replaceAll(newline, "@");
        meds_s = meds_s.replaceAll(newline, "@");
        //JOptionPane.showMessageDialog(null, meds, "meds", JOptionPane.INFORMATION_MESSAGE);
        allergies_s = allergies_s.replaceAll(newline, "@");
        consultationNote_s = consultationNote_s.replaceAll(newline, "@");
        TBSkinTest_s = TBSkinTest_s.replaceAll(newline, "@");
        familyHistory_s = familyHistory_s.replaceAll(newline, "@");
        specialists_s = specialists_s.replaceAll(newline, "@");
        consultants_s = consultants_s.replaceAll(newline, "@");
        
        labs_er = labs_er.replaceAll(newline, "@");
        radiology_er = radiology_er.replaceAll(newline, "@");
        meds_er = meds_er.replaceAll(newline, "@");
        //JOptionPane.showMessageDialog(null, meds, "meds", JOptionPane.INFORMATION_MESSAGE);
        allergies_er = allergies_er.replaceAll(newline, "@");
        consultationNote_er = consultationNote_er.replaceAll(newline, "@");
        TBSkinTest_er = TBSkinTest_er.replaceAll(newline, "@");
        familyHistory_er = familyHistory_er.replaceAll(newline, "@");
        specialists_er = specialists_er.replaceAll(newline, "@");
        consultants_er = consultants_er.replaceAll(newline, "@");
        
        lastEdit = lastEdit.replaceAll(newline, ";");

        for (int i = 0; i < this.images.imageNames.length; i++)
        {
            images.imageNames[i] = images.imageNames[i].replace(';', '-');
            images.imageNames[i] = images.imageNames[i].replace(',', '-');
            if (images.imageNames[i].equals(""))
                images.imageNames[i] = " ";
        }

        for (int i = 0; i < this.images.labels.length; i++)
        {
            images.labels[i] = images.labels[i].replace(';', '-');
            images.labels[i] = images.labels[i].replace(',', '-');
            if (images.labels[i].equals(""))
                images.labels[i] = " ";
        }

    }
    
    /* Creates an XML entry with the parameters from this class */
    public void createXMLEntry()
    {
        File fileEntry = new File(filePath + "patientInfo_enc");
        //char[] c = null;
        if (fileEntry == null)
            return;

        this.sanitizeParams(); //sanitize these parameters before outputting them

        try
        {
            BufferedWriter output = new BufferedWriter(new FileWriter(fileEntry));

            output.write(first + delim + last + delim + gender + delim +
                        birthDate + delim + birthCity  + delim + birthHospital + newline);
            //output.newLine();
            output.write(insuranceProvider + delim + memberID + newline);
            //output.newLine();
            output.write(fatherName + delim + motherName + delim + phone + delim + email + delim+ emcall + newline);
            //output.newLine();
            output.write(eyeColor + delim + cordBlood + newline);
            //output.newLine();
            output.write(labs + newline);
            //output.newLine();
            output.write(radiology + newline);
            //output.newLine();
            output.write(meds + newline);
            //output.newLine();
            output.write(allergies + newline);
            //output.newLine();
            output.write(consultationNote + newline);
            //output.newLine();
            output.write(TBSkinTest + newline);
            //output.newLine();
            output.write(medicalHistory + newline);
            //output.newLine();
            output.write(mentalHealth + newline);
            //output.newLine();
            output.write(socialIssues + newline);
            //output.newLine();
            output.write(familyHistory + newline);
            //output.newLine();
            output.write(specialFeatures + newline);
            //output.newLine();

            output.write(primaryCare + newline);
            //output.newLine();
            output.write(specialists + newline);
            //output.newLine();
            output.write(consultants + newline);
            //output.newLine();
            output.write(lastEdit + newline);
            //output.newLine();

            String row1 = "";
            String row2 = "";
            String row3 = "";

            for (int i = 0; i < MAX_GROWTH_COLUMNS; i++)
            {
                String item1 = "";
                String item2 = "";
                String item3 = "";
                
                if (growth1[i] != null)
                    item1 = growth1[i].toString();
                if (growth2[i] != null)
                    item2 = growth2[i].toString();
                if (growth3[i] != null)
                    item3 = growth3[i].toString();
                
                row1 += item1;
                row2 += item2;
                row3 += item3;
                    
                if (i != (MAX_GROWTH_COLUMNS-1))
                {
                    row1 += ",";
                    row2 += ",";
                    row3 += ",";
                }
            }
            output.write(row1 + newline);
            output.write(row2 + newline);
            output.write(row3 + newline);

            String bmiRow = "";
            for (int i = 0; i < MAX_BMI_COLUMNS; i++)
            {
                String item = "";

                if (bmi[i] != null)
                    item = bmi[i].toString();

                bmiRow += item;

                if (i != (MAX_BMI_COLUMNS-1))
                {
                    bmiRow += ",";
                }
            }
            output.write(bmiRow + newline);

            //---------------------------------------------------------
            //Now, it changes the name of the image file to the caption
            //---------------------------------------------------------
            
            String dictoryPath = fileEntry.getPath().substring(0, fileEntry.getPath().length()-11);
            //the current dictory path (entry + user)
            for (int i=0; i < this.images.imageNames.length ; i++)
            {
                if (this.images.imageNames.length != this.images.labels.length)
                    break;
                
                if (!this.images.imageNames[i].equals(this.images.labels[i])) {
                    String file_tobeRenamed = dictoryPath + this.images.imageNames[i];                
                    File toBeRenamed = new File(file_tobeRenamed);
                    //file that name will be renamed!
                    
                    String tmp_newName = this.images.labels[i];
                    char c = '\0'; 
                    for (int j = 0; j < ILLEGAL_CHARACTERS.length; j++)
                        tmp_newName = removeChar(tmp_newName, ILLEGAL_CHARACTERS[j]);                    
                        //tmp_newName = tmp_newName.replace(ILLEGAL_CHARACTERS[j], c);
                        //removing all illegal characters
                    
                    int dotIdx = this.images.imageNames[i].lastIndexOf(".");
                    //index where the last "." is
                    String fileExt = this.images.imageNames[i].substring(dotIdx, this.images.imageNames[i].length());
                    String file_newFile = dictoryPath + tmp_newName + fileExt;
                    File newFile = new File(file_newFile);
                    //the new name!
                    
                    if ((tmp_newName != null) && (!tmp_newName.equals(" ")) && (!tmp_newName.equals(""))) { 
                    //check one more time, if the new file name is legal
                        if(toBeRenamed.renameTo(newFile))                  
                            this.images.imageNames[i] = tmp_newName + fileExt;                        
                    }
                    //renaming the file                   
                }
            }
            
            // Writing the image information
            String imageString = "";
            for (int i=0; i < this.images.imageNames.length ; i++)
            {
                if (this.images.imageNames.length != this.images.labels.length)
                {
                    imageString = "mismatch";
                    break;
                }

                imageString += this.images.imageNames[i]+","+this.images.labels[i];

                if (i != (this.images.imageNames.length-1))
                {
                    imageString += ";";
                }
            }
            output.write(imageString + newline);
                
            
            
            for (int i = 0; i < MAX_SCHED_ROWS; i++)
            {
                String schedSingleRow = "";
                for (int j = 0; j < MAX_SCHED_COLUMNS; j++)
                {
                    String item = "";

                    if (sched[i][j] != null)
                        item = sched[i][j];

                    schedSingleRow += item;

                    if (j != (MAX_SCHED_COLUMNS-1))
                    {
                        schedSingleRow += ",";
                    }
                }
                output.write(schedSingleRow + newline);
            }
            output.write(consultationNote_s + newline);
            output.write(consultationNote_er + newline);
            output.write("2.0"); //version info. 
            //note that the encryption is added >= 2.0
            
            output.close();
            
            FileInputStream fis = new FileInputStream(filePath + "patientInfo_enc");
            FileOutputStream fos = new FileOutputStream(fileName);
            try {
                encrypt(key, fis, fos);
            } 
            catch (Throwable t) {
                return;
            }
            
            //fileEntry.delete(); //deleting the non-encrypted file
            
        }
        catch (Exception ev)
        {
            ev.printStackTrace();
            return;
        }
    }

    //removes a character from string.
    public static String removeChar(String s, char c) {
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
    public static void encrypt(String key, InputStream is, OutputStream os) throws Throwable {
		encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, is, os);
    }

    public static void decrypt(String key, InputStream is, OutputStream os) throws Throwable {
            encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os);
    }

    public static void encryptOrDecrypt(String key, int mode, InputStream is, OutputStream os) throws Throwable {

            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = skf.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for SunJCE

            if (mode == Cipher.ENCRYPT_MODE) {
                    cipher.init(Cipher.ENCRYPT_MODE, desKey);
                    CipherInputStream cis = new CipherInputStream(is, cipher);
                    doCopy(cis, os);
            } else if (mode == Cipher.DECRYPT_MODE) {
                    cipher.init(Cipher.DECRYPT_MODE, desKey);
                    CipherOutputStream cos = new CipherOutputStream(os, cipher);
                    doCopy(is, cos);
            }
    }

    public static void doCopy(InputStream is, OutputStream os) throws IOException {
            byte[] bytes = new byte[64];
            int numBytes;
            while ((numBytes = is.read(bytes)) != -1) {
                    os.write(bytes, 0, numBytes);
            }
            os.flush();
            os.close();
            is.close();
    }
    
    //read already existing xml and fill in this classes parameters
    public void readXMLEntry()
    {
        try
        {
            List<String> list = new ArrayList<String>();
            String line;
            String[] attributes;
            String[] schedRow = null;
            int lineNumber = 0;
            boolean encrypted = false;
            
            //Decript the file
            FileInputStream fis2 = new FileInputStream(fileName);
            FileOutputStream fos2 = new FileOutputStream(filePath + "patientInfo_dec");
            
            try {
                decrypt(key, fis2, fos2);
                encrypted = true;
            } 
            catch (Throwable t) {
                encrypted = false;
            }
            
            FileReader reader = new FileReader(filePath + "patientInfo_dec");
            BufferedReader bufrdr = new BufferedReader(reader);
            line = bufrdr.readLine();
            while (line != null) {
                //lineNumber++;
                list.add(line);
                line = bufrdr.readLine();
            }
            bufrdr.close();
            reader.close();
            File file = new File(filePath + "patientInfo_dec");
            file.delete(); //deleting the decrypted file
            
            if (((list.size() != 74) && (list.size() != 76) && (list.size() != 77)) || !encrypted) //the file is NOT encrypted
            {
                list = new ArrayList<String>();
                reader = new FileReader(fileName);
                bufrdr = new BufferedReader(reader);
                line = bufrdr.readLine();
                while (line != null) {
                    //lineNumber++;
                    list.add(line);
                    line = bufrdr.readLine();
                }
                bufrdr.close();
                reader.close();                
            }
            
            String[] array = new String[list.size()];
            list.toArray(array);
            
            attributes = array[0].split(delim, 6);
            //if (attributes.length != 6) ;
            first = attributes[0];
            last = attributes[1];
            gender = attributes[2];
            birthDate = attributes[3];
            birthCity = attributes[4];
            birthHospital = attributes[5];

            attributes = array[1].split(delim, 2);
            //if (attributes.length != 2) continue;
            insuranceProvider = attributes[0];
            memberID = attributes[1];

            attributes = array[2].split(delim);
            //if (attributes.length != 4) continue;
            fatherName = attributes[0];
            motherName = attributes[1];
            phone = attributes[2];
            email= attributes[3];
            if(attributes.length==5)
            {
                emcall = attributes[4];
            }
            attributes = array[3].split(delim,2);
            //if (attributes.length != 2) continue;
               
            eyeColor = attributes[0];
            cordBlood = attributes[1];

            labs = array[4].replaceAll("@", newline);
            radiology = array[5].replaceAll("@", newline);
            meds = array[6].replaceAll("@", newline);
            //JOptionPane.showMessageDialog(null, meds, "meds2", JOptionPane.INFORMATION_MESSAGE);
            allergies = array[7].replaceAll("@", newline);
            consultationNote = array[8].replaceAll("@", newline);
            TBSkinTest = array[9].replaceAll("@", newline);
            medicalHistory = array[10].replaceAll("@", newline);
            mentalHealth = array[11].replaceAll("@", newline);
            socialIssues = array[12].replaceAll("@", newline);      
            familyHistory = array[13].replaceAll("@", newline);            
            specialFeatures = array[14].replaceAll("@", newline);
            primaryCare = array[15].replaceAll("@", newline);
            primaryCare = primaryCare.replace("#$", "@");
            specialists = array[16].replaceAll("@", newline);
            consultants = array[17].replaceAll("@", newline);            
            lastEdit = array[18].replaceAll(";", newline);

            String[] row1 = array[19].split(delim);
            //if (row1.length != MAX_GROWTH_COLUMNS) continue;
            for (int i = 0; i < row1.length ; i++) {
                if (row1[i].equals("") || row1[i].equals(" "))
                    this.growth1[i] = null;
                else
                    this.growth1[i] = Double.parseDouble(row1[i]);
            }

            String[] row2 = array[20].split(delim);
            //if (row2.length != MAX_GROWTH_COLUMNS) continue;
            for (int i = 0; i < row2.length ; i++) {
                if (row2[i].equals("") || row2[i].equals(" "))
                    this.growth2[i] = null;
                else
                    this.growth2[i] = Double.parseDouble(row2[i]);
            }

            String[] row3 = array[21].split(delim);
            //if (row3.length != MAX_GROWTH_COLUMNS) continue;
            for (int i = 0; i < row3.length ; i++) {
                if (row3[i].equals("") || row3[i].equals(" "))
                    this.growth3[i] = null;
                else
                    this.growth3[i] = Double.parseDouble(row3[i]);
            }

            String[] bmiRow = array[22].split(delim);
            //if (bmiRow.length != MAX_BMI_COLUMNS) continue;
            for (int i = 0; i < bmiRow.length ; i++) {
                if (bmiRow[i].equals("") || bmiRow[i].equals(" "))
                    this.bmi[i] = null;
                else
                    this.bmi[i] = Double.parseDouble(bmiRow[i]);
            }

            String[] pairs = array[23].split(";");
            String[] temp_imageNames = new String[pairs.length];
            String[] temp_labels = new String[pairs.length];
            for (int i=0; i < pairs.length; i++) {
                String[] pair = pairs[i].split(delim);
                if (pair.length != 2)
                    continue;
                temp_imageNames[i] = pair[0];
                temp_labels[i] = pair[1];
            }
            this.images.setLabels(temp_imageNames, temp_labels);


            int SCHED_STARTING_ROW = 24;
            //doing the following instead of writing dozens of empty case statements

            for (lineNumber = SCHED_STARTING_ROW; lineNumber < SCHED_STARTING_ROW + MAX_SCHED_ROWS; lineNumber++) {
                schedRow = array[lineNumber].split(delim);
            
                for (int j = 0; j < schedRow.length ; j++) {
                    if (schedRow[j].equals("") || schedRow[j].equals(" "))
                        this.sched[lineNumber-SCHED_STARTING_ROW][j] = null;
                    else {
                        if (schedRow[j].charAt(schedRow[j].length()-1) == ' ') //if last char is a space, remove it
                            this.sched[lineNumber-SCHED_STARTING_ROW][j] = schedRow[j].substring(0,schedRow[j].length()-1);
                        else
                            this.sched[lineNumber-SCHED_STARTING_ROW][j] = schedRow[j];
                    }
                }
            }
            
            /* Version 1 has the array length 50 and
             * Version 2 has the array length 68.
             * We create the following if-statement in order to make Version 1 and Version 2 compatible
             */
            if (array.length > 74) {
                consultationNote_s = array[74].replaceAll("@", newline);
                consultationNote_er = array[75].replaceAll("@", newline);
            }
                


            /*
            String line = null;
            while((line = inputBuffer.readLine()) != null) {
                lineNumber++;
                String[] attributes;
                //String[] texts;

                switch (lineNumber)
                {
                    case 1:
                        attributes = line.split(delim, 6);
                        if (attributes.length != 6) continue;
                        first = attributes[0];
                        last = attributes[1];
                        gender = attributes[2];
                        birthDate = attributes[3];
                        birthCity = attributes[4];
                        birthHospital = attributes[5];
                        break;
                    case 2:
                        attributes = line.split(delim, 2);
                        if (attributes.length != 2) continue;
                        insuranceProvider = attributes[0];
                        memberID = attributes[1];
                        break;
                    case 3:
                        attributes = line.split(delim,4);
                        if (attributes.length != 4) continue;
                        fatherName = attributes[0];
                        motherName = attributes[1];
                        phone = attributes[2];
                        email = attributes[3];
                        break;
                    case 4:
                        attributes = line.split(delim,2);
                        if (attributes.length != 2) continue;
                        eyeColor = attributes[0];
                        cordBlood = attributes[1];
                        break;
                    case 5:
                        labs = line.replace('@', '\n');
                        break;
                    case 6:
                        radiology = line.replace('@', '\n');
                        break;
                    case 7:
                        JOptionPane.showMessageDialog(null, line, "meds", JOptionPane.INFORMATION_MESSAGE);
                        meds = line.replace('@', '\n');
                        //for (int i = 0; i < 3; i++)
                            //meds += texts[i];
                        //JOptionPane.showMessageDialog(null, meds, "meds", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case 8:
                        allergies = line.replace('@', '\n');
                        //JOptionPane.showMessageDialog(null, allergies, "allergies", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case 9:
                        consultationNote = line.replace('@','\n');
                        break;
                    case 10:
                        TBSkinTest = line.replace('@','\n');
                        break;
                    case 11:
                        medicalHistory = line.replace('@','\n');
                        break;
                    case 12:
                        mentalHealth = line.replace('@', '\n');
                        break;
                    case 13:
                        socialIssues = line.replace('@', '\n');
                        break;
                    case 14:
                        familyHistory = line.replace('@','\n');
                        break;
                    case 15:
                        specialFeatures = line.replace('@','\n');
                        break;
                    case 16:
                        primaryCare = line.replace('@','\n');
                        break;
                    case 17:
                        specialists = line.replace('@','\n');
                        break;
                    case 18:
                        consultants = line.replace('@','\n');
                        break;
                    case 19:
                        lastEdit = line.replace('@','\n');
                        break;
                    case 20:
                        String[] row1 = line.split(delim);
                        if (row1.length != MAX_GROWTH_COLUMNS) continue;
                        for (int i = 0; i < row1.length ; i++)
                        {
                            if (row1[i].equals("") || row1[i].equals(" "))
                                this.growth1[i] = null;
                            else
                                this.growth1[i] = Double.parseDouble(row1[i]);
                        }
                        break;
                    case 21:
                        String[] row2 = line.split(delim);
                        if (row2.length != MAX_GROWTH_COLUMNS) continue;
                        for (int i = 0; i < row2.length ; i++)
                        {
                            if (row2[i].equals("") || row2[i].equals(" "))
                                this.growth2[i] = null;
                            else
                                this.growth2[i] = Double.parseDouble(row2[i]);
                        }
                        break;
                    case 22:
                        String[] row3 = line.split(delim);
                        if (row3.length != MAX_GROWTH_COLUMNS) continue;
                        for (int i = 0; i < row3.length ; i++)
                        {
                            if (row3[i].equals("") || row3[i].equals(" "))
                                this.growth3[i] = null;
                            else
                                this.growth3[i] = Double.parseDouble(row3[i]);
                        }
                        break;
                    case 23:
                        String[] bmiRow = line.split(delim);
                        if (bmiRow.length != MAX_BMI_COLUMNS) continue;
                        for (int i = 0; i < bmiRow.length ; i++)
                        {
                            if (bmiRow[i].equals("") || bmiRow[i].equals(" "))
                                this.bmi[i] = null;
                            else
                                this.bmi[i] = Double.parseDouble(bmiRow[i]);
                        }
                        break;
                    case 24:
                        String[] pairs = line.split(";");
                        String[] temp_imageNames = new String[pairs.length];
                        String[] temp_labels = new String[pairs.length];
                        for (int i=0; i < pairs.length; i++)
                        {
                            String[] pair = pairs[i].split(delim);
                            if (pair.length != 2)
                                continue;
                            temp_imageNames[i] = pair[0];
                            temp_labels[i] = pair[1];
                        }
                        this.images.setLabels(temp_imageNames, temp_labels);
                        break;
                    default:
                        break;
                }

                int SCHED_STARTING_ROW = 25;
                //doing the following instead of writing dozens of empty case statements
                if ((lineNumber >= SCHED_STARTING_ROW) && (lineNumber < SCHED_STARTING_ROW+MAX_SCHED_ROWS))
                {
                    String[] schedRow = line.split(delim);
                    if (schedRow.length != MAX_SCHED_COLUMNS) continue;
                    for (int j = 0; j < schedRow.length ; j++)
                    {
                        if (schedRow[j].equals("") || schedRow[j].equals(" "))
                            this.sched[lineNumber-SCHED_STARTING_ROW][j] = null;
                        else
                        {
                            if (schedRow[j].charAt(schedRow[j].length()-1) == ' ') //if last char is a space, remove it
                                this.sched[lineNumber-SCHED_STARTING_ROW][j] = schedRow[j].substring(0,schedRow[j].length()-1);
                            else
                                this.sched[lineNumber-SCHED_STARTING_ROW][j] = schedRow[j];
                        }
                    }
                }
            }
            inputBuffer.close();*/
        }
        catch (Exception ex)
        {
            //ex.printStackTrace(); //debug only
            return;
        }
    }

}
