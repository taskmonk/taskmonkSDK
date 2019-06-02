package io.taskmonk.entities;

public class TaskImportResponse {
    public String batchId;
    public String jobId;

    public TaskImportResponse(String batchId, String jobId) {
        this.batchId = batchId;
        this.jobId = jobId;
    }
}
