
<%@ page import="com.appscio.mpf.grails.core.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'simpleMpfEventData.label', default: 'SimpleMpfEventData')}" />
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
            <g:hasErrors bean="${simpleMpfEventDataInstance}">
            <div class="errors">
                <g:renderErrors bean="${simpleMpfEventDataInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
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

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="body"><g:message code="simpleMpfEventData.body.label" default="Body" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: simpleMpfEventDataInstance, field: 'body', 'errors')}">
                                    <g:textArea name="body" cols="40" rows="5" value="${simpleMpfEventDataInstance?.body}" />
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
