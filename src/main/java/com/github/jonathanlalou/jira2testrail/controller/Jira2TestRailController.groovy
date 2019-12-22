package com.github.jonathanlalou.jira2testrail.controller

import lombok.extern.log4j.Log4j
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

@RestController
@Log4j
class Jira2TestRailController {
    @ResponseBody
    @GetMapping(path = "sequence/create", produces = "text/json")
    String get(
            @RequestHeader(value = "referer", required = false) final String referer,
            final HttpServletRequest request) {
        println referer
        return '{"status": "OK"}'
    }
}
