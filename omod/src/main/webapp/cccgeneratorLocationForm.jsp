<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage CCC Numbers" otherwise="/login.htm"
                 redirect="/module/cccgenerator/cccgeneratorLocationForm.form"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localheader.jsp" %>

<b class="boxHeader"><spring:message code="CCC Generator - Code Entry Form"/></b>

<div class="box">
    <c:choose>
        <c:when test="${not empty locationList}">
            <table border="0">
                <tr>
                    <th>Location</th>
                    <th>Parent Location</th>
                    <th>CCC code</th>
                    <th>Action</th>
                </tr>
                <c:forEach var="cccLocation" items="${locationList}">
                    <c:set var="location" value="${cccLocation.location}"/>
                    <form name="newCCCLocation" method="post">
                        <input type="hidden" name="location_id" value=${location.locationId}>
                        <tr>
                            <td>${location}</td>
                            <td>${location.parentLocation}</td>
                            <td><input type="text" name="CCC" size="10" maxlength="5" value="${cccLocation.CCC}"/></td>
                            <td><input type="submit" name="submit" value="Save Entry"/></td>
                        </tr>
                    </form>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            <p>
                No locations exist. Please add locations and return here to set CCC codes.
            </p>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
