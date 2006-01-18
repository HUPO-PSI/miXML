package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.CvType;
import psidev.psi.mi.validator.extensions.mi25.model.ExperimentType;
import psidev.psi.mi.validator.extensions.mi25.model.InteractionElementType;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.*;

/**
 * <b> Checks that interactor types are valid. </b>.
 * <p/>
 *
 * @author Samuel Kerrien
 * @version $Id: InteractionDetectionRule.java,v 1.1 2006/01/18 16:55:44 skerrien Exp $
 * @since 04.01.2006; 17:22:27
 */
public class InteractionDetectionRule extends Mi25Rule {

    public InteractionDetectionRule( Map ontologies ) {
        super( ontologies );

        // describe the rule.
        setName( "Interaction Detection Check" );
        setDescription( "Checks that InteractorType has " + Mi25Ontology.InteractionDetectionTypeRoot + " for parent." );
        addTip( "Check in the psi-mi25.obo the list of children terms of " + Mi25Ontology.InteractionDetectionTypeRoot + "." );
    }

    /**
     * Check that an Interactor type is specified for each interactor, that the format of the MI id is correct and that
     * the specified term is a child of the right ontology term.
     *
     * @param jaxbObject an interaction to check on.
     *
     * @return a collection of validator messages.
     *
     * @throws psidev.psi.mi.validator.framework.ValidatorException
     *          if we fail to retreive the MI Ontology.
     */
    public Collection<ValidatorMessage> check( Object jaxbObject ) throws ValidatorException {

        // Object upon which we perform the check
        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        // list of messages to return
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        // checking that the interaction detection is correct
        Iterator i = interaction.getExperimentList().getExperimentRefOrExperimentDescription().iterator();
        while ( i.hasNext() ) {
            Object exp = i.next();

            ExperimentType experimentDescription = null;

            if ( exp instanceof InteractionElementType.ExperimentListType.ExperimentRef ) {

                throw new ValidatorException( "The given interaction should be of expanded form. " +
                                              "We found an experimentRef instead of an experimentDescription" );

            } else if ( exp instanceof ExperimentType ) {

                experimentDescription = (ExperimentType) exp;

            } else {
                // this should never happens but who knows ...
                throw new IllegalStateException();
            }

            int experimentId = experimentDescription.getId();

            // check on interaction detection here

            CvType interactionDetectionMethod = experimentDescription.getInteractionDetectionMethod();
            String miId = interactionDetectionMethod.getXref().getPrimaryRef().getId();

            Mi25Ontology ont = super.getMiOntology();
            OntologyTerm term = ont.search( miId );

            if ( term == null ) {

                Mi25Context context = new Mi25Context();
                context.setInteractionId( interactionId );
                context.setExperimentId( experimentId );

                messages.add( new ValidatorMessage( "Could not find MI reference ("+ miId +") in psi-mi25.obo",
                                                    MessageLevel.ERROR,
                                                    context,
                                                    this ) );

            } else if ( ! ont.isValidIdentifier( miId ) ) {

                Mi25Context context = new Mi25Context();
                context.setInteractionId( interactionId );
                context.setExperimentId( experimentId );

                messages.add( new ValidatorMessage( "Invalid MI identifier format (" + term.getId() + ")",
                                                    MessageLevel.ERROR,
                                                    context,
                                                    this ) );

            } else {

                OntologyTerm root = ont.getInteractionDetectionTypeRoot();
                if ( ! term.isChildOf( root ) ) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId( interactionId );
                    context.setExperimentId( experimentId );

                    messages.add( new ValidatorMessage( "Invalid interaction detection (" + term.getId() + ")",
                                                        MessageLevel.ERROR,
                                                        context,
                                                        this ) );
                }
            }

        } // while

        return messages;
    }
}