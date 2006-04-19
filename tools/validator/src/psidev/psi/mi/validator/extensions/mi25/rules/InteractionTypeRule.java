package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.CvType;
import psidev.psi.mi.validator.extensions.mi25.model.InteractionElementType;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.*;

/**
 * <b> -- Short Description -- </b>.
 * <p/>
 * -- Description of Class goes here -- </p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: InteractionTypeRule.java,v 1.2 2006/04/19 12:24:31 luisa_montecchi Exp $
 * @since 04.01.2006; 17:22:27
 */
public class InteractionTypeRule extends Mi25Rule {


    public InteractionTypeRule( Map ontologies ) {
        super( ontologies );
        setName("Interaction Type Rule");
        setDescription("Checks that each interaction has a CV interaction Type (not mandatory in PSI)");
        addTip("It is recomended to provide a CV term for interaction type, see  in the psi-mi25.obo the list of children terms of " + Mi25Ontology.InteractionTypeRoot +".");
    }

    public Collection<ValidatorMessage> check( Object jaxbObject ) throws ValidatorException {
        checkExpandedFormat(jaxbObject);

        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        Mi25Ontology ont = super.getMiOntology();

        if( interaction.getInteractionType().isEmpty() ) {
             Mi25Context context = new Mi25Context();
             context.setInteractionId( interactionId );
             messages.add( new ValidatorMessage(" CV interaction type not specified in this interaction)",
                                                    MessageLevel.WARN,
                                                    context,
                                                    this ) );
        }

        for ( Object o : interaction.getInteractionType() ) {
            CvType interactionType = (CvType) o;
            String interactionTypeId = interactionType.getXref().getPrimaryRef().getId();

            OntologyTerm term = ont.search( interactionTypeId );
            if ( term == null ) {

                Mi25Context context = new Mi25Context();
                context.setInteractionId( interactionId );
                messages.add( new ValidatorMessage("Invalid MI identifier for CV interaction Type (" + interactionTypeId + ")",
                                                    MessageLevel.ERROR,
                                                    context,
                                                    this ) );
            } else {

                OntologyTerm root = ont.getInteractionTypeRoot();
                if ( !term.isChildOf( root ) ) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId( interactionId );
                    messages.add( new ValidatorMessage( "Term does not belong to CV interaction type (" + interactionTypeId  + ")",
                                                        MessageLevel.ERROR,
                                                        context,
                                                        this ) );
                }
            }
        }

        return messages;
    }
}