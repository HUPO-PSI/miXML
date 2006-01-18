package psidev.psi.mi.validator.client.gui.view.messages;

import psidev.psi.mi.validator.framework.ValidatorMessage;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Collection;

/**
 * TableDemo is just like SimpleTableDemo, except that it uses a custom TableModel.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ReportTable.java,v 1.1 2006/01/18 16:53:10 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public class ReportTable extends JPanel {

    ///////////////////////////////
    // Constructor

    public ReportTable( Collection<ValidatorMessage> validatorMessages ) {
        super( new GridLayout( 1, 0 ) );

        ReportTableModel model = new ReportTableModel( validatorMessages );

        JTable table = new JTable( model );
        table.setPreferredScrollableViewportSize( new Dimension( 900, 300 ) );

        // size of the first column is fixed to 60
        int vColIndex = 0;
        int width = 60;
        TableColumn col = table.getColumnModel().getColumn( vColIndex );
        col.setMinWidth( width );
        col.setMaxWidth( width );
        col.setPreferredWidth( width );

        table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane( table );

        //Add the scroll pane to this panel.
        add( scrollPane );
    }
}