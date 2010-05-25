<%@ page import="com.appscio.mpf.grails.core.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfEvent.label', default: 'MpfEvent')}" />
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
            <g:hasErrors bean="${mpfEventInstance}">
            <div class="errors">
                <g:renderErrors bean="${mpfEventInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="streamData"><g:message code="mpfEvent.streamData.label" default="Stream Data" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventInstance, field: 'streamData', 'errors')}">
                                    <g:select name="streamData.id" from="${MpfEventStreamData.list()}" optionKey="id" value="${mpfEventInstance?.streamData?.id}" noSelection="['null': '']" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="data"><g:message code="mpfEvent.data.label" default="Data" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventInstance, field: 'data', 'errors')}">
                                    <g:select name="data.id" from="${MpfEventData.list()}" optionKey="id" value="${mpfEventInstance?.data?.id}"  />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="mpfTask"><g:message code="mpfEvent.mpfTask.label" default="Mpf Task" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventInstance, field: 'mpfTask', 'errors')}">
                                    <g:select name="mpfTask.id" from="${MpfTask.list()}" optionKey="id" value="${mpfEventInstance?.mpfTask?.id}"  />
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
