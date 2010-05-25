

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'cotMpfEventData.label', default: 'CotMpfEventData')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>

        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${cotMpfEventDataInstance}">
            <div class="errors">
                <g:renderErrors bean="${cotMpfEventDataInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="mpfEvent"><g:message code="cotMpfEventData.mpfEvent.label" default="Mpf Event" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'mpfEvent', 'errors')}">
                                    <g:select name="mpfEvent.id" from="${MpfEvent.list()}" optionKey="id" value="${cotMpfEventDataInstance?.mpfEvent?.id}" noSelection="['null': '']" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotUID"><g:message code="cotMpfEventData.cotUID.label" default="Cot UID" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotUID', 'errors')}">
                                    <g:textField name="cotUID" value="${cotMpfEventDataInstance?.cotUID}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotType"><g:message code="cotMpfEventData.cotType.label" default="Cot Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotType', 'errors')}">
                                    <g:textField name="cotType" value="${cotMpfEventDataInstance?.cotType}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotStart"><g:message code="cotMpfEventData.cotStart.label" default="Cot Start" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotStart', 'errors')}">
                                    <g:datePicker name="cotStart" precision="day" value="${cotMpfEventDataInstance?.cotStart}" noSelection="['': '']" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotTime"><g:message code="cotMpfEventData.cotTime.label" default="Cot Time" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotTime', 'errors')}">
                                    <g:datePicker name="cotTime" precision="day" value="${cotMpfEventDataInstance?.cotTime}" noSelection="['': '']" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotHow"><g:message code="cotMpfEventData.cotHow.label" default="Cot How" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotHow', 'errors')}">
                                    <g:textField name="cotHow" value="${cotMpfEventDataInstance?.cotHow}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotDetail"><g:message code="cotMpfEventData.cotDetail.label" default="Cot Detail" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotDetail', 'errors')}">
                                    <g:select name="cotDetail.id" from="${MpfCotDetail.list()}" optionKey="id" value="${cotMpfEventDataInstance?.cotDetail?.id}" noSelection="['null': '']" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotPointHae"><g:message code="cotMpfEventData.cotPointHae.label" default="Cot Point Hae" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotPointHae', 'errors')}">
                                    <g:textField name="cotPointHae" value="${fieldValue(bean: cotMpfEventDataInstance, field: 'cotPointHae')}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotPointLat"><g:message code="cotMpfEventData.cotPointLat.label" default="Cot Point Lat" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotPointLat', 'errors')}">
                                    <g:textField name="cotPointLat" value="${fieldValue(bean: cotMpfEventDataInstance, field: 'cotPointLat')}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotPointLon"><g:message code="cotMpfEventData.cotPointLon.label" default="Cot Point Lon" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotPointLon', 'errors')}">
                                    <g:textField name="cotPointLon" value="${fieldValue(bean: cotMpfEventDataInstance, field: 'cotPointLon')}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotPointCe"><g:message code="cotMpfEventData.cotPointCe.label" default="Cot Point Ce" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotPointCe', 'errors')}">
                                    <g:textField name="cotPointCe" value="${fieldValue(bean: cotMpfEventDataInstance, field: 'cotPointCe')}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotStale"><g:message code="cotMpfEventData.cotStale.label" default="Cot Stale" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotStale', 'errors')}">
                                    <g:datePicker name="cotStale" precision="day" value="${cotMpfEventDataInstance?.cotStale}"  />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cotPointLe"><g:message code="cotMpfEventData.cotPointLe.label" default="Cot Point Le" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: cotMpfEventDataInstance, field: 'cotPointLe', 'errors')}">
                                    <g:textField name="cotPointLe" value="${fieldValue(bean: cotMpfEventDataInstance, field: 'cotPointLe')}" />
                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
