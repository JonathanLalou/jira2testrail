package com.github.jonathanlalou.jira2testrail.bean

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.JiraRestClientFactory
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.gurock.testrail.APIClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.Assert

import javax.annotation.PostConstruct

@Configuration
class Jira2TestRailConfig {
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

    @PostConstruct
    void postConstruct() {
        Assert.notNull(jiraUsername, "jiraUsername cannot be null")
        Assert.notNull(jiraToken, "jiraPassword cannot be null")
        Assert.notNull(jiraUrl, "jiraUrl cannot be null")

        Assert.notNull(testrailUsername, "testrailUsername cannot be null")
        Assert.notNull(testrailToken, "testrailPassword cannot be null")
        Assert.notNull(testrailUrl, "testrailUrl cannot be null")
    }

    @Bean
    APIClient testrailApiClient() {
        def testrailApiClient = new APIClient(testrailUrl)
        testrailApiClient.user = testrailUsername
        testrailApiClient.password = testrailToken
        testrailApiClient
    }

    @Bean
    JiraRestClient jiraRestClient() {
        final JiraRestClientFactory jiraRestClientFactory = new AsynchronousJiraRestClientFactory()
        final URI jiraServerUri = new URI(jiraUrl)
        jiraRestClientFactory.createWithBasicHttpAuthentication(jiraServerUri, jiraUsername, jiraToken)
    }

}
