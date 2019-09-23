package example;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
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
    @Deployment(resources = BPMN_RESOURCE)
    public void testHappyPath() {
        // Either: Drive the process by API and assert correct behavior by camunda-bpm-assert, e.g.:
        ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

        // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
        assertThat(processInstance).isWaitingAtExactly("WaitHere");
        complete(externalTask());
        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = BPMN_RESOURCE)
    public void testWaitState() {
        ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
        assertThat(processInstance).isWaitingAtExactly("WaitHere");
        ClockUtil.setCurrentTime(new DateTime().plusWeeks(2).toDate());
        assertThat(processInstance).isWaitingAtExactly("WaitHere", "MessageEndEvent");
    }
}
