package ca.jrvs.apps.trading.dto.portfolio;


import ca.jrvs.apps.trading.entity.Position;
import ca.jrvs.apps.trading.entity.Quote;

import java.util.List;

public class PortfolioView {

    private List<SecurityRow> securityRows;

    public PortfolioView() {

    }

    public PortfolioView(List<SecurityRow> securityRows) {
        this.securityRows = securityRows;
    }

    public List<SecurityRow> getSecurityRows() {
        return securityRows;
    }

    public void setSecurityRows(List<SecurityRow> securityRows) {
        this.securityRows = securityRows;
    }
}
