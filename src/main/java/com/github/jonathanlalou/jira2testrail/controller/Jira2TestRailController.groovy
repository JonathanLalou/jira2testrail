package com.github.jonathanlalou.jira2testrail.controller

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.JiraRestClientFactory
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.github.jonathanlalou.jira2testrail.pojo.ParsedDescription
import com.github.jonathanlalou.jira2testrail.pojo.Precondition
import com.github.jonathanlalou.jira2testrail.pojo.Sequence
import com.google.common.collect.Lists
import com.gurock.testrail.APIClient
import lombok.extern.log4j.Log4j
import org.apache.commons.lang3.builder.ToStringBuilder
import org.json.simple.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

import static org.apache.commons.lang3.StringUtils.split
import static org.apache.commons.lang3.StringUtils.substringAfter
import static org.apache.commons.lang3.StringUtils.substringBetween
import static org.apache.commons.lang3.StringUtils.trim

@RestController
@Log4j
class Jira2TestRailController {
    @Value('${jira.username}')
    String jiraUsername
    @Value('${jira.token}')
    String jiraToken
    @Value('${jira.server.url}')
    String jiraUrl

    @Value('${testrail.username}')
    String testrailUsername
    @Value('${testrail.token}')
    String testrailToken
    @Value('${testrail.server.url}')
    String testrailUrl
    @Value('${testrail.projectId}')
    String testrailProjectId

    @PostConstruct
    void postConstruct() {
        Assert.notNull(jiraUsername, "jiraUsername cannot be null")
        Assert.notNull(jiraToken, "jiraPassword cannot be null")
        Assert.notNull(jiraUrl, "jiraUrl cannot be null")

        Assert.notNull(testrailUsername, "testrailUsername cannot be null")
        Assert.notNull(testrailToken, "testrailPassword cannot be null")
        Assert.notNull(testrailUrl, "testrailUrl cannot be null")
        Assert.notNull(testrailProjectId, "testrailProjectId cannot be null")
    }


    @ResponseBody
    @GetMapping(path = "sequence/create/{issueKey}", produces = "text/json")
    String get(
            @PathVariable issueKey,
            @RequestHeader(value = "referer", required = false) final String referer,
            final HttpServletRequest request) {

        def issue = retrieveJiraIssue(issueKey)
        def parsedDescription = parseDescription(issue.description)

        final APIClient apiClient = new APIClient(testrailUrl)
        apiClient.user = testrailUsername
        apiClient.password = testrailToken

        final Map section = [
                "description": jiraUrl + "browse/" + issueKey,
                "name"       : issueKey + ": " + issue.summary
        ]
        JSONObject responseSection = (JSONObject) apiClient.sendPost("add_section/${testrailProjectId}".toString(), section)
        println responseSection
        def suiteId = responseSection.get("suite_id")
        def sectionId = responseSection.get("id")

        final Map kase = [
                "title"            : issue.summary
                , "custom_preconds": parsedDescription.preconditions.collect { it -> new String(it.item + " " + it.information + " " + it.referenceLink) }.join("\r\n")
        ]
        JSONObject responseCase = (JSONObject) apiClient.sendPost("add_case/" + sectionId, kase)
        println responseCase

        // TODO do a redirect to TestRail page
        return '{"status": "OK"}'
    }

    Issue retrieveJiraIssue(String issueKey) {
        final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory()
        final URI jiraServerUri = new URI(jiraUrl)
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUsername, jiraToken)

        final Issue issue = restClient.getIssueClient().getIssue(issueKey).claim()
        println ToStringBuilder.reflectionToString(issue)
        issue
    }

    ParsedDescription parseDescription(String description) {
        def businessGoal = trim(substringBetween(description, "h3. Business Goal", "h3."))

        def preconditions = extractPreconditions(description)
        def sequences = extractSequences(description)

        return new ParsedDescription(
                businessGoal: businessGoal
                , preconditions: preconditions
                , sequences: sequences
        )
    }

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
}
