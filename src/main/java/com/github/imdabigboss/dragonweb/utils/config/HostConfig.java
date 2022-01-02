package com.github.imdabigboss.dragonweb.utils.config;

import java.util.List;

public class HostConfig {
    private String hostname;
    private String directory;
    private List<String> forbidden;

    public HostConfig(String hostname, String directory, List<String> forbidden) {
        this.hostname = hostname;
        this.directory = directory;
        this.forbidden = forbidden;
    }

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getDirectory() {
        return directory;
    }
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<String> getForbidden() {
        return forbidden;
    }
    public void setForbidden(List<String> forbidden) {
        this.forbidden = forbidden;
    }

    public boolean checkHostname(String hostname, String reqHostname) {
        if (hostname.contains(":")) {
            hostname = hostname.split(":")[0];
        }

        if (hostname.equals("*")) {
            return true;
        } else if (reqHostname.equals(hostname)) {
            return true;
        } else if (hostname.contains(".") && reqHostname.contains(".")) {
            int upTo = 0;
            boolean isOk = true;
            String[] hostnameArray = hostname.split("\\.");
            String[] reqHostnameArray = reqHostname.split("\\.");

            while (upTo < hostnameArray.length) {
                if (upTo >= reqHostnameArray.length) {
                    isOk = false;
                    break;
                }

                if (!hostnameArray[upTo].equals("*") && !hostnameArray[upTo].equals(reqHostnameArray[upTo])) {
                    isOk = false;
                    break;
                }

                upTo++;
            }

            return isOk;
        } else if (reqHostname.equals("")) {
            return true;
        } else {
            return false;
        }
    }
}
