# Request for Comments (RFC): Best Practices for Event-Driven Architectures

## Summary
This RFC outlines best practices for designing event-driven architectures, with a focus on the use of Dead Letter Topics (DLTs), Retry Topics, and the organization of topics for different features. The goal is to provide guidance on whether to use a single, centralized DLT or to create separate topics for each feature or event stream.

---

## Context
Event-driven architectures rely on asynchronous communication between services using message brokers like Kafka. While this pattern offers scalability and decoupling, it also introduces challenges in handling message processing failures. Two common patterns for addressing these challenges are:

1. **Retry Topics**: Used to temporarily store messages that failed processing, allowing for retries.
2. **Dead Letter Topics (DLTs)**: Used to store messages that could not be processed after multiple retries.

The organization of these topics significantly impacts the maintainability, scalability, and observability of the system.

---

## Problem Statement
There is no one-size-fits-all approach to organizing Retry Topics and DLTs. The following questions need to be addressed:

1. Should there be a single, centralized DLT for all features, or should each feature have its own DLT?
2. Should Retry Topics and DLTs be tightly coupled to their corresponding primary topics, or should they be shared across features?
3. How can we balance simplicity, scalability, and observability in topic design?

---

## Proposed Best Practices

### 1. Topic Organization
#### Option A: Centralized DLT
- **Description**: Use a single DLT for all features.
- **Advantages**:
  - Simplifies topic management.
  - Easier to monitor and analyze failed messages in one place.
- **Disadvantages**:
  - Difficult to identify which feature a message belongs to.
  - Risk of the DLT becoming a bottleneck or single point of failure.

#### Option B: Feature-Specific Topics
- **Description**: Each feature has its own primary topic, Retry Topic, and DLT.
- **Advantages**:
  - Clear separation of concerns.
  - Easier to debug and monitor specific features.
  - Reduces the risk of one feature's failures impacting others.
- **Disadvantages**:
  - Increased topic management overhead.
  - Higher resource usage.

**Recommendation**: Use feature-specific topics for critical or high-throughput features. For less critical features, consider grouping them under a shared DLT.

### 2. Retry Mechanism
- Use Retry Topics to decouple retries from the primary topic.
- Implement exponential backoff or fixed delays to avoid overwhelming downstream systems.
- Limit the number of retries to prevent infinite loops.

### 3. Dead Letter Queue (DLQ) Handling
- Ensure that messages in the DLT are logged and monitored.
- Implement a consumer for the DLT to:
  - Notify relevant teams.
  - Store messages in a database for further analysis.
  - Trigger compensating actions if necessary.

### 4. Observability
- Use tools like Prometheus and Grafana to monitor topic metrics (e.g., lag, message age).
- Tag messages with metadata (e.g., feature name, retry count) to improve traceability.

### 5. Governance
- Define naming conventions for topics (e.g., `<feature>`, `<feature>-retry`, `<feature>-dlt`).
- Document the purpose and lifecycle of each topic.
- Regularly review and clean up unused topics.

---

## Alternatives Considered
1. **Single Topic for All Messages**: Simplifies management but lacks scalability and observability.
2. **Dynamic Topic Creation**: Allows for flexibility but increases complexity and operational overhead.

---

## Conclusion
The choice between centralized and feature-specific topics depends on the system's scale, complexity, and operational requirements. By following the proposed best practices, teams can design resilient and maintainable event-driven architectures.

---

## Next Steps
1. Gather feedback from stakeholders.
2. Pilot the recommended approach in a non-critical environment.
3. Refine the guidelines based on real-world experience.
