package psidev.psi.mi.validator.client.gui.view.validator;

/**
 * Define the different state of a validator.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ValidationStatus.java,v 1.1 2006/01/18 16:53:10 skerrien Exp $
 * @since <pre>17-Jan-2006</pre>
 */
public enum ValidationStatus {

    NOT_STARTED( "Not Started" ),
    IN_PROGRESS( "In progress" ),
    FAILED( "Failed" ),
    COMPLETED("Completed");

    private String name;

    ValidationStatus( String name ) {
       this.name = name;
   }

    public String getName() {
        return name;
    }
}