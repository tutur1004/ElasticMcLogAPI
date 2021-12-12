package fr.milekat.elastimclogapi;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import fr.milekat.elastimclogapi.data.DataManager;

import java.util.List;

public record ESMcLogApi(DataManager dataManager) {
    /**
     * Get pending BulkOperation
     */
    public List<BulkOperation> getPending() {
        return dataManager.getPending();
    }

    /**
     * Add an object to send into ElasticSearch
     */
    public void addPendingBulk(Object object) {
        dataManager.addPending(object);
    }

    /**
     * Reload config.yml without loss of pending
     */
    public void reloadConfigs() {
        dataManager.reloadConfigs();
    }

    /**
     * Start pool processing if not currently running, use {@link #restartPool()} to force start
     */
    public void startPool() {
        dataManager.startPool();
    }

    /**
     * Restart pool processing
     */
    public void restartPool() {
        dataManager.restartPool();
    }

    /**
     * Process all reaming pending and then stop processing
     */
    public void closePool() {
        dataManager.closePool();
    }

    /**
     * Stop processing without processing pending
     */
    public void forceClosePool() {
        dataManager.forceClosePool();
    }
}
