package psidev.psi.mi.validator.client.gui.view.validator;

import psidev.psi.mi.validator.client.gui.view.messages.ReportFrame;
import psidev.psi.mi.validator.framework.Validator;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * JTable displaying the result of a Validator run. <br/>
 * The adapted source code was found at: http://iharder.sourceforge.net/filedrop/
 *
 * @author Robert Harder, Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ValidatorTable.java,v 1.1 2006/01/18 16:53:10 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public class ValidatorTable extends JPanel {

    ///////////////////////////////
    // Constants

    private static final boolean DEBUG = false;

    //////////////////////////////
    // Instance variables

    private ValidatorTableModel model;

    private Validator validator;

    ///////////////////////////////
    // Constructor

    public ValidatorTable() {
        super( new GridLayout( 1, 0 ) );

        model = new ValidatorTableModel();

        // setup tooltip for validation report
        final JTable table = new JTable( model ) {
            public Component prepareRenderer( TableCellRenderer renderer, int rowIndex, int vColIndex ) {

                Component c = super.prepareRenderer( renderer, rowIndex, vColIndex );

                ValidatorTableRow tableRow = model.getTableRowAt( rowIndex );
                if ( tableRow.getFilteredMessages().size() > 0 ) {

                    if ( c instanceof JComponent ) {
                        JComponent jc = (JComponent) c;

                        jc.setToolTipText( "Click on the cell to open validation report" );
                    }
                }
                return c;
            }
        };

        // setup cell renderer for error level
        // if there are any messages, then display the cell in red, otherwise in green.
        table.setDefaultRenderer( Integer.class, new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent( JTable table,
                                                            Object value,
                                                            boolean isSelected,
                                                            boolean hasFocus,
                                                            int row,
                                                            int column ) {
                Component cell = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

                if ( value instanceof Integer ) {

                    // set alignement to center
                    setHorizontalAlignment( JTextField.CENTER );

                    Integer amount = (Integer) value;
                    if ( amount.intValue() > 0 ) {
                        cell.setBackground( new Color( 238, 0, 0 ) ); // dark red
                    } else if ( amount.intValue() == 0 ) {
                        cell.setBackground( new Color( 0, 134, 100 ) ); // dark green
                    }

                    cell.setForeground( Color.white );
                }

                return cell;
            }
        } );

        table.setPreferredScrollableViewportSize( new Dimension( 700, 150 ) );

        // Enable cell selection in that table
        table.setColumnSelectionAllowed( false );
        table.setRowSelectionAllowed( false );
        table.setCellSelectionEnabled( true );
        table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        // setup selection listener
        table.addMouseListener( new MouseAdapter() {
            public void mouseClicked
                    ( MouseEvent
                            e ) {
                // get row index
                int row = table.getSelectedRow();
                // get column index
                int col = table.getSelectedColumn();

                if ( DEBUG ) {
                    System.out.println( "MouseListener[ row:" + row + " col:" + col + " ]" );
                }

                if ( col == 2 ) {
                    // open a report frame when there are more than 0 message.
                    Integer count = (Integer) model.getValueAt( row, col );
                    if ( count > 0 ) {
                        // open report
                        ValidatorTableRow tableRow = model.getTableRowAt( row );
                        JFrame reportFrame = new ReportFrame( tableRow.getFile().getAbsolutePath(), tableRow.getFilteredMessages() );
                        reportFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
                    }
                }
            }
        } );

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane( table );

        //Add the scroll pane to this panel.
        add( scrollPane );
    }

    public ValidatorTable( Validator validator ) {
        this();
        this.validator = validator;
    }

    public void addTableRow( ValidatorTableRow row ) {
        model.addRow( row );

        // refresh the table content
        updateRow( model.getRowCount() );

        // Start validation if it has not been done yet.
        if ( row.getStatus() == ValidationStatus.NOT_STARTED ) {

            row.setStatus( ValidationStatus.IN_PROGRESS );
            // refresh row in the view
            updateRow( row );

            try {

                FileInputStream f = new FileInputStream( row.getFile() );
                Collection<ValidatorMessage> validatorMessages = validator.validate( f );

                if ( DEBUG ) {
                    System.out.println( validatorMessages.size() + " messages found." );
                }

                row.setMessages( validatorMessages );

                row.setStatus( ValidationStatus.COMPLETED );
                // refresh row in the view
                updateRow( row );

                if ( row.isOpen() && row.getFilteredMessages().size() > 0 ) {
                    // if open requested and any message, then open report.
                    JFrame reportFrame = new ReportFrame( row.getFile().getAbsolutePath(), validatorMessages );
                    reportFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
                }

            } catch ( FileNotFoundException e ) {
                // refresh row in the view
                row.setStatus( ValidationStatus.FAILED );
                updateRow( row );
                e.printStackTrace();
            } catch ( ValidatorException e ) {
                // refresh row in the view
                row.setStatus( ValidationStatus.FAILED );
                updateRow( row );
                e.printStackTrace();
            } catch ( Exception e ) {
                row.setStatus( ValidationStatus.FAILED );
                updateRow( row );
                e.printStackTrace();
            }
        }
    }

    public void updateRow( int index ) {
        // refresh the table content (a specific row)
        model.fireTableRowsInserted( index, index );
    }

    public int getIndexOfRow( ValidatorTableRow row ) {
        return model.getIndexOfRow( row );
    }

    public void updateRow( ValidatorTableRow row ) {
        if ( DEBUG ) {
            System.out.println( "=====================================================================" );
            System.out.println( "Updating row for file: " + row.getFile().getAbsolutePath() );
            System.out.println( row );
        }
        updateRow( model.getIndexOfRow( row ) );
    }
}