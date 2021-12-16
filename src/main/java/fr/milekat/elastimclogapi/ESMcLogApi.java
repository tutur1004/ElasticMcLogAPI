package fr.milekat.elastimclogapi;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import fr.milekat.elastimclogapi.data.DataManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * Get the first hit object by query from index
     */
    public Object getObject(Class<?> classType, Map<String, Object> parameters) throws IOException {
        return dataManager.query(classType, parameters).stream().findFirst();
    }

    /**
     * Get a list of objects hits by query from index
     */
    public List<? extends Hit<?>> getObjects(Class<?> classType, Map<String, Object> parameters) throws IOException {
        return dataManager.query(classType, parameters);
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
