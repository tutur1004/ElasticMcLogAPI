package fr.milekat.elastimclogapi.data;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;

public class DataManager {
    private final Plugin plugin;
    private FileConfiguration configuration;
    private PoolSender pool;
    private final List<BulkOperation> pendingBulk = new LinkedList<>();

    public DataManager(Plugin plugin) {
        this.plugin = plugin;
        configuration = plugin.getConfig();
        pool = new PoolSender(configuration, pendingBulk);
    }

    /**
     * Query objects from index
     */
    public List<? extends Hit<?>> query(Class<?> classType, Map<String, Object> parameters) throws IOException {
        List<Query> queries = new ArrayList<>();
        parameters.forEach((key, value) -> {
            if (value instanceof String) {
                queries.add(new Query(new TermQuery.Builder()
                        .field(key)
                        .value(v -> v.stringValue(String.valueOf(value)))
                        .build())
                );
            } else if (value instanceof Number) {
                queries.add(new Query(new TermQuery.Builder()
                        .field(key)
                        .value(v -> v.stringValue(String.valueOf(value)))
                        .build())
                );
            }
        });
        return new ESClients(configuration).getClient()
                .search(s -> s
                        .index((configuration.getString("config.base-index-name", "mc-log") +
                                "-" + classType.getSimpleName()).toLowerCase(Locale.ROOT))
                        .query(q -> q.bool(new BoolQuery.Builder().must(queries).build())),
                        classType).hits().hits();
    }

    /**
     * Add an object to pending saves
     */
    public void addPending(Object object) {
        pendingBulk.add(new BulkOperation.Builder()
                .create(
                        new CreateOperation.Builder<>()
                                .index((configuration.getString("config.base-index-name", "mc-log") +
                                        "-" + object.getClass().getSimpleName()).toLowerCase(Locale.ROOT))
                                .document(object)
                                .build()
                )
                .build()
        );
    }

    /**
     * Get pending BulkOperation
     */
    public List<BulkOperation> getPending() {
        return pendingBulk;
    }

    /**
     * Reload configurations
     */
    public void reloadConfigs() {
        this.configuration = this.plugin.getConfig();
    }

    /**
     * Start pool, nothing done if already started
     */
    public void startPool() {
        if (pool==null) pool = new PoolSender(configuration, pendingBulk);
    }

    /**
     * Restart pool
     */
    public void restartPool() {
        if (pool!=null) closePool();
        pool = new PoolSender(configuration, pendingBulk);
    }

    /**
     * Execute all reaming pending in sync
     */
    public void closePool() {
        if (pool==null) return;
        pool.stop();
        ESClients esClients = new ESClients(configuration);
        try {
            if (pendingBulk.size()>0) esClients.getClient().bulk(
                    new BulkRequest.Builder()
                            .operations(pendingBulk)
                            .build()
            );
            pendingBulk.clear();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        pool = null;
    }

    /**
     * Close without processing {@link #pendingBulk}
     */
    public void forceClosePool() {
        if (pool==null) return;
        pool.stop();
        pool = null;
    }
}
