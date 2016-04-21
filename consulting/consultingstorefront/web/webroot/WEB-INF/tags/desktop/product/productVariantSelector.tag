<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

	

		<c:set var="showAddToCart"  value="" scope="session" />
		
		 <%-- <h2 id="variantOptionQualifier">${variantStyle.variantOptionQualifier[0]}</h2> --%>
		<!--  product.baseOptions[0].options = Service
		 	  product.baseOptions[1].options = Level    -->
<div id="shoot">
	<!-- <h2>Variant Product Detail</h2> -->
	<%-- <h2 id="variantType">${product.variantType}</h2>
	


	<h2 id=product_variant_Type>${product.variantType}</h2>
	<h2 id="product.baseOptions[0].options length">
		${fn:length(product.baseOptions[0].options)}</h2>
	<h2 id="product.baseOptions[0].options[0]">${product.baseOptions[0].options[0].variantOptionQualifiers[0].qualifier}</h2>

	<h2 id="product.baseOptions[1].options length">
		${fn:length(product.baseOptions[1].options)}</h2> --%>
		
		
	<%-- <h2 id="product.baseOptions[1]options[0]">
		${product.baseOptions[1].options[0].variantOptionQualifiers[0].qualifier}</h2>
	<h2 id="product.baseOptions[1]">
		${product.baseOptions[1].options[1].variantOptionQualifiers[0].qualifier}</h2>
	<h2 id="product.baseOptions[1]">
		${product.baseOptions[1].options[2].variantOptionQualifiers[0].qualifier}</h2> --%>
		
	<%-- 
	<h2 id="product baseOptions">${fn:length(product.baseOptions)}</h2>
	<h2 id="product baseOptions_element1">${product.baseOptions[0]}</h2>
	<h2 id="product baseOptions_element2">${product.baseOptions[1]}</h2>
	
	
	<h2>${fn:length(product.variantOptions)}</h2>

	<h2 id="0-Constult Service">${product.baseOptions[0].variantType}</h2>
	<h2 id="0-Constult Service">${product.name}</h2>
	<h2 id="1-Constult Level">${product.baseOptions[1].variantType}</h2> --%>
	
</div> 


<%-- Determine if product is one of apparel style or size variant --%>
		<%-- <c:if test="${product.variantType eq 'ApparelStyleVariantProduct'}"> --%>
		
<%-- 		<h2 id='Alan-product-variant--ConsultantServiceVariantProduct'>${product.variantType}</h2>
       <h2 id='Alan2-product-baseoption-0--ConsultantLevelVariantProduc'>${product.baseOptions[0].variantType}</h2> 
       <h2 id='Alan3-product-baseoption-1'>${product.baseOptions[1].variantType}</h2> 
       <h2 id='p-variantOptons-1'>${fn:length(product.variantOptions)}</h2> 
       <h2 id='p-variantOptons-2'>${product.variantOptions[2]}</h2> 
        <h2 id='p-level'>${product.baseOptions[0].options[0]}</h2>  --%>
        
		
		<%-- <c:if test="${product.variantType eq 'ConsultantLevelVariantProduct'}">
		<h2>${product.variantType eq 'ConsultantServiceVariantProduct'}</h2> --%> 
		
		<c:if test="${product.variantType eq 'ConsultantServiceVariantProduct'}">
			<%-- <c:set var="variantStyles" value="${product.variantOptions}" /> --%>
			<c:set var="variantStyles" value="${product.baseOptions[0].options}" />
		</c:if>
		
	<%-- 	
		<h2 id='size-option-1'>${fn:length(product.baseOptions[0].options)}</h2> 
		<h2 id='size-option-1'>${fn:length(product.baseOptions[1].options)}</h2> 
		<h2 id='size-option-1'>${product.baseOptions[0].variantType}</h2> 
		 --%>
		
		<%-- ------------------------------------------------------------------------------- 	
			This would be correct block code would render
		    1. product.variantType                  is "ConsultantServiceVariantProduct"
		    2. product.variantOptions               is 2 (SystemReview,CodeReview)
		    3. product.baseOptions[0].variantType   is " ConsultantLevelVariantProduct"
		    4. product.baseOptions[1].variantType   is "Null"
		    
			5. ${fn:length(product.baseOptions[0].options)} is  3 ,(Man) "Principal,Senior,Standard"
			6. ${fn:length(product.baseOptions[1].options) is   0        "..."
			7. product.baseOptions[0].variantType is "ConsultantLevelVariantProduct"
		       So the size options should be "${product.baseOptions[0].options}"	
			------------------------------------------------------------------------------- 
		 --%>
		 
		<c:if test="${(not empty product.baseOptions[0].options) 
			and (product.baseOptions[0].variantType eq 'ConsultantLevelVariantProduct')}">
			<c:set var="variantStyles" value="${product.baseOptions[0].options}" />
			<%-- <c:set var="variantStyles" value="${product.variantOptions}" /> --%>
			<%-- <c:set var="variantSizes" value="${product.baseOptions[0].variantOptions}" /> --%>
			<c:set var="variantSizes" value="${product.variantOptions}" />
			<c:set var="currentStyleUrl" value="${product.url}" />
		</c:if>
		
		<c:if test="${(not empty product.baseOptions[1].options) 
			and (product.baseOptions[0].variantType eq 'ConsultantLevelVariantProduct')}">
			<c:set var="variantStyles" value="${product.baseOptions[0].options}" />	
			<%-- <c:set var="variantSizes" value="${product.baseOptions[1].options}" />  --%>
			<c:set var="variantSizes" value="${product.variantOptions}" /> 
			<c:set var="currentStyleUrl" value="${product.baseOptions[1].selected.url}" />
		</c:if>
		
		<c:url value="${currentStyleUrl}" var="currentStyledProductUrl"/>
		
		<%-- Determine if product is other variant --%>
		<%-- <h1 id='value-variantStyles-detemine'>${empty variantStyles}</h1>  --%>
		<c:if test="${empty variantStyles}">
			<%-- -------------------------------------------------------------------------------
			  Current Product Responsiblity like SystemReview,CodeReview
			  -------------------------------------------------------------------------------
			--%>
			<%-- <h3 id='variantOp--product.variantOptions]'>${fn:length(product.variantOptions)}</h3> --%>
			<c:if test="${not empty product.variantOptions}">
				<c:set var="variantOptions" value="${product.variantOptions}" />
			</c:if>
			<%--------------------------------------------------------------------------------
			   Product Level like Stand,Senior
			  -------------------------------------------------------------------------------
			--%>
			<%-- <h3 id='variantOp--product.baseOptions[0]'>${fn:length(product.baseOptions[0].options)}</h3> --%>
			<c:if test="${not empty product.baseOptions[0].options}">
				<c:set  var="variantOptions" value="${product.baseOptions[0].options}" />
			</c:if>
		</c:if>
		<%-- <h1 id='two varianable'>${not empty variantStyles or not empty variantSizes}</h1> 
		<h1 id='stockLevel-staus'>${product.stock.stockLevelStatus.code}</h1>
		<h1 id='stock-Level-number'>${product.stock.stockLevel}</h1>  
		<h1 id='purchasable'>${product.purchasable}</h1>  --%>
		
		<c:if test="${not empty variantStyles or not empty variantSizes}">
			<c:choose>
				<c:when test="${product.stock.stockLevelStatus.code ne 'outOfStock' }">
					<c:set var="showAddToCart"  value="${true}" scope="session" />
				</c:when>
				<c:otherwise>
					<c:set var="showAddToCart" value="${false}" scope="session" />
				</c:otherwise>
			</c:choose> 
			<%-- <h1 id='showAddToCart'>${showAddToCart}</h1>  --%>
			
			<div class="variant_options_customersization">
			<%-- 	<c:if test="${not empty variantStyles}">
					<div class="colour clearfix">
						<div>Level Type</div>
						<ul class="colorlist">
							<c:forEach items="${variantStyles}" var="variantStyle">
								<c:forEach items="${variantStyle.variantOptionQualifiers}" var="variantOptionQualifier">
									
									<h3 id="v_name">${variantOptionQualifier.qualifier}</h3>
									
									<c:if test="${variantOptionQualifier.qualifier eq 'level'}">
										<c:set var="styleValue" value="${variantOptionQualifier.value}" />
										<c:set var="imageData" value="${variantOptionQualifier.image}" />
										<c:set var="vname" value="${variantOptionQualifier.name}" />
									</c:if>
								</c:forEach>
								
								<li <c:if test="${variantStyle.url eq currentStyleUrl}">class="selected"</c:if>>
									<c:url value="${variantStyle.url}" var="productStyleUrl"/>
									<a href="${productStyleUrl}" class="colorVariant" name="${variantStyle.url}">
										<c:if test="${not empty imageData}">
											<img src="${imageData.url}" title="${styleValue}" alt="${styleValue}"/>
										</c:if>
										<c:if test="${empty imageData}">
											<span class="swatch_colour_a" title="${styleValue}" >${variantStyle.code} -- ${styleValue}</span>
										</c:if>
									</a>
								</li>
							</c:forEach>
							
						</ul>
						
					</div>
				</c:if> --%>
				
				<c:if test="${not empty variantSizes}">
				
					<div class="size clearfix">
						<!-- <form> -->
							<label for="Size">Service Type</label>
									<select id="Size" class="variant-select" disabled="disabled" >
										<c:if test="${empty variantSizes}">
											<option selected="selected"><spring:theme code="product.variants.select.style"/></option>
										</c:if>
										<c:if test="${not empty variantSizes}">
											
											
											<%-- <option value="${currentStyledProductUrl}" --%>
											<%-- <option value=""
												 <c:if test="${empty variantParams['service']}">selected="selected"</c:if>>
												<spring:theme code="product.variants.select.size"/> 
											</option> --%>
											<c:forEach items="${variantSizes}" var="variantSize">
												<c:set var="optionsString" value="" />											
												
												<c:forEach items="${variantSize.variantOptionQualifiers}" var="variantOptionQualifier">
													<c:if test="${variantOptionQualifier.qualifier eq 'service'}">
														<c:set var="optionsString">${optionsString}&nbsp;${variantOptionQualifier.name}&nbsp;${variantOptionQualifier.value}, </c:set>
														<c:set var="serviceValue" value="${variantOptionQualifier.value}" />
													</c:if>
												</c:forEach>
												
												
												<%-- -------------------------------------------------------------------------------
												 	> 或 gt	大于检查
												 	<= 或 le	小于等于检查
												  -------------------------------------------------------------------------------
												--%>
												
												<%-- <h1 id='stock-button'>${(product.stock.stockLevel gt 0) and (product.stock.stockLevelStatus ne 'outOfStock')}</h1>  --%>
												<c:if test="${(product.stock.stockLevel gt 0) and (product.stock.stockLevelStatus ne 'outOfStock')}">
													<c:set var="stockLevel">${product.stock.stockLevel}&nbsp;<spring:theme code="product.variants.in.stock"/></c:set>
												</c:if>
												<c:if test="${(product.stock.stockLevel le 0) and (product.stock.stockLevelStatus eq 'inStock')}">
													<c:set var="stockLevel"><spring:theme code="product.variants.available"/></c:set>
												</c:if>
												<c:if test="${(product.stock.stockLevel le 0) and (product.stock.stockLevelStatus ne 'inStock')}">
													<c:set var="stockLevel"><spring:theme code="product.variants.out.of.stock"/></c:set>
												</c:if>

												<%-- <c:if test="${(variantSize.url eq product.url)}"> --%>
													<c:set var="showAddToCart" value="${true}" scope="session" />
												<%-- </c:if>  --%>
												
												<c:url value="${variantSize.url}" var="variantOptionUrl"/>
												<%-- <option value="${variantOptionUrl}" 
													${(variantSize.url eq product.url) ? 'selected="selected"' : ''}>
													${optionsString}&nbsp;
													<format:price priceData="${variantSize.priceData}"/>&nbsp;&nbsp;
													${variantSize.stock.stockLevel}
												</option> --%>
												
												<%-- <option value="${variantOptionUrl}"> --%>
												<option value="">
												${serviceValue}													
												</option>
											</c:forEach>
										</c:if>
									</select>
							
						<!-- </form> -->
						<a href="#"  class="size-guide" title="<spring:theme code="product.variants.size.guide"/>">&nbsp;</a>
					</div>
				</c:if>
			</div>
		</c:if>
		<%-- <h1 id='variantOption-number'>${fn:length(variantOptions}</h1>
		<c:if test="${not empty variantOptions}">
			<div class="variant_options">
				<div class="size">
					<select id="variant" class="variant-select" disabled="disabled">
						<option selected="selected" disabled="disabled"><spring:theme code="product.variants.select.variant"/></option>
						<c:forEach items="${variantOptions}" var="variantOption">
							<c:set var="optionsString" value="" />
							<c:forEach items="${variantOption.variantOptionQualifiers}" var="variantOptionQualifier">
								<c:set var="optionsString">${optionsString}&nbsp;${variantOptionQualifier.name}&nbsp;${variantOptionQualifier.value}, </c:set>
							</c:forEach>

							<c:if test="${(variantOption.stock.stockLevel gt 0) and (variantSize.stock.stockLevelStatus ne 'outOfStock')}">
								<c:set var="stockLevel">${variantOption.stock.stockLevel} <spring:theme code="product.variants.in.stock"/></c:set>
							</c:if>
							<c:if test="${(variantOption.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus eq 'inStock')}">
								<c:set var="stockLevel"><spring:theme code="product.variants.available"/></c:set>
							</c:if>
							<c:if test="${(variantOption.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus ne 'inStock')}">
								<c:set var="stockLevel"><spring:theme code="product.variants.out.of.stock"/></c:set>
							</c:if>

							<c:choose>
								<c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
									<c:set var="showAddToCart"  value="${true}" scope="session"/>
								</c:when>
								<c:otherwise>
									<c:set var="showAddToCart" value="${false}" scope="session" />
								</c:otherwise>
							</c:choose>
							
							<c:url value="${variantOption.url}" var="variantOptionUrl"/>
							<option value="${variantOptionUrl}" ${(variantOption.url eq product.url) ? 'selected="selected"' : ''}>
								${optionsString}&nbsp;<format:price priceData="${variantOption.priceData}"/>&nbsp;&nbsp;${variantOption.stock.stockLevel}
							</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</c:if> --%>