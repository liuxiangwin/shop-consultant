<%@ page trimDirectiveWhitespaces="true" %>
<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>



<c:if test="${ currentPos.id ne 'empty'}">
    <span id="pos-selector-selected-label">Selected country ${currentPos.name}-(<a id="pos-selector-edit" href="#">Edit</a>)</span>
</c:if>

<div style="display: none">
    <div id="pos-selector-modal">
        <h2><spring:theme code=""/></h2>

        <div>
            <p>
                <spring:theme code=""/>
            </p>
        </div>
        <c:choose>
            <c:when test="${not empty pos}">
                <c:url value="/_s/pos" var="postURL"/>
                <form:form action="${postURL}" method="post" id="pos-form">
                    <div class="form-group">
                        <select name="code" id="pos-selector" class="form-control" sessionstore="${SELECTED_BASE_STORE}">
                            <c:forEach items="${pos}" var="pos">
                                <c:choose>
                                    <c:when test="${pos.id == currentPos.id}">
                                        <option value="${pos.id}" selected="selected">
                                                ${pos.name}
                                        </option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${pos.id}">
                                                ${pos.name}
                                        </option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-default"><spring:theme
                                code="posselector.select.store"/></button>
                    </div>
                </form:form>
            </c:when>
            <c:otherwise>
                <h3><spring:theme code="posselector.no.pos.data.available"/></h3>
            </c:otherwise>
        </c:choose>

    </div>
</div>



