

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>MpfTask List</title>
    </head>
    <body>

        <div class="body">
            <h1>MpfTask List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                             <g:sortableColumn property="id" title="Id" />

                             <g:sortableColumn property="name" title="Name" />

                             <g:sortableColumn property="mpfDetectorName" title="Mpf Detector Name" />

                             <th>Detector Prefix</th>

                             <g:sortableColumn property="videoFileUrl" title="Video File Url" />

                             <th>Status</th>

                             <g:sortableColumn property="statusMsg" title="Status Msg" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mpfTaskInstanceList}" status="i" var="mpfTaskInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${mpfTaskInstance.id}">${fieldValue(bean:mpfTaskInstance, field:'id')}</g:link></td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'name')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'mpfDetectorName')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'detectPrefix')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'videoFileUrl')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'status')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'statusMsg')}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${mpfTaskInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
