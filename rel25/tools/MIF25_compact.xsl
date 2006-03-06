<!-- 
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 XSLT stylesheet to fold PSI-MI files into canonical form, where   
 1. every interactor, experiment, and availability description is  
    declared respectively in the entrySet's global InteractorList, 
    experimentList or availabilityList; and                        
 2. every interactor, experiment, and availability description     
    within an interaction is replaced by a xxxRef to the globally- 
    declared element.                                              
 This stylesheet can be used by a data producer to normalize       
 interaction-oriented files to submittable form.                   
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 Written by Cezanne, Even, Roumegous, Jolibert, Thomas-Nelson,     
 Marques, Cros, Sablayrolles at the ENSEIRB (www.enseirb.fr)       
 with a little advice from David Sherman 2003/04/02                
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 Updated 2006-02-17 by Antony Quinn [aquinn@ebi.ac.uk] to
 conform with MIF version 2.5. Restructured to make more understandable
 and to add top-level optional elements if required.
 Note: could improve performance by removing "//" selections, for example,
 replace "psi:interactionList//psi:experimentDescription" with
 "psi:interactionList/psi:interaction/psi:experimentList/psi:experimentDescription"
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-->
<xsl:stylesheet 
	version="1.0" 
	xmlns="net:sf:psidev:mi"
    xmlns:psi="net:sf:psidev:mi"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="psi">
	
<xsl:output method="xml" indent="yes"/>

<xsl:key name="avail-ids" use="@id"
         match="//psi:availability | //psi:availabilityDescription"/>
<xsl:key name="exp-ids" use="@id" match="//psi:experimentDescription"/>
<xsl:key name="int-ids" use="@id" match="//psi:interactor"/>

<xsl:template match="psi:entry">
    <xsl:copy>
	<!-- Copy any attributes -->
	<xsl:apply-templates select="@*"/>
	<!-- Copy source element -->
	<xsl:apply-templates select="psi:source"/>
	<!-- Find all availability, experiment and interactor descriptions
	     in the entire document and copy them to the top-level
	     (as children of entry element). The descriptions will be replaced
	     by references to these top-level elements (see eg.
	     match="psi:interactionList//psi:experimentDescription") -->
	<availabilityList>  
	    <xsl:for-each
		select="//psi:availability[generate-id(.)=generate-id(key('avail-ids', @id)[1])]	| 
			//psi:availabilityDescription[generate-id(.)=generate-id(key('avail-ids', @id)[1])]">
		<xsl:sort select="@id"/>
		<availability id="{@id}"><xsl:value-of select="."/></availability>
	    </xsl:for-each>
	</availabilityList>
	<experimentList>  
	    <xsl:for-each select="//psi:experimentDescription[generate-id(.)=generate-id(key('exp-ids', @id)[1])]">
		<xsl:sort select="@id"/>
		<xsl:copy-of select="."/>
	    </xsl:for-each> 
	</experimentList>
	<interactorList>  
	    <xsl:for-each select="//psi:interactor[generate-id(.)=generate-id(key('int-ids', @id)[1])]">
		<xsl:sort select="@id"/>
		<xsl:copy-of select="."/>
	    </xsl:for-each> 
	</interactorList>
	<!-- Copy interactionList and attributeList elements -->
	<xsl:apply-templates select="psi:interactionList"/>
	<xsl:apply-templates select="psi:attributeList"/>
    </xsl:copy>    
</xsl:template>

<!-- Do not copy *List elements (already created in psi:entry template) -->
<xsl:template match="psi:entry/psi:availabilityList |
		     psi:entry/psi:experimentList |
		     psi:entry/psi:interactorList">
</xsl:template>

<!-- Replace every availability in the interactionList with a reference -->
<xsl:template match="psi:interactionList//psi:availabilityDescription">
    <availabilityRef><xsl:value-of select="@id"/></availabilityRef>
</xsl:template>

<!-- Replace every experiment in the interactionList with a reference -->
<xsl:template match="psi:interactionList//psi:experimentDescription">
    <experimentRef><xsl:value-of select="@id"/></experimentRef>
</xsl:template>

<!-- Replace every interaction in the interactionList with a reference -->
<xsl:template match="psi:interactionList//psi:interactor">
  <interactorRef><xsl:value-of select="@id"/></interactorRef>
</xsl:template>

<!-- Make a deep copy of all other attributes and nodes -->
<xsl:template match="@* | node()">
    <xsl:copy>
	<xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>