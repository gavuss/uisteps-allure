package com.qantium.uisteps.allure.tests.listeners;

import com.qantium.uisteps.allure.tests.BaseTest;
import com.qantium.uisteps.allure.tests.listeners.functions.ListenerFunction;
import com.qantium.uisteps.core.lifecycle.MetaInfo;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.*;
import ru.yandex.qatools.allure.experimental.LifecycleListener;
import ru.yandex.qatools.allure.model.TestCaseResult;
import ru.yandex.qatools.allure.storages.StepStorage;
import ru.yandex.qatools.allure.model.Step;
import ru.yandex.qatools.allure.storages.TestCaseStorage;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Anton Solyankin
 */
public class StepListener extends LifecycleListener {

    private final Set<ListenerFunction> functions = new LinkedHashSet();
    private Step lastStep;
    private List<Step> steps = new LinkedList();
    private TestCaseResult testResult;
    private final BaseTest test;


    public StepListener(BaseTest test) {
        this.test = test;
        testResult = getTestStorage().get();
    }

    public BaseTest getTest() {
        return test;
    }

    public Step getLastStep() {
        return lastStep;
    }

    public TestCaseResult getTestResult() {
        return testResult;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public StepListener add(ListenerFunction function) {
        function.setListener(this);
        functions.add(function);
        return this;
    }

    @Override
    public void fire(StepStartedEvent event) {
        lastStep = getStepStorage().getLast();
        steps.add(getStepStorage().getLast());
        fire(Event.STEP_STARTED);
    }

    @Override
    public void fire(TestCaseStartedEvent event) {
    }

    @Override
    public void fire(TestCaseFinishedEvent event) {
        fire(Event.TEST_FINISHED);
    }

    @Override
    public void fire(StepFinishedEvent event) {
        fire(Event.STEP_FINISHED);
    }

    @Override
    public void fire(StepEvent event) {
        if (event instanceof StepFailureEvent) {
            fire(Event.STEP_FAILED);
        }
    }

    public void setTitleTo(Step step) {
        if (step != null) {
            String lastStepTitle = step.getTitle();

            if (!StringUtils.isEmpty(step.getTitle())) {
                step.setTitle(new MetaInfo(lastStepTitle).getTitleWithoutMeta());
            }
        }
    }

    private void setTitleToTest() {
        TestCaseResult test = getTestStorage().get();
        String lastStepTitle = test.getTitle();

        if (!StringUtils.isEmpty(test.getTitle())) {
            test.setTitle(new MetaInfo(lastStepTitle).getTitleWithoutMeta());
        }
    }

    protected void fire(Event event) {
        Exception exception = null;
        ListenerFunction failedFunction = null;
        for (ListenerFunction function : functions) {
            if (function.needsOn(event)) {
                try {
                    function.execute();
                } catch (Exception ex) {
                    failedFunction = function;
                    exception = ex;
                }
            }
        }
        if(exception != null) {
            throw new RuntimeException("Function " + failedFunction.getClass().getSimpleName() + " was failed in step listener!", exception);
        }
    }

    public StepStorage getStepStorage() {
        return get(StepStorage.class, "stepStorage");
    }

    public TestCaseStorage getTestStorage() {
        return get(TestCaseStorage.class, "testCaseStorage");
    }

    private <T> T get(Class<T> fieldType, String fieldName) {
        try {
            Field field = Allure.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(Allure.LIFECYCLE);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException("Cannot get " + fieldName + " from Allure.LIFECYCLE!", ex);
        }
    }
}
