package com.github.jonathanlalou.jira2testrail.controller

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.JiraRestClientFactory
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import lombok.extern.log4j.Log4j
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@RestController
@Log4j
class Jira2TestRailController {
    @Value('${jira.username}')
    String jiraUsername
    @Value('${jira.token}')
    String jiraToken
    @Value('${jira.server.url}')
    String jiraUrl

    @PostConstruct
    void postConstruct() {
        Assert.notNull(jiraUsername, "jiraUsername cannot be null")
        Assert.notNull(jiraToken, "jiraPassword cannot be null")
        Assert.notNull(jiraUrl, "jiraUrl cannot be null")
    }


    @ResponseBody
    @GetMapping(path = "sequence/create", produces = "text/json")
    String get(
            @RequestHeader(value = "referer", required = false) final String referer,
            final HttpServletRequest request) {
        println referer

        final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory()
        final URI jiraServerUri = new URI(jiraUrl)
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUsername, jiraToken)

        final Issue issue = restClient.getIssueClient().getIssue("XINET-1").claim()
        println ToStringBuilder.reflectionToString(issue)
        return '{"status": "OK"}'
    }

    String extratData(Issue issue) {
        def description = issue.description

    }
}
