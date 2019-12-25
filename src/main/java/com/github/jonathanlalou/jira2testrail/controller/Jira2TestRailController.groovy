package com.github.jonathanlalou.jira2testrail.controller

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import com.github.jonathanlalou.jira2testrail.bean.Jira2TestRailHelper
import com.github.jonathanlalou.jira2testrail.pojo.ParsedDescription
import com.gurock.testrail.APIClient
import lombok.extern.log4j.Log4j
import org.apache.commons.lang3.builder.ToStringBuilder
import org.json.simple.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

import javax.annotation.PostConstruct

import static org.apache.commons.lang3.StringUtils.substringBetween
import static org.apache.commons.lang3.StringUtils.trim

@RestController
@Log4j
class Jira2TestRailController {

    @Value('${testrail.server.url}')
    String testrailUrl
    @Value('${jira.server.url}')
    String jiraUrl
    @Value('${testrail.projectId}')
    String testrailProjectId

    @Autowired
    APIClient testrailApiClient
    @Autowired
    JiraRestClient jiraRestClient
    @Autowired
    Jira2TestRailHelper jira2TestRailHelper

    @PostConstruct
    void postConstruct() {
        Assert.notNull(jiraUrl, "jiraUrl cannot be null")
        Assert.notNull(testrailUrl, "testrailUrl cannot be null")
        Assert.notNull(testrailProjectId, "testrailProjectId cannot be null")

        Assert.notNull(testrailApiClient, "testrailApiClient cannot be null")
        Assert.notNull(jiraRestClient, "jiraRestClient cannot be null")
        Assert.notNull(jira2TestRailHelper, "jira2TestRailHelper cannot be null")
    }


    @ResponseBody
    @GetMapping(path = "sequence/create/{issueKey}", produces = "text/json")
    RedirectView jira2TestRail(@PathVariable issueKey) {

        def issue = retrieveJiraIssue(issueKey)

        def caseId = createEntriesInTestRail(issueKey, issue)

        String targetUrl = "${testrailUrl}/?/cases/view/${caseId}"
        println targetUrl
        addCommentInJira(issue, targetUrl)

        return new RedirectView(targetUrl)
    }

    String createEntriesInTestRail(String issueKey, Issue issue) {
        def parsedDescription = parseDescription(issue.description)
        def sectionId = createSectionInTestRail(issueKey, issue)
        createCaseInTestRail(parsedDescription, issue, sectionId)
    }

    String createCaseInTestRail(ParsedDescription parsedDescription, Issue issue, String sectionId) {
        def customPreconds = parsedDescription.preconditions
                .findAll { it -> ["Environment", "Credentials"].contains(it.item) }
                .collect { it -> new String("1. " + it.item + ": " + it.information + " " + it.referenceLink) }
                .join("\r\n")

        final Map kase = [
                "title"                   : issue.summary
                , "custom_preconds"       : customPreconds
                , "custom_steps_separated": parsedDescription.sequences.collect { it -> ["content": it.interaction, "expected": it.expectedOutcome] }
                , "custom_steps"          : parsedDescription.testRailSteps
        ]
        // TODO check when expected outcome as bullets (* or #)
        JSONObject responseCase = (JSONObject) testrailApiClient.sendPost("add_case/" + sectionId, kase)
        println responseCase
        responseCase.get("id")
    }

    String createSectionInTestRail(String issueKey, Issue issue) {
        final Map section = [
                "description": jiraUrl + "browse/" + issueKey,
                "name"       : issueKey + ": " + issue.summary
        ]
        JSONObject responseSection = (JSONObject) testrailApiClient.sendPost("add_section/${testrailProjectId}".toString(), section)
        println responseSection
        def sectionId = responseSection.get("id")
        sectionId
    }

    Issue retrieveJiraIssue(String issueKey) {
        final Issue issue = jiraRestClient.getIssueClient().getIssue(issueKey).claim()
        println ToStringBuilder.reflectionToString(issue)
        issue
    }

    Comment addCommentInJira(Issue issue, String targetUrl) {
        def comment = Comment.valueOf("Corresponding case in TestRail: ${targetUrl}")
        jiraRestClient.getIssueClient().addComment(issue.commentsUri, comment).claim()
        comment
    }

    ParsedDescription parseDescription(String description) {
        def businessGoal = trim(substringBetween(description, "h3. Business Goal", "h3."))

        def preconditions = jira2TestRailHelper.extractPreconditions(description)
        def sequences = jira2TestRailHelper.extractSequences(description)
        def testRailSteps = jira2TestRailHelper.formatSteps(description)

        return new ParsedDescription(
                businessGoal: businessGoal
                , preconditions: preconditions
                , sequences: sequences
                , testRailSteps: testRailSteps
        )
    }

}
