GOAL
----

    Build a framework that will allow a curator to write Sanity check rule that can be applied to a
    valid PSI-MI XML document. No prior technical knowledge of XML should be required.


Technologies
------------

   - Java for cross plateform
   - Groovy for ease of use
   - JAXB for easy data Binding (that will prevent coding oursef any XML parsing)
   - <XML:DB> that may help to split and store the document into a database
     that would allow us to broswe all interactions in a PSI document without loading all interactors/experiments
     only the required object would be loaded by simply following the attribute 'ref'.


How will we proceed
-------------------

    1. Load an XML Document into a Java Object Model
    2. Use a set of Rules that we will apply
       What should be given to a Rule, an Interaction ? an Experiment ? an Interactor ? any of the 3 before ?
    3. For each Rule, apply() returns a Collection of VelidatorMessage
    4. Each Rule is a Class, actually a Groovy script that has been compiled

    a Groovy script needs to be able to check CVs
       eg. is MI:xxxx a parent of MI:yyyy
       we can write a Java Class that implement that thing, Groovu can then call it upon need
       eg CV( "MI:xxxx" ).isParentOf( "MI:yyyy" )