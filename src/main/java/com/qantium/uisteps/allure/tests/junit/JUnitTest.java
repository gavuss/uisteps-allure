package com.qantium.uisteps.allure.tests.junit;

import com.qantium.uisteps.allure.tests.BaseTest;
import com.qantium.uisteps.allure.tests.listeners.StepListener;
import org.junit.Rule;
import ru.yandex.qatools.allure.annotations.Step;

/**
 * Created by Anton Solyankin
 */
public class JUnitTest extends BaseTest {

    public JUnitTest() {
        this(new StepListener());
    }

    public JUnitTest(StepListener listener) {
        super(listener);
    }

    @Rule
    public RetryRule retryRule = new RetryRule(this);


    @Step("-------- [Retry test. Attempt: {0}] -------- META[listen=false]")
    protected void retryTest(int attempt) {

    }
}
