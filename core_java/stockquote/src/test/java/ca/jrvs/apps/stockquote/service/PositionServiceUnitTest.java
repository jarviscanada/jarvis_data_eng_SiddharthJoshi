package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dto.Position;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionServiceUnitTest {

    private PositionService positionService;
    private PositionDao mockedPositionDao;
    private QuoteService mockedQuoteService;

    @BeforeEach
    void setup() {

        // Mocking external dependencies (Dependencies outside the Position Service Module)
        this.mockedPositionDao = mock(PositionDao.class);
        this.mockedQuoteService = mock(QuoteService.class);

        // Injecting the mocks
        this.positionService = new PositionService(this.mockedQuoteService, this.mockedPositionDao);
    }

    @AfterEach
    void teardown() {

    }

    @Test
    void buyTest() throws SQLException {

        Position expectedPosition = new Position("TEST", 10, 10000);

        // Stubbing the behavior which checks the total amount of shares
        when(this.mockedQuoteService.areEnoughShares("TEST", 10)).thenReturn(true);

        // Stubbing the save operation to the database
        when(this.mockedPositionDao.save(expectedPosition)).thenReturn(expectedPosition);

        Position result = this.positionService.buy("TEST", 10, 10000);

        verify(this.mockedQuoteService).areEnoughShares("TEST", 10);
        verify(this.mockedPositionDao).save(expectedPosition);

        assertNotNull(result);
        assertEquals(expectedPosition, result);
    }

    @Test
    void sellTest() throws SQLException {

        // Crafting a dummy record from the position table
        Position testPosition = new Position("TEST", 50, 21000.50);

        // Stubbing the behavior
        when(this.mockedPositionDao.findById("TEST")).thenReturn(Optional.of(testPosition));
        doNothing().when(this.mockedPositionDao).deleteById("TEST");

        boolean result = this.positionService.sell("TEST");

        verify(this.mockedPositionDao).findById("TEST");
        verify(this.mockedPositionDao).deleteById("TEST");

        assertTrue(result);
    }

    @Test
    void displayAllRecordsTest() throws SQLException {

        // Crafting a dummy list
        List<Position> positionList = new ArrayList<>();
        positionList.add(new Position("TEST", 10, 4932.50));

        // Stubbing a method
        when(this.mockedPositionDao.findAll()).thenReturn(positionList.iterator());

        this.positionService.displayAllRecords();

        verify(this.mockedPositionDao).findAll();
    }
}