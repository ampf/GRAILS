

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfEvent.label', default: 'MpfEvent')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfEvent.id.label" default="Id" /></td>

                            <td valign="top" class="value">${fieldValue(bean: mpfEventInstance, field: "id")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfEvent.streamData.label" default="Stream Data" /></td>

                            <td valign="top" class="value"><g:link controller="mpfEventStreamData" action="show" id="${mpfEventInstance?.streamData?.id}">${mpfEventInstance?.streamData?.encodeAsHTML()}</g:link></td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfEvent.contributors.label" default="Contributors" /></td>

                            <td valign="top" style="text-align: left;" class="value">
                                <ul>
                                <g:each in="${mpfEventInstance.contributors}" var="c">
                                    <li><g:link controller="mpfReport" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfEvent.data.label" default="Data" /></td>

                            <td valign="top" class="value"><g:link controller="mpfEventData" action="show" id="${mpfEventInstance?.data?.id}">${mpfEventInstance?.data?.encodeAsHTML()}</g:link></td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="mpfEvent.mpfTask.label" default="Mpf Task" /></td>

                            <td valign="top" class="value"><g:link controller="mpfTask" action="show" id="${mpfEventInstance?.mpfTask?.id}">${mpfEventInstance?.mpfTask?.encodeAsHTML()}</g:link></td>

                        </tr>

                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${mpfEventInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
