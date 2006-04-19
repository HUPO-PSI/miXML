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
 * <b> Check in case a feature is specified that the CV feature type is valid. </b>.
 * <p/>
 *
 * @author Luisa Montecchi
 * @version $Id: FeatureTypeRule.java,v 1.2 2006/04/19 12:23:40 luisa_montecchi Exp $
 * @since 20-Mar-2006
 */
public class FeatureTypeRule extends Mi25Rule {

    public FeatureTypeRule(Map ontologies) {
        super(ontologies);

        // describe the rule.
        setName("Feature Type check");
        setDescription("In case a participant has features it checks if each has a valid CV Feature Type.");
        addTip("Check in the psi-mi25.obo the list of children terms of " + Mi25Ontology.FeatureTypeRoot + ".");
    }

    /**
     * Check that an feature type is specified when the feature element is not mull, that the format of the MI id is correct and that
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

        // check possible feature of each participant.
        for (Object o : interaction.getParticipantList().getParticipant()) {
            ParticipantType participantType = (ParticipantType) o;


            int participantId = participantType.getId();

            ParticipantType.FeatureListType featList = participantType.getFeatureList();
            if (featList != null) {

                for (Object p : participantType.getFeatureList().getFeature()) {

                    FeatureElementType feature = (FeatureElementType) p;
                    CvType featureCV = null;
                    int featureId = feature.getId();  // try catch? to get a feature id to point to on the error message?
                    if (feature.getFeatureType() != null) {
                        CvType featureTypeCV = feature.getFeatureType();
                        String id = featureTypeCV.getXref().getPrimaryRef().getId();
                        Mi25Ontology ont = super.getMiOntology();
                        OntologyTerm term = ont.search(id);
                        if (term == null) {

                            Mi25Context context = new Mi25Context();
                            context.setInteractionId(interactionId);
                            context.setParticipantId(participantId);
                            context.setFeatureId(featureId);
                            messages.add(new ValidatorMessage("Invalid MI identifier for CV Fetaure type("+id+")",
                                    MessageLevel.WARN,
                                    context,
                                    this));
                        } else {

                            OntologyTerm t2 = ont.getFeatureTypeRoot();
                            if (!term.isChildOf(t2)) {

                                Mi25Context context = new Mi25Context();
                                context.setInteractionId(interactionId);
                                context.setParticipantId(participantId);
                                context.setFeatureId(featureId);

                                messages.add(new ValidatorMessage("Term does not belong to CV feature type (" + term.getId() + ")",
                                        MessageLevel.WARN,
                                        context,
                                        this));
                            }

                        }

                    } else {
                        Mi25Context context = new Mi25Context();
                        context.setInteractionId(interactionId);
                        context.setParticipantId(participantId);
                        context.setFeatureId(featureId);

                        messages.add(new ValidatorMessage("Feature without CV feature type ",
                                MessageLevel.WARN,
                                context,
                                this));

                    }
                } // for each feature
            } // if feature list not null
        }  // for each participant


        return messages;
    }
}