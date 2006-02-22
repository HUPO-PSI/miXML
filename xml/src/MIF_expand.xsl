<!-- 
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 XSLT stylesheet to unfold canonical PSI-MI files into a simpler,  
 interaction-oriented form where every interactor, experiment, and 
 availability description referred to in an interaction is         
 expanded in place, based on the target declared in the entrySet's 
 global InteractorList, experimentList or availabilityList.        
 This stylesheet can be used by a data producer to convert down-   
 loaded data into a simpler, directly-parseable format.            
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 Written by Cezanne, Even, Roumegous, Jolibert, Thomas-Nelson,     
 Marques, Cros, Sablayrolles at the ENSEIRB (www.enseirb.fr)       
 with a little advice from David Sherman 2003/04/02                
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 Updated 2006-02-17 by Antony Quinn [aquinn@ebi.ac.uk] to
 conform with MIF version 2.5
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 -->
<xsl:stylesheet 
	version="1.0" 
	xmlns="net:sf:psidev:mi"
	xmlns:psi="net:sf:psidev:mi" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<xsl:output method="xml" indent="yes"/>

<!-- Do not copy *List elements - we do not use references in the expanded
     format so there is no need to have this information at the top-level -->
<xsl:template match="psi:entry/psi:availabilityList |
		     psi:entry/psi:experimentList |
		     psi:entry/psi:interactorList">
</xsl:template>

<!--
    Replace reference to elements with a copy of the element itself
    For example, an experimentRef is replaced by an entire experimentDescripiton
    element with all its correspoding child elements.
-->
<xsl:template match="psi:availabilityRef">
    <availabilityDescription id="{current()/text()}">
	<xsl:value-of select="/psi:entrySet/psi:entry/psi:availabilityList/psi:availability[@id=current()/text()]"/>
    </availabilityDescription>
</xsl:template>
<xsl:template match="psi:experimentRef">
    <xsl:copy-of select="/psi:entrySet/psi:entry/psi:experimentList/psi:experimentDescription[@id=current()/text()]"/>
</xsl:template>
<xsl:template match="psi:interactorRef">
    <xsl:copy-of select="/psi:entrySet/psi:entry/psi:interactorList/psi:interactor[@id=current()/text()]"/>
</xsl:template>

<!-- Make a deep copy of all other attributes and nodes -->
<xsl:template match="@* | node()">
    <xsl:copy>
	<xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>