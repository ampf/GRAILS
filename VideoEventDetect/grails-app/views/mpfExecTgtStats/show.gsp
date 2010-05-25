
<%@ page import="com.appscio.mpf.grails.core.MpfExecTgtStats" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfExecTgtStats.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: mpfExecTgtStatsInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfExecTgtStats.sampleTime.label" default="Sample Time" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${mpfExecTgtStatsInstance?.sampleTime}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfExecTgtStats.et.label" default="Et" /></td>
                            
                            <td valign="top" class="value"><g:link controller="mpfExecTgt" action="show" id="${mpfExecTgtStatsInstance?.et?.id}">${mpfExecTgtStatsInstance?.et?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfExecTgtStats.runnningPerTgt.label" default="Runnning Per Tgt" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: mpfExecTgtStatsInstance, field: "runnningPerTgt")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfExecTgtStats.runningPerDb.label" default="Running Per Db" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: mpfExecTgtStatsInstance, field: "runningPerDb")}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${mpfExecTgtStatsInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
