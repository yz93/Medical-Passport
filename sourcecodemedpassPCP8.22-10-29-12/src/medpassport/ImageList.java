/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medpassport;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Hagop
 */
public class ImageList
{
    public int length = 0, click = 0;
    public String[] imageNames;
    public String[] labels;

    private String entryLocation;
    private File[] imageFiles;
    private int pointer = 0;
    private final String FIRST_IMAGE_NAME = "mainImage.jpg";

    FilenameFilter jpegFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return (!name.toLowerCase().endsWith("chart.png"))
                    && (name.toLowerCase().endsWith(".jpg")
                        || name.toLowerCase().endsWith(".jpeg")
                        || name.toLowerCase().endsWith(".png")
                        || name.toLowerCase().endsWith(".bmp")); }
    };

    public ImageList(String entryLoc)
    {
        this.entryLocation = entryLoc;
        File imageDir = new File(entryLocation);
        this.imageFiles = imageDir.listFiles(jpegFilter);

        if (imageFiles != null)
        {
            this.length = imageFiles.length;
            this.imageNames = new String[length];
            this.labels = new String[length];

            for (int i=0; i < imageFiles.length; i++)
            {
                imageNames[i] = imageFiles[i].getName();
                labels[i] = "No Description.";
            }
        }
        else
            this.length = -1; //directory has no images
    }

    //Given a list of image names and labels, it will update this data structures
    //labels based on which image names are in common
    public void setLabels(String[] storedImageNames, String[] storedLabels)
    {
        //so its O(n^2), no big deal, there won't be that many images per patient
        for (int i = 0; i < storedImageNames.length; i++)
        {
            int itemIndex = -1;
            for (int j = 0; j < this.imageNames.length; j++)
            {
                if (this.imageNames[j].equals(storedImageNames[i]))
                    itemIndex = j;
            }
            if (itemIndex >= 0)
                this.labels[itemIndex] = storedLabels[i];
        }
    }

    public void setCurrentLabel(String label)
    {
        if ((pointer >= 0) && (pointer < length))
            this.labels[pointer] = label;

    }

    public int getLength() {
        return this.length;
    }

    public void add(String img, String lab) {
        int incr = this.length - 1;
        this.imageNames[incr] = img;
        this.labels[incr] = lab;
        //this.length++;
    }
    /*
    public void add1(String img, String lab) {
        int incr = this.length;
        this.imageNames[incr] = img;
        this.labels[incr] = lab;
        //this.length++;
    }*/
/*
    public void add(String img, String lab, int index) {
        //int incr = this.length - 1;
        this.imageNames[index] = img;
        this.labels[index] = lab;
    }
*/
    public String[] getFirst()
    {
        if (length <= 0)
        {
            String[] badReturn = {""," "};
            return badReturn;
        }

        pointer = 0;

        for (int i = 0; i < length; i++)
        {
            if (imageNames[i].equals(FIRST_IMAGE_NAME))
            {
                pointer = i;
                break;
            }
        }
        
        String[] retVal = {imageNames[pointer],labels[pointer]};
        return retVal;
    }

    public String[] find(String name) {
        pointer = 0;

        for (int i = 0; i < length; i++)
        {
            if (imageNames[i].equals(name))
            {
                pointer = i;
                break;
            }
        }
        labels[pointer] = "No Description.";
        String[] retVal = {imageNames[pointer],labels[pointer]};
        return retVal;
    }

    public String[] getNext()
    {
        if (length <= 0)
        {
            String[] badReturn = {""," "};
            return badReturn;
        }

        if (pointer == (length - 1))/*
            if (click == 0 && length >= 1)
                pointer = 1;
            else*/
                pointer = 0;
        else
            pointer++;

        String[] retVal = {imageNames[pointer],labels[pointer]};
        return retVal;
    }

    public String[] getPrev()
    {
        if (length <= 0)
        {
            String[] badReturn = {""," "};
            return badReturn;
        }
        
        if (pointer == 0)
            //if (click == 0 && length >= 2)
                //pointer = length - 2;
            //else
                pointer = length - 1;
        else
            pointer--;

        click++;
        String[] retVal = {imageNames[pointer],labels[pointer]};
        return retVal;
    }

}
