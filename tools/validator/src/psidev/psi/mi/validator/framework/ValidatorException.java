package psidev.psi.mi.validator.framework;

/**
 * <b> Exception specific to the validator framework. </b>.
 *
 * @author Matthias Oesterheld
 * @version $Id: ValidatorException.java,v 1.1 2006/01/18 16:57:59 skerrien Exp $
 * @since 2006-01-04
 */
public class ValidatorException extends Exception {

    public ValidatorException( String message ) {
        super( message );
    }

    public ValidatorException( String message, Throwable cause ) {
        super( message, cause );
    }
}