<%@ page import="com.appscio.mpf.grails.core.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'simpleMpfEventData.label', default: 'SimpleMpfEventData')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${simpleMpfEventDataInstance}">
            <div class="errors">
                <g:renderErrors bean="${simpleMpfEventDataInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${simpleMpfEventDataInstance?.id}" />
                <g:hiddenField name="version" value="${simpleMpfEventDataInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="mpfEvent"><g:message code="simpleMpfEventData.mpfEvent.label" default="Mpf Event" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: simpleMpfEventDataInstance, field: 'mpfEvent', 'errors')}">
                                    <g:select name="mpfEvent.id" from="${com.appscio.mpf.grails.core.MpfEvent.list()}" optionKey="id" value="${simpleMpfEventDataInstance?.mpfEvent?.id}" noSelection="['null': '']" />
                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
