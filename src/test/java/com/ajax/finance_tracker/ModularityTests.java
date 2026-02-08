package com.ajax.finance_tracker;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(FinanceTrackerApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}
