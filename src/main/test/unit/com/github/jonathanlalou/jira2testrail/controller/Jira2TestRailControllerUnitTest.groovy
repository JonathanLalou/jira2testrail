package com.github.jonathanlalou.jira2testrail.controller

import org.apache.commons.lang3.builder.ToStringBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Jira2TestRailControllerUnitTest {

    final Jira2TestRailController jira2TestRailController = new Jira2TestRailController();
    def description = """
h3. Business Goal

this is the business goal

h3. Pre-conditions

||Pre-condition Item||Pre-condition Information||Reference Link... ||
|System Settings|NA| |
|Environment|Xinet Server http;//10.26.52.15 | |
|Credentials |nativeadmin/nativeadmin| |

h3. Scenario

||Seq# ||User interaction sequence ||Expected Outcome || ||
|1 |user interaction sequence 1 |expected outcome 1 | |
|2 |user interaction sequence 2 |expected outcome 2 | |
|3 |user interaction sequence 3 |expected outcome 3 | |
|4 |user interaction sequence 4 |expected outcome 4 | |
|5 |user interaction sequence 5 |expected outcome 5 | |
|6 |user interaction sequence 6 |expected outcome 6 | |
|7 |user interaction sequence 7 |expected outcome 7 | |
"""

    @Test
    void extratData() {
        def actual = jira2TestRailController.parseDescription(description)

        assert "this is the business goal" == actual.businessGoal

        assert 3 == actual.preconditions.size()
        assert ToStringBuilder.reflectionToString(actual.preconditions[0]).endsWith("[item=System Settings,information=NA,referenceLink=]")
        assert ToStringBuilder.reflectionToString(actual.preconditions[1]).endsWith("[item=Environment,information=Xinet Server http;//10.26.52.15,referenceLink=]")
        assert ToStringBuilder.reflectionToString(actual.preconditions[2]).endsWith("[item=Credentials,information=nativeadmin/nativeadmin,referenceLink=]")

        assert 7 == actual.sequences.size()
        assert ToStringBuilder.reflectionToString(actual.sequences[0]).endsWith("[number=1,interaction=user interaction sequence 1,expectedOutcome=expected outcome 1]")
        assert ToStringBuilder.reflectionToString(actual.sequences[1]).endsWith("[number=2,interaction=user interaction sequence 2,expectedOutcome=expected outcome 2]")
        assert ToStringBuilder.reflectionToString(actual.sequences[2]).endsWith("[number=3,interaction=user interaction sequence 3,expectedOutcome=expected outcome 3]")
        assert ToStringBuilder.reflectionToString(actual.sequences[3]).endsWith("[number=4,interaction=user interaction sequence 4,expectedOutcome=expected outcome 4]")
        assert ToStringBuilder.reflectionToString(actual.sequences[4]).endsWith("[number=5,interaction=user interaction sequence 5,expectedOutcome=expected outcome 5]")
        assert ToStringBuilder.reflectionToString(actual.sequences[5]).endsWith("[number=6,interaction=user interaction sequence 6,expectedOutcome=expected outcome 6]")
        assert ToStringBuilder.reflectionToString(actual.sequences[6]).endsWith("[number=7,interaction=user interaction sequence 7,expectedOutcome=expected outcome 7]")

        assert "nativeadmin/nativeadmin" == actual.credentials
        assert "Xinet Server http;//10.26.52.15" == actual.environment
    }

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

        Assertions.assertEquals(expected, jira2TestRailController.formatSteps(description))
    }

}