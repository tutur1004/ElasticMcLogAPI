package fr.milekat.elastimclogapi;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import fr.milekat.elastimclogapi.data.DataManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // TODO: 17/12/2021 Range selection ?

    /**
     * Get the first hit object by query from index
     * @param classType Instance of object (Can be empty or filed)
     * @param parameters Map of list of query filters with Field name / Field value
     * @param sorts Map of sorting filters with Field name / Bool value (true= Max / Desc, false= Min / Asc)
     * @return an object of type classType, if no result found, return the classType object
     */
    public Object getObject(Object classType, Map<String, Object> parameters, Map<String, Boolean> sorts) throws IOException {
        Optional<? extends Hit<?>> optional = dataManager
                .query(classType, parameters, sorts, 1).stream().findFirst();
        if (optional.isPresent()) return optional.get().source();
        return classType;
    }

    /**
     * Get the first hit object by query from index
     * @param classType Instance of object (Can be empty or filed)
     * @param parameters Map of list of query filters with Field name / Field value
     * @return an object of type classType, if no result found, return the classType object
     */
    public Object getObject(Object classType, Map<String, Object> parameters) throws IOException {
        Optional<? extends Hit<?>> optional = dataManager
                .query(classType, parameters, new HashMap<>(), 1).stream().findFirst();
        if (optional.isPresent()) return optional.get().source();
        return classType;
    }

    /**
     * Get a list of objects hits by query from index
     * @param classType Instance of object
     * @param parameters Map of list of query filters with Field name / Field value
     * @param sorts Map of sorting filters with Field name / Bool value (true= Max / Desc, false= Min / Asc)
     * @param limit Number of results
     * @return a list of object of type classType, if no result, return an empty list
     */
    public List<Object> getObjects(Object classType, Map<String, Object> parameters, Map<String, Boolean> sorts,
                              int limit) throws IOException {
        return dataManager.query(classType, parameters, sorts, limit)
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    /**
     * Get a list of objects hits by query from index
     * @param classType Instance of object
     * @param parameters Map of list of query filters with Field name / Field value
     * @param limit Number of results
     * @return a list of object of type classType, if no result, return an empty list
     */
    public List<Object> getObjects(Object classType, Map<String, Object> parameters, int limit) throws IOException {
        return dataManager.query(classType, parameters, new HashMap<>(), limit)
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
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
