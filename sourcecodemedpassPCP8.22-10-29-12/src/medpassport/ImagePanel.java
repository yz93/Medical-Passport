/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medpassport;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Hagop
 */
public class ImagePanel extends JPanel
{
    private BufferedImage img;
    public File imageFile;
    public int w = 339;
    public int h = 200;

    public ImagePanel(String img)
    {
        BufferedImage chosenImage = null;
        imageFile = new File(img);


        if (imageFile.exists())
        {
            try
            {
                chosenImage = ImageIO.read(imageFile);
                //chosenImage = new ImageIcon(img).getImage();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        setImage(chosenImage);
    }
    
    public ImagePanel(File imageFile)
    {
        BufferedImage chosenImage = null;
        if (imageFile.exists())
        {
            try
            {
                chosenImage = ImageIO.read(imageFile);
                //chosenImage = new ImageIcon(img).getImage();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        setImage(chosenImage);
    }
    
    public ImagePanel()
    {
        BufferedImage chosenImage = null;
    }

    public void setImage(BufferedImage img)
    {
        this.img = img;
        //Dimension size = new Dimension(img.getWidth(null),img.getHeight(null));
        this.setAllSizes(new Dimension(w,h));

    }

    public String getImageName() {
        return imageFile.getName();
    }

    public void setAllSizes(Dimension s)
    {
        this.w = s.width;
        this.h = s.height;

        setPreferredSize(s);
        setMinimumSize(s);
        setMaximumSize(s);
        setSize(s);
        setLayout(null);

    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(img,1,1,w,h,null);
    }

}