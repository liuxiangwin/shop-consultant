<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>

<h2>Choose Country Page</h2>
<template:page pageTitle="${pageTitle}">


	<%--  <div class="span-24 section2">

		<cms:pageSlot position="Section2B" var="feature" element="div"
			class="span-6 zone_b thumbnail_detail">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</div> --%>
	
	
	<c:url value="${chooseUrl}" var="countryUrl" />
	<div class="form-group">
		<select id="country" name="country">
			<option value="UK">United Kingdom</option>
			<option value="ZH">China</option>
		</select> <input type="submit" onclick="formSubmit()">
	</div>

	<script type="text/javascript">
		function formSubmit() {
			//document.getElementById("chooseCountryform").submit();
			console.debug("This choose country from submmit");
			console.debug(document.getElementById('country').value)
			var countrySelected = document.getElementById('country').value;
			if (countrySelected == 'ZH') {
				//window.location.href='http://localhost:9001/consultingstorefront?clear=true&site=zh-consultingsite&country-selected=zh';
				//window.location.href='https://localhost:9002/consultingstorefront/zh-consultingsite/zh/%E5%BC%80%E5%8F%91/c/Development';
				//'https://localhost:9002/consultingstorefront/?clear=true&site=zh-consultingsite/zh/%E5%BC%80%E5%8F%91/c/Development';
				window.location.href ='https://zh-consultingsite.local:9002/consultingstorefront/zh-consultingsite/zh/Development/c/Development'
					
					
					
					
			} else {
				//window.location.href='https://localhost:9002/consultingstorefront?clear=true&site=uk-consultingsite&country-selected=uk';
				//window.location.href='https://localhost:9002/consultingstorefront/uk-consultingsite/en/Development/c/Development';

				//window.location.href = 'https://localhost:9002/consultingstorefront/?clear=true&site=uk-consultingsite/en/Development/c/Development'
				window.location.href ='https://uk-consultingsite.local:9002/consultingstorefront/uk-consultingsite/en/Development/c/Development'
			}
			sleep(500);
		}

		function sleep(numberMillis) {
			var now = new Date();
			var exitTime = now.getTime() + numberMillis;
			while (true) {
				now = new Date();
				if (now.getTime() > exitTime)
					return;
			}
		}
	</script>
</template:page>

