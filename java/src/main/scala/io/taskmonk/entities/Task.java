package io.taskmonk.entities;

import java.util.HashMap;
import java.util.Map;

public class Task {
    public String externalId;
    public String projectId;
    public String batchId;
    public Map<String, String> input;
    public Map<String, String> output = new HashMap<String, String>();

    public Task(String externalId, String projectId, String batchId,
                Map<String, String> input) {
        this.externalId = externalId;
        this.projectId = projectId;
        this.batchId = batchId;
        this.input = input;
    }
}
