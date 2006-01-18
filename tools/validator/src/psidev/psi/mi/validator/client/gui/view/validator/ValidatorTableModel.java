package psidev.psi.mi.validator.client.gui.view.validator;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The data model allowing to display the result of validator runs.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ValidatorTableModel.java,v 1.1 2006/01/18 16:53:10 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public class ValidatorTableModel extends AbstractTableModel {

    private boolean DEBUG = false;

    private String[] columnNames = { "Filename",
            "Validation status",
            "Message count",
            "Open report upon completion" };

    //////////////////////////////
    // Data Handling

    /**
     * Data holder for the rows of the table
     */
    private List<ValidatorTableRow> data = new ArrayList<ValidatorTableRow>();


    public void addRow( ValidatorTableRow row ) {
        data.add( row );
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName( int col ) {
        return columnNames[ col ];
    }

    public Object getValueAt( int row, int col ) {
        ValidatorTableRow tableRow = (ValidatorTableRow) data.get( row );
        return tableRow.get( col );
    }

    public ValidatorTableRow getTableRowAt( int row ) {
        return (ValidatorTableRow) data.get( row );
    }

    /**
     * JTable uses this method to determine the default renderer/ editor for each cell.  If we didn't implement this
     * method, then the last column would contain text ("true"/"false"), rather than a check box.
     */
    public Class getColumnClass( int c ) {
        return getValueAt( 0, c ).getClass();
    }

    /**
     * Don't need to implement this method unless your table's editable.
     */
    public boolean isCellEditable( int row, int col ) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if ( col < 3 ) {
            return false;
        } else {
            return true; // only 'open' is editable
        }
    }

    /**
     * Don't need to implement this method unless your table's data can change.
     */
    public void setValueAt( Object value, int row, int col ) {
        if ( DEBUG ) {
            System.out.println( "Setting value at " + row + "," + col
                                + " to " + value
                                + " (an instance of "
                                + value.getClass() + ")" );
        }

        ValidatorTableRow tableRow = (ValidatorTableRow) data.get( row );
        tableRow.set( col, value );

        fireTableCellUpdated( row, col );

        if ( DEBUG ) {
            System.out.println( "New value of data:" );
            printDebugData();
        }
    }

    private void printDebugData() {

        for ( Iterator<ValidatorTableRow> iterator = data.iterator(); iterator.hasNext(); ) {
            ValidatorTableRow tableRow = iterator.next();
            System.out.println( tableRow );
        }

        System.out.println( "--------------------------" );
    }

    public int getIndexOfRow( ValidatorTableRow aRow ) {

        for ( int i = 0; i < data.size(); i++ ) {
            ValidatorTableRow row = (ValidatorTableRow) data.get( i );
            if ( row.equals( aRow ) ) {
                return i;
            }
        }

        throw new IllegalArgumentException();
    }
}