

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Edit MpfExecTgt</title>
</head>
<body>

<div class="body">
<h1>Edit MpfExecTgt</h1>
<g:if test="${flash.message}">
  <div class="message">${flash.message}</div>
</g:if> <g:hasErrors bean="${mpfExecTgtInstance}">
  <div class="errors"><g:renderErrors bean="${mpfExecTgtInstance}"
    as="list" /></div>
</g:hasErrors> <g:form method="post">
  <input type="hidden" name="id" value="${mpfExecTgtInstance?.id}" />
  <input type="hidden" name="version"
    value="${mpfExecTgtInstance?.version}" />
  <div class="dialog">
  <table>
    <tbody>


      <tr class="prop">
        <td valign="top" class="name"><label for="tgtName">Exec
        Target Label:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'tgtName','errors')}">
        <input type="text" id="tgtName" name="tgtName"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'tgtName')}" /></td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="ip">Machine
        Name or IP:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'ip','errors')}">
        <input type="text" id="ip" name="ip"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'ip')}" /></td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="limitTasks">Limit
        Tasks:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'limitTasks','errors')}">
        <input type="text" id="limitTasks" name="limitTasks"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'limitTasks')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="priority">Priority:</label>
        </td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'priority','errors')}">
        <input type="text" id="priority" name="priority"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'priority')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="pw">Target
        User PW:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'pw','errors')}">
        <input type="text" id="pw" name="pw"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'pw')}" /></td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="username">Target
        Username:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'username','errors')}">
        <input type="text" id="username" name="username"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'username')}" />
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="capabilities">Capabilities:</label>
        </td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'capabilities','errors')}">
        <g:select name="capabilities" from="${capabilities}"
          size="5" multiple="yes" optionKey="id"
          value="${mpfExecTgtInstance?.capabilities}" /></td>
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
