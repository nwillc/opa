# OPA!

This framework provides a simple Object Persistence Approach, designed to manage entities
in a collection. The use and design is independent of the persistence mechanism.
Only simple operations and queries are provided, however the framework is sufficient for
managing small collections, and is trivial to implement over almost any persistence. Additionally, 
multiple persistence mechanism may be used simultaneously. 

Memory, MongoDb and JDBC persistence implementations are provided.

## Example Use

Assume you have a simple entity, with a Key, and Value:

```java

public class KeyValue extends HasKey<String> {
    private String value;
    
    public KeyValue(String key, String value) {
        super(key);
        this.value = value;
    }
    
    public setValue(String value) { this.value = value; }
    
    public String getValue() { return value; }
}
```

Entities inherit from `HasKey` to ensure they have a unique identifier. For the sake of the example
lets assume were going to manage a collection of these persisted in memory.

```java

    Dao<String, KeyValue> dao = new MemoryBackedDao<>();

```

With that `dao` you can perform various collection operations:

```java

    KeyValue pi = new KeyValue("pi","3.14");
    dao.save(pi); // save it
    pi.setValue("3.142");
    dao.save(pi); // update it
    Optional<KeyValue> havePi = dao.findOne("pi");   // search
    dao.delete("pi");  // delete

```

And you can perform basic queries in a persistence independent way:

```java

    Query<KeyValue> query = new QueryBuilder(KeyValue.class)
                            .contains("value","3")
                            .build();
    Stream<KeyValue> found = dao.find(query);
    
```

## Documentation

See the [Javadoc](https://nwillc.github.io/opa/javadoc/)

-----
[![Coverage](https://codecov.io/gh/nwillc/opa/branch/master/graphs/badge.svg?branch=master)](https://codecov.io/gh/nwillc/opa)
[![license](https://img.shields.io/github/license/nwillc/opa.svg)](https://tldrlegal.com/license/-isc-license)
[![Travis](https://img.shields.io/travis/nwillc/opa.svg)](https://travis-ci.org/nwillc/opa)
[![Download](https://api.bintray.com/packages/nwillc/maven/opa/images/download.svg)](https://bintray.com/nwillc/maven/opa/_latestVersion)
