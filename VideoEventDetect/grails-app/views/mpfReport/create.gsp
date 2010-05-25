<%@ page import="com.appscio.mpf.grails.core.*" %>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create MpfReport</title>
    </head>
    <body>

        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">MpfReport List</g:link></span>
        </div>

        <div class="body">
            <h1>Create MpfReport</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${mpfReportInstance}">
            <div class="errors">
                <g:renderErrors bean="${mpfReportInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="body">Body:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:mpfReportInstance,field:'body','errors')}">
                                    <input type="text" id="body" name="body" value="${fieldValue(bean:mpfReportInstance,field:'body')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="dateCreated">Date Created:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:mpfReportInstance,field:'dateCreated','errors')}">
                                    <g:datePicker name="dateCreated" value="${mpfReportInstance?.dateCreated}" precision="minute" ></g:datePicker>
                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
