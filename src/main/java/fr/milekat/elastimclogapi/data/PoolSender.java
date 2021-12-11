package fr.milekat.elastimclogapi.data;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import fr.milekat.elastimclogapi.ESMcLog;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.List;

public class PoolSender {
    private final FileConfiguration configuration;
    private final List<BulkOperation> pendingBulk;
    private final BukkitTask processor;

    public PoolSender(FileConfiguration configuration, List<BulkOperation> pendingBulk) {
        this.configuration = configuration;
        this.pendingBulk = pendingBulk;
        processor = getPoolProcessor();
    }

    /**
     * Periodically send PENDING to ElasticSearch cluster
     */
    private BukkitTask getPoolProcessor() {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(JavaPlugin.getPlugin(ESMcLog.class), () -> {
            ESClients esClients = new ESClients(configuration);
            try {
                esClients.getAsyncClient().bulk(
                        new BulkRequest.Builder()
                                .operations(pendingBulk)
                                .build()
                );
                pendingBulk.clear();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }, 0, configuration.getInt("config.pool-delay", 200));
    }

    /**
     * Stop PoolProcessor
     */
    public void stop() {
        processor.cancel();
    }
}
