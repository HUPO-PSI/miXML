<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="net:sf:psidev:mi" xmlns:xs="http://www.w3.org/2001/XMLSchema">
<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="/">
		<html>
			<head>
			<style>
			TH {color: #0050B2;} 
  			.R0 { text-align: left; 
			      padding-left: 0.5em;} 
			.R1 {text-align: left; 
			     color:#000000; 
			     padding-left: 2em;} 
			.R2 {text-align: left; 
			     color: red;
			     padding-left: 3em;}
			.parts {text-align: left; 
			     color: black;
			     font-size: 16pt;
			     font-weight: bold;
			     padding-left: 1em;
			     padding-top: 1em;
			     padding-bottom: 1em;}
			</style>
			</head>

			<body>
			<br/>
			<center>
				<xsl:for-each select="n1:entrySet">
					<xsl:for-each select="n1:entry">
						<table width ="80%" border="0" cellpadding="0" cellspacing="0">
						<tr align="center">
								<td><a href="http://psidev.sourceforge.net"><img src="http://psidev.sourceforge.net/images/psi.gif" width="113" height="75" border="0" align="left"/></a></td> 
								<td><xsl:call-template name="sources"/></td>
								<td><a href="http://www.hupo.org/"><img src="http://psidev.sourceforge.net/images/hupo.gif" width="113" height="75" border="0" align="right"/></a></td>
						</tr>
						<br/>

						<!-- AvailabilityList -->

						<xsl:for-each select="n1:availabilityList">
							<xsl:for-each select="n1:availability">
								<xsl:if test="position()=1">									
									<tr valign="center">
										<td colspan="3" class="parts">Availability List</td>
									</tr>
								</xsl:if>
								<tr valign="center">
										<td colspan="3">
											<xsl:value-of select="current()"/>
										</td>
								</tr>
							</xsl:for-each>
						</xsl:for-each>
						
						<!-- InteractionList -->

						<xsl:for-each select="n1:interactionList">
							<xsl:for-each select="n1:interaction">
								<xsl:if test="position()=1">									
									<tr valign="center">
										<td colspan="3" class="parts">Interaction List</td>
									</tr>
								</xsl:if>
								<tr>
									<td colspan="3">
										<xsl:for-each select="n1:participantList">
											<xsl:for-each select="n1:participant">
												<xsl:if test="position()=1">
													<xsl:text disable-output-escaping="yes">&lt;table cellpadding="5" cellspacing="0" border="0"&gt;</xsl:text>
												</xsl:if>
												<xsl:if test="position()=1">
													<thead>
														<tr bgcolor="#CCCCCC" align="center">
															<td width="300"/>
															<td width="100">roles</td>
															<td width="200">isTaggedProtein</td>
															<td width="200">isOverexpressedProtein</td>
															<td width="200">interactionType</td>
														</tr>
													</thead>

												</xsl:if>
												<xsl:if test="position()=1">
													<xsl:text disable-output-escaping="yes">&lt;tbody&gt;</xsl:text>
												</xsl:if>
												<xsl:element name="tr"><xsl:attribute name="bgcolor">
												<xsl:choose>
													<xsl:when test="position() mod 2 =1">
														<xsl:text disable-output-escaping="yes">#FF0066</xsl:text>
													</xsl:when>
													<xsl:otherwise>
														<xsl:text disable-output-escaping="yes">#00FF00</xsl:text>
													</xsl:otherwise>
												</xsl:choose>
												</xsl:attribute><xsl:attribute name="align">center</xsl:attribute>
												<td width="105">
														<xsl:for-each select="n1:interactorRef">
															<xsl:for-each select="@ref">
																<xsl:value-of select="."/>
															</xsl:for-each>
														</xsl:for-each>
														<br/>
														<xsl:for-each select="n1:interactor">
															<xsl:for-each select="n1:names">
																<xsl:for-each select="n1:shortLabel">
																	<xsl:apply-templates/>
																</xsl:for-each>
															</xsl:for-each>
														</xsl:for-each>
													</td>
													<td>
														<xsl:for-each select="n1:role">
															<xsl:apply-templates/>
														</xsl:for-each>
													</td>
													<td>
														<xsl:for-each select="n1:isTaggedProtein">
															<xsl:apply-templates/>
														</xsl:for-each>
													</td>
													<td>
														<xsl:for-each select="n1:isOverexpressedProtein">
															<xsl:apply-templates/>
														</xsl:for-each>
													</td>
													<xsl:if  test="position()=1">
														<xsl:element name="td">
															<xsl:attribute name="bgcolor">#3399FF</xsl:attribute>
															<xsl:attribute name="rowspan"><xsl:value-of select="last()"/></xsl:attribute>
															<xsl:for-each select="../../n1:interactionType">
																<xsl:apply-templates/>
															</xsl:for-each>
														</xsl:element>
													</xsl:if>
												</xsl:element>
												<xsl:if test="position()=last()">
													<xsl:text disable-output-escaping="yes">&lt;/tbody&gt;</xsl:text>
												</xsl:if>
												<xsl:if test="position()=last()">
													<xsl:text disable-output-escaping="yes">&lt;/table&gt;</xsl:text>
												</xsl:if>
											</xsl:for-each>
										</xsl:for-each>
									</td>
									
								</tr>
								<xsl:if test="position()=last()">
									<xsl:text disable-output-escaping="yes">&lt;/tbody&gt;</xsl:text>
								</xsl:if>
							</xsl:for-each>
						</xsl:for-each>
					</table>
					</xsl:for-each>
				</xsl:for-each>
			</center>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="n1:shortLabel">
		<b><xsl:value-of select="current()"></xsl:value-of></b>
	</xsl:template>
	
	<xsl:template name="sources">
		<xsl:for-each select="n1:source">
			<xsl:for-each select="n1:names">
				<h2><xsl:value-of select="n1:shortLabel"/></h2><br/>
				<h2><xsl:value-of select="n1:fullName"/></h2>
				
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="spaceline">
		<tr><td>.</td></tr>
	</xsl:template>

</xsl:stylesheet>
