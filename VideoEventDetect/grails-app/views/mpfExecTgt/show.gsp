

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show MpfExecTgt</title>
    </head>
    <body>

        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">MpfExecTgt List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New MpfExecTgt</g:link></span>
        </div>

        <div class="body">
            <h1>Show MpfExecTgt</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>


                        <tr class="prop">
                            <td valign="top" class="name">ID:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'id')}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">MachineName/IP:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'ip')}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Username:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'username')}</td>

                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name">Execution Dir:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'execDir')}</td>

                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name">Password:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'pw')}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Capabilities:</td>

                            <td  valign="top" style="text-align:left;" class="value">
                                <ul>
                                <g:each var="c" in="${mpfExecTgtInstance.capabilities}">
                                    <li><g:link controller="mpfCapability" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Current Tasks (mpfExecTgt column):</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'currentTasks')}</td>

                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name">Tasks in DB RUNNING for this tgt:</td>

                            <td valign="top" class="value">${runningCount}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Limit Tasks:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'limitTasks')}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Priority:</td>

                            <td valign="top" class="value">${fieldValue(bean:mpfExecTgtInstance, field:'priority')}</td>

                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${mpfExecTgtInstance?.id}" />
                    <span class="button"><g:link controller="mpfExecTgt" action="runningList" id="${mpfExecTgtInstance.id}">RunningList</g:link></span>
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
