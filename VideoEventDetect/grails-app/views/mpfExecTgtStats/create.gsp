
<%@ page import="com.appscio.mpf.grails.core.MpfExecTgtStats" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${mpfExecTgtStatsInstance}">
            <div class="errors">
                <g:renderErrors bean="${mpfExecTgtStatsInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="sampleTime"><g:message code="mpfExecTgtStats.sampleTime.label" default="Sample Time" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfExecTgtStatsInstance, field: 'sampleTime', 'errors')}">
                                    <g:datePicker name="sampleTime" precision="day" value="${mpfExecTgtStatsInstance?.sampleTime}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="et"><g:message code="mpfExecTgtStats.et.label" default="Et" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfExecTgtStatsInstance, field: 'et', 'errors')}">
                                    <g:select name="et.id" from="${com.appscio.mpf.grails.core.MpfExecTgt.list()}" optionKey="id" value="${mpfExecTgtStatsInstance?.et?.id}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="runnningPerTgt"><g:message code="mpfExecTgtStats.runnningPerTgt.label" default="Runnning Per Tgt" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfExecTgtStatsInstance, field: 'runnningPerTgt', 'errors')}">
                                    <g:textField name="runnningPerTgt" value="${fieldValue(bean: mpfExecTgtStatsInstance, field: 'runnningPerTgt')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="runningPerDb"><g:message code="mpfExecTgtStats.runningPerDb.label" default="Running Per Db" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfExecTgtStatsInstance, field: 'runningPerDb', 'errors')}">
                                    <g:textField name="runningPerDb" value="${fieldValue(bean: mpfExecTgtStatsInstance, field: 'runningPerDb')}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
