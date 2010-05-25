

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Edit MpfTask</title>
</head>
<body>
<!--
<div class="nav"><span class="menuButton"><a class="home"
  href="${resource(dir:'')}">Home</a></span> <span class="menuButton"><g:link
  class="list" action="list">MpfTask List</g:link></span> <span
  class="menuButton"><g:link class="create" action="create">New MpfTask</g:link></span>
</div>
-->
<div class="body">
<h1>Clone MpfTask</h1>
<g:if test="${flash.message}">
  <div class="message">${flash.message}</div>
</g:if> <g:hasErrors bean="${mpfTaskInstance}">
  <div class="errors"><g:renderErrors bean="${mpfTaskInstance}"
    as="list" /></div>
</g:hasErrors> <g:form method="post">
  <input type="hidden" name="id" value="${mpfTaskInstance?.id}" />
  <input type="hidden" name="version" value="${mpfTaskInstance?.version}" />
  <div class="dialog">
  <table>
    <tbody>

      <tr class="prop">
        <td valign="top" class="name"><label for="name">Name:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'name','errors')}">
        <input type="text" id="name" name="name"
          value="${fieldValue(bean:mpfTaskInstance,field:'name')}" /></td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="mpfDetectorName">Mpf
        Detector Name:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'mpfDetectorName','errors')}">
        <input type="text" id="mpfDetectorName" name="mpfDetectorName"
          value="${fieldValue(bean:mpfTaskInstance,field:'mpfDetectorName')}" />
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="detectPrefix">Detector Prefix:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'detectPrefix','errors')}">
        <input type="text" id="detectPrefix" name="detectPrefix" size="80"
          value="${fieldValue(bean:mpfTaskInstance,field:'detectPrefix')}" />
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="videoFileUrl">Video
        File Url:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'videoFileUrl','errors')}">
        <input type="text" id="videoFileUrl" name="videoFileUrl"
          value="${fieldValue(bean:mpfTaskInstance,field:'videoFileUrl')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="status">Status:</label>
        </td>
        <td valign="top">${mpfTaskInstance?.status}</td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="statusMsg">Status
        Msg:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'statusMsg','errors')}">
        <input type="text" id="statusMsg" name="statusMsg"
          value="${fieldValue(bean:mpfTaskInstance,field:'statusMsg')}" /></td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="execTgt">Exec
        Tgt:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'execTgt','errors')}">
          <g:select name="execTgt.id" from="${targets}" optionKey="id" optionValue="tgtName"></g:select></td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="sourcekey">Sourcekey:</label>
        </td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'sourcekey','errors')}">
        <input type="text" id="sourcekey" name="sourcekey"
          value="${fieldValue(bean:mpfTaskInstance,field:'sourcekey')}" /></td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="eventRelUrl">Event
        Rel Url:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'eventRelUrl','errors')}">
        <input type="text" id="eventRelUrl" name="eventRelUrl"
          value="${fieldValue(bean:mpfTaskInstance,field:'eventRelUrl')}" />
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name">Send to host:</td>
        <td>
         <ul id="logcontrols" style="list-style-type:none; padding-right:20px">
            <li style="display:inline;">LOG: <g:select from="${com.appscio.mpf.utility.LogLevels.levelnames}" name="log_to_host" value="${mpfTaskInstance?.log_to_host}"></g:select></li>
            <li style="display:inline;">OUT: <g:select from="${com.appscio.mpf.utility.LogLevels.levelnames}" name="out_to_host" value="${mpfTaskInstance?.out_to_host}"></g:select></li>
            <li style="display:inline;">ERR: <g:select from="${com.appscio.mpf.utility.LogLevels.levelnames}" name="err_to_host" value="${mpfTaskInstance?.err_to_host}"></g:select></li>
           </ul>
       </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="statusRelUrl">Status
        Rel Url:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'statusRelUrl','errors')}">
        <input type="text" id="statusRelUrl" name="statusRelUrl"
          value="${fieldValue(bean:mpfTaskInstance,field:'statusRelUrl')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="remoteStagingDir">Remote
        Staging Dir:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'remoteStagingDir','errors')}">
        <input type="text" id="remoteStagingDir" name="remoteStagingDir"
          value="${fieldValue(bean:mpfTaskInstance,field:'remoteStagingDir')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="dateCreated">Date
        Created:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'dateCreated','errors')}">
        <g:datePicker name="dateCreated"
          value="${mpfTaskInstance?.dateCreated}" precision="minute"></g:datePicker>
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="lastUpdated">Last
        Updated:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'lastUpdated','errors')}">
        <g:datePicker name="lastUpdated"
          value="${mpfTaskInstance?.lastUpdated}" precision="minute"></g:datePicker>
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="detectedEvents">Detected
        Events:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'detectedEvents','errors')}">

        <ul>
          <g:each var="d" in="${mpfTaskInstance?.detectedEvents?}">
            <li><g:link controller="mpfEvent" action="show" id="${d.id}">${d?.encodeAsHTML()}</g:link></li>
          </g:each>
        </ul>
        <g:link controller="mpfEvent"
          params="['mpfTask.id':mpfTaskInstance?.id]" action="create">Add MpfEvent</g:link>

        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="foregroundThread">Foreground
        Thread:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'foregroundThread','errors')}">
        <g:checkBox name="foregroundThread"
          value="${mpfTaskInstance?.foregroundThread}"></g:checkBox></td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="requiredCapability">Required
        Capability:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'requiredCapability','errors')}">
        <g:select name="requiredCapability" from="${capabilities}"
          size="5" multiple="yes" optionKey="id"
          value="${mpfTaskInstance?.requiredCapability}" /></td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="totalRunTimeMs">Total
        Run Time Ms:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'totalRunTimeMs','errors')}">
        <input type="text" id="totalRunTimeMs" name="totalRunTimeMs"
          value="${fieldValue(bean:mpfTaskInstance,field:'totalRunTimeMs')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="detector">Detector:</label>
        </td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'detector','errors')}">
        <g:select optionKey="id"
          from="${com.appscio.mpf.MpfDetector.list()}" name="detector.id"
          value="${mpfTaskInstance?.detector?.id}"></g:select></td>
      </tr>

    </tbody>
  </table>
  </div>
  <div class="buttons"><span class="button"><g:actionSubmit
    class="save" value="Update" /></span> <span class="button"><g:actionSubmit
    class="delete" onclick="return confirm('Are you sure?');"
    value="Delete" /></span></div>
</g:form></div>
</body>
</html>
