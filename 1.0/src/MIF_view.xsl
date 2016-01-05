<xsl:stylesheet version="1.0"
  xmlns:psi="net:sf:psidev:mi"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="ISO-8859-1"/>

<!-- 
This XSLT-Script was designed for generating HTML out of a PSI-MI-XML-File,
which satisfies MIF.xsd.
Its only a visualisation and does not show all details included in PSI !

The implementation is highly recursive - a lot of templates are used at 
different Levels.
This XSLT-Script was developed and tested using SAXON 6.5.2.

Author: Henning Mersch (hmersch@ebi.ac.uk)
-->
<!-- Element: entrySet
Here we start with element entrySet - also giving out Header/Footer.
-->
<xsl:template match="psi:entrySet">
<html>
  <head>
  <title>HUPO Proteomics Standards Initiative Protein Interaction
  Result Visualisation</title>
  </head>
  <body>
  <a href="http://psidev.sourceforge.net"><img src="http://psidev.sourceforge.net/images/psi.gif" width="113" height="75" border="0" align="left"/></a>
  <a href="http://www.hupo.org/"><img src="http://psidev.sourceforge.net/images/hupo.gif" width="113" height="75" border="0" align="right"/></a>
  <br clear="all"/>
		
  <center>
  <h2><a href="http://psidev.sourceforge.net">Proteomics Standards Initiative</a> </h2>
  <h2>Result Visualisation</h2>
  
  <xsl:apply-templates/>
  </center>
  
  </body>
</html>
</xsl:template>
<!--Level 0 -->
<!-- Element: entry -->
<xsl:template match="psi:entry">
  <table border="1">
    <xsl:apply-templates select="psi:source"/>
    <xsl:apply-templates select="psi:interactionList"/>
    <xsl:apply-templates select="psi:availabilityList"/>
    <xsl:apply-templates select="psi:experimentList"/>
    <xsl:apply-templates select="psi:interactorList"/>
    <xsl:apply-templates select="psi:attributeList"/>
  </table>
</xsl:template>

<!--Level 1 -->
<!-- Element: source -->
<xsl:template match="psi:source">
  <tr><td valign="top" bgcolor="#BBBBBB">Source:</td><td><table border="1">
  <xsl:apply-templates/>
  </table></td></tr>
</xsl:template>
<xsl:template match="psi:availabilityList">
  <tr><td valign="top" bgcolor="#BBBBBB">Availabilities:</td><td><table border="1">
  <xsl:apply-templates/>
  </table></td></tr>
</xsl:template>
<xsl:template match="psi:experimentList">
  <tr><td valign="top" bgcolor="#BBBBBB">Experiments:</td><td><table border="1">
  <xsl:apply-templates/>
  </table></td></tr>
</xsl:template>
<xsl:template match="psi:interactorList">
  <tr><td valign="top" bgcolor="#BBBBBB">Interactors:</td><td><table border="1">
  <xsl:apply-templates/>
  </table></td></tr>
</xsl:template>
<xsl:template match="psi:interactionList">
  <tr><td valign="top" bgcolor="#BBBBBB">Interactions:</td><td><table border="1">
  <xsl:apply-templates/> 
  </table></td></tr>
</xsl:template>
<xsl:template match="psi:attributeList">
  <tr><td valign="top" bgcolor="#BBBBBB">Attributes:</td><td><table border="1">
  <xsl:apply-templates/>
  </table></td></tr>
</xsl:template>

<!--Level 2 -->
<!-- Element: names-->
<xsl:template match="psi:names">
  <tr><td valign="top" bgcolor="#BBBBBB">Name:</td>
  <td><b><xsl:value-of select="psi:shortLabel"/></b>
  <xsl:choose>
    <xsl:when test="psi:fullName!='' and psi:shortLabel!='' ">: <xsl:value-of select="psi:fullName"/></xsl:when>
	<xsl:otherwise><xsl:value-of select="psi:fullName"/> </xsl:otherwise>
  </xsl:choose>
  </td></tr>
</xsl:template>
<xsl:template match="psi:bibref">
  <tr><td>
  <xsl:apply-templates/>
  </td></tr>
</xsl:template>

<!-- Element: xref-->
<xsl:template match="psi:xref">
  <xsl:apply-templates/>
</xsl:template>

<!-- Element: primaryRef-->
<xsl:template match="psi:primaryRef">
<xsl:param name="id"><xsl:value-of select="@id"/></xsl:param>
  <tr>
  <xsl:if test="@id != ''">
    <td bgcolor="#BBBBBB"><xsl:value-of select="@db"/></td>  
    <xsl:if test="@db = 'Swiss-Prot'">
      <td><a href="http://www.expasy.org/cgi-bin/sprot-search-ac?{$id}">
                  <xsl:value-of select="@id"/>
          </a>
      </td>
    </xsl:if>
    <xsl:if test="@db != 'Swiss-Prot'"> 
      <td><xsl:value-of select="@id"/></td>
    </xsl:if>
  </xsl:if>
  </tr>
</xsl:template>

<!-- Element: secondaryRef-->
<xsl:template match="psi:secondaryRef">
<xsl:param name="id"><xsl:value-of select="@id"/></xsl:param>
  <tr>
  <xsl:if test="@id != ''">
    <td bgcolor="#BBBBBB"><xsl:value-of select="@db"/></td>  
    <xsl:if test="@db = 'Swiss-Prot'">
      <td><a href="http://www.expasy.org/cgi-bin/sprot-search-ac?{$id}">
                  <xsl:value-of select="@id"/>
          </a>
      </td>
    </xsl:if>
    <xsl:if test="@db != 'Swiss-Prot'"> 
      <td><xsl:value-of select="@id"/></td>
    </xsl:if>
  </xsl:if>
  </tr>
</xsl:template>

<!-- Element: confidence-->
<xsl:template match="psi:confidence">
  <tr>
    <td bgcolor="#BBBBBB">Confidence</td>
    <td><xsl:value-of select="./@unit"/>:
        <xsl:value-of select="./@value"/>
    </td>
  </tr>
</xsl:template>


<!-- Element: attributeList-->
<xsl:template match="psi:attributeList">
  <tr><td valign="top" bgcolor="#BBBBBB">Attributes:</td><td>
  <xsl:value-of select="psi:attributeList"/></td>
  </tr>
</xsl:template>
<!-- Element: availability-->
<xsl:template match="psi:availability">
  <xsl:param name="ref"><xsl:value-of select="@id"/></xsl:param>
  <tr>
    <td><a name="{$ref}"><xsl:value-of select="@id"/></a>: <xsl:value-of select="."/></td>
  </tr>
</xsl:template>
<!-- Element: experimentDescription-->
<xsl:template match="psi:experimentDescription">
  <xsl:param name="ref"><xsl:value-of select="@id"/></xsl:param>
  <tr>
    <td valign="top" bgcolor="#BBBBBB">Name:</td>
    <td><a name="{$ref}"><xsl:value-of select="@id"/></a>: <xsl:value-of select="psi:names/psi:shortLabel"/> </td>
  </tr>
  <tr>
    <td valign="top" bgcolor="#BBBBBB">Description:</td>
    <td><a name="{$ref}"><xsl:value-of select="@id"/></a>: <xsl:value-of select="psi:names/psi:fullName"/> </td>
    <td><xsl:apply-templates select="psi:bibref"/></td>
  </tr>
  <tr><xsl:apply-templates select="psi:interactionDetection"/></tr>
  <tr><xsl:apply-templates select="psi:participantDetection"/></tr>
</xsl:template>
<!-- Element: proteinInteractor-->
<xsl:template match="psi:proteinInteractor">
  <xsl:param name="ref"><xsl:value-of select="@id"/></xsl:param>
  <tr>
    <td><a name="{$ref}"><xsl:value-of select="@id"/></a>: <xsl:value-of select="psi:names/psi:shortLabel"/> </td>
	<td><xsl:apply-templates select="psi:xref"/></td>
  </tr>
</xsl:template>
<!-- Element: interaction-->
<xsl:template match="psi:interaction">
  <tr><td><table border="3"> 
  <xsl:if test="psi:experimentRef[1]/@ref != ''">
    <tr><td valign="top" bgcolor="#BBBBBB">Experiments:</td><td><table boarder="1"><tr><xsl:apply-templates select="psi:experimentRef"/></tr></table></td></tr>
  </xsl:if>
  <xsl:if test="psi:experimentDescription[1]/@id != ''">
    <tr><td valign="top" bgcolor="#BBBBBB">Experiments:</td><td><table boarder="1"><xsl:apply-templates select="psi:experimentDescription"/></table></td></tr>
  </xsl:if>
  <xsl:if test="psi:availabilityRef[1]/@ref != ''">
    <tr><td valign="top" bgcolor="#BBBBBB">Availability:</td><td><table boarder="1"><tr><xsl:apply-templates select="psi:availabilityRef"/></tr></table></td></tr>
  </xsl:if>
  <tr><xsl:apply-templates select="psi:participantList"/></tr>
  <tr><xsl:apply-templates select="psi:interactionType"/></tr>
  <tr><xsl:apply-templates select="psi:confidence"/></tr>

  </table></td></tr>
</xsl:template>

<!--Level 3 -->
<!-- Element: interactionDetection-->
<xsl:template match="psi:interactionDetection">
  <tr><td valign="top" bgcolor="#BBBBBB">Interaction detection:</td><td><table border="1">
  <xsl:apply-templates/> <!-- names, x-ray-->
  </table></td></tr>
</xsl:template>
<!-- Element: participantDetection-->
<xsl:template match="psi:participantDetection">
  <tr><td valign="top" bgcolor="#BBBBBB">Participant detection:</td><td><table border="1">
  <xsl:apply-templates/> <!-- names, x-ray-->
  </table></td></tr>
</xsl:template>
<!-- Element: organism-->
<xsl:template match="psi:organism">
  <tr><td valign="top" bgcolor="#BBBBBB">Organisms:</td><td><table border="1">
  <xsl:apply-templates/> <!-- names, x-ray-->
  </table></td></tr>
</xsl:template>
<!-- Element: sequence-->
<xsl:template match="psi:sequence">
  <tr><td valign="top" bgcolor="#BBBBBB">Sequence:</td><td>
  <xsl:value-of select="."/>
  </td></tr>
</xsl:template>
<!-- Element: participantList-->
<xsl:template match="psi:participantList">
  <tr><td valign="top" bgcolor="#BBBBBB">Participants:</td><td><table border="1"><th bgcolor="#BBBBBB">Reference</th><th bgcolor="#BBBBBB">Role</th><th bgcolor="#BBBBBB">tagged</th><th bgcolor="#BBBBBB">overexpressed</th>
  <xsl:apply-templates/> <!-- proteinParticipant -->     
  </table></td> </tr>
</xsl:template>
<!-- Element: interactionType-->
<xsl:template match="psi:interactionType">
  <tr><td valign="top" bgcolor="#BBBBBB">Interaction Type:</td><td><table border="1">
  <xsl:apply-templates/> <!-- names, x-ray-->
  </table></td></tr>
</xsl:template>
<!-- Element: experimentRef-->
<xsl:template match="psi:experimentRef">
  <xsl:param name="ref"><xsl:value-of select="@ref"/></xsl:param>
  <td>
  <a href="#{$ref}"><xsl:value-of select="@ref"/></a> 
  </td>
</xsl:template>
<!-- Element: availabilityRef-->
<xsl:template match="psi:availabilityRef">
  <xsl:param name="ref"><xsl:value-of select="@ref"/></xsl:param>
  <td>
  <a href="#{$ref}"><xsl:value-of select="@ref"/></a> 
  </td>
</xsl:template>

<!--Level 4 -->
<!-- Element: proteinParticipant-->
<xsl:template match="psi:proteinParticipant">
  <xsl:param name="ref"><xsl:value-of select="psi:proteinInteractorRef/@ref"/></xsl:param>
  <tr>
  <xsl:choose>
    <xsl:when test="psi:proteinInteractorRef/@ref != ''"><td align="center"><a href="#{$ref}"><xsl:value-of select="psi:proteinInteractorRef/@ref"/></a></td></xsl:when>
	<xsl:otherwise><td align="center"><xsl:value-of select="psi:proteinInteractor/@id"/></td></xsl:otherwise>
  </xsl:choose>
  <td align="center"><xsl:value-of select="psi:role"/></td>
  <td align="center"><xsl:value-of select="psi:isTaggedProtein"/></td>
  <td align="center"><xsl:value-of select="psi:isOverexpressedProtein"/></td>
  </tr>
</xsl:template>

</xsl:stylesheet>


