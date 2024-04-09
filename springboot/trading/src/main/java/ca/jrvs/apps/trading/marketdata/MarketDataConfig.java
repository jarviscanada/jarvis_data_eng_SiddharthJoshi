package ca.jrvs.apps.trading.marketdata;

import org.springframework.stereotype.Component;

/**
 * Responsible for setting up the HTTP client utilized by MarketDataDao to make the API call
 */
@Component
public class MarketDataConfig {

    private String host;
    private String token;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
