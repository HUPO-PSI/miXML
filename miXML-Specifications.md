# PSI-MI XML (miXML)
Molecular Interaction XML Format

# PSI-MI XML 2.5 #

The PSI-MI XML 2.5 is a community standard for molecular interactions which has been jointly developed by major data providers (BIND, CellZome, DIP, GSK, HPRD, Hybrigenics, IntAct, MINT, MIPS, Serono, U. Bielefeld, U. Bordeaux, U. Cambridge, and others).

This format is stable and used for several years now : published in October 2007 ([Broadening the Horizon – Level 2.5 of the HUPO-PSI Format for Molecular Interactions; Samuel Kerrien et al. BioMed Central. 2007.](http://www.biomedcentral.com/1741-7007/5/44)), it has been adapted for many different usages.

It can be used for storing any kind of molecular interaction data :
  * complexes and binary interactions
  * not only protein-protein interactions, can describe nucleic acids interactions and others
  * hierarchical complexes modelling by using interactionRef in participants instead of an interactor

Data representation in PSI-MI 2.5 XML relies heavily on the use of controlled vocabularies. They can be accessed easily via the Ontology Lookup Service: [PSI-MI](http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI), [PSI-MOD](http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MOD).

<br />
# Simplified view of the schema #

<img width='800' src='http://psimi.googlecode.com/svn/wiki/images/xml-schema.png' />

<br />
# Detailed documentation of the schema #

[This documentation](https://rawgit.com/HUPO-PSI/miXML/master/2.5/doc/MIF254.html) is aimed at describing the various elements that compose the PSI-MI 2.5 XML schema.

<br />
# XML Compact and Expanded formats #

Two different flavours of this format have been developed : the **Compact** format and the **Expanded** format. These two different formats are described in a single XML schema which can be confusing because it is easy to mix the two different flavours. To be able to fully enjoy the flexibility of the PSI-MI XML 2.5 format, it is important to understand what are the two different flavours and what are the limits between them.

<br />
## Compact format ##

In the Compact format, the description of experiments and interactors is done at the beginning of the **entry** using **experimentList** and **interactorList** elements. When describing the interactions (in the next element **interactionList**), we will use references to the previously described experiments and interactors using their **id** attributes.

<img width='800' src='http://psimi.googlecode.com/svn/wiki/images/compact-xml.png' />

## How to build it? ##

1) Describe all the experiments of your file in the experimentList at the entry level. All the id attributes of the experiments must be present and unique in the all file (not only with experiments but also must be different from interaction ids, interactor ids, etc.)

2) Describe all the interactors of your file in the interactorList at the entry level. All the id attributes of the interactors must be present and unique in the all file (not only with interactors but also must be different from interaction ids, experiment ids, etc.)

3) For each interaction, use the element **experimentRef** in the experimentList. You should refer to the id attribute of one of the experiments described at the entry level.

4) For each participant, use the element **interactorRef** (or **interactionRef** if you want to describe a complex). You should refer to the id attribute of one of the interactors/interactions described at the entry level.

5) For each experimental interactor (participant level), use the element **interactorRef**. You should refer to the id attribute of one of the interactors described at the entry level.

6) For each inferred interaction (interaction level), the participantRef (interacting participant) or featureRef (binding site interacting with other binding sites) should refer to a participant or feature described within the interaction and not to a participant or feature described in another interaction.

7) Several elements can have experiment references which should point to one experiment described at the entry level :

  * interaction confidences
  * interaction parameters
  * inferred interaction
  * participant identification method
  * participant experimental role
  * participant experimental preparation
  * participant experimental interactor
  * participant feature
  * participant host organism
  * participant confidence
  * participant parameters

### Advantages ###

  * when using several time the same experiments and same interactors for different interactions, it allows to reduce redundancy of the experiment descriptions and/or interactor descriptions in the file.
  * As a result, the file is smaller

### Drawbacks ###

  * It can be more difficult to edit manually the file. Indeed, there is a lot of interactors and/or experiments in the file, using references to them using their 'id' attributes can be fastidious and take more time.
  * When a user reads the file, it can be annoying to come back to the beginning of the file every time he wants to read the experiment description of an interaction.

### Warnings ###

  * it is strongly recommended to avoid describing experiments and/or interactors within the interaction instead of describing it at the level of the entry. It will not be a problem at the parsing time if no experiment references are used for the experiment described within the interaction. The experiment described within the interaction will always be considered as the default experiment for this interaction, independently from the Compact or Expanded flavour of the file. However, it will be impossible to refer to it later in the file and it can be really messy.

### Examples ###

<img width='800' src='http://psimi.googlecode.com/svn/wiki/images/compact-example.png' />


<br />
<br />
## Expanded format ##

In the Expanded format, the description of experiments and interactors is done within each interaction. The file doesn't contain any **experimentList** or **interactorList** at the **entry** level, it only contains an **interactionList**.

<img width='800' src='http://psimi.googlecode.com/svn/wiki/images/expanded-xml.png' />

## How to build it? ##

1) For each interaction, describe fully your experiments. Use the **experimentDescription** element in the experimentList.

2) For each participant (interaction level), describe fully your interactor. Use the **interactor** element (or **interactionRef** element in case of complex. The references point to an id attribute of one interaction described in the interactionList at the entry level).

3) For each experimental interactor (participant level), use the element **interactor**. You can use the **interacorRef** element but it should only refer to an interactor described within this interaction.

4) For each inferred interaction (interaction level), the participantRef (interacting participant) or featureRef (binding site interacting with other binding sites) should refer to a participant or feature described within the interaction and not to a participant or feature described in another interaction.

5) Several elements can have experiment references which should point to one experiment described within the interaction :

  * interaction confidences
  * interaction parameters
  * inferred interaction
  * participant identification method
  * participant experimental role
  * participant experimental preparation
  * participant experimental interactor
  * participant feature
  * participant host organism
  * participant confidence
  * participant parameters

### Advantages ###

  * It is easier to edit and read the file

### Drawbacks ###

  * Can be very redundant because we may have to repeat the description of the same experiment and interactor several times in different interactions.
  * It is easier to forget some elements in the experiment description and/or interactor description.
  * The file is bigger

### Warnings ###

It is possible to use experiment references in the Expanded format **only** if we refer to an experiment described within the current interaction. In the Expanded format, each interaction is independent from each other and it is not possible to be be within an interaction and use references to other experiments and interactors which have been described in other interactions.

### Examples ###

<img width='500' src='http://psimi.googlecode.com/svn/wiki/images/expanded-example.png' />

# Overwriting elements #

The XML format is very flexible and allow you to overwrite some elements :

  * **participant identification methods** : By default, you can describe a single participant identification method at the experiment level if this participant identification method has been used to identify all the participants of the interactions.
In some cases, a specific participant will be identified with different method(s) and you can overwrite the participant identification method of the experiment within the participant element using the participantIdentificationMethodList. If several experiments are attached to the interaction, it is necessary to specify the experimentRef for each participantIdentificationMethod described at the participant level.

  * **feature detection methods** : By default, you can describe a single feature detection method at the experiment level if this feature detection method has been used to identify all the features of the participants.
In some cases, a specific feature will be detected with different method(s) and you can overwrite the feature detection method of the experiment within the feature element using the featureDetectionMethodList.

<br />
# Limitations #

## Incomplete elements ##

  * interactionType : an interaction can have several interaction types and can have several experiments. The interaction type currently doesn't have an experimentRef element so when several experiments are attached to a same interaction and this interaction has several interaction types, it is impossible to link the interaction type to a specific experiment. This is currently a bug of the schema as the interaction type is strongly linked to the experiment. If you want to describe several interaction types for one interaction, you may currently have to duplicate the interaction for each interaction type you want to describe instead of listing all the interaction types in the same interaction element.

  * featureDetectionMethod : a feature can describe a list of featureDetection methods but it is impossible to link a specific detection method to an experiment as we do with the participantIdentificationMethod. To avoid confusion, you may need to duplicate the interaction for each different set of participants/features instead of listing all the feature detection methods in a same feature.

  * interaction and participant confidences : this element doesn't have a **method** element to describe the confidence score method. The unit element should not be mandatory as some scoring systems don't have any units defined. You may have to describe your method in the unit element or the attributes of the unit element

## Publication ##

  * There isn't any publication element in the XML, only a bibRef element in each experimentDescription. You should always describe your publication (pubmed id -> XRef OR authors, journal, etc. -> attributes of bibRef) within the bibRef element.

  * A limitation of the current schema is that you cannot fully describe your publication in the bibRef element if you already gave a publication cross reference. In this case, the publication details such as author list, contact-email, etc. must be added in the experiment attribute list.

## Complexes ##

> You can describe complexes in two different ways :
  * You can use an interactor with interactorType = 'complex' (MI:0314 or one of its children). However you will not be able to give several sequences.
  * You can use an interaction. However, you will have to describe an experiment which is not always relevant for describing a complex. The interaction type as well is not really adapted neither (a new Cv term should be created to really describe interaction type = complex).
