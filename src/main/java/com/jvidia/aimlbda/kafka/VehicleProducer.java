/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.kafka;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;

/**
 * Kafka Producer for vehicle inventory events.
 * This producer demonstrates sending events using different schema versions (V1 and V2)
 * to show compatibility using Avro and Confluent Schema Registry.
 * The VIN is used as the message key to ensure ordering for a specific vehicle.
 */
public class VehicleProducer {

    private final Producer<String, GenericRecord> producer;
    private final String topicName;
    private final Schema vehicleCreatedSchemaV1; // Represents an older schema version
    private final Schema vehicleCreatedSchemaV2; // Represents a newer, evolved schema version
    
    @Value("application.base-url")
    private String appHostPort;    

    /**
     * Constructor for the VehicleProducer.
     * Initializes Kafka producer properties and loads Avro schemas from resources.
     *
     * @param bootstrapServers The Kafka broker connection string (e.g., "localhost:9092").
     * @param schemaRegistryUrl The Confluent Schema Registry URL (e.g., "http://localhost:8088").
     * @param topicName The Kafka topic to produce messages to.
     * @throws IOException If Avro schema files cannot be loaded.
     */
    public VehicleProducer(String bootstrapServers, String schemaRegistryUrl, String topicName) throws IOException {
        this.topicName = topicName;

        Properties props = new Properties();
        // Kafka broker(s) to connect to
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Serializer for the message key (VIN, which is a String)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class.getName());
        // Serializer for the message value (Avro GenericRecord)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        // Schema Registry URL for Avro serialization
        //props.put("schema.registry.url", schemaRegistryUrl);
        //props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        // Acks configuration for producer: 'all' ensures all in-sync replicas have received the message.
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        // Retries configuration: how many times the producer will retry sending a message.
        props.put(ProducerConfig.RETRIES_CONFIG, 0); // Set to 0 for simplicity, use higher in production
        
        /* additional security settings - Azure etc
        props.put("compression.type", "lz4");
        props.put("linger.ms", "10");
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$ConnectionString\" password=\"<EVENT_HUB_CONN_STR>\";");
        // */

        this.producer = new KafkaProducer<>(props);

        // Load Avro schemas from classpath resources
        // Ensure that src/main/resources/avro/VehicleCreated_v1.avsc and VehicleCreated_v2.avsc exist
        try (InputStream isV1 = getClass().getClassLoader().getResourceAsStream("avro/VehicleCreated_v1.avsc");
             InputStream isV2 = getClass().getClassLoader().getResourceAsStream("avro/VehicleCreated_v2.avsc")) {
            if (isV1 == null || isV2 == null) {
                throw new IOException("Avro schema files (VehicleCreated_v1.avsc or VehicleCreated_v2.avsc) not found in resources. " +
                                      "Please ensure they are located in src/main/resources/avro/");
            }
            this.vehicleCreatedSchemaV1 = new Schema.Parser().parse(isV1);
            this.vehicleCreatedSchemaV2 = new Schema.Parser().parse(isV2);
        }
    }

    /**
     * Sends a VehicleCreated event using Schema V1.
     *
     * @param vin The Vehicle Identification Number.
     * @param make The vehicle's make.
     * @param model The vehicle's model.
     * @param year The vehicle's manufacturing year.
     */
    public void sendVehicleCreatedEventV1(String vin, String make, String model, int year) {
        // Create a GenericRecord based on the V1 schema
        GenericRecord vehicleEvent = new GenericData.Record(vehicleCreatedSchemaV1);
        vehicleEvent.put("vin", vin);
        vehicleEvent.put("make", make);
        vehicleEvent.put("model", model);
        vehicleEvent.put("year", year);
        vehicleEvent.put("timestamp", System.currentTimeMillis());

        // Create a ProducerRecord with VIN as key and Avro record as value
        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topicName, vin, vehicleEvent);

        // Send the record asynchronously with a callback for acknowledgement
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.printf("Producer V1: Successfully sent event for VIN=%s to Topic=%s, Partition=%d, Offset=%d%n",
                            vin, metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    System.err.printf("Producer V1: Error sending event for VIN=%s: %s%n", vin, exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
        
        
        // Send the record asynchronously with a callback for acknowledgement
        /* Lambda expression
        producer.send(record, (RecordMetadata metadata, Exception exception) -> {
            if (exception == null) {
                System.out.printf("Producer V1: Successfully sent event for VIN=%s to Topic=%s, Partition=%d, Offset=%d%n",
                        vin, metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                System.err.printf("Producer V1: Error sending event for VIN=%s: %s%n", vin, exception.getMessage());
                exception.printStackTrace();
            }
        });
        // */
    }

    /**
     * Sends a VehicleCreated event using Schema V2.
     * This schema includes the new 'color' field.
     *
     * @param vin The Vehicle Identification Number.
     * @param make The vehicle's make.
     * @param model The vehicle's model.
     * @param year The vehicle's manufacturing year.
     * @param color The vehicle's color (new field in V2).
     */
    public void sendVehicleCreatedEventV2(String vin, String make, String model, int year, String color) {
        // Create a GenericRecord based on the V2 schema
        GenericRecord vehicleEvent = new GenericData.Record(vehicleCreatedSchemaV2);
        vehicleEvent.put("vin", vin);
        vehicleEvent.put("make", make);
        vehicleEvent.put("model", model);
        vehicleEvent.put("year", year);
        vehicleEvent.put("color", color); // This field is new in V2 and handled by Schema Registry
        vehicleEvent.put("timestamp", System.currentTimeMillis());

        // Create a ProducerRecord with VIN as key and Avro record as value
        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topicName, vin, vehicleEvent);

        // Send the record asynchronously with a callback for acknowledgement
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.printf("Producer V2: Successfully sent event for VIN=%s to Topic=%s, Partition=%d, Offset=%d%n",
                            vin, metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    System.err.printf("Producer V2: Error sending event for VIN=%s: %s%n", vin, exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * Closes the Kafka producer, flushing any outstanding records.
     */
    public void close() {
        System.out.println("Closing VehicleProducer.");
        producer.close();
    }

    /**
     * Main method to run the producer.
     * It simulates sending V1 events first, then V2 events.
     *
     * @param args Command line arguments (not used).
     * @throws IOException If schema files cannot be loaded.
     * @throws InterruptedException If thread sleep is interrupted.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String bootstrapServers = "localhost:9092"; // Default Kafka broker address
        String schemaRegistryUrl = "http://localhost:8081"; // Default Confluent Schema Registry address
        String topic = "vehicle-inventory-events"; // Kafka topic name

        System.out.println("Initializing VehicleProducer...");
        VehicleProducer producer = new VehicleProducer(bootstrapServers, schemaRegistryUrl, topic);

        // Simulate sending V1 events (e.g., from an older application version)
        System.out.println("\n--- Sending VehicleCreated events using Schema V1 ---");
        producer.sendVehicleCreatedEventV1("VIN1234567890ABCDE1", "Toyota", "Camry", 2020);
        Thread.sleep(500);
        producer.sendVehicleCreatedEventV1("VIN234567890ABCDE2F", "Honda", "Civic", 2022);
        Thread.sleep(500);

        // Simulate sending V2 events (e.g., from a newer application version)
        System.out.println("\n--- Sending VehicleCreated events using Schema V2 ---");
        producer.sendVehicleCreatedEventV2("VIN34567890ABCDE3G", "Ford", "F-150", 2023, "Blue");
        Thread.sleep(500);
        producer.sendVehicleCreatedEventV2("VIN4567890ABCDE4H", "Tesla", "Model 3", 2024, "Red");
        Thread.sleep(500);
        producer.sendVehicleCreatedEventV2("VIN567890ABCDE5I", "Nissan", "Titan", 2021, "Black");
        Thread.sleep(500); // Give some time for messages to be sent

        producer.close();
        System.out.println("VehicleProducer finished sending messages and closed.");
    }
}   
