<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="net:sf:psidev:mi" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<html>
			<head/>
			<body>
				<xsl:for-each select="n1:entrySet">
					<xsl:for-each select="n1:entry">
						<xsl:for-each select="n1:source">
							<xsl:for-each select="n1:names">
								<xsl:for-each select="n1:shortLabel">
									<xsl:apply-templates/>
								</xsl:for-each>
							</xsl:for-each>
						</xsl:for-each>
						<br/>
						<xsl:for-each select="n1:interactionList">
							<xsl:for-each select="n1:interaction">
								<xsl:if test="position()=1">
									<xsl:text disable-output-escaping="yes">&lt;table border="1"&gt;</xsl:text>
								</xsl:if>
								<xsl:if test="position()=1">
									<thead>
										<tr>
											<td/>
											<td>interactionType</td>
										</tr>
									</thead>
								</xsl:if>
								<xsl:if test="position()=1">
									<xsl:text disable-output-escaping="yes">&lt;tbody&gt;</xsl:text>
								</xsl:if>
								<tr>
									<td>
										<xsl:for-each select="n1:participantList">
											<xsl:for-each select="n1:participant">
												<xsl:if test="position()=1">
													<xsl:text disable-output-escaping="yes">&lt;table border="1"&gt;</xsl:text>
												</xsl:if>
												<xsl:if test="position()=1">
													<thead>
														<tr>
															<td/>
															<td>roles</td>
															<td>isTaggedProtein</td>
															<td>isOverexpressedProtein</td>
														</tr>
													</thead>
												</xsl:if>
												<xsl:if test="position()=1">
													<xsl:text disable-output-escaping="yes">&lt;tbody&gt;</xsl:text>
												</xsl:if>
												<tr>
													<td>
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
												</tr>
												<xsl:if test="position()=last()">
													<xsl:text disable-output-escaping="yes">&lt;/tbody&gt;</xsl:text>
												</xsl:if>
												<xsl:if test="position()=last()">
													<xsl:text disable-output-escaping="yes">&lt;/table&gt;</xsl:text>
												</xsl:if>
											</xsl:for-each>
										</xsl:for-each>
									</td>
									<td>
										<xsl:for-each select="n1:interactionType">
											<xsl:apply-templates/>
										</xsl:for-each>
									</td>
								</tr>
								<xsl:if test="position()=last()">
									<xsl:text disable-output-escaping="yes">&lt;/tbody&gt;</xsl:text>
								</xsl:if>
								<xsl:if test="position()=last()">
									<xsl:text disable-output-escaping="yes">&lt;/table&gt;</xsl:text>
								</xsl:if>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
