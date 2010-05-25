

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Show MpfTask</title>
</head>
<body>

<div class="body">
<h1>Show MpfTask</h1>
<g:if test="${flash.message}">
  <div class="message">${flash.message}</div>
</g:if>
<div class="dialog">
<table>
  <tbody>


    <tr class="prop">
      <td valign="top" class="name">Id:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'id')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Name:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'name')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Mpf Detector Name:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'mpfDetectorName')}</td>

    </tr>
        <tr class="prop">
      <td valign="top" class="name">Detector Prefix:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'detectPrefix')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Video File Url:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'videoFileUrl')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Timeout:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'timeout')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Status:</td>

      <td valign="top" class="value">${mpfTaskInstance?.status?.encodeAsHTML()}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Status Msg:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'statusMsg')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Exec Tgt:</td>

      <td valign="top" class="value"><g:link controller="mpfExecTgt"
        action="show" id="${mpfTaskInstance?.execTgt?.id}">${mpfTaskInstance?.execTgt?.tgtName}</g:link></td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Sourcekey:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'sourcekey')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Event Rel Url:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'eventRelUrl')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Status Rel Url:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'statusRelUrl')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Remote Staging Dir:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'remoteStagingDir')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Date Created:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'dateCreated')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Last Updated:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'lastUpdated')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Detected Events:</td>

      <td valign="top" style="text-align: left;" class="value">
      <ul>
        <g:each var="d" in="${mpfTaskInstance.detectedEvents}">
          <li><g:link controller="mpfEvent" action="show" id="${d.id}">${d?.encodeAsHTML()}</g:link></li>
        </g:each>
      </ul>
      </td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Foreground Thread:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'foregroundThread')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Required Capability:</td>

      <td valign="top" style="text-align: left;" class="value">
      <ul>
        <g:each var="r" in="${mpfTaskInstance.requiredCapability}">
          <li><g:link controller="mpfCapability" action="show"
            id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
        </g:each>
      </ul>
      </td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Total Run Time Ms:</td>

      <td valign="top" class="value">${fieldValue(bean:mpfTaskInstance,
      field:'totalRunTimeMs')}</td>

    </tr>

    <tr class="prop">
      <td valign="top" class="name">Detector:</td>

      <td valign="top" class="value"><g:link controller="null"
        action="show" id="${mpfTaskInstance?.detector?.id}">${mpfTaskInstance?.detector?.encodeAsHTML()}</g:link></td>

    </tr>

  </tbody>
</table>
</div>
<div class="buttons"><g:form>
  <input type="hidden" name="id" value="${mpfTaskInstance?.id}" />
  <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
  <span class="button"><g:actionSubmit class="delete"
    onclick="return confirm('Are you sure?');" value="Delete" /></span>
</g:form></div>
</div>
</body>
</html>
