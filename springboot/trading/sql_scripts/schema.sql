CREATE TABLE public.quote (
    ticker          varchar             NOT NULL,
    last_price      double precision    NOT NULL,
    bid_price       double precision    NOT NULL,
    bid_size        integer             NOT NULL,
    ask_price       double precision    NOT NULL,
    ask_size        integer             NOT NULL,
    CONSTRAINT      quote_pk            PRIMARY KEY (ticker)
);

