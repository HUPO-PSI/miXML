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
 * @version $Id: InteractionTypeCheckRule.java,v 1.1 2006/01/18 16:55:44 skerrien Exp $
 * @since 04.01.2006; 17:22:27
 */
public class InteractionTypeCheckRule extends Mi25Rule {

    public InteractionTypeCheckRule( Map ontologies ) {
        super( ontologies );
    }

    public Collection<ValidatorMessage> check( Object jaxbObject ) throws ValidatorException {
        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        Mi25Ontology ont = super.getMiOntology();

        for ( Object o : interaction.getInteractionType() ) {
            CvType interactionType = (CvType) o;

            String interactionTypeId = interactionType.getXref().getPrimaryRef().getId();

            OntologyTerm term = ont.search( interactionTypeId );
            if ( term == null ) {

                Mi25Context context = new Mi25Context();
                context.setInteractionId( interactionId );

                messages.add( new ValidatorMessage( "No CV term",
                                                    MessageLevel.ERROR,
                                                    context,
                                                    this ) );
            } else if ( ! ont.isValidIdentifier( interactionTypeId ) ) {

                Mi25Context context = new Mi25Context();
                context.setInteractionId( interactionId );

                messages.add( new ValidatorMessage( "Invalid interaction type MI identifier format (" + term.getId() + ")",
                                                    MessageLevel.ERROR,
                                                    context,
                                                    this ) );

            } else {

                OntologyTerm root = ont.getInteractionTypeRoot();
                if ( !term.isChildOf( root ) ) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId( interactionId );

                    messages.add( new ValidatorMessage( "Invalid interaction type (" + term.getId() + ")",
                                                        MessageLevel.ERROR,
                                                        context,
                                                        this ) );
                }
            }
        }

        return messages;
    }
}