
<%@ page import="com.appscio.mpf.grails.core.MpfExecTgtStats" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfExecTgtStats.label', default: 'MpfExecTgtStats')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'mpfExecTgtStats.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="sampleTime" title="${message(code: 'mpfExecTgtStats.sampleTime.label', default: 'Sample Time')}" />
                        
                            <g:sortableColumn property="et.id" title="${message(code:'mpfExecTgtStats.et.label', default:'ET')}"/>
                   	    
                            <g:sortableColumn property="runnningPerTgt" title="${message(code: 'mpfExecTgtStats.runnningPerTgt.label', default: 'Per Tgt')}" />
                        
                            <g:sortableColumn property="runningPerDb" title="${message(code: 'mpfExecTgtStats.runningPerDb.label', default: 'Per Db')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mpfExecTgtStatsInstanceList}" status="i" var="mpfExecTgtStatsInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${mpfExecTgtStatsInstance.id}">${fieldValue(bean: mpfExecTgtStatsInstance, field: "id")}</g:link></td>
                        
                            <td><g:formatDate date="${mpfExecTgtStatsInstance.sampleTime}" /></td>
                        
                            <td><g:link action="show" controller="mpfExecTgt" id="${mpfExecTgtStatsInstance.et.id}">
                               ${fieldValue(bean: mpfExecTgtStatsInstance, field: "et.id")}</g:link></td>
                        
                            <td>${fieldValue(bean: mpfExecTgtStatsInstance, field: "runnningPerTgt")}</td>
                        
                            <td>${fieldValue(bean: mpfExecTgtStatsInstance, field: "runningPerDb")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${mpfExecTgtStatsInstanceTotal}" />
            </div>
        </div>
        <div id="chart">
        	<img src="${ chartUrl }"/>
        </div>
    </body>
</html>
