<%@ page import="com.appscio.mpf.grails.core.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit MpfReport</title>
    </head>
    <body>

        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">MpfReport List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New MpfReport</g:link></span>
        </div>

        <div class="body">
            <h1>Edit MpfReport</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${mpfReportInstance}">
            <div class="errors">
                <g:renderErrors bean="${mpfReportInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${mpfReportInstance?.id}" />
                <input type="hidden" name="version" value="${mpfReportInstance?.version}" />
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
                                    <label for="children">Children:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:mpfReportInstance,field:'children','errors')}">
                                    <g:select name="children"
from="${MpfReport.list()}"
size="5" multiple="yes" optionKey="id"
value="${mpfReportInstance?.children}" />

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

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="detectedEvents">Detected Events:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:mpfReportInstance,field:'detectedEvents','errors')}">

                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
