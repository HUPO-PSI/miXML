<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- XSLT stylesheet to unfold canonical PSI-MI files into a simpler,  -->
<!-- interaction-oriented form where every interactor, experiment, and -->
<!-- availability description referred to in an interaction is         -->
<!-- expanded in place, based on the target declared in the entrySet's -->
<!-- global InteractorList, experimentList or availabilityList.        -->
<!-- This stylesheet can be used by a data producer to convert down-   -->
<!-- loaded data into a simpler, directly-parseable format.            -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- Written by Cezanne, Even, Roumegous, Jolibert, Thomas-Nelson,     -->
<!-- Marques, Cros, Sablayrolles at the ENSEIRB (www.enseirb.fr)       -->
<!-- with a little advice from David Sherman 2003/04/02                -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

<xsl:stylesheet version="1.0"
  xmlns:psi="net:sf:psidev:mi" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes"/>
<xsl:namespace-alias
     stylesheet-prefix = "xsi"
     result-prefix="psi"/>
 
<!-- We empty the interactorList, experimentList and availabilityList  -->
<!-- values in the output file.                                        -->

<xsl:template match="psi:interactorList">
    <xsl:copy/>
</xsl:template>

<xsl:template match="psi:experimentList">
    <xsl:copy/>
</xsl:template>

<xsl:template match="psi:availabilityList">
    <xsl:copy/>
</xsl:template>

<!-- We replace an availabilityRef node by an availabilityDescription -->
<!-- node with the same contents as the availabilityList/availability -->
<!-- node whose id attribute is the same as the ref attribute here.   -->

<xsl:template match="psi:availabilityRef">
  <xsl:element name="availabilityDescription">
    <xsl:attribute name="id">
      <xsl:value-of select="@ref"/>
    </xsl:attribute>
    <xsl:value-of select="/psi:entrySet/psi:entry/psi:availabilityList/psi:availability[@id=current()/@ref]"/>
  </xsl:element>
</xsl:template>

<!-- We replace an experimentRef node by the experimentDescription  -->
<!-- node whose id attribute is the same as the ref attribute here. -->

<xsl:template match="psi:experimentRef">
  <xsl:copy-of select="/psi:entrySet/psi:entry/psi:experimentList/psi:experimentDescription[@id=current()/@ref]"/>
</xsl:template>


<!-- We replace an interactorRef node by the interactorDescription  -->
<!-- node whose id attribute is the same as the ref attribute here. -->

<xsl:template match="psi:interactorRef">
  <xsl:copy-of select="/psi:entrySet/psi:entry/psi:interactorList/psi:proteinInteractor[@id=current()/@ref]"/>
</xsl:template>


<!-- For all of the nodes and their attributes... -->
<xsl:template match="/ | @* | node()">
  <!-- we make a shallow copy... -->
  <xsl:copy>
    <!-- and continue -->
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
