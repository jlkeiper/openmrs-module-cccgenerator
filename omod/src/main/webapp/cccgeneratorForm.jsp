<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage CCC Numbers" otherwise="/login.htm"
                 redirect="/module/cccgenerator/cccgeneratornForm.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localheader.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/highlight/jquery.highlight-3.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.filteringDelay.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<link href="<openmrs:contextPath/>/scripts/jquery-ui/css/<spring:theme code='jqueryui.theme.name' />/jquery-ui.custom.css" type="text/css" rel="stylesheet" />

<script type="text/javascript">
    $j(document).ready(function(){
        $j('#download').click(function() {
            var addi=$j('#additionals').val();
            var loc= $j('#downloadcontent').val();
            window.open("csvdownload.htm?sitelocationToDownload="+loc+"&additionalNumbers="+addi, 'Download csv');
            return false;
        });

    });
    function checkIt(evt) {
        evt = (evt) ? evt : window.event
        var charCode = (evt.which) ? evt.which : evt.keyCode
        if (charCode > 31 && (charCode < 48 || charCode > 57)) {
            status = "This field accepts numbers only."
            return false
        }
        status = ""
        return true
    }
</script>

<b class="boxHeader"><spring:message code="CCC Generator - Patients Per Location" /></b>
 <div class="box">
  <form method="POST">
     <table>
         <tr>
             <td>Select cohort</td>
             <td>
                 <select name="cohort" id="cohortid">
                     <c:forEach var="listOfCohorts" items="${listOfCohort}">
                         <option  value="${listOfCohorts.uuid}">${listOfCohorts.name}</option>
                     </c:forEach>
                 </select>
             </td>
             <td>Select Site</td>
             <td>
                 <select name="site" id="siteid">
                     <c:forEach var="sitelocation" items="${siteLocations}">
                         <option  value="${sitelocation.locationId}">${sitelocation.name}</option>
                     </c:forEach>
                 </select>
             </td>
             <td>
                 <input type="submit" value="Generate" />
             </td>
         </tr>
     </table>
  </form>
 </div>

<input type="hidden" name="sitelocationToDownload" id="downloadcontent" value="${location1}"  />

<c:if test="${not empty location1}">
<b class="boxHeader">Information</b>


<div class="box">
    <p>${totalcccnumbersgenerated}&nbsp;CCC Numbers created.</p>
      <table>
          <tr>
            <td>Enter additional numbers to generate:</td>
            <td> <input type="text" name="additionalNumbers" id="additionals" value="" onKeyPress="return checkIt(event)"> </td>
            <td><input type="button" id="download" value="Download CSV"  /> </td>
          </tr>
      </table>
</div>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>
