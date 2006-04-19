package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.CvType;
import psidev.psi.mi.validator.extensions.mi25.model.ExperimentType;
import psidev.psi.mi.validator.extensions.mi25.model.InteractionElementType;
import psidev.psi.mi.validator.extensions.mi25.model.BioSourceType;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.*;

/**
 * <b> check every experiment host organism has an attribute TAX ID. </b>.
 * <p/>
 *
 * @author Luisa Montecchi
 * @version $Id: ExperimentHostOrgRule.java,v 1.1 2006/04/19 10:58:24 luisa_montecchi Exp $
 * @since 07-Feb-2006
 */
public class ExperimentHostOrgRule extends Mi25Rule {

    public ExperimentHostOrgRule(Map ontologies) {
        super(ontologies);         // necessary even if I do not use CVs?

        // describe the rule.
        setName("Experiment Host Organism Check");
        setDescription("Checks that each experiment has an host organisms element with a valid tax id as attribute");
        addTip("Search http://www.ebi.ac.uk/newt/display with an organism name to retrieve its tax id, for 'in vitro' by conventio tax id = -1 ");
    }

    /**
     * Checks that each experiment has an host organisms element with a valid tax id as attribute.
     * Tax id must be a positive integer or -1
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

        // checking that the tax id is correct
        Iterator i = interaction.getExperimentList().getExperimentRefOrExperimentDescription().iterator();
        for (Object exp : interaction.getExperimentList().getExperimentRefOrExperimentDescription()) {
            if (exp instanceof InteractionElementType.ExperimentListType.ExperimentDescription) {

                ExperimentType experimentDescription = (ExperimentType) exp;

                int experimentId = experimentDescription.getId();

                // check on host organism start  here

                ExperimentType.HostOrganismListType hostOrganismList = experimentDescription.getHostOrganismList();
                if (hostOrganismList != null) {

                    for (Object hostOrg : hostOrganismList.getHostOrganism()) {

                        BioSourceType HostOrganism = null;
                        HostOrganism = (BioSourceType) hostOrg;
                        int taxId = HostOrganism.getNcbiTaxId();

                        if (taxId < 0 && taxId != -1) {
                            Mi25Context context = new Mi25Context();
                            context.setInteractionId(interactionId);
                            context.setExperimentId(experimentId);

                            messages.add(new ValidatorMessage("Unvalid value for the host organism tax ID (" + taxId + ")",
                                    MessageLevel.WARN,
                                    context,
                                    this));
                        }


                    }   //for
                } else {
                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setExperimentId(experimentId);

                    messages.add(new ValidatorMessage("Missing host organism list at experiment level",
                            MessageLevel.WARN,
                            context,
                            this));


                }
            }
        } // For

        return messages;
    }
}