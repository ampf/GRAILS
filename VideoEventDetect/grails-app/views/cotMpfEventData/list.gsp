

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'cotMpfEventData.label', default: 'CotMpfEventData')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>

        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                            <g:sortableColumn property="id" title="${message(code: 'cotMpfEventData.id.label', default: 'Id')}" />

                            <th><g:message code="cotMpfEventData.mpfEvent.label" default="Mpf Event" /></th>

                            <g:sortableColumn property="cotUID" title="${message(code: 'cotMpfEventData.cotUID.label', default: 'Cot UID')}" />

                            <g:sortableColumn property="cotType" title="${message(code: 'cotMpfEventData.cotType.label', default: 'Cot Type')}" />

                            <g:sortableColumn property="cotStart" title="${message(code: 'cotMpfEventData.cotStart.label', default: 'Cot Start')}" />

                            <g:sortableColumn property="cotTime" title="${message(code: 'cotMpfEventData.cotTime.label', default: 'Cot Time')}" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${cotMpfEventDataInstanceList}" status="i" var="cotMpfEventDataInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${cotMpfEventDataInstance.id}">${fieldValue(bean: cotMpfEventDataInstance, field: "id")}</g:link></td>

                            <td>${fieldValue(bean: cotMpfEventDataInstance, field: "mpfEvent")}</td>

                            <td>${fieldValue(bean: cotMpfEventDataInstance, field: "cotUID")}</td>

                            <td>${fieldValue(bean: cotMpfEventDataInstance, field: "cotType")}</td>

                            <td><g:formatDate date="${cotMpfEventDataInstance.cotStart}" /></td>

                            <td><g:formatDate date="${cotMpfEventDataInstance.cotTime}" /></td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${cotMpfEventDataInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
