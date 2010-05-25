class NotaskEventTestFunctionalTests extends functionaltestplugin.FunctionalTestCase {
    void doTextEventPostAndVerify(url){
        post(url){
            body {
                testEvent
            }
        }

        assertStatus 200

        // we expect an XML doc back, with a SimpleMpfEvent assigned and a <body> same as we sent
        assertContentType "text/xml"

        def resp = new XmlSlurper().parseText(page.webResponse.contentAsString)
        assertEquals 1,resp.size() // the MpfEvent is at teh op level
    }
    void doXmlEventPostAndVerify(url){
        post(url){
            body {
                testEvent
            }
        }

        assertStatus 200

        // we expect an XML doc back, with a SimpleMpfEvent assigned and a <body> same as we sent
        assertContentType "text/xml"

        def resp = new XmlSlurper().parseText(page.webResponse.contentAsString)
        assertEquals 1,resp.size()
    }

    void testPostEventWithoutTask() {
        doTextEventPostAndVerify("/mpfTask/event")
        doXmlEventPostAndVerify ("/mpfTask/event")
    }
    void testPost3EventsForTask() {
        3.times {doTextEventPostAndVerify("/mpfTask/event")}
        3.times {doXmlEventPostAndVerify("/mpfTask/event")}
    }



    def testEvent ='''
<?xml version='1.0' standalone='yes'?>
<!-- bits-map-point-vad-event is abbreviated as b-m-p-v-e -->
<event version='2.0'
uid='../../../data/predator/predator-1.mpg | ./test-detect-events-cot-mock'
type='b-m-p-v-e' how='m-g'
time='1261169554841' start='1123867578027' stale='1123867638027'>
<point lat='34' lon='-117' hae='1756' ce='9999999' le='9999999' />
    <detail>
        <appscio>
            <vad activity="false" tracks="0">
            </vad>
        </appscio>
    </detail>
</event>
'''

    /*
<?xml version="1.0" encoding="UTF-8"?>
<list>
    <mpfEvent id="2">
        <contributors><
            mpfReport id="2" /></contributors>
        <data id="2" />
        <mpfTask id="1" />
        <streamData />
    </mpfEvent>
</list>
     */
}
