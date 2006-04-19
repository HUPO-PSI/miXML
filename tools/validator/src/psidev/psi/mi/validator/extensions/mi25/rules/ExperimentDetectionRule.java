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
 * @version $Id: ExperimentDetectionRule.java,v 1.1 2006/04/19 10:58:24 luisa_montecchi Exp $
 * @since 04.01.2006; 17:22:27
 */
public class ExperimentDetectionRule extends Mi25Rule {

    public ExperimentDetectionRule(Map ontologies) {
        super(ontologies);

        // describe the rule.
        setName("Interaction Detection Check");
        setDescription("Checks that InteractorType has " + Mi25Ontology.InteractionDetectionTypeRoot + " for parent.");
        addTip("Check in the psi-mi25.obo the list of children terms of " + Mi25Ontology.InteractionDetectionTypeRoot + ".");
    }

    /**
     * Check that an Interactor type is specified for each interactor, that the format of the MI id is correct and that
     * the specified term is a child of the right ontology term.
     *
     * @param jaxbObject an interaction to check on.
     * @return a collection of validator messages.
     * @throws psidev.psi.mi.validator.framework.ValidatorException
     *          if we fail to retreive the MI Ontology.
     */
    public Collection<ValidatorMessage> check(Object jaxbObject) throws ValidatorException {

        checkExpandedFormat(jaxbObject);

        // Object upon which we perform the check
        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        // list of messages to return
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        // checking that the interaction detection is correct
        for (Object exp : interaction.getExperimentList().getExperimentRefOrExperimentDescription()) {
            if (exp instanceof InteractionElementType.ExperimentListType.ExperimentDescription) {

                ExperimentType experimentDescription = (ExperimentType) exp;


                int experimentId = experimentDescription.getId();

                // check on interaction detection here

                CvType interactionDetectionMethod = experimentDescription.getInteractionDetectionMethod();
                String miId = interactionDetectionMethod.getXref().getPrimaryRef().getId();

                Mi25Ontology ont = super.getMiOntology();
                OntologyTerm term = ont.search(miId);

                if (term == null) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setExperimentId(experimentId);
                    messages.add(new ValidatorMessage("Invalid MI identifier for CV interaction detection method (" + miId + ")",
                            MessageLevel.ERROR,
                            context,
                            this));

                } else {

                    OntologyTerm root = ont.getInteractionDetectionTypeRoot();
                    if (! term.isChildOf(root)) {

                        Mi25Context context = new Mi25Context();
                        context.setInteractionId(interactionId);
                        context.setExperimentId(experimentId);

                        messages.add(new ValidatorMessage("Term does not belong to CV interaction detection (" + miId + ")",
                                MessageLevel.ERROR,
                                context,
                                this));
                    }
                }
            }
        } // for

        return messages;
    }
}