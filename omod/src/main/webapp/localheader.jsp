<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
    </li>
    <openmrs:hasPrivilege privilege="Manage CCC Numbers">
        <li <c:if test='<%= request.getRequestURI().contains("cccgeneratorLocationForm") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/cccgenerator/cccgeneratorLocationForm.form">
            Location List
        </a>
        </li>
        <li <c:if test='<%= request.getRequestURI().contains("cccgeneratorForm") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/cccgenerator/cccgeneratorForm.form">
           Generate CCC Numbers per site
        </a>
        </li>
        <li <c:if test='<%= request.getRequestURI().contains("csvdownload") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/cccgenerator/csvdownload.form">
           Additional CCC Numbers per site
        </a>
        </li>
    </openmrs:hasPrivilege>
</ul>