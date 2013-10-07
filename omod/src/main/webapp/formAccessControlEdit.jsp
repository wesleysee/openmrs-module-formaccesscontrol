<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Form Access Control" otherwise="/login.htm" redirect="/module/formaccesscontrol/formAccessControlEdit.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="/WEB-INF/view/admin/forms/localHeader.jsp" %>

<script>
// hack to highlight Manage Forms menu link
$j('#menu').children()[1].className = 'active';
</script>

<h2><spring:message code="formaccesscontrol.title"/></h2>

<form:form method="post" modelAttribute="formAccessControlForm">
<table style="border-spacing: 10px;">
	<tr>
		<th align="right"><spring:message code="formaccesscontrol.form"/></th>
		<td>
			${form.name}
		</td>
	</tr>
	<tr>
		<th align="right" style="vertical-align: top;"><spring:message code="formaccesscontrol.accessControl"/></th>
		<td>
		
			<table cellpadding="2" cellspacing="0" width="100%">
				<tr>
					<th><spring:message code="formaccesscontrol.role"/></th>
					<th align="center"><spring:message code="formaccesscontrol.accessControl.create"/></th>
					<th align="center"><spring:message code="formaccesscontrol.accessControl.view"/></th>
					<th align="center"><spring:message code="formaccesscontrol.accessControl.update"/></th>
				</tr>
				<tr class="oddRow">
					<td> ${superuser}</td>
					<td align="center"><input type="checkbox" checked="checked" disabled="disabled" /></td>
					<td align="center"><input type="checkbox" checked="checked" disabled="disabled" /></td>
					<td align="center"><input type="checkbox" checked="checked" disabled="disabled" /></td>
				</tr>
				<c:forEach items="${formAccessControlForm.formAccessControls}" var="formAccessControl" varStatus="rowStatus">
					<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
						<td>${formAccessControl.role.role}</td>
						<td align="center"><form:checkbox path="formAccessControls[${rowStatus.index}].canCreate" /></td>
						<td align="center"><form:checkbox path="formAccessControls[${rowStatus.index}].canView" /></td>
						<td align="center"><form:checkbox path="formAccessControls[${rowStatus.index}].canUpdate" /></td>
					</tr>	
				</c:forEach>
				<c:if test="${empty formAccessControlForm.formAccessControls}">
					<tr>
						<td colspan="4" style="padding: 10px; text-align: center"><spring:message code="formaccesscontrol.noresults"/></td>
					</tr>
				</c:if>
			</table>
		</td>
	</tr>
</table>

<input type="submit" value="<openmrs:message code="formaccesscontrol.save"/>">
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
