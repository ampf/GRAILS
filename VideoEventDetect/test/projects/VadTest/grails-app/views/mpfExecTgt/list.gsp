

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>MpfExecTgt List</title>
    </head>
    <body>

        <div class="body">
            <h1>MpfExecTgt List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <g:sortableColumn property="id" title="ID" />

                            <g:sortableColumn property="tgtName" title="Exec Target Label" />

                             <g:sortableColumn property="ip" title="Machine Name/IP" />

                             <g:sortableColumn property="ip" title="Username" />

                             <g:sortableColumn property="ip" title="User PW" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mpfExecTgtInstanceList}" status="i" var="mpfExecTgtInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${mpfExecTgtInstance.id}">${fieldValue(bean:mpfExecTgtInstance, field:'id')}</g:link></td>

                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'tgtName')}</td>

                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'ip')}</td>

                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'username')}</td>

                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'pw')}</td>


                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${mpfExecTgtInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
