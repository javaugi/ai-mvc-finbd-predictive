/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.kafka;

//import org.apache.kafka.clients.consumer.*;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.avro.generic.GenericRecord;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * Kafka Consumer for vehicle inventory events.
 * This consumer is designed to be fully compatible, meaning it can read messages
 * produced by both older (V1) and newer (V2) schema versions.
 * It leverages Avro's schema evolution capabilities and Confluent Schema Registry.
 */
public class VehicleConsumer {

    private final Consumer<String, GenericRecord> consumer;
    private final String topicName;
    private volatile boolean running = true; // Flag to control the consumer loop

    /**
     * Constructor for the VehicleConsumer.
     * Initializes Kafka consumer properties.
     *
     * @param bootstrapServers The Kafka broker connection string (e.g., "localhost:9092").
     * @param schemaRegistryUrl The Confluent Schema Registry URL (e.g., "http://localhost:8088").
     * @param topicName The Kafka topic to consume messages from.
     * @param groupId The consumer group ID.
     */
    public VehicleConsumer(String bootstrapServers, String schemaRegistryUrl, String topicName, String groupId) {
        this.topicName = topicName;

        Properties props = new Properties();
        // Kafka broker(s) to connect to
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Consumer group ID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // Key deserializer (VIN, which is a String)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        // Value deserializer (Avro GenericRecord)
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        // Schema Registry URL for Avro deserialization
        //props.put("schema.registry.url", schemaRegistryUrl);
        //props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        
        
        // Important for backward/forward compatibility:
        // When set to 'false', the deserializer will return a GenericRecord that conforms
        // to the writer's schema, and can then be resolved against the reader's schema (your code's expectation).
        // This is the default and generally desired behavior for schema evolution.
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);

        // Auto-commit offsets periodically. For production, consider manual commits for exactly-once processing.
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"); // Commit every 1 second
        // Reset strategy if no previous offset is found for a partition or the offset is out of range
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start from the beginning of the topic

        this.consumer = new KafkaConsumer<>(props);
        // Subscribe to the specified topic
        this.consumer.subscribe(Collections.singletonList(topicName));
        // Set - Collections.singleton(topicName) does not preserve order
        // while Collections.singletonList(topicName) List does.
        //This is the correct and preferred way when calling subscribe(List<String> topics) in Kafka.

        // Add a shutdown hook to gracefully close the consumer
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Starts the consumer loop to continuously poll for and process messages.
     */
    public void startConsuming() {
        System.out.println("Starting VehicleConsumer for topic: " + topicName + " in group: " + consumer.groupMetadata().groupId());
        try {
            while (running) {
                // Poll for records with a timeout. A short timeout helps in quick shutdown.
                ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));

                if (!records.isEmpty()) {
                    System.out.printf("Polled %d records.%n", records.count());
                }

                for (ConsumerRecord<String, GenericRecord> record : records) {
                    GenericRecord vehicleEvent = record.value();

                    // Avro deserializer automatically handles schema resolution.
                    // Fields present in the message but not in the consumer's expected schema are ignored.
                    // Fields missing in the message but present in the consumer's expected schema (with defaults/null union)
                    // will be populated with their default/null values.

                    String vin = (String) vehicleEvent.get("vin");
                    String make = (String) vehicleEvent.get("make");
                    String model = (String) vehicleEvent.get("model");
                    int year = (Integer) vehicleEvent.get("year");

                    // The 'color' field might not exist in V1 events.
                    // Avro's GenericRecord.get() will return null if the field is not present
                    // in the writer's schema OR if it's explicitly null in the data.
                    // Since 'color' is defined as ["null", "string"] with a "default": null in V2,
                    // when processing V1 messages (which don't have 'color'), vehicleEvent.get("color")
                    // will return null, which is handled gracefully.
                    Object color = vehicleEvent.get("color");

                    System.out.printf("Consumed: VIN=%s, Make=%s, Model=%s, Year=%d", vin, make, model, year);
                    if (color != null) {
                        System.out.printf(", Color=%s", color);
                    } else {
                        System.out.print(", Color=N/A (or null)"); // Indicate if color was not present/null
                    }
                    System.out.printf(" (Topic: %s, Partition: %d, Offset: %d)%n",
                            record.topic(), record.partition(), record.offset());
                }
            }
        } catch (Exception e) {
            System.err.println("Consumer error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(); // Ensure consumer is closed on exit or error
        }
    }

    /**
     * Signals the consumer loop to stop.
     */
    public void shutdown() {
        System.out.println("Initiating graceful shutdown for VehicleConsumer...");
        this.running = false;
    }

    /**
     * Closes the Kafka consumer.
     */
    public void close() {
        if (consumer != null) {
            consumer.close();
            System.out.println("VehicleConsumer closed.");
        }
    }

    /**
     * Main method to run the consumer.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092"; // Default Kafka broker address
        String schemaRegistryUrl = "http://localhost:8081"; // Default Confluent Schema Registry address
        String topic = "vehicle-inventory-events"; // Kafka topic name
        String groupId = "vehicle-inventory-consumer-group"; // Consumer group for this application

        System.out.println("Initializing VehicleConsumer...");
        VehicleConsumer consumer = new VehicleConsumer(bootstrapServers, schemaRegistryUrl, topic, groupId);
        consumer.startConsuming(); // This call blocks until the application is shut down
    }
}
