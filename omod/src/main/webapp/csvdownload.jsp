<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage CCC Numbers" otherwise="/login.htm"
                 redirect="/module/cccgenerator/csvdownload.form" />

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
    $j('#csv').click(function() {
       var v1 = $j('#csvsite').val();
        var v2=$j('#csvval').val(v1);
        var v3=$j('#csvval').val();
        var addition=$j('#additionals').val();
        window.open("csvdownloadafter.htm?site=" + v1+"&additionalNumbers="+addition, 'Download csv');
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
<b class="boxHeader"><spring:message code="CSV Download - Patients Per Location" /></b>
<div class="box">
    <form>
        <table>
            <tr>
                <td>Select Site</td>
                <td>
                    <select name="site" id="csvsite">
                        <c:forEach var="sitelocation" items="${siteLocation}">
                            <option  value="${sitelocation.name}">${sitelocation.name}</option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                    Additional Numbers to generate:
                </td>
                <td>
                    <input type="text" name="additionalNumbers" id="additionals" value="" onKeyPress="return checkIt(event)">
                </td>

                <td>
                    <input type="button" value="Download CSV" id="csv" />
                </td>
                <td>
                    <div id="numbererror" class="error" style="display: none;" />
                </td>
            </tr>
        </table>
    </form>

</div>
<input type="hidden" name="csvsite"  id="csvval" />




































<%@ include file="/WEB-INF/template/footer.jsp"%>