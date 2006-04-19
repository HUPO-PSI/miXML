package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.CvType;
import psidev.psi.mi.validator.extensions.mi25.model.ExperimentType;
import psidev.psi.mi.validator.extensions.mi25.model.InteractionElementType;
import psidev.psi.mi.validator.extensions.mi25.model.ParticipantType;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.*;

/**
 * <b>
 * check that each interactor has at least name or a short label. </b>.
 * <p/>
 *
 * @author Luisa Montecchi
 * @version $Id: InteractorNameRule.java,v 1.1 2006/04/19 10:58:24 luisa_montecchi Exp $
 * @since 20-Mar-2006
 */
public class InteractorNameRule extends Mi25Rule {

    public InteractorNameRule(Map ontologies) {
        super(ontologies);

        // describe the rule.
        setName("Interactor Name check");
        setDescription("check that each interactor has at least name or a short label");
        addTip("Provide a not empty full name or short label using the gene name or a systematic orf name or a acession number or any meaningfyl acronyme to name the " +
                "interactor");
    }

    /**
     * check that each interactor has at least name or a short label.
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

        // write the rule here ...
        for (Object o : interaction.getParticipantList().getParticipant()) {

            ParticipantType participantType = (ParticipantType) o;
            int participantId = participantType.getId();
            String fullname = participantType.getInteractor().getNames().getFullName();
            String shortlabel = participantType.getInteractor().getNames().getShortLabel();
            int interactorId = participantType.getInteractor().getId();
            if (participantType.getInteractor().getNames().getFullName() != null && participantType.getInteractor().getNames().getShortLabel() != null){

                if (fullname.equals("") && shortlabel.equals("")) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setParticipantId(participantId);
                    context.setInteractorId(interactorId);

                    messages.add(new ValidatorMessage("Interactor does not have a fullname nor a short label ",
                            MessageLevel.ERROR,
                            context,
                            this));
                }

            }else if (participantType.getInteractor().getNames().getFullName() == null && participantType.getInteractor().getNames().getShortLabel() != null){
                 if (shortlabel.equals("")) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setParticipantId(participantId);
                    context.setInteractorId(interactorId);

                    messages.add(new ValidatorMessage("Interactor only has an empty short label ",
                            MessageLevel.ERROR,
                            context,
                            this));
                 }
            }else if (participantType.getInteractor().getNames().getFullName() != null && participantType.getInteractor().getNames().getShortLabel() == null){
                 if (fullname.equals("")) {

                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setParticipantId(participantId);
                    context.setInteractorId(interactorId);

                    messages.add(new ValidatorMessage("Interactor only has an empty full name ",
                            MessageLevel.ERROR,
                            context,
                            this));
                 }
            }

        }    // for participant interactor
        return messages;
    }
}