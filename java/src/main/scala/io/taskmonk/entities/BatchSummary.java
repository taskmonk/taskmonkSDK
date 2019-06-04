package io.taskmonk.entities;

public class BatchSummary {
    public Integer newCount;
    public Integer inProgress;
    public Integer completed;
    public Integer total;
    public String jobId;
    public String fileUrl;

    public BatchSummary(BatchSummaryScala batchSummaryScala) {
        newCount = batchSummaryScala.new_count();
        inProgress = batchSummaryScala.in_progress();
        completed = batchSummaryScala.completed();
        total = batchSummaryScala.total();
        if (batchSummaryScala.job_id().isDefined()) {
            jobId = batchSummaryScala.job_id().get();
        }
        if (batchSummaryScala.file_url().isDefined()) {
            fileUrl = batchSummaryScala.file_url().get();
        }
    }

    public Boolean isBatchComplete() {
        return completed == total;
    }
}
