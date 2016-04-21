<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<h2>Choose Country Page</h2>

<%-- <c:url value="${currentStepUrl}" var="consolidatePickupUrl"/>
<form:form id="selectDeliverylocationForm" action="${consolidatePickupUrl}" method="POST">
 --%>
<c:url value="${chooseUrl}" var="countryUrl"/>
<form:form id="chooseCountryform"  action="${countryUrl}" method="POST">
	<div class="form-group">
		<select id="country" name="country">
			<option value="UK">United Kingdom</option>
			<option value="ZH">China</option>
		</select>
		 <input type="submit">  
	</div>
</form:form>


