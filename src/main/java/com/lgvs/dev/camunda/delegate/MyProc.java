package com.lgvs.dev.camunda.delegate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyProc implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Receive message: {}", execution.getVariable("payload"));
        // -------other logic-----------
        // .......

        execution.setVariable("rec", "success");
    }
}
