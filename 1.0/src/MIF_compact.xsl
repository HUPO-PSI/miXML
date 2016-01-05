<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- XSLT stylesheet to fold PSI-MI files into canonical form, where   -->
<!-- 1. every interactor, experiment, and availability description is  -->
<!--    declared respectively in the entrySet's global InteractorList, -->
<!--    experimentList or availabilityList; and                        -->
<!-- 2. every interactor, experiment, and availability description     -->
<!--    within an interaction is replaced by a xxxRef to the globally- -->
<!--    declared element.                                              -->
<!-- This stylesheet can be used by a data producer to normalize       -->
<!-- interaction-oriented files to submittable form.                   -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- Written by Cezanne, Even, Roumegous, Jolibert, Thomas-Nelson,     -->
<!-- Marques, Cros, Sablayrolles at the ENSEIRB (www.enseirb.fr)       -->
<!-- with a little advice from David Sherman 2003/04/02                -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<xsl:stylesheet version="1.0"
  xmlns="net:sf:psidev:mi" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>
<!-- ### availabilityList ### -->
<!-- Define an index over availability ids -->
<xsl:key name="avail-ids" 
     match="//availability | //availabilityDescription" use="@id"/>
<!-- Copy availability description for each distinct availability id to availabilityList -->
<xsl:template match="availabilityList" >
  <xsl:element name="availabilityList">  
    <xsl:for-each select="//availability[generate-id(.)=
                   generate-id(key('avail-ids', @id)[1])]
                        | //availabilityDescription[generate-id(.)=
                   generate-id(key('avail-ids', @id)[1])]">
      <xsl:sort select="@id"/>
      <xsl:element name="availability">
<xsl:attribute name="id">
          <xsl:value-of select="@id"/>
</xsl:attribute>
        <xsl:value-of select="."/>
      </xsl:element>
    </xsl:for-each>
  </xsl:element>
</xsl:template>
<!-- Replace every availability in the interactionList with a reference -->
<xsl:template match="interactionList//availabilityDescription">
  <xsl:element name="availabilityRef">  
    <xsl:attribute name="ref">
      <xsl:value-of select="@id"/>
    </xsl:attribute>
  </xsl:element>
</xsl:template>
<!-- ### experimentList ### -->
<!-- Define an index over interactor ids -->
<xsl:key name="exp-ids" match="//experimentDescription" use="@id"/>
<!-- Copy experiment description for each distinct experiment id to experimentList -->
<xsl:template match="experimentList" >
  <xsl:element name="experimentList">  
    <xsl:for-each select="//experimentDescription[generate-id(.)=
                           generate-id(key('exp-ids', @id)[1])]">
      <xsl:sort select="@id"/>
      <xsl:copy-of select="."/>
    </xsl:for-each> 
  </xsl:element>
</xsl:template>
<!-- Replace every experiment in the interactionList with a reference -->
<xsl:template match="interactionList//experimentDescription" >
  <xsl:element name="experimentRef">  
    <xsl:attribute name="ref">
    <xsl:value-of select="@id"/>
  </xsl:attribute>
</xsl:element>
</xsl:template>
<!-- ### interactorList ### -->
<!-- Define an index over interactor ids -->
<xsl:key name="int-ids" match="//proteinInteractor" use="@id"/>
<!-- Copy interactor for each distinct interactor id to interactorList -->
<xsl:template match="interactorList" >
  <xsl:element name="interactorList">  
    <xsl:for-each select="//proteinInteractor[generate-id(.)=
                           generate-id(key('int-ids', @id)[1])]">
      <xsl:sort select="@id"/>
      <xsl:copy-of select="."/>
    </xsl:for-each> 
  </xsl:element>
</xsl:template>
<!-- Replace every interaction in the interactionList with a reference -->
<xsl:template match="interactionList//ProteinInteractor" >
  <xsl:element name="proteinInteractorRef">  
    <xsl:attribute name="ref">
      <xsl:value-of select="@id"/>
    </xsl:attribute>
  </xsl:element>
</xsl:template>
<!-- ### Everything else ### -->
<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="@* | node()" >
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>
</xsl:stylesheet>
