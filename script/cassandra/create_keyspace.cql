CREATE KEYSPACE hummingbird
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };
