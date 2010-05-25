

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>MpfExecTgt List</title>
    </head>
    <body>
        
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New MpfExecTgt</g:link></span>
        </div>
        
        <div class="body">
            <h1>MpfExecTgt Listing</h1>
            <h4>click on the 'current' field to get real-time runningList (XML doc) for that target</h4>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <g:sortableColumn property="id" title="ID" />

                            <g:sortableColumn property="tgtName" title="Target Name" />
                            
                            <g:sortableColumn property="currentTasks" title="current" />
                            
                            <g:sortableColumn property="limitTasks" title="limit" />
                            
                             <g:sortableColumn property="ip" title="Machine Name/IP" />

                              
                             <g:sortableColumn property="ip" title="Username" />

                             <g:sortableColumn property="ip" title="Exec Dir" />

                             <g:sortableColumn property="ip" title="User PW" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mpfExecTgtInstanceList}" status="i" var="mpfExecTgtInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${mpfExecTgtInstance.id}">${fieldValue(bean:mpfExecTgtInstance, field:'id')}</g:link></td>

                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'tgtName')}</td>
                            
                            <td><g:link controller="mpfExecTgt" action="runningList" id="${mpfExecTgtInstance.id}">
                            ${fieldValue(bean:mpfExecTgtInstance, field:'currentTasks')}</g:link></td>
                                                        
                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'limitTasks')}</td>
                            
                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'ip')}</td>
                            
                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'username')}</td>

                            <td>${fieldValue(bean:mpfExecTgtInstance, field:'execDir')}</td>

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
