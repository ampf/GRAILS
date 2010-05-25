<%@ page import="com.appscio.mpf.grails.core.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>MpfReport List</title>
    </head>
    <body>

        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New MpfReport</g:link></span>
        </div>

        <div class="body">
            <h1>MpfReport List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                   	        <g:sortableColumn property="id" title="Id" />

                   	        <g:sortableColumn property="body" title="Body" />

                   	        <g:sortableColumn property="dateCreated" title="Date Created" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mpfReportInstanceList}" status="i" var="mpfReportInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${mpfReportInstance.id}">${fieldValue(bean:mpfReportInstance, field:'id')}</g:link></td>

                            <td>${fieldValue(bean:mpfReportInstance, field:'body')}</td>

                            <td>${fieldValue(bean:mpfReportInstance, field:'dateCreated')}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${mpfReportInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
