class StatusHandlingFunctionalTests extends functionaltestplugin.FunctionalTestCase {
    void testBadTaskId() {
        // a status message for a non-existent task

        post("/mpfTask/status/999"){ /* no body needed*/ }
        assertStatus 404
        assertContentContains "999"
    }
}
