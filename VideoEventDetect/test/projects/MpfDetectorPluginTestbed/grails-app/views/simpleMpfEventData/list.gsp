<%@ page import="com.appscio.mpf.grails.core.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'simpleMpfEventData.label', default: 'SimpleMpfEventData')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                            <g:sortableColumn property="id" title="${message(code: 'simpleMpfEventData.id.label', default: 'Id')}" />

                            <th><g:message code="simpleMpfEventData.mpfEvent.label" default="Mpf Event" /></th>

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${simpleMpfEventDataInstanceList}" status="i" var="simpleMpfEventDataInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${simpleMpfEventDataInstance.id}">${fieldValue(bean: simpleMpfEventDataInstance, field: "id")}</g:link></td>

                            <td>${fieldValue(bean: simpleMpfEventDataInstance, field: "mpfEvent")}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${simpleMpfEventDataInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
