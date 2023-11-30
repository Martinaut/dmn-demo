package at.jku.dke.dmn;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws IOException {
        // Get DMN files (can be done better, not optimal solution)
        var files = Paths.get(".", "src", "main", "resources").toFile().listFiles((dir, name) -> name.endsWith(".dmn"));

        // Init file system
        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (var file : files) {
            kfs.write("src/main/resources/" + file.getName(), Files.readString(file.toPath()));
        }

        // Build file system
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            String err = kb.getResults().getMessages()
                    .stream()
                    .map(x -> x.getPath() + ": " + x.getText())
                    .collect(Collectors.joining());
            throw new RuntimeException("At least one model is invalid: " + err);
        }

        // Create container
        KieRepository kr = ks.getRepository();
        KieContainer container = ks.newKieContainer(kr.getDefaultReleaseId());

        // Obtain DMN runtime
        DMNRuntime runtime = KieRuntimeFactory.of(container.getKieBase()).get(DMNRuntime.class);
        runtime.addListener(new DMNDebugListener());

        // Get model
        var model = runtime.getModel("http://dke.jku.at/dmn", "Traffic Violation");

        // Create input data
        var ctx = runtime.newContext();
        ctx.set("Driver", new HashMap<String, Object>() {{
            put("Name", "Max Muster");
            put("City", "Vienna");
            put("Age", "25");
            put("Points", "1");
        }});
        ctx.set("Violation", new HashMap<String, Object>() {{
            put("Actual Speed", "100");
            put("Speed Limit", "50");
            put("Type", "speed");
            put("Date", new Date());
        }});

        // Execute
        var result = runtime.evaluateAll(model, ctx);

        // Print result
        System.out.println(resultToString(result));
    }

    private static String resultToString(DMNResult result) {
        if (result == null)
            return "";

        StringBuilder buf = new StringBuilder();
        buf.append(resultToString("INFO", result.getMessages(DMNMessage.Severity.INFO)));
        buf.append(resultToString("WARNING", result.getMessages(DMNMessage.Severity.WARN)));
        buf.append(resultToString("ERROR", result.getMessages(DMNMessage.Severity.ERROR)));

        buf.append("=============================================================================================================").append(System.lineSeparator())
                .append("DECISIONS").append(System.lineSeparator())
                .append("=============================================================================================================")
                .append(System.lineSeparator());
        buf.append(String.format("%-40s%-60s%-15s", "ID", "Name", "Status")).append(System.lineSeparator());
        for (var dr : result.getDecisionResults()) {
            buf.append(String.format("%-40s%-60s%-15s%n", dr.getDecisionId(), dr.getDecisionName(), dr.getEvaluationStatus()));

            if (!dr.getMessages().isEmpty()) {
                for (var msg : dr.getMessages()) {
                    buf.append("\t- ").append(msg.getText()).append(System.lineSeparator());
                }
            }
            if (dr.getResult() != null)
                buf.append("\t Result: ").append(dr.getResult()).append(System.lineSeparator());

            buf.append("-------------------------------------------------------------------------------------------------------------")
                    .append(System.lineSeparator());
        }
        return buf.toString();
    }

    private static String resultToString(String title, List<DMNMessage> messages) {
        if (messages.isEmpty())
            return "";

        StringBuilder buf = new StringBuilder();
        buf.append("=============================================================================================================")
                .append(System.lineSeparator());
        buf.append(title).append(System.lineSeparator());
        buf.append("=============================================================================================================")
                .append(System.lineSeparator());
        for (var msg : messages) {
            buf.append(msg.getText());
            buf.append(System.lineSeparator());

            if (msg.getFeelEvent() != null) {
                buf.append("\t");
                buf.append(msg.getFeelEvent().getSeverity()).append(' ');
                buf.append('[').append(msg.getLine()).append(':').append(msg.getColumn()).append(']').append(' ');
                buf.append(msg.getFeelEvent().getMessage());
                buf.append(System.lineSeparator());
            }

            if (msg.getException() != null)
                buf.append("\t").append(msg.getException()).append(System.lineSeparator());
        }

        buf.append("=============================================================================================================")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return buf.toString();
    }
}
