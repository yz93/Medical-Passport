
package medpassport;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Hagop
 */
public class DatabaseList
{
    public ItemList list;
    public String databaseDir;

    FileFilter dirFilter = new FileFilter() {
        public boolean accept(File dir) { return dir.isDirectory(); }
    };
    /*
    FilenameFilter xmlFileNameFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return (name.toLowerCase().endsWith(".xml")); }
    };
     */

    public DatabaseList(String curDir)
    {
        this.list = new ItemList(1);
        this.databaseDir = curDir + File.separator + "Entries";

        File entriesDir = new File(databaseDir);

        if (!entriesDir.exists())
            entriesDir.mkdir();

    }

    public void populatePatients()
    {
        //TODO: Eventually replace "." with a smarter location for the database
        File currentDir = new File(databaseDir);
        File[] subDirs = currentDir.listFiles(dirFilter);

        if (subDirs != null)
        {
            list = new ItemList(subDirs.length);

            for (int i = 0; i < subDirs.length; i++)
            {
                list.addItem(subDirs[i].getName());
            }
        }
    }

    /*
    public void populateEntries(String patientName)
    {
        File currentDir = new File(databaseDir + File.separator + patientName);
        File[] subDirs = currentDir.listFiles(xmlFileNameFilter);

        if (subDirs != null)
        {
            list = new ItemList(subDirs.length);

            for (int i = 0; i < subDirs.length; i++)
            {
                list.addItem(subDirs[i].getName());
            }
        }
    }
    */

    public ItemList getList()
    {
        return this.list;
    }

}
