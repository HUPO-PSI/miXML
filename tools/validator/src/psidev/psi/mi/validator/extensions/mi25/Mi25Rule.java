package psidev.psi.mi.validator.extensions.mi25;

import psidev.psi.mi.validator.framework.Rule;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.extensions.mi25.model.InteractionElementType;
import psidev.psi.mi.validator.extensions.mi25.model.ExperimentType;
import psidev.psi.mi.validator.extensions.mi25.model.ParticipantType;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

/**
 * <b> PSI-MI 2.5.1 Specific Rule </b>.
 * <p/>
 * That class is only meant to be extended, it give basic fonctionalities for building PSI-MI 2.5.1 specific rules. </p>
 *
 * @author Matthias Oesterheld
 * @version $Id: Mi25Rule.java,v 1.2 2006/04/19 11:08:22 luisa_montecchi Exp $
 * @since 04.01.2006; 15:36:52
 */
public abstract class Mi25Rule extends Rule {

    public Mi25Rule(Map ontologies) {
        super(ontologies);
    }

    /**
     * Check that the given interaction is defined using the expanded format.
     *
     * @param jaxbObject a jaxb object (actually an interaction)
     * @throws ValidatorException if the format is not expanded.
     */
    protected void checkExpandedFormat(Object jaxbObject) throws ValidatorException {

        // Object upon which we perform the check
        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        for (Object exp : interaction.getExperimentList().getExperimentRefOrExperimentDescription()) {
            if (exp instanceof InteractionElementType.ExperimentListType.ExperimentRef) {

                throw new ValidatorException("The given interaction should be of expanded form. " +
                        "We found an experimentRef instead of an experimentDescription");
            }

        }

        for (Object o : interaction.getParticipantList().getParticipant()) {
            ParticipantType participantType = (ParticipantType) o;
            if (participantType.getInteractorRef() != null) {
                throw new ValidatorException("The given interaction should be of expanded form. " +
                        "We found an interactorRef instead of an interactor full Description");
            }
        }
    }


    /**
     * Return the MI ontology
     * @return the ontology
     * @throws ValidatorException
     */
    public Mi25Ontology getMiOntology() throws ValidatorException {
        return new Mi25Ontology(getOntology("MI")); //TODO: Insert correct ID
    }
}