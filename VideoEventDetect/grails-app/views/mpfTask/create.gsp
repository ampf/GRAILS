<%@ page import="com.appscio.mpf.grails.core.*" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Create MpfTask</title>
</head>
<body>

<div class="nav"><span class="menuButton"><a class="home"
  href="${createLink(uri:'')}">Home</a></span> <span class="menuButton"><g:link
  class="list" action="list">MpfTask List</g:link></span>
</div>

<div class="body">
<h1>Create MpfTask</h1>
<g:if test="${flash.message}">
  <div class="message">${flash.message}</div>
</g:if> <g:hasErrors bean="${mpfTaskInstance}">
  <div class="errors"><g:renderErrors bean="${mpfTaskInstance}"
    as="list" /></div>
</g:hasErrors>

<g:form action="save" method="post">
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
        <td valign="top" class="name"><label for="detector">Detector:</label>
        </td>

        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'mpfDetectorName','errors')}">
        <g:select from="${detectors}" name="mpfDetectorName"
          noSelection="['null':'']"></g:select></td>
      </tr>
      <tr>
        <td></td>
        <td><table>
	      <g:each in="${prefix_strings}">
	        	<tr><td>${it.key}</td><td>${it.value}<br/></td></tr>
	       </g:each>
	      </table>
	      </td></tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="detectPrefix">Detector Prefix:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'detectPrefix','errors')}">
        <input type="text" id="detectPrefix" name="detectPrefix" size="80"
          value="${fieldValue(bean:mpfTaskInstance,field:'detectPrefix')}" />
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="timeout">Timeout:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'timeout','errors')}">
        <input type="text" id="timeout" name="timeout"
          value="${fieldValue(bean:mpfTaskInstance,field:'timeout')}" /> (blank is no timeout; use d, m, h, s suffixes )</td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="videoFileUrl">Video
        File Url:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'videoFileUrl','errors')}">
        <input type="text" id="videoFileUrl" name="videoFileUrl" size="80"
          value="${fieldValue(bean:mpfTaskInstance,field:'videoFileUrl')}" />
        </td>
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
        <td valign="top" class="name"><label for="detector">Use
        GPU?:</label></td>

        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'useGPU','errors')}">
        <g:select from="${['if_avail','required', 'do_not_use']}"
          name="useGPU" noSelection="['':'if_avail']"></g:select></td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><label for="execTgt">Exec
        Tgt:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'execTgt','errors')}">
          <g:select name="execTgt.id" from="${targets}" optionKey="id" optionValue="tgtName"></g:select></td>
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
        <input type="text" id="statusRelUrl" name="statusRelUrl"  size="80"
          value="${fieldValue(bean:mpfTaskInstance,field:'statusRelUrl')}" />
        </td>
      </tr>

      <tr class="prop">
        <td valign="top" class="name"><label for="eventRelUrl">Event
        Rel Url:</label></td>
        <td valign="top"
          class="value ${hasErrors(bean:mpfTaskInstance,field:'eventRelUrl','errors')}">
        <input type="text" id="eventRelUrl" name="eventRelUrl"  size="80"
          value="${fieldValue(bean:mpfTaskInstance,field:'eventRelUrl')}" />
        </td>
      </tr>

    </tbody>
  </table>
  </div>
  <div class="buttons"><span class="button"><input
    class="save" type="submit" value="Create" /></span></div>
</g:form></div>
</body>
</html>
