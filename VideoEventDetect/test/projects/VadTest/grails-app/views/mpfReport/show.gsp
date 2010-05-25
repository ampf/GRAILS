

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show MpfReport</title>
    </head>
    <body>

        <div class="body">
            <h1>Show MpfReport</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>


                        <tr class="prop">
                            <td valign="top" class="name">Id:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfReportInstance, field:'id')}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Body:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfReportInstance, field:'body')}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Children:</td>

                            <td  valign="top" style="text-align:left;" class="value">
                                <ul>
                                <g:each var="c" in="${mpfReportInstance.children}">
                                    <li><g:link controller="mpfReport" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Date Created:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfReportInstance, field:'dateCreated')}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Detected Events:</td>

                            <td  valign="top" style="text-align:left;" class="value">
                                <ul>
                                <g:each var="d" in="${mpfReportInstance.detectedEvents}">
                                    <li><g:link controller="mpfEvent" action="show" id="${d.id}">${d?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>

                        </tr>

                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${mpfReportInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
