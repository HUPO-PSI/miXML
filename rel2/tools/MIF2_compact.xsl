<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- XSLT stylesheet to fold PSI-MI files into canonical form, where   -->
<!-- 1. every interactor, experiment, and availability description is  -->
<!--    declared respectively in the entrySet's global InteractorList, -->
<!--    experimentList or availabilityList; and                        -->
<!-- 2. every interactor, experiment, and availability description     -->
<!--    within an interaction is replaced by a xxxRef to the globally- -->
<!--    declared element.                                              -->
<!-- This stylesheet can be used by a data producer to normalize       -->
<!-- interaction-oriented / mixed files to submittable form.           -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- ** Written by Matthias @ MIPS (mips.gsf.de) **                    -->
<!-- ** XSL for MIF version 1 available at psidev.sourceforge.net **   -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<xsl:stylesheet version="1.0" xmlns="net:sf:psidev:mi" 
xmlns:psi="net:sf:psidev:mi" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:template match="psi:entry">
	<xsl:copy>
	<xsl:apply-templates select="psi:source"/>
	<!-- create availabilityList if it does not exist -->
	<xsl:if test="count(psi:availabilityList)=0">
		<xsl:element name="availabilityList">
			<xsl:call-template name="av-list"/> <!-- call template to copy availabilities in here -->
		</xsl:element>
	</xsl:if>
	<!-- else apply availabilityList template -->
	<xsl:apply-templates select="psi:availabilityList"/>
	<!-- create experimentList if it does not exist -->
	<xsl:if test="count(psi:experimentList)=0">
		<xsl:element name="experimentList">
			<xsl:call-template name="exp-list"/> <!-- call template to copy experimentDescriptions in here -->
		</xsl:element>
	</xsl:if>
	<!-- else apply experimentList template -->
	<xsl:apply-templates select="psi:experimentList"/>
	<!-- create experimentList if it does not exist -->
	<xsl:if test="count(psi:interactorList)=0">
		<xsl:element name="interactorList">
			<xsl:call-template name="int-list"/> <!-- call template to copy interactors in here -->
		</xsl:element>
	</xsl:if>
	<!-- else apply interactionList template -->
	<xsl:apply-templates select="psi:interactionList"/>
	<xsl:apply-templates select="psi:attributeList"/>
	</xsl:copy>
</xsl:template>

<!-- Create a entry in the global availabilityList for each availability in the interactionList (used if entry/availabilityList exists) -->
<xsl:template match="psi:entry/psi:availabilityList">
	<xsl:copy>
		<!-- copy every availability for this entry(..) from the interactionList-->
		<xsl:for-each select="../psi:interactionList/psi:interaction/psi:availability">
			<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		</xsl:for-each>
		<!-- copy all possible availabilities already in the global list (for mixed normalized/unnormalized documents) -->
		<xsl:apply-templates select="*"/>
	</xsl:copy>
</xsl:template>

<!-- Create a entry in the global availabilityList for each availability in the interactionList (used if entry/availabilityList had to be created) -->
<xsl:template name="av-list">
		<xsl:for-each select="psi:interactionList/psi:interaction/psi:availability">
			<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		</xsl:for-each>
</xsl:template>

<!-- Create a entry in the global experimentList for each experimentDescription in the interactionList (used if entry/experimentList exists) -->
<xsl:template match="psi:entry/psi:experimentList">
	<xsl:copy>
		<!-- copy every experimentDescription from the interactionList-->
		<xsl:for-each select="../psi:interactionList/psi:interaction/psi:experimentList/psi:experimentDescription">
			<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		</xsl:for-each>
		<!-- copy all possible experimentDescriptions already in the global list (for mixed normalized/unnormalized documents) -->
		<xsl:apply-templates select="*"/>
	</xsl:copy>
</xsl:template>

<!-- Create a entry in the global experimentList for each experimentDescription in the interactionList (used if entry/experimentList had to be created) -->
<xsl:template name="exp-list">
		<xsl:for-each select="psi:interactionList/psi:interaction/psi:experimentList/psi:experimentDescription">
			<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		</xsl:for-each>
</xsl:template>

<!-- Create a entry in the global interactorList for each participant in the interactionList (used if entry/interactorList exists) -->
<xsl:template match="psi:entry/psi:experimentList">
	<xsl:copy>
		<!-- copy every participant (no matter what kind) from the interactionList-->
		<xsl:for-each select="../psi:interactionList/psi:interaction/psi:participantList/*/node()[substring-before(name(.), 'Interactor')!='']">
			<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		</xsl:for-each>
		<!-- copy all possible interactors already in the global list (for mixed normalized/unnormalized documents) -->
		<xsl:apply-templates select="*"/>
	</xsl:copy>
</xsl:template>

<!-- Create a entry in the global interactorList for each participant in the interactionList (used if entry/experiment had to be created) -->
<xsl:template name="int-list">
		<xsl:for-each select="psi:interactionList/psi:interaction/psi:participantList/*/node()[substring-before(name(.), 'Interactor')!='']">
			<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		</xsl:for-each>
</xsl:template>

<!-- Replace every occurance of a experimentDescription in the interactionList with the corresponding reference -->
<xsl:template match="psi:interaction/psi:experimentList/psi:experimentDescription">
	<xsl:element name="psi:experimentRef">
		<xsl:attribute name="ref"><xsl:value-of select="@id"/></xsl:attribute>
	</xsl:element>	
</xsl:template>

<!-- Replace every occurance of a availabilty in the interactionList with the corresponding reference -->
<xsl:template match="psi:interaction/psi:availability">
	<xsl:element name="psi:availabilityRef">
		<xsl:attribute name="ref"><xsl:value-of select="@id"/></xsl:attribute>
	</xsl:element>
</xsl:template>

<!-- Replace every occurance of a participant in the interactionList with the corresponding reference -->
<xsl:template match="psi:interaction/psi:participantList">
	<xsl:copy>
		<xsl:for-each select="node()">
			<xsl:variable name="prefix" select="substring-before(name(), 'Participant')"/><!-- read prefix: protein, dna, rna, smallMolecule -->
			<xsl:copy>
				<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
				<xsl:element name="{concat($prefix, 'InteractorRef')}">
					<xsl:attribute name="ref"><xsl:value-of select="@id"/></xsl:attribute>
				</xsl:element>
			</xsl:copy>
		</xsl:for-each>
	</xsl:copy>
</xsl:template>

<!-- Copy everything else -->
<xsl:template match="@* | node()" >
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>
</xsl:stylesheet>
