create table if not exists events (
  aid varchar,    	// aggregate id
  tid varchar,      // command id
  ctime bigint,     // create time
  version bigint,
  body varchar,     // event serialization
  meta map<varchar, varchar>, // meta data
primary key (aid, version)
) with clustering order by (version desc);


create table if not exists snapshot (
  aid varchar,		// aggregate id
  mt varchar, 		// aggregate type
  ctime bigint,		// create time
  version bigint,	// aggregate version
  body varchar,		// aggregate
primary key (aid, version)
) with clustering order by (version desc);

