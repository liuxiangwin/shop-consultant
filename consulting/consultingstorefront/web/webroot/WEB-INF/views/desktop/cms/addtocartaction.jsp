<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="${url}" var="addToCartUrl"/>
<form:form method="post" id="addToCartForm" class="add_to_cart_form span-5" action="${addToCartUrl}">
	<c:if test="${product.purchasable}">
		<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
	</c:if>
	<input type="hidden" name="productCodePost" value="${product.code}"/>
    
  <%--   <h2 id='showAddToCart'>${showAddToCart}</h2>
    <h2 id='product.code'>${product.code}</h2>
    <h2 id='product.baseOptions[0].variantType'>${product.baseOptions[0].variantType}</h2>
     
    <h2 id='product.purchasable'>${product.purchasable}</h2>
     <h2 id='product.stock.stockLevelStatus'>${product.stock.stockLevelStatus}</h2>
    <h2 id='contains(buttonType'>${fn:contains(buttonType, 'button')}</h2>
     <h2 id='type'>${product.baseOptions[0].variantType eq 'ConsultantServiceVariantProduct'}</h2> --%>
    
	<c:if test="${empty showAddToCart ? true : showAddToCart}">
		<c:set var="buttonType">button</c:set>
	
	
		<c:choose>
			<c:when test="${product.baseOptions[0].variantType eq 'ConsultantServiceVariantProduct'}">
		
			</c:when>

			<c:otherwise>
				<c:if test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
					<c:set var="buttonType">submit</c:set>
				</c:if>
			</c:otherwise>
		</c:choose>
	
	
		
		
		
		
		<%-- <h2 id='contains type'>${fn:contains(buttonType, 'button')}</h2> --%>
		<c:choose>
			<c:when test="${fn:contains(buttonType, 'button')}">
				<button type="${buttonType}" class="addToCartButton outOfStock" disabled="disabled">
					<spring:theme code="product.variants.out.of.stock"/>
				</button>
			</c:when>

			<c:otherwise>
				<button id="addToCartButton" type="${buttonType}" class="addToCartButton" disabled="disabled">
					<spring:theme code="basket.add.to.basket"/>
				</button>
			</c:otherwise>
		</c:choose>
	</c:if>
</form:form>
