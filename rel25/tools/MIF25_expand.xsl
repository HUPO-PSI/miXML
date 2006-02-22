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
<!-- ** Modified for MIF version 2.5 by Matthias Oesterheld @ MIPS (mips.gsf.de) ** -->
<!-- ** XSL for MIF version 1 available at psidev.sourceforge.net   ** -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<xsl:stylesheet version="1.0" xmlns="net:sf:psidev:mi" xmlns:psi="net:sf:psidev:mi" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<!-- We delete the interactorList, experimentList and availabilityList in the output file. -->
<xsl:template match="psi:entrySet/psi:entry/psi:interactorList"/>
<xsl:template match="psi:entrySet/psi:entry/psi:experimentList"/>
<xsl:template match="psi:entrySet/psi:entry/psi:availabilityList"/>

<!-- We replace an availabilityRef node by an availabilityDescription -->
<!-- node with the same contents as the availabilityList/availability -->
<!-- node whose id attribute is the same as the ref attribute here.   -->
<xsl:template match="psi:availabilityRef">
    <xsl:copy-of select="../../../psi:availabilityList/psi:availability[@id=text()]"/>
</xsl:template>

<!-- We replace an experimentRef node by the experimentDescription  -->
<!-- node whose id attribute is the same as the ref attribute here. -->
<xsl:template match="psi:experimentRef">
  <xsl:copy-of select="../../../../psi:experimentList/psi:experimentDescription[@id=current()/text()]"/>
</xsl:template>

<!-- We replace an interactorRef node by the interactorDescription  -->
<!-- node whose id attribute is the same as the ref attribute here. -->
<xsl:template match="psi:interactorRef">
  <xsl:copy-of select="../../../../../psi:interactorList/psi:interactor[@id=current()/text()]"/>
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