package fr.milekat.elastimclogapi.data;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

public class ESClients {
    RestClientTransport transport;

    /**
     * Init RestClient
     */
    public ESClients(FileConfiguration config) {
        //  Load configs
        String hostname = config.getString("elasticsearch.hostname", "localhost");
        int port = config.getInt("elasticsearch.port", 9200);
        String username = config.getString("elasticsearch.username", null);
        String password = config.getString("elasticsearch.password", null);

        //  Init Builder
        RestClientBuilder restClient = RestClient.builder(new HttpHost(hostname, port));

        // Login with user if set in configs
        if (username!=null) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            restClient.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            });
        }

        //  Build client
        transport = new RestClientTransport(restClient.build(), new JacksonJsonpMapper());
    }

    /**
     * Get a sync ES client
     */
    public ElasticsearchClient getClient() {
        return new ElasticsearchClient(transport);
    }

    /**
     * Get an async ES client
     */
    public ElasticsearchAsyncClient getAsyncClient() {
        return new ElasticsearchAsyncClient(transport);
    }
}
