

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfEventData.label', default: 'MpfEventData')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${mpfEventDataInstance}">
            <div class="errors">
                <g:renderErrors bean="${mpfEventDataInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${mpfEventDataInstance?.id}" />
                <g:hiddenField name="version" value="${mpfEventDataInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="mpfEvent"><g:message code="mpfEventData.mpfEvent.label" default="Mpf Event" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventDataInstance, field: 'mpfEvent', 'errors')}">
                                    <g:select name="mpfEvent.id" from="${MpfEvent.list()}" optionKey="id" value="${mpfEventDataInstance?.mpfEvent?.id}" noSelection="['null': '']" />
                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
