

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'cotMpfEventData.label', default: 'CotMpfEventData')}" />
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
                            <td valign="top" class="name"><g:message code="cotMpfEventData.id.label" default="Id" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "id")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.mpfEvent.label" default="Mpf Event" /></td>

                            <td valign="top" class="value"><g:link controller="mpfEvent" action="show" id="${cotMpfEventDataInstance?.mpfEvent?.id}">${cotMpfEventDataInstance?.mpfEvent?.encodeAsHTML()}</g:link></td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotUID.label" default="CoT UID" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotUID")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotType.label" default="CoT Type" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotType")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotStart.label" default="CoT Start" /></td>

                            <td valign="top" class="value"><g:formatDate date="${cotMpfEventDataInstance?.cotStart}" /></td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotTime.label" default="CoT Time" /></td>

                            <td valign="top" class="value"><g:formatDate date="${cotMpfEventDataInstance?.cotTime}" /></td>

                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotStale.label" default="CoT Stale" /></td>

                            <td valign="top" class="value"><g:formatDate date="${cotMpfEventDataInstance?.cotStale}" /></td>

                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotHow.label" default="CoT How" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotHow")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotDetail.label" default="CoT Detail" /></td>

                            <td valign="top" class="value"><g:link controller="mpfCotDetail" action="show" id="${cotMpfEventDataInstance?.cotDetail?.id}">${cotMpfEventDataInstance?.cotDetail?.encodeAsHTML()}</g:link></td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotPointHae.label" default="CoT Point Hae" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotPointHae")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotPointLat.label" default="CoT Point Lat" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotPointLat")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotPointLon.label" default="CoT Point Lon" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotPointLon")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotPointCe.label" default="CoT Point Ce" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotPointCe")}</td>

                        </tr>



                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="cotMpfEventData.cotPointLe.label" default="Cot Point Le" /></td>

                            <td valign="top" class="value">${fieldValue(bean: cotMpfEventDataInstance, field: "cotPointLe")}</td>

                        </tr>

                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${cotMpfEventDataInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
