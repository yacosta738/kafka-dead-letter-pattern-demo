# Kafka Dead Letter Pattern Demo

This project demonstrates the implementation of a Dead Letter Queue (DLQ) pattern in Apache Kafka using Spring Boot. The DLQ pattern is a common approach to handle message processing failures in distributed systems.

## Features

- **Kafka Listener with Error Handling**: Configured to handle exceptions during message processing.
- **Dead Letter Queue**: Messages that fail processing are redirected to a DLQ for further analysis.
- **Retry Mechanism**: Implements a fixed backoff retry strategy.
- **Monitoring**: Includes Prometheus and Grafana for monitoring Kafka metrics.

## Project Structure

- `src/main/java`: Contains the main application code.
  - `KafkaConfig.java`: Configures Kafka listeners and error handling.
  - `KafkaListenerExample.java`: Example Kafka listener implementation.
  - `KafkaPublisher.java`: Publishes messages to Kafka topics.
  - `KafkaPublisherController.java`: REST controller for publishing messages.
- `src/main/resources`: Contains application properties and other resources.
  - `application.properties`: Configuration for Kafka, Prometheus, etc.
- `docs`: Documentation files for the project.
  - `grafana-monitoring.md`: Guide to setting up Grafana for monitoring.
  - `publish_event.md`: Instructions for publishing events to Kafka.

## Prerequisites

- Java 17 or higher
- Apache Kafka
- Docker (for running Prometheus and Grafana)
- Maven

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-repo/kafka-dead-letter-pattern-demo.git
cd kafka-dead-letter-pattern-demo
```

### 2. Build the Project
```bash
./mvnw clean install
```

### 3. Run Kafka and Zookeeper
Ensure Kafka and Zookeeper are running locally or in Docker.

### 4. Run the Application
```bash
./mvnw spring-boot:run
```

### 5. Access the REST API
Use the following endpoint to publish messages:
- `POST /publish` with a JSON payload.

### 6. Monitor with Grafana
- Access Grafana at `http://localhost:3000`.
- Import the dashboard from `grafana/provisioning/dashboards/kafka-dlq-dashboard.json`.

## Configuration

### Kafka Configuration
Kafka settings can be modified in `src/main/resources/application.properties`.

### Prometheus Configuration
Prometheus settings are defined in `prometheus.yml`.

## How It Works

1. **Message Processing**: Messages are consumed by `KafkaListenerExample`.
2. **Error Handling**: If an exception occurs, the `DefaultErrorHandler` retries processing with a fixed backoff.
3. **Dead Letter Queue**: After retries are exhausted, the message is sent to the DLQ.

## Monitoring

- **Prometheus**: Collects metrics from the application.
- **Grafana**: Visualizes metrics with pre-configured dashboards.

## Documentation

- [Retry and Dead Letter Queue in Kafka](docs/Retry%20and%20Dead%20Letter%20Queue%20in%20Kafka.md)
- [Publishing Events](docs/publish_event.md)
- [Grafana Monitoring Setup](docs/grafana-monitoring.md)

## License

This project is licensed under the MIT License. See the LICENSE file for details.

---

Happy coding! 🚀
