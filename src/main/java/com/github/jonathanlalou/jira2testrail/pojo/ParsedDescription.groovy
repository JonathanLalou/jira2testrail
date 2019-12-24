package com.github.jonathanlalou.jira2testrail.pojo

import lombok.Data
import lombok.ToString

@Data
@ToString
class ParsedDescription {
    String businessGoal
    List<Precondition> preconditions
    List<Sequence> sequences
    /** String of JIRA steps converted to TestRail particular implementation of Markdown */
    String testRailSteps

    String getEnvironment() {
        preconditions.find { it -> it.item.equalsIgnoreCase("Environment") }.information
    }

    String getCredentials() {
        preconditions.find { it -> it.item.equalsIgnoreCase("Credentials") }.information
    }

}
