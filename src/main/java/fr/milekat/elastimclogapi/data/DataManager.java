package fr.milekat.elastimclogapi.data;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
     * Add an object to pending saves
     */
    public void addPending(Object object) {
        pendingBulk.add(new BulkOperation.Builder()
                .create(
                        new CreateOperation.Builder<>()
                                .index(configuration.getString("config.base-index-name", "mc-log") +
                                        "-" + object.getClass().getName())
                                .document(object)
                                .build()
                )
                .build()
        );
    }

    /**
     * Reload configurations
     */
    public void reloadConfigs() {
        this.configuration = this.plugin.getConfig();
    }

    /**
     * Restart pool
     */
    public void restartPool() {
        pool.stop();
        pool = new PoolSender(configuration, pendingBulk);
    }

    /**
     * Execute all reaming pending in sync
     */
    public void closePool() {
        pool.stop();
        ESClients esClients = new ESClients(configuration);
        try {
            esClients.getClient().bulk(
                    new BulkRequest.Builder()
                            .operations(pendingBulk)
                            .build()
            );
            pendingBulk.clear();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
