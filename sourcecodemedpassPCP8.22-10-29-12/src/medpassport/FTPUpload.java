/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medpassport;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author Jihyoung
 */
/*
class FTPclientConn {
    public final String host;
    public final String user;
    protected final String password;
    protected URLConnection urlc;

    public FTPclientConn(String _host, String _user, String _password) {
        host= _host;  
        user= _user;
        password= _password;
        urlc = null;
    }

    protected URL makeURL(String targetfile) throws MalformedURLException  {
        if (user== null)
            return new URL("ftp://"+ host+ "/"+ targetfile+ ";type=i");
        else
            return new URL("ftp://"+ user+ ":"+ password+ "@"+ host+ "/"+ targetfile+ ";type=i");
    }

    protected InputStream openDownloadStream(String targetfile) throws Exception {
        URL url= makeURL(targetfile);
        urlc = url.openConnection();
        InputStream is = urlc.getInputStream();
        return is;
    }

    protected OutputStream openUploadStream(String targetfile) throws Exception {
        URL url= makeURL(targetfile);
        urlc = url.openConnection();
        OutputStream os = urlc.getOutputStream();
        return os;
    }

    protected void close() {
        urlc= null;
    }
}
*/
public class FTPUpload {
    public final String host;
    public final String user;
    protected final String password;
    protected URLConnection urlc;
    public final String[] localfile;
    public final String[] targetfile;

    public FTPUpload(String _host, String _user, String _password, String[] _localfile, String[] _targetfile) {
        host = _host;
        user = _user;
        password = _password;
        localfile = Arrays.copyOf(_localfile, _localfile.length);
        targetfile = Arrays.copyOf(_targetfile, _targetfile.length);
        //JOptionPane.showMessageDialog(null, targetfile[3], "Field required", JOptionPane.ERROR_MESSAGE);

        doit();
    }
/*
    public FTPUpload(String _host, String _user, String _password, String _file) {
        host = _host;
        user = _user;
        password = _password;
        localfile = _file;
        targetfile = _file;
        doit();
    }
*/
    protected URL makeURL(String target) throws MalformedURLException  {
        if (user== null)
            return new URL("ftp://"+ host+ "/"+ target + ";type=i");
        else
            return new URL("ftp://"+ user+ ":"+ password+ "@"+ host+ "/"+ target + ";type=i");
    }

    protected InputStream openDownloadStream(String target) throws Exception {
        URL url= makeURL(target);
        urlc = url.openConnection();
        InputStream is = urlc.getInputStream();
        return is;
    }

    protected OutputStream openUploadStream(String target) throws Exception {
        URL url= makeURL(target);

        urlc = url.openConnection();
        OutputStream os = urlc.getOutputStream();

        return os;
    }

    protected void close() {
        urlc = null;
    }

    protected void doit() {
        try {
            OutputStream[] os = null;
            
            for (int i = 0; i < targetfile.length; i++)
                os[i] = this.openUploadStream(targetfile[i]);
            //FileInputStream is =  new FileInputStream(localfile);
            FileInputStream[] is =  new FileInputStream[localfile.length];
            
            for (int i = 0; i < is.length; i++) {
                byte[] buf= new byte[16384];
                int c;

                while (true) {
                    c = is[i].read(buf);

                    if (c<= 0)
                        break;

                    os[i].write(buf, 0, c);
                }

                os[i].close();
                is[i].close();
                this.close(); // section 3.2.5 of RFC1738
            }
        } catch (Exception E) {
            System.err.println(E.getMessage());
            E.printStackTrace();
        }
    }
}
