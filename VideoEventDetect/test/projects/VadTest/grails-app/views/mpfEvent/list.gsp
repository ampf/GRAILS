

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfEvent.label', default: 'MpfEvent')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="body">
            <h1>MpfEvent List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                             <g:sortableColumn property="id" title="${message(code: 'mpfEvent.id.label', default: 'Id')}" />

                            <th><g:message code="mpfEvent.streamData.label" default="Stream Data" /></th>

                            <th><g:message code="mpfEvent.data.label" default="Data" /></th>

                            <th><g:message code="mpfEvent.mpfTask.label" default="Mpf Task Name (ID)" /></th>

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mpfEventInstanceList}" status="i" var="mpfEventInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${mpfEventInstance.id}">${fieldValue(bean: mpfEventInstance, field: "id")}</g:link></td>

                            <td>${fieldValue(bean: mpfEventInstance, field: "streamData")}</td>

                            <td>${fieldValue(bean: mpfEventInstance, field: "data")}</td>

                            <td><g:link action="show" controller="mpfTask" id="${mpfEventInstance.mpfTask.id}">
                                ${fieldValue(bean: mpfEventInstance, field: "mpfTask.name")} (${fieldValue(bean: mpfEventInstance, field: "mpfTask.id")})
                                 </g:link></td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${mpfEventInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
