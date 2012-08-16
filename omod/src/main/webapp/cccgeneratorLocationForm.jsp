<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage CCC Numbers" otherwise="/login.htm"
                 redirect="/module/cccgenerator/cccgeneratorLocationForm.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localheader.jsp"%>

<script type="text/javascript">

</script>
<b class="boxHeader"><spring:message code="CCC Generator - Code Entry Form" /></b>


<div class="box">
<c:choose>
	<c:when test="${not empty locationList}">
	
<table border="0">
  <tr>
   <th>Location ID</th>
   <th>Name</th>
   <th>Parent Location</th>
   <th>Enter CCC code</th>
   <th>Save Entry</th>
   
  </tr>
  <c:forEach var="locations" items="${locationList}">
      <form name="newCCCLocation"  method="post">
      <tr align="">
        <td>${locations.locationId}<input type="hidden" name="location_id" value=${locations.locationId}></td>
        <td> ${locations.name}</td>
        <td>${locations.parentLocation}</td>
        <td><input type="text" name="CCC" size="10" maxlength="5"></td>
        <td><input type="submit" name="submit" value="Save Entry"></td>
        </tr> 
        </form>       
  </c:forEach>
</table>
</c:when>
<c:otherwise>
       <p> All CCC numbers have been entered. Please click <a href="cccgeneratorForm.form">here</a>for redirection</p>
</c:otherwise>
</c:choose>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
