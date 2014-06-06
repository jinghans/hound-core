package com.yeezhao.hound.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.zookeeper.KeeperException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.yeezhao.commons.config.ParamConfiguration;
import com.yeezhao.commons.util.config.CommConsts;
import com.yeezhao.hound.core.HoundConsts;
import com.yeezhao.sealion.util.SealionConsts;

/**
 * elastic search 工厂
 * 
 * @author Darren Mo
 * @date 2014年5月28日 上午10:22:27
 */
public class ElasticSearchFactory {

	private static ElasticSearchFactory elasticSearchFactory;
	private static Client client;

	private ElasticSearchFactory() {
		client = getESClient();
	}
	
	public Client getESClient() {
		try {
			if(client == null){
				client = getESClient(getESHosts());
			}
		} catch (KeeperException e) {
			e.printStackTrace();
			throw new RuntimeException(String.format("get %s error, throw by KeeperException", HoundConsts.PARAM_ES_HOST));
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(String.format("get %s error, throw by InterruptedException", HoundConsts.PARAM_ES_HOST));
		}
		return client;
	}
	
	public static String[] getESHosts() throws KeeperException, InterruptedException{
		ParamConfiguration pconf = new ParamConfiguration(HBaseConfiguration.create(), CommConsts.ORG, CommConsts.APP);
		String esHosts = pconf.getParam(HoundConsts.PARAM_ES_HOST);
		return esHosts.split("[,]");
	}

	private static Client getESClient(String[] hosts) {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put(SealionConsts.ES_CLUSTER_NAME, HoundConsts.ES_CLUSTER_NAME)
				.put(SealionConsts.ES_CLIENT_PING_TIMEOUT,	SealionConsts.ES_DEFAULT_PING_TIME)
				.put(SealionConsts.ES_DEFAULT_PING_RETRIES,	SealionConsts.ES_DEFAULT_PING_RETRYNUM).build();
		Client client = new TransportClient(settings);
		List<String> hostList = Arrays.asList(hosts);
		Collections.shuffle(hostList);
		for (String host : hostList) {
			String[] vals = host.split(":");
			int port = vals.length > 1 ? Integer.parseInt(vals[1])
					: SealionConsts.ES_DEFAULT_HOST_PORT;
			((TransportClient) client)
					.addTransportAddress(new InetSocketTransportAddress(
							vals[0], port));
		}
		return client;
	}

	public static ElasticSearchFactory getInstance() {
		if (elasticSearchFactory == null) {
			elasticSearchFactory = new ElasticSearchFactory();
		}
		return elasticSearchFactory;
	}

}
