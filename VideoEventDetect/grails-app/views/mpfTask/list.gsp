<%@ page import="com.appscio.mpf.grails.core.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>MpfTask List</title>
    </head>
    <body>

        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New MpfTask</g:link></span>
        </div>

        <div class="body">
            <h1>MpfTask List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
            <h3>RUNNING on all targets: ${runningTasksForAllTargets}</h3>
                <table>
                    <thead>
                        <tr>

                             <g:sortableColumn property="id" title="Id" />

                             <g:sortableColumn property="name" title="Name" />

                             <g:sortableColumn property="mpfDetectorName" title="Mpf Detector Name" />

                             <g:sortableColumn property="detectPrefix" title="Detector Prefix"/>

                             <g:sortableColumn property="status" title="Status"/>

                             <g:sortableColumn property="statusMsg" title="Status Msg" />

                             <g:sortableColumn property="elapsedSecs" title="Time (S)"/>

                             <g:sortableColumn property="videoFileUrl" title="Video File Url" />
                             
                             <g:sortableColumn property="execTgt.id" title="[ExecTgt] execDir" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mpfTaskInstanceList}" status="i" var="mpfTaskInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${mpfTaskInstance.id}">${fieldValue(bean:mpfTaskInstance, field:'id')}</g:link></td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'name')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'mpfDetectorName')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'detectPrefix')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'status')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'statusMsg')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance.execTgtCompletionTimes, field:'elapsedSecs')}</td>

                            <td>${fieldValue(bean:mpfTaskInstance, field:'videoFileUrl')}</td>
                            
                            <td><g:link controller="mpfExecTgt" action="show" id="${fieldValue(bean:mpfTaskInstance, field:'execTgt.id')}">
                               [${fieldValue(bean:mpfTaskInstance, field:'execTgt.id').padLeft(2,"0")}] ${fieldValue(bean:mpfTaskInstance, field:'execTgt.execDir')}
                               </g:link></td>

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
