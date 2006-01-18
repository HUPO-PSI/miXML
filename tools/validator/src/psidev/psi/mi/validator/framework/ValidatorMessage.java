package psidev.psi.mi.validator.framework;

/**
 * The definition of a Validator message.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ValidatorMessage.java,v 1.1 2006/01/18 16:57:59 skerrien Exp $
 * @since <pre>26-Dec-2005</pre>
 */
public class ValidatorMessage {

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    ///////////////////////////////
    // Instance variables

    /**
     * the message.
     */
    private final String message;

    /**
     * Level of the message. it qualifies the severity of the error.
     */
    private final MessageLevel level;

    /**
     * Context supposed to help the user to figure out where is the error coming from.
     */
    private final Context context;

    /**
     * The Rule that generated that message.
     */
    private final Rule rule;

    /////////////////////////////
    // Constructor

    public ValidatorMessage( String message, MessageLevel level, Context context, Rule rule ) {

        if ( message == null ) {
            throw new IllegalArgumentException( "A message must not be null when creating a ValidatorMessage." );
        }
        this.message = message;

        if ( level == null ) {
            throw new IllegalArgumentException( "A message level must not be null when creating a ValidatorMessage." );
        }
        this.level = level;

        if ( context == null ) {
            throw new IllegalArgumentException( "A context must not be null when creating a ValidatorMessage." );
        }
        this.context = context;

        if ( rule == null ) {
            throw new IllegalArgumentException( "A rule must not be null when creating a ValidatorMessage." );
        }
        this.rule = rule;
    }

    //////////////////////////////
    // Getters

    public String getMessage() {
        return message;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public Context getContext() {
        return context;
    }

    public Rule getRule() {
        return rule;
    }

    ///////////////////////////
    // Object's overload.

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final ValidatorMessage that = (ValidatorMessage) o;

        if ( !context.equals( that.context ) ) {
            return false;
        }
        if ( !message.equals( that.message ) ) {
            return false;
        }
        if ( !rule.equals( that.rule ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = message.hashCode();
        result = 29 * result + context.hashCode();
        result = 29 * result + rule.hashCode();
        return result;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append( level ).append( ": " ).append( message ).append( NEW_LINE );
        sb.append( context ).append( NEW_LINE );
        sb.append( rule );
        return sb.toString();
    }
}