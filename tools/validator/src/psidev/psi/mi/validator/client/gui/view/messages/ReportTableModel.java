package psidev.psi.mi.validator.client.gui.view.messages;

import psidev.psi.mi.validator.framework.ValidatorMessage;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Describe the content of the JTable.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ReportTableModel.java,v 1.1 2006/01/18 16:53:10 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public class ReportTableModel extends AbstractTableModel {

    private java.util.List<ValidatorMessage> validatorMessages;

    /**
     * Column titles
     */
    private String[] columnNames = { "Type", "Context", "Message", "Tips" };

    ///////////////////////////
    // Constructor

    public ReportTableModel( Collection<ValidatorMessage> validatorMessages ) {
        this.validatorMessages = new ArrayList<ValidatorMessage>( validatorMessages );
    }

    /////////////////////////////////////////////////////
    // AbstractTableModel abstract methods definition

    public int getRowCount() {
        return validatorMessages.size();
    }

    public int getColumnCount() {
        return 4;
    }

    public Object getValueAt( int rowIndex, int columnIndex ) {

        ValidatorMessage msg = validatorMessages.get( rowIndex );

        switch ( columnIndex ) {
            case 0:
                return msg.getLevel();

            case 1:
                return msg.getContext();

            case 2:
                return msg.getMessage();

            case 3:
                if ( msg.getRule() != null ) {
                    StringBuffer sb = new StringBuffer( 200 );
                    for ( Iterator<String> iterator = msg.getRule().getHowToFixTips().iterator(); iterator.hasNext(); )
                    {
                        sb.append( iterator.next() );
                        if ( iterator.hasNext() ) {
                            sb.append( "\n" );
                        }
                    }
                    return sb.toString();
                } else {
                    return "none";
                }

            default:
                throw new IndexOutOfBoundsException( columnIndex + " not in 0..2" );
        }
    }

    /////////////////////////////////////
    // AbstractTableModel overload

    public String getColumnName( int col ) {
        return columnNames[ col ];
    }

    public ValidatorMessage getMessageAt( int index ) {
        return validatorMessages.get( index );
    }
}