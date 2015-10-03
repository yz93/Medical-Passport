/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medpassport;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Hagop
 */
public class ItemList implements ListModel
{
    private int size = 0;
    private int num_el = 0;
    public String[] items;


    public ItemList(int s)
    {
        this.size = s;
        this.items = new String[s];
    }

    public int addItem(String item)
    {
        if (this.size == this.num_el)
            return -1;

        this.items[num_el] = item;
        this.num_el++;

        return 0;
    }

    public void addListDataListener(ListDataListener l)
    {
    
    }
    public String getElementAt(int index)
    {
        return items[index];
    }
    public int getSize()
    {
        return this.num_el;
    }
    public void remove(int index) {
        if (index == num_el - 1) {
            items[index] = "";
            num_el--;
        }
        else {
            for (int i = index; i < num_el; i++) {
                items[index] = items[index + 1];
            }
            items[num_el - 1] = "";
            num_el--;
        }
    }

    public int find(String target) {
        int index = 0;

        for (int i = 0; i < num_el; i++)
            if (items[i].equals(target))
                index = i;

        return index;
    }
    
    public void removeListDataListener(ListDataListener l)
    {

    }

}
