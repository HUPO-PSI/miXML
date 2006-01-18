package psidev.psi.mi.validator.client.gui.view.validator;

import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ValidatorMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Data to be displayed in a row of the table view.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ValidatorTableRow.java,v 1.1 2006/01/18 16:53:10 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public class ValidatorTableRow {

    ////////////////////////
    // instance variable

    private File file;

    private ValidationStatus status = ValidationStatus.NOT_STARTED;

    private int progress = 0;

    private Boolean open = false;

    private Collection<ValidatorMessage> validatorMessages = new ArrayList<ValidatorMessage>();

    private MessageLevel level;

    ///////////////////////////
    // Constructor

    public ValidatorTableRow( File file, MessageLevel level, boolean open ) {
        this( file, open );
        this.level = level;
    }

    public ValidatorTableRow( File file, boolean open ) {
        this( file );
        this.open = open;
    }

    public ValidatorTableRow( File file ) {
        this.file = file;
    }

    ////////////////////////////
    // Getters & Setters

    public Object get( int index ) {
        switch ( index ) {
            case 0:
                return file;

            case 1:
                return status;

            case 2:
                return getMessageCount();

            case 3:
                return open;

            default:
                throw new IllegalArgumentException( "index (" + index + ") can only be in range 0..3" );
        }
    }

    public void set( int index, Object object ) {

        if ( object == null ) {
            throw new NullPointerException();
        }

        switch ( index ) {
            case 0:
                throw new IllegalArgumentException( "File cannot be set twice." );

            case 1:
                if ( object instanceof ValidationStatus ) {
                    setStatus( (ValidationStatus) object );
                } else {
                    throw new ClassCastException( object.getClass().getName() );
                }
                break;

            case 3:
                if ( object instanceof Boolean ) {
                    setOpen( (Boolean) object );
                } else {
                    throw new ClassCastException( object.getClass().getName() );
                }
                break;

            default:
                throw new IllegalArgumentException( "index (" + index + ") can only be in range 0..3" );
        }
    }

    public File getFile() {
        return file;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public void setStatus( ValidationStatus status ) {
        this.status = status;
    }

    public int getMessageCount() {
        return getFilteredMessages().size();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen( boolean open ) {
        this.open = open;
    }

    public void setMessages( Collection<ValidatorMessage> validatorMessages ) {
        this.validatorMessages.addAll( validatorMessages );
    }

    public Collection<ValidatorMessage> getMessages() {
        return new ArrayList<ValidatorMessage>( validatorMessages );
    }

    /**
     * Return all messages taking into account the log level set by the user.
     *
     * @return a collection of ValidatorMessages or an empty collection.
     */
    public Collection<ValidatorMessage> getFilteredMessages() {
        ArrayList<ValidatorMessage> messages = new ArrayList<ValidatorMessage>( this.validatorMessages );
        for ( Iterator<ValidatorMessage> iterator = messages.iterator(); iterator.hasNext(); ) {
            ValidatorMessage msg = iterator.next();

            if ( level.isHigher( msg.getLevel() ) ) {
                iterator.remove(); // filter it
            }
        }
        return messages;
    }

    ////////////////////////////
    // Object's overload.

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append( "TableRow" );
        sb.append( "{file=" ).append( file );
        switch ( status ) {
            case IN_PROGRESS:
                sb.append( ", status=" ).append( status ).append( " (" ).append( progress ).append( "%)" );
                break;
            default:
                sb.append( ", status=" ).append( status );
        }
        sb.append( ", status=" ).append( status );
        sb.append( ", progress=" ).append( progress );
        sb.append( ", messageCount=" ).append( validatorMessages.size() );
        sb.append( ", open=" ).append( open );
        sb.append( '}' );
        return sb.toString();
    }

    /**
     * Equals only involved the file (never null).
     *
     * @param o
     *
     * @return true if the file held are the same.
     */
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final ValidatorTableRow row = (ValidatorTableRow) o;

        if ( !file.equals( row.file ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return file.hashCode();
    }
}