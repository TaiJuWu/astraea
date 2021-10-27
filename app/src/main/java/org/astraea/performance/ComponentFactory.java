package org.astraea.performance;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

/** An interface used for creating producer, consumer. */
public interface ComponentFactory {
  Producer createProducer();

  Consumer createConsumer();

  /**
   * (Optional) Setting partitioner for producer. If not set, use default partitioner RoundRobin
   *
   * @param partitionerName The partitioner class to use
   * @return This factory
   */
  default ComponentFactory partitioner(String partitionerName) {
    return this;
  }

  /**
   * Used for creating Kafka producer, consumer of the same Kafka server and the same topic. The
   * consumers generated by the same object from `fromKafka(brokers)` subscribe the same topic and
   * have the same groupID.
   */
  static ComponentFactory fromKafka(String brokers, String topic, Map<String, Object> config) {
    final Properties prop = new Properties();
    final String groupId = "groupId:" + System.currentTimeMillis();
    prop.putAll(config);
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    prop.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

    return new ComponentFactory() {
      /** Create Producer with KafkaProducer<byte[], byte[]> functions */
      @Override
      public Producer createProducer() {
        return Producer.fromKafka(prop, topic);
      }

      /** Create Consumer with KafkaConsumer<byte[], byte[]> functions */
      @Override
      public Consumer createConsumer() {
        return Consumer.fromKafka(prop, Collections.singleton(topic));
      }

      @Override
      public ComponentFactory partitioner(String partitionerName) {
        prop.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, partitionerName);
        return this;
      }
    };
  }
}