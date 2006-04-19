package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.InteractionElementType;
import psidev.psi.mi.validator.extensions.mi25.model.ParticipantType;
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
 * @version $Id: InteractorTypeRule.java,v 1.2 2006/04/19 11:00:16 luisa_montecchi Exp $
 * @since 04.01.2006; 17:22:27
 */
public class InteractorTypeRule extends Mi25Rule {

    public InteractorTypeRule(Map ontologies) {
        super(ontologies);

        // describe the rule.
        setName("Interactor Type Check");
        setDescription("Checks that InteractorType has " + Mi25Ontology.InteractorTypeRoot + " for parent.");
        addTip("Check in the psi-mi25.obo the list of children terms of " + Mi25Ontology.InteractorTypeRoot + ".");
    }

    /**
     * Check that an Interactor type is specified for each interactor, that the format of the MI id is correct and that
     * the specified term is a child of the right ontology term.
     *
     * @param jaxbObject an interaction to check on.
     * @return a collection of validator messages.
     * @throws ValidatorException if we fail to retreive the MI Ontology.
     */
    public Collection<ValidatorMessage> check(Object jaxbObject) throws ValidatorException {
        checkExpandedFormat(jaxbObject);
        // Object upon which we perform the check
        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        // list of messages to return
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        // checking all interactors of that interaction
        for (Object o : interaction.getParticipantList().getParticipant()) {
            ParticipantType participantType = (ParticipantType) o;

            int participantId = participantType.getId();

            String id = participantType.getInteractor().getInteractorType().getXref().getPrimaryRef().getId();

            int interactorId = participantType.getInteractor().getId();

            Mi25Ontology ont = super.getMiOntology();
            OntologyTerm t = ont.search(id);

            if (t == null) {

                Mi25Context context = new Mi25Context();
                context.setInteractionId(interactionId);
                context.setParticipantId(participantId);
                context.setInteractorId(interactorId);

                messages.add(new ValidatorMessage("Invalid MI identifier for CV interactor Type (" + id + ")",
                        MessageLevel.ERROR,
                        context,
                        this));

            } else {

                OntologyTerm t2 = ont.getInteractorTypeRoot();
                if (!t.isChildOf(t2)) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setParticipantId(participantId);
                    context.setInteractorId(interactorId);

                    messages.add(new ValidatorMessage("Term does not belong to CV interactor type (" + id + ")",
                            MessageLevel.ERROR,
                            context,
                            this));
                }
            }
        }
        return messages;
    }
}