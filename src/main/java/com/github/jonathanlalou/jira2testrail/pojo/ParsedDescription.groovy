package com.github.jonathanlalou.jira2testrail.pojo

import lombok.Data
import lombok.ToString

@Data
@ToString
class ParsedDescription {
    String businessGoal
    List<Precondition> preconditions
    List<Sequence> sequences

}
