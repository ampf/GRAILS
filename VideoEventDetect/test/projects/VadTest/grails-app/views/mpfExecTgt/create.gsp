

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Create MpfExecTgt</title>
</head>
<body>

<div class="body">
<h1>Create MpfExecTgt</h1>
<g:if test="${flash.message}">
  <div class="message">${flash.message}</div>
</g:if> <g:hasErrors bean="${mpfExecTgtInstance}">
  <div class="errors"><g:renderErrors bean="${mpfExecTgtInstance}"
    as="list" /></div>
</g:hasErrors> <g:form action="save" method="post">
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
        <td valign="top" class="name"><label for="username">Target
        Username:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'username','errors')}">
        <input type="text" id="username" name="username"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'username')}" />
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="pw">Target
        PW:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'pw','errors')}">
        <input type="text" id="pw" name="pw"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'pw')}" /></td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="limitTasks">Set
        Limit Tasks:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'limitTasks','errors')}">
        <input type="text" id="limitTasks" name="limitTasks"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'limitTasks')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="priority">Default
        Priority:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'priority','errors')}">
        <input type="text" id="priority" name="priority"
          value="${fieldValue(bean:mpfExecTgtInstance,field:'priority')}" />
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="capabilities">Capabilities:</label>
        </td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfExecTgtInstance,field:'capabilities','errors')}">
        <g:select name="capabilities" from="${capabilities}" size="5"
          multiple="yes" optionKey="id"
          value="${mpfExecTgtInstance?.capabilities}" /></td>
      </tr>
    </tbody>
  </table>
  </div>
  <div class="buttons"><span class="button"><input
    class="save" type="submit" value="Create" /></span></div>
</g:form></div>
</body>
</html>
