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
 * <b> Check that experimental Role is not empty and that all participant of an interaction have consistent Roles. </b>.
 * <p/>
 *
 * @author Luisa Montecchi
 * @version $Id: ParticipantExperimentalRoleRule.java,v 1.1 2006/04/19 10:58:24 luisa_montecchi Exp $
 * @since 22-Feb-2006
 */
public class ParticipantExperimentalRoleRule extends Mi25Rule {

    public ParticipantExperimentalRoleRule(Map ontologies) {
        super(ontologies);

        // describe the rule.
        setName("Experimental Role check");
        setDescription("Checks that ExperimentalRole has " + Mi25Ontology.ExperimentalRoleRoot + "" +
                " for parent and that all participants of an interaction have consistent Roles .");
        addTip("Check in the psi-mi25.obo the list of children terms of " + Mi25Ontology.ExperimentalRoleRoot + ".");
    }

    /**
     * TODO Checks if each participant has at least one Experimental Role, if there are more than one in the list
     * an ExperimentalRef should be specified. Then the collection of roles given for all participant of an interaction
     * is checked for consistency.
     *
     * @param jaxbObject an interaction to check on.
     * @return a collection of validator messages.
     * @throws psidev.psi.mi.validator.framework.ValidatorException
     *          if we fail to retreive the MI Ontology.
     */
    public Collection<ValidatorMessage> check(Object jaxbObject) throws ValidatorException {

        // Object upon which we perform the check
        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        // list of messages to return
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        // checking all participant of that interaction

        for (Object o : interaction.getParticipantList().getParticipant()) {
            int bait_count = 0 ;  // role counter should be set to zero for each interaction
            ParticipantType participantType = (ParticipantType) o;
            int participantId = participantType.getId();

             // add check len list of RoleList if greater then 2 check if expREf

            ParticipantType.ExperimentalRoleListType expList = participantType.getExperimentalRoleList() ;
            if (expList != null ){
                int count = expList.getExperimentalRole().size();

            }  // here check that if count greater than 1 experimentRef is not empty

            for (Object p : participantType.getExperimentalRoleList().getExperimentalRole()) {

                CvType experimentalRole = null;

                if (p == null) {
                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setParticipantId(participantId);


                    messages.add(new ValidatorMessage("Missing XML element for Experimental Role ",
                            MessageLevel.WARN,
                            context,
                            this));

                } else if (p instanceof CvType) {
                    experimentalRole = (CvType) p;
                    String id = experimentalRole.getXref().getPrimaryRef().getId();
                    Mi25Ontology ont = super.getMiOntology();
                    OntologyTerm t = ont.search(id);       // name ?
                    if (t == null) {

                        Mi25Context context = new Mi25Context();
                        context.setInteractionId(interactionId);
                        context.setParticipantId(participantId);


                        messages.add(new ValidatorMessage("No CV term specified for Experimental Role",
                                MessageLevel.WARN,
                                context,
                                this));
                    } else {

                        OntologyTerm t2 = ont.getExperimentalRoleRoot();
                        if (!t.isChildOf(t2)) {

                            Mi25Context context = new Mi25Context();
                            context.setInteractionId(interactionId);
                            context.setParticipantId(participantId);

                            messages.add(new ValidatorMessage("Invalid Experimental Role term (" + t.getId() + ")",
                                    MessageLevel.WARN,
                                    context,
                                    this));
                        } else {
                            if ( id.equals(Mi25Ontology.BAIT_MI) ) {
                                ++bait_count ;
                                // here add all role counts
                            }


                            }      // counting role types

                        }   //term has the appropriate root term

                    }   // ontology term is not null

                } // role  is not null
            } // for role on each participant
           // here at interaction level the consistency of the counts should be done  
         //for participant in an interaction


     return messages;
    }
}