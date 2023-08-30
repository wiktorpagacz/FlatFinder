create TABLE IF NOT EXISTS "offer" (
    "id" uuid not null,
    "title" varchar(255),
    "source" varchar(255),
    "link" varchar(255),
    "comment" varchar(255),
    "price" integer,
    "original_price" integer,
    "space" double precision,
    "address" varchar(255),
    "insert_date" timestamp,
    "last_modified_time" timestamp,
    "write_to_docs" char(1),
    "write_to_docs_time" timestamp,
    "send_by_email" char(1),
    "send_by_email_time" timestamp,
    PRIMARY KEY ("id")
);

create index IF NOT EXISTS idx_offer_link
ON offer(link);