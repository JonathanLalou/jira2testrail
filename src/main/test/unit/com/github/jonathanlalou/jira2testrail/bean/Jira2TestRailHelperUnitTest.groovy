package com.github.jonathanlalou.jira2testrail.bean

import com.github.jonathanlalou.jira2testrail.controller.Jira2TestRailControllerUnitTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Jira2TestRailHelperUnitTest {

    Jira2TestRailHelper jira2TestRailHelper = new Jira2TestRailHelper()

    @Test
    void formatSteps() {
        def expected = """
|||:Seq#:|:User interaction sequence:|:Expected Outcome :|
||1 |user interaction sequence 1 |expected outcome 1 | 
||2 |user interaction sequence 2 |expected outcome 2 | 
||3 |user interaction sequence 3 |expected outcome 3 | 
||4 |user interaction sequence 4 |expected outcome 4 | 
||5 |user interaction sequence 5 |expected outcome 5 | 
||6 |user interaction sequence 6 |expected outcome 6 | 
||7 |user interaction sequence 7 |expected outcome 7 | 
""".trim()

        Assertions.assertEquals(expected, jira2TestRailHelper.formatSteps(Jira2TestRailControllerUnitTest.description))
    }

}