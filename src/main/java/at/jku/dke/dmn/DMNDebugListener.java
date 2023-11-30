package at.jku.dke.dmn;

import org.kie.dmn.api.core.event.*;

/**
 * Writes info messages to the logger on each DMN runtime event.
 */
final class DMNDebugListener implements DMNRuntimeEventListener {

    /**
     * Creates a new instance of class {@link DMNDebugListener}.
     */
    public DMNDebugListener() {
    }

    @Override
    public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
        System.out.printf("[BeforeEvaluateDecision] %s%n", event.getDecision().getName());
    }

    @Override
    public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
        System.out.printf("[AfterEvaluateDecision] %s: %s%n", event.getDecision().getName(), event.getResult());
    }

    @Override
    public void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {
        System.out.printf("[BeforeEvaluateBKM] %s%n", event.getBusinessKnowledgeModel().getName());
    }

    @Override
    public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {
        System.out.printf("[AfterEvaluateBKM] %s: %s%n", event.getBusinessKnowledgeModel().getName(), event.getResult());
    }

    @Override
    public void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {
        System.out.printf("[BeforeEvaluateContextEntry] %s.%s%n", event.getNodeName(), event.getVariableName());
    }

    @Override
    public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
        System.out.printf("[AfterEvaluateContextEntry] %s.%s: %s%n", event.getNodeName(), event.getVariableName(), event.getResult());
    }

    @Override
    public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {
        System.out.printf("[BeforeEvaluateDecisionTable] %s.%s%n", event.getNodeName(), event.getDecisionTableName());
    }

    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
        System.out.printf("[BeforeEvaluateDecisionTable] %s.%s: %s%n", event.getNodeName(), event.getDecisionTableName(), event.getResult());
    }

    @Override
    public void beforeEvaluateDecisionService(BeforeEvaluateDecisionServiceEvent event) {
        System.out.printf("[BeforeEvaluateDecisionService] %s%n", event.getDecisionService().getName());
    }

    @Override
    public void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {
        System.out.printf("[AfterEvaluateDecisionService] %s: %s%n", event.getDecisionService().getName(), event.getResult());
    }

    @Override
    public void beforeInvokeBKM(BeforeInvokeBKMEvent event) {
        System.out.printf("[BeforeInvokeBKM] %s: %s%n", event.getBusinessKnowledgeModel().getName(), event.getInvocationParameters());
    }

    @Override
    public void afterInvokeBKM(AfterInvokeBKMEvent event) {
        System.out.printf("[AfterInvokeBKM] %s: %s%n", event.getBusinessKnowledgeModel().getName(), event.getInvocationResult());
    }

    @Override
    public void beforeEvaluateAll(BeforeEvaluateAllEvent event) {
        System.out.printf("[BeforeEvaluateAll] %s # %s%n", event.getModelNamespace(), event.getModelName());
    }

    @Override
    public void afterEvaluateAll(AfterEvaluateAllEvent event) {
        System.out.printf("[AfterEvaluateAll] %s # %s%n", event.getModelNamespace(), event.getModelName());
    }

}
