Implementing full compatibility for Kafka consumers and producers in a vehicle inventory management system using VIN and event schema versioning in Java involves several key components and best practices. The goal is to allow producers and consumers to evolve independently without breaking the system when schema changes occur.

Here's a breakdown of the approach and Java code considerations:

1. Core Principles for Compatibility
Schema Registry: This is paramount. A schema registry (like Confluent Schema Registry or Apicurio Registry) centralizes schema management, stores schema versions, and enforces compatibility rules. Both producers and consumers interact with it to retrieve and validate schemas.
Serialization Format: Use a robust serialization format that supports schema evolution, such as Apache Avro or Protobuf. Avro is particularly popular with Kafka due to its excellent schema evolution capabilities.
Compatibility Modes: Understand and leverage schema compatibility modes provided by the Schema Registry:
Backward Compatibility: New consumers can read data produced by old producers. (Most common and often a minimum requirement).
Forward Compatibility: Old consumers can read data produced by new producers.
Full Compatibility: Both backward and forward compatible. This is the ideal for maximum flexibility.
Backward Transitive, Forward Transitive, Full Transitive: These modes apply the compatibility rule across all previous schema versions, not just the immediate previous one.
VIN as Key: Using the Vehicle Identification Number (VIN) as the Kafka message key is an excellent choice for vehicle inventory management.
Guaranteed Ordering: Messages for the same VIN will always go to the same partition and be processed in order. This is crucial for maintaining the correct state of a vehicle (e.g., "created," "in-transit," "sold").
Efficient Lookups: Consumers interested in a specific vehicle can leverage the key for targeted consumption or joins in stream processing.
Compacted Topics: If you configure your topic as a compacted topic, Kafka will retain only the last message for each key, which is beneficial for maintaining the latest state of each VIN.
2. Event Schema Versioning Strategies
While the Schema Registry handles the technical aspect of schema evolution, you still need a logical strategy for your event types:

Add Version in Event Payload/Metadata:
Include a version field directly within your Avro schema (e.g., eventVersion: 1).
Consumers can then use this field to apply different processing logic based on the event version.
Pros: Centralized version information.
Cons: Consumer code might become more complex if it needs to handle multiple versions of the same event type.
Separate Topics/Streams for Breaking Changes:
If a schema change is not backward or forward compatible (a "breaking change"), consider creating a new topic (e.g., vehicle-events-v1, vehicle-events-v2).
Pros: Clear isolation, older consumers are unaffected.
Cons: Can lead to topic sprawl, producers need to publish to multiple topics during transition, harder to aggregate data across versions.
Recommendation: Strive for compatible changes first. Only use separate topics as a last resort for truly breaking changes where direct backward/forward compatibility is impossible.
3. Java Code Implementation (Illustrative Examples)
Let's assume you're using Confluent Schema Registry and Avro.

Maven Dependencies
XML

<dependencies>
    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
        <version>3.7.0</version> </dependency>

    <dependency>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro</artifactId>
        <version>1.11.1</version>
    </dependency>

    <dependency>
        <groupId>io.confluent</groupId>
        <artifactId>kafka-avro-serializer</artifactId>
        <version>7.6.1</version> </dependency>
</dependencies>
Avro Schema Definition (Example: VehicleEvent.avsc)
Let's start with a basic schema for a VehicleCreated event.

src/main/resources/avro/VehicleCreated_v1.avsc

JSON

{
  "type": "record",
  "name": "VehicleCreated",
  "namespace": "com.example.inventory.events",
  "fields": [
    {"name": "vin", "type": "string"},
    {"name": "make", "type": "string"},
    {"name": "model", "type": "string"},
    {"name": "year", "type": "int"},
    {"name": "timestamp", "type": "long"}
  ]
}
Now, let's evolve it to VehicleCreated_v2.avsc by adding an optional field color with a default value, ensuring backward compatibility.

src/main/resources/avro/VehicleCreated_v2.avsc

JSON

{
  "type": "record",
  "name": "VehicleCreated",
  "namespace": "com.example.inventory.events",
  "fields": [
    {"name": "vin", "type": "string"},
    {"name": "make", "type": "string"},
    {"name": "model", "type": "string"},
    {"name": "year", "type": "int"},
    {"name": "color", "type": ["null", "string"], "default": null}, // Added optional field with default
    {"name": "timestamp", "type": "long"}
  ]
}
Important for Avro Schema Evolution:

Adding fields: Add new fields as ["null", "newType"] with a default value. This makes them optional for older data, ensuring backward compatibility.
Removing fields: Not directly backward compatible without careful planning. If you must remove, ensure old consumers are updated first, or use a translation layer.
Renaming fields: Use Avro aliases to maintain compatibility.
Changing types: Generally a breaking change. Avoid if possible, or create a new event type/topic.
Kafka Producer
Java

import org.apache.kafka.clients.producer.*;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class VehicleProducer {

    private final Producer<String, GenericRecord> producer;
    private final String topicName;
    private final Schema vehicleCreatedSchemaV1; // For demonstration of V1
    private final Schema vehicleCreatedSchemaV2; // For demonstration of V2

    public VehicleProducer(String bootstrapServers, String schemaRegistryUrl, String topicName) throws IOException {
        this.topicName = topicName;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);

        this.producer = new KafkaProducer<>(props);

        // Load Avro schemas from resources
        try (InputStream isV1 = getClass().getClassLoader().getResourceAsStream("avro/VehicleCreated_v1.avsc");
             InputStream isV2 = getClass().getClassLoader().getResourceAsStream("avro/VehicleCreated_v2.avsc")) {
            if (isV1 == null || isV2 == null) {
                throw new IOException("Avro schema files not found.");
            }
            this.vehicleCreatedSchemaV1 = new Schema.Parser().parse(isV1);
            this.vehicleCreatedSchemaV2 = new Schema.Parser().parse(isV2);
        }
    }

    public void sendVehicleCreatedEventV1(String vin, String make, String model, int year) {
        GenericRecord vehicleEvent = new GenericData.Record(vehicleCreatedSchemaV1);
        vehicleEvent.put("vin", vin);
        vehicleEvent.put("make", make);
        vehicleEvent.put("model", model);
        vehicleEvent.put("year", year);
        vehicleEvent.put("timestamp", System.currentTimeMillis());

        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topicName, vin, vehicleEvent);

        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.printf("Successfully sent V1 event: VIN=%s, Topic=%s, Partition=%d, Offset=%d%n",
                            vin, metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void sendVehicleCreatedEventV2(String vin, String make, String model, int year, String color) {
        GenericRecord vehicleEvent = new GenericData.Record(vehicleCreatedSchemaV2);
        vehicleEvent.put("vin", vin);
        vehicleEvent.put("make", make);
        vehicleEvent.put("model", model);
        vehicleEvent.put("year", year);
        vehicleEvent.put("color", color); // This field is new in V2
        vehicleEvent.put("timestamp", System.currentTimeMillis());

        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topicName, vin, vehicleEvent);

        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.printf("Successfully sent V2 event: VIN=%s, Topic=%s, Partition=%d, Offset=%d%n",
                            vin, metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void close() {
        producer.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String bootstrapServers = "localhost:9092"; // Your Kafka broker(s)
        String schemaRegistryUrl = "http://localhost:8088"; // Your Schema Registry URL
        String topic = "vehicle-inventory-events";

        VehicleProducer producer = new VehicleProducer(bootstrapServers, schemaRegistryUrl, topic);

        // Simulate sending V1 events (older producer)
        System.out.println("--- Sending V1 events ---");
        producer.sendVehicleCreatedEventV1("VIN12345", "Toyota", "Camry", 2020);
        Thread.sleep(1000);
        producer.sendVehicleCreatedEventV1("VIN67890", "Honda", "Civic", 2022);
        Thread.sleep(1000);

        // Simulate sending V2 events (newer producer)
        System.out.println("--- Sending V2 events ---");
        producer.sendVehicleCreatedEventV2("VIN11223", "Ford", "F-150", 2023, "Blue");
        Thread.sleep(1000);
        producer.sendVehicleCreatedEventV2("VIN44556", "Tesla", "Model 3", 2024, "Red");
        Thread.sleep(1000);

        producer.close();
    }
}
Kafka Consumer
Java

import org.apache.kafka.clients.consumer.*;
import org.apache.avro.generic.GenericRecord;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class VehicleConsumer {

    private final Consumer<String, GenericRecord> consumer;
    private final String topicName;

    public VehicleConsumer(String bootstrapServers, String schemaRegistryUrl, String topicName, String groupId) {
        this.topicName = topicName;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);
        // Important for backward compatibility:
        // This allows the deserializer to use the consumer's schema when the producer's schema is newer.
        // It's the default for Avro, but good to be explicit.
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);

        // Auto-commit offsets for simplicity, in production consider manual commits
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topicName));
    }

    public void startConsuming() {
        System.out.println("Starting consumer for topic: " + topicName);
        while (true) {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, GenericRecord> record : records) {
                GenericRecord vehicleEvent = record.value();

                // Handle schema versioning dynamically based on the event payload
                // The Avro deserializer will automatically resolve the schema.
                // You can access fields by their name, and if a field doesn't exist in the record's schema
                // but exists in your consumer's reader schema (because of a default value or being optional),
                // it will be handled gracefully (e.g., return null or default value).

                String vin = (String) vehicleEvent.get("vin");
                String make = (String) vehicleEvent.get("make");
                String model = (String) vehicleEvent.get("model");
                int year = (Integer) vehicleEvent.get("year");

                // Check for the 'color' field, which might not exist in V1 events
                Object color = vehicleEvent.get("color"); // Will be null for V1 events

                System.out.printf("Received event: VIN=%s, Make=%s, Model=%s, Year=%d", vin, make, model, year);
                if (color != null) {
                    System.out.printf(", Color=%s", color);
                }
                System.out.printf(" (Topic: %s, Partition: %d, Offset: %d)%n",
                        record.topic(), record.partition(), record.offset());
            }
        }
    }

    public void close() {
        consumer.close();
    }

    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092"; // Your Kafka broker(s)
        String schemaRegistryUrl = "http://localhost:8088"; // Your Schema Registry URL
        String topic = "vehicle-inventory-events";
        String groupId = "vehicle-inventory-consumer-group";

        VehicleConsumer consumer = new VehicleConsumer(bootstrapServers, schemaRegistryUrl, topic, groupId);
        try {
            consumer.startConsuming();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
4. Setting up Confluent Schema Registry (Prerequisite)
Before running the code, ensure you have Kafka and Confluent Schema Registry running. You can typically do this using Docker Compose:

YAML

version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.1
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  broker:
    image: confluentinc/cp-kafka:7.6.1
    hostname: broker
    container_name: broker
    ports:
      - "9092:9092"
      - "9093:9093"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://broker:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    depends_on:
      - zookeeper

  schema-registry:
    image: confluentinc/cp-schema-registry:7.6.1
    hostname: schema-registry
    container_name: schema-registry
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: 'broker:29092'
      SCHEMA_REGISTRY_LISTENERS: http://0.0.0.0:8081
    depends_on:
      - broker
Save this as docker-compose.yml and run docker-compose up -d.

5. Compatibility Verification
The KafkaAvroSerializer and KafkaAvroDeserializer are designed to work seamlessly with Confluent Schema Registry to ensure compatibility.

When the producer sends a message, it checks with the Schema Registry if the schema it's using is compatible with the latest registered schema for that topic's value (or key, if you serialize the key with Avro). If it's a new schema, it registers it.
When the consumer receives a message, the Avro deserializer reads the schema ID embedded in the message. It then fetches that specific schema from the Schema Registry and uses it to deserialize the message. Crucially, it then attempts to project that data onto the consumer's expected schema (the schema derived from the GenericRecord or SpecificRecord type the consumer is configured to use).
If your producer sends V1 data and your consumer expects V2 data, the color field will simply be null in the GenericRecord when you call vehicleEvent.get("color").
If your producer sends V2 data and your consumer (an older version) expects V1 data, the KafkaAvroDeserializer will still be able to deserialize the V2 message because the color field has a default value (null) and is marked as optional in the V2 schema. The older consumer code would simply not have a line like vehicleEvent.get("color") or would handle a null gracefully.
Best Practices for Vehicle Inventory Management
Event Granularity: Design your events to be small, immutable, and representing a single fact (e.g., VehicleCreated, VehicleStatusUpdated, VehicleLocationChanged, VehicleSold).
State Management: For your inventory, a Kafka topic can serve as an event log. To get the current state of a vehicle, consumers would typically process all events related to a VIN in order. For performance, you might use Kafka Streams or KSQL to build materialized views (e.g., a compacted topic or a database) of the current vehicle state.
VIN as Key: Reinforce that VIN as the key is critical for ordering and partitioning.
Idempotent Consumers: Design your consumers to be idempotent. If a consumer processes the same event twice (due to retries or rebalances), it should not lead to incorrect state changes.
Monitoring: Monitor your Kafka cluster, Schema Registry, and application logs for deserialization errors or schema compatibility issues.
CI/CD Integration: Integrate schema compatibility checks into your CI/CD pipeline using tools like the Confluent Schema Registry Maven Plugin or REST API calls. This helps catch breaking changes early.
By following these principles and leveraging the Schema Registry with Avro, you can achieve robust and maintainable Kafka consumer and producer full compatibility for your vehicle inventory management system.


Sources





How to implement Kafka consumer and producer full compatibility (Java code) for vehicle inventory management using VIN and Event schema versioning



Show thinking
I can certainly help you with that! Implementing full compatibility for Kafka consumers and producers, especially with schema versioning and VIN as a key, is crucial for a robust vehicle inventory management system.

Below, you'll find a detailed explanation of the concepts involved, the necessary Maven dependencies, examples of Avro schemas for versioning, and the complete Java code for both your Kafka producer and consumer. This setup leverages Confluent Schema Registry and Avro to ensure seamless compatibility as your data evolves.

Key Concepts for Full Compatibility
Schema Registry: This is fundamental. A schema registry acts as a centralized repository for your data schemas (e.g., Avro, Protobuf, JSON Schema). It serves several vital purposes:
Centralized Management: All producers and consumers get schemas from a single source.
Schema Evolution Enforcement: It validates new schema versions against existing ones based on configured compatibility rules (e.g., backward, forward, full).
Serialization/Deserialization (SerDes): Kafka clients integrate with the Schema Registry to serialize outgoing messages and deserialize incoming messages, handling the schema lookup transparently.
Apache Avro: A data serialization framework that is highly recommended for Kafka due to its efficient binary format and robust support for schema evolution. Avro schemas are defined in JSON, and Avro provides strong guarantees for compatibility.
Compatibility Modes: The Schema Registry enforces these rules to prevent breaking changes:
Backward Compatibility: A new consumer can read data produced by an older producer. This is typically the minimum requirement. New fields should be added with default values or as optional (union with null).
Forward Compatibility: An older consumer can read data produced by a newer producer. This is harder to achieve but important if you update producers before consumers. It usually means that new fields in the newer schema must be ignorable by older consumers (e.g., they don't depend on them, or the fields are optional).
Full Compatibility: Both backward and forward compatible. This offers the most flexibility, allowing producers and consumers to be deployed independently. Changes typically involve adding new optional fields or removing fields that were previously optional.
Transitive Modes: Apply the compatibility rule across all previous schema versions, not just the immediate prior one. FULL_TRANSITIVE is often the safest choice for long-lived topics.
VIN as Key: Using the Vehicle Identification Number (VIN) as the Kafka message key for vehicle events is an excellent practice:
Message Ordering: All events for a specific VIN will be sent to the same Kafka partition, guaranteeing that they are processed in the order they were produced. This is crucial for maintaining the correct state of a vehicle (e.g., VehicleCreated before VehicleStatusUpdated).
Stateful Processing: Facilitates stateful stream processing, allowing you to easily build materialized views of current vehicle inventory by aggregating events for each VIN.
Compacted Topics: If your topic is configured for log compaction, using VIN as the key ensures that only the latest event for each VIN is retained, which is useful for maintaining the most up-to-date vehicle state.
Event Schema Versioning Strategy
The recommended approach is to embed the version logic within your Avro schema evolution, guided by the Schema Registry's compatibility rules.

Adding New Fields: When you need to add a new field (e.g., color to a VehicleCreated event), define it as a union with null and provide a default value in your new Avro schema.

JSON

{"name": "color", "type": ["null", "string"], "default": null}
This ensures that older consumers (using an older schema) can still read messages from newer producers (using the updated schema) because they will simply ignore the new field. Likewise, newer consumers can read older messages, with the color field being null for those older messages. This achieves backward and forward compatibility.

Handling Non-Compatible Changes: For truly breaking changes (e.g., renaming a mandatory field, changing a field's data type fundamentally), the best practice is to:

Introduce a new event type (e.g., VehicleCreatedV2 instead of VehicleCreated). This allows you to create a completely new schema without impacting existing consumers.
Use a new Kafka topic (e.g., vehicle-events-v2). This provides clear isolation. However, this can lead to "topic sprawl" and requires producers to potentially publish to multiple topics during a migration period.
<!-- end list -->

General advice: Always strive for backward/forward compatible changes first. Only resort to new event types/topics when a change is fundamentally incompatible and cannot be handled gracefully via schema evolution.
Prerequisites: Confluent Platform (Kafka & Schema Registry)
Before running the Java code, you'll need a Kafka cluster and a Confluent Schema Registry instance running. The easiest way to set this up for local development is using Docker Compose.

docker-compose.yml

YAML

version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.1
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  broker:
    image: confluentinc/cp-kafka:7.6.1
    hostname: broker
    container_name: broker
    ports:
      - "9092:9092"
      - "9093:9093"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://broker:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    depends_on:
      - zookeeper

  schema-registry:
    image: confluentinc/cp-schema-registry:7.6.1
    hostname: schema-registry
    container_name: schema-registry
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: 'broker:29092'
      SCHEMA_REGISTRY_LISTENERS: http://0.0.0.0:8081
    depends_on:
      - broker
To start: Save this content as docker-compose.yml in a directory, then run docker-compose up -d in your terminal from that directory.

Java Implementation
You will need a Maven project.

Maven Dependencies (pom.xml)
XML

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example.inventory</groupId>
    <artifactId>vehicle-kafka-app</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>vehicle-kafka-app</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <kafka.version>3.7.0</kafka.version> <!-- Use a compatible Kafka version -->
        <avro.version>1.11.1</avro.version>
        <confluent.version>7.6.1</confluent.version> <!-- Use a compatible Confluent Platform version -->
    </properties>

    <repositories>
        <!-- Confluent Maven Repository for Kafka Avro Serializer -->
        <repository>
            <id>confluent</id>
            <url>https://packages.confluent.io/maven/</url>
        </repository>
    </repositories>

    <dependencies>
        <!-- Apache Kafka Clients -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>${kafka.version}</version>
        </dependency>

        <!-- Apache Avro -->
        <dependency>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro</artifactId>
            <version>${avro.version}</version>
        </dependency>

        <!-- Confluent Kafka Avro Serializer/Deserializer -->
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>${confluent.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>com.example.inventory.producer.VehicleProducer</mainClass>
                        </manifest>
                    </archive>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!-- Avro Plugin to generate Java classes from .avsc files (optional but good for specific records) -->
            <plugin>
                <groupId>org.apache.avro</groupId>
                <artifactId>avro-maven-plugin</artifactId>
                <version>${avro.version}</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>schema</goal>
                        </goals>
                        <configuration>
                            <sourceDirectory>${project.basedir}/src/main/resources/avro/</sourceDirectory>
                            <outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
Avro Schema Definitions
Create a directory src/main/resources/avro/ in your project.

src/main/resources/avro/VehicleCreated_v1.avsc
(Initial schema for a new vehicle)
VehicleCreated_v1 Avro Schema
Jun 20, 5:17 PM

Open

src/main/resources/avro/VehicleCreated_v2.avsc
(Evolved schema with an optional color field, ensuring backward compatibility)
VehicleCreated_v2 Avro Schema
Jun 20, 5:17 PM

Open

Kafka Producer Java Code (src/main/java/com/example/inventory/producer/VehicleProducer.java)
This producer demonstrates sending both V1 and V2 VehicleCreated events. The Schema Registry handles the registration and compatibility checks automatically.
Kafka Vehicle Event Producer
Jun 20, 5:17 PM

Open

Kafka Consumer Java Code (src/main/java/com/example/inventory/consumer/VehicleConsumer.java)
This consumer will correctly process messages produced by both V1 and V2 producers due to the Avro deserializer's schema resolution capabilities and the SPECIFIC_AVRO_READER_CONFIG setting.
Kafka Vehicle Event Consumer
Jun 20, 5:17 PM

Open

How to Run
1. Start Docker Compose: Navigate to the directory containing docker-compose.yml and run docker-compose up -d. This will start Kafka and Schema Registry.
2. Compile and Package (Maven):
    Open a terminal in your Java project's root directory.
    Run mvn clean install to compile the code and package it into a JAR. This will also generate Java classes from your .avsc files if you included 
        the avro-maven-plugin. For this example, we are using GenericRecord, so generating specific classes is not strictly necessary but a good 
        practice for larger projects.
3. Run Producer:
    Execute the producer: java -jar target/vehicle-kafka-app-1.0-SNAPSHOT-jar-with-dependencies.jar 
        (replace vehicle-kafka-app-1.0-SNAPSHOT-jar-with-dependencies.jar with your actual JAR name if it differs).
    You should see output indicating messages being sent (both V1 and V2 events).
4. Run Consumer:
    Open a separate terminal.
    Execute the consumer: java -jar target/vehicle-kafka-app-1.0-SNAPSHOT-jar-with-dependencies.jar 
        com.example.inventory.consumer.VehicleConsumer
    You should see the consumer receiving and printing the vehicle events. Notice how it handles the color field gracefully, printing "Color=N/A (or null)" for V1 events and the actual color for V2 events.

This setup provides a robust foundation for building a fully compatible Kafka-based vehicle inventory management system with evolving schemas.
