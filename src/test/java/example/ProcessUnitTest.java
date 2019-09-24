package example;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.RequiredHistoryLevel;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;


/**
 * Test case starting an in-memory database-backed Process Engine.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ProcessUnitTest {

    private static final String PROCESS_DEFINITION_KEY = "ExampleWait";
    private static final String BPMN_RESOURCE = "process.bpmn";

    static {
        LogFactory.useSlf4jLogging(); // MyBatis
    }

    @Rule
    public final ProcessEngineRule processEngine = new StandaloneInMemoryTestConfiguration().rule();

    @Test
    @RequiredHistoryLevel(ProcessEngineConfiguration.HISTORY_FULL)
    @Deployment(resources = BPMN_RESOURCE)
    public void testHappyPath() {
        // Either: Drive the process by API and assert correct behavior by camunda-bpm-assert, e.g.:
        ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

        // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
        assertThat(processInstance).isWaitingAtExactly("UserTask");
        execute(job());
        assertThat(processInstance).isEnded();
    }

    @Test
    @RequiredHistoryLevel(ProcessEngineConfiguration.HISTORY_FULL)
    @Deployment(resources = BPMN_RESOURCE)
    public void testWaitState() {
        ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
        assertThat(processInstance).isWaitingAtExactly("UserTask");
        ClockUtil.setCurrentTime(new DateTime().plusWeeks(2).toDate());
        assertThat(processInstance).isWaitingAtExactly("UserTask", "MessageEndEvent");
    }
}
