package cn.loli.client.connection;

import java.util.ArrayList;
import java.util.Collections;

public class ProxyEntry {
    public String name;
    public ArrayList<String> proxyList;

    public ProxyEntry(String name, String... ips) {
        this.name = name;
        proxyList = new ArrayList<>();
        Collections.addAll(proxyList, ips);
    }
}
