package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.*;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.*;

/**
 * <b> When a raw sequence is not specified in the participant/sequence element
 * this rule checks there is a valid sequence XREF </b>.
 * <p/>
 *
 * @author Luisa Montecchi
 * @version $Id: ParticipantSeqXrefRule.java,v 1.1 2006/04/19 10:58:24 luisa_montecchi Exp $
 * @since 10-Apr-2006
 */
public class ParticipantSeqXrefRule extends Mi25Rule {

    public ParticipantSeqXrefRule(Map ontologies) {
        super(ontologies);

        // describe the rule.
        setName("Participant sequence cross reference rule");
        setDescription("When a raw sequence is not specified in the participant/sequence element this rule checks there is a valid sequence XREF");
        addTip("Enter a raw sequence in the element interactorElementType/sequence or a cross reference to a sequence database in interactorElementType/xref");
    }

    /**
     * TODO comment.
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
            int interactorId = participantType.getInteractor().getId();

            // String sequence = participantType.getInteractor().getSequence();
            //  DbReferenceType xref = participantType.getInteractor().getXref().getPrimaryRef();
            // !!!!!!NOTE BOTH XREF AND SEQ OPTIONAL in the schema
            if (participantType.getInteractor().getSequence() == null && participantType.getInteractor().getXref() == null)
            {

                Mi25Context context = new Mi25Context();
                context.setInteractionId(interactionId);
                context.setParticipantId(participantId);
                context.setInteractorId(interactorId);

                messages.add(new ValidatorMessage("Interactor does not have a sequence nor a sequence primary ref ",
                        MessageLevel.ERROR,
                        context,
                        this));
            } else
            if (participantType.getInteractor().getSequence() != null && participantType.getInteractor().getXref() == null)
            {

                Mi25Context context = new Mi25Context();
                context.setInteractionId(interactionId);
                context.setParticipantId(participantId);
                context.setInteractorId(interactorId);

                messages.add(new ValidatorMessage("Interactor has a raw sequence an no sequence primary ref ",
                        MessageLevel.INFO,
                        context,
                        this));

            } else if (participantType.getInteractor().getXref() != null) {
                DbReferenceType xref = participantType.getInteractor().getXref().getPrimaryRef();
                String db = xref.getDb();
                String id = xref.getId();
                if (db.equals("")) {
                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setParticipantId(participantId);
                    context.setInteractorId(interactorId);

                    messages.add(new ValidatorMessage("The attribute db in primary ref of the  participant is an empty string",
                            MessageLevel.WARN,
                            context,
                            this));

                } else if (id.equals("")) {
                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setParticipantId(participantId);
                    context.setInteractorId(interactorId);

                    messages.add(new ValidatorMessage("The attribute id in primary ref of the participant is an empty string",
                            MessageLevel.WARN,
                            context,
                            this));

                } else if (! id.equals("") && ! db.equals("")) {

                    Mi25Ontology ont = super.getMiOntology();     //  db is name not MI and dcAc MI of db not mandatory

                    String mi_db = null;
                    mi_db = ont.shortlabel2mi.get(db);
                    OntologyTerm t = ont.search(mi_db);

                    if (t == null) {

                        Mi25Context context = new Mi25Context();
                        context.setInteractionId(interactionId);
                        context.setParticipantId(participantId);
                        context.setInteractorId(interactorId);

                        messages.add(new ValidatorMessage("Not appropriate db name in participant primary xref (" + db + ")",
                                MessageLevel.WARN,
                                context,
                                this));

                    } else if (! mi_db.equals("MI:0486")) {
                        Mi25Context context = new Mi25Context();
                        context.setInteractionId(interactionId);
                        context.setParticipantId(participantId);
                        context.setInteractorId(interactorId);

                        messages.add(new ValidatorMessage("Participant primary xref different from UniProtKB (" + mi_db + ")",
                                MessageLevel.INFO,
                                context,
                                this));
                    } else {

                        OntologyTerm t2 = ont.getDatabaseCitationRoot(); // add extra condition going down to sequence database once term created
                        if (!t.isChildOf(t2)) {

                            Mi25Context context = new Mi25Context();
                            context.setInteractionId(interactionId);
                            context.setParticipantId(participantId);
                            context.setInteractorId(interactorId);

                            messages.add(new ValidatorMessage("Term does not belong to CV database citation (" + t.getId() + ")",
                                    MessageLevel.ERROR,
                                    context,
                                    this));
                        }
                    }
                } // if db and id are not empty
            }   //if  xref not null
        }// for participant
        return messages;
    }
}