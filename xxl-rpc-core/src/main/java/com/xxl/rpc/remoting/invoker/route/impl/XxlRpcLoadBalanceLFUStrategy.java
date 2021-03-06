package com.xxl.rpc.remoting.invoker.route.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.xxl.rpc.remoting.invoker.route.XxlRpcLoadBalance;

/**
 * lru
 *
 * @author xuxueli 2018-12-04
 */
public class XxlRpcLoadBalanceLFUStrategy extends XxlRpcLoadBalance {
	private ConcurrentHashMap<String, HashMap<String, Integer>> jobLfuMap = new ConcurrentHashMap<String, HashMap<String, Integer>>();
	private long CACHE_VALID_TIME = 0;

	public String doRoute(String serviceKey, TreeSet<String> addressSet) {
		// cache clear
		if (System.currentTimeMillis() > CACHE_VALID_TIME) {
			jobLfuMap.clear();
			CACHE_VALID_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
		}
		// lfu item init
		HashMap<String, Integer> lfuItemMap = jobLfuMap.get(serviceKey); // Key排序可以用TreeMap+构造入参Compare；Value排序暂时只能通过ArrayList；
		if (lfuItemMap == null) {
			lfuItemMap = new HashMap<String, Integer>();
			jobLfuMap.putIfAbsent(serviceKey, lfuItemMap); // 避免重复覆盖
		}
		for (String address : addressSet) {
			if (!lfuItemMap.containsKey(address) || lfuItemMap.get(address) > 1000000) {
				lfuItemMap.put(address, 0);
			}
		}
		// load least userd count address
		List<Map.Entry<String, Integer>> lfuItemList = new ArrayList<Map.Entry<String, Integer>>(lfuItemMap.entrySet());
		Collections.sort(lfuItemList, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Map.Entry<String, Integer> addressItem = lfuItemList.get(0);
		String minAddress = addressItem.getKey();
		addressItem.setValue(addressItem.getValue() + 1);
		return minAddress;
	}

	@Override
	public String route(String serviceKey, TreeSet<String> addressSet) {
		String finalAddress = doRoute(serviceKey, addressSet);
		return finalAddress;
	}
}
