

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'mpfEvent.label', default: 'MpfEvent')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${mpfEventInstance}">
            <div class="errors">
                <g:renderErrors bean="${mpfEventInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${mpfEventInstance?.id}" />
                <g:hiddenField name="version" value="${mpfEventInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="streamData"><g:message code="mpfEvent.streamData.label" default="Stream Data" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventInstance, field: 'streamData', 'errors')}">
                                    <g:select name="streamData.id" from="${MpfEventStreamData.list()}" optionKey="id" value="${mpfEventInstance?.streamData?.id}" noSelection="['null': '']" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="contributors"><g:message code="mpfEvent.contributors.label" default="Contributors" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventInstance, field: 'contributors', 'errors')}">
                                    <g:select name="contributors" from="${MpfReport.list()}" multiple="yes" optionKey="id" size="5" value="${mpfEventInstance?.contributors}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="data"><g:message code="mpfEvent.data.label" default="Data" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventInstance, field: 'data', 'errors')}">
                                    <g:select name="data.id" from="${MpfEventData.list()}" optionKey="id" value="${mpfEventInstance?.data?.id}"  />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="mpfTask"><g:message code="mpfEvent.mpfTask.label" default="Mpf Task" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: mpfEventInstance, field: 'mpfTask', 'errors')}">
                                    <g:select name="mpfTask.id" from="${MpfTask.list()}" optionKey="id" value="${mpfEventInstance?.mpfTask?.id}"  />
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
