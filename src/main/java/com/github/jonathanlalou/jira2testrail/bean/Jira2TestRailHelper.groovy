package com.github.jonathanlalou.jira2testrail.bean

import com.github.jonathanlalou.jira2testrail.pojo.Precondition
import com.github.jonathanlalou.jira2testrail.pojo.Sequence
import com.google.common.collect.Lists
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

import static org.apache.commons.lang3.StringUtils.split
import static org.apache.commons.lang3.StringUtils.substringAfter
import static org.apache.commons.lang3.StringUtils.substringBetween
import static org.apache.commons.lang3.StringUtils.trim

@Component
class Jira2TestRailHelper {

    @SuppressWarnings("GrMethodMayBeStatic")
    ArrayList<Precondition> extractPreconditions(String description) {
        final List<Precondition> preconditions = Lists.newArrayList()
        def szPreconditions = trim(substringBetween(description, "h3. Pre-conditions", "h3."))
        Arrays.asList(split(szPreconditions, "\n")).findAll { it -> !it.startsWith("||") }.each {
            //noinspection RegExpEmptyAlternationBranch
            def splitPrecondition = Arrays.asList(it.split('\\|'))
            preconditions.add(new Precondition(
                    item: trim(splitPrecondition[1])
                    , information: trim(splitPrecondition[2])
                    , referenceLink: trim(splitPrecondition[3])
            ))
        }
        preconditions
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    ArrayList<Sequence> extractSequences(String description) {
        final List<Sequence> sequences = Lists.newArrayList()
        def szSequences = trim(substringAfter(description, "h3. Scenario"))
        Arrays.asList(split(szSequences, "\n")).findAll { it -> !it.startsWith("||") }.each {
            //noinspection RegExpEmptyAlternationBranch
            def splitSequence = Arrays.asList(it.split('\\|'))
            sequences.add(new Sequence(
                    number: trim(splitSequence[1])
                    , interaction: trim(splitSequence[2])
                    , expectedOutcome: trim(splitSequence[3])
            ))
        }
        sequences
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    String formatSteps(String description) {
        def jiraTable = trim(substringAfter(description, "h3. Scenario"))
        // headers for the first line
        def response = "|||:Seq#:|:User interaction sequence:|:Expected Outcome :\n"
        // handle the other lines
        Arrays.asList(split(jiraTable, "\n")).findAll { it -> !it.startsWith("||") }.each {
            response += "|" + StringUtils.removeEnd(it, "|") + "\n"
        }
        response.trim()
    }

}
