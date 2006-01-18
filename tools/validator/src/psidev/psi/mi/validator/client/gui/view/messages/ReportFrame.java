package psidev.psi.mi.validator.client.gui.view.messages;

import psidev.psi.mi.validator.framework.ValidatorMessage;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * This class makes it easy to drag and drop files from the operating system to a Java program. Any
 * <tt>java.awt.Component</tt> can be dropped onto, but only <tt>javax.swing.JComponent</tt>s will indicate the drop
 * event with a changed border.
 * <p/>
 * To use this class, construct a new <tt>FileDrop</tt> by passing it the target component and a <tt>Listener</tt> to
 * receive notification when file(s) have been dropped. Here is an example:
 * <p/>
 * <code><pre>
 *      JPanel myPanel = new JPanel();
 *      new FileDrop( myPanel, new FileDrop.Listener()
 *      {   public void filesDropped( java.io.File[] files )
 *          {
 *              // handle file drop
 *              ...
 *          }   // end filesDropped
 *      }); // end FileDrop.Listener
 * </pre></code>
 * <p/>
 * You can specify the border that will appear when files are being dragged by calling the constructor with a
 * <tt>javax.swing.border.Border</tt>. Only <tt>JComponent</tt>s will show any indication with a border.
 * <p/>
 * You can turn on some debugging features by passing a <tt>PrintStream</tt> object (such as <tt>System.out</tt>) into
 * the full constructor. A <tt>null</tt> value will result in no extra debugging information being output.
 * <p/>
 * <p/>
 * <p>I'm releasing this code into the Public Domain. Enjoy. </p> <p><em>Original author: Robert Harder,
 * rharder@usa.net</em></p>
 *
 * @author Robert Harder, Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ReportFrame.java,v 1.1 2006/01/18 16:53:10 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public class ReportFrame extends JFrame {

    //////////////////////////////
    // Constructor

    public ReportFrame( String filename, Collection<ValidatorMessage> validatorMessages ) throws HeadlessException {

        super();

        if ( filename == null ) {
            throw new NullPointerException();
        }

        if ( validatorMessages.isEmpty() ) {
            // display a message: Nothing to be displayed.
        }

        setTitle( "Validator Report - " + filename );

        // build the view
        ReportTable reportTable = new ReportTable( validatorMessages );

        reportTable.setOpaque( true ); //content panes must be opaque
        this.setContentPane( reportTable );

        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        setBounds( 200, 200, 300, 400 );
        setSize( new Dimension( 700, 200 ) );
        pack();
        setVisible( true );
    }
}