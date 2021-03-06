package com.qantium.uisteps.allure.tests.listeners.handlers;

import com.qantium.uisteps.allure.tests.listeners.Event;

import static com.qantium.uisteps.allure.tests.listeners.Event.TEST_FINISHED;

/**
 * Created by Anton Solyankin
 */
public class CloseBrowsers extends EventHandler {

    public CloseBrowsers() {
        super(new Event[]{TEST_FINISHED});
    }

    @Override
    public Object handle(Event event, Object... args) {
        closeBrowsers();
        return null;
    }

    private void closeBrowsers() {
        getListener().getTest().closeAllBrowsers();
    }
}
