# Dead Letter Queue Demo - Create Order

This demo showcases the Dead Letter Queue pattern in Kafka. When an order fails to process after multiple retries, it will be sent to the Dead Letter Queue (DLQ).

## Create an Order

To create an order and publish it to the `createOrder` topic, use the following `curl` command:

### Successful Order Example

```bash
curl -X POST \
  http://localhost:8080/api/orders/create \
  -H "Content-Type: application/json" \
  -d '"Order #12345 - Product: Laptop"'
```

This order will be processed successfully by the listener.

### Failed Order Example (Triggers Retry & DLQ)

```bash
curl -X POST \
  http://localhost:8080/api/orders/create \
  -H "Content-Type: application/json" \
  -d '"Order #99999 - fail - Product: Broken Item"'
```

This order contains the word "fail" which will trigger an API call exception. The flow will be:

1. **createOrder topic** → Initial attempt fails
2. **createOrderRetry topic** → Retries up to 5 times
3. **createOrderDeadLetter topic** → After 5 failed retries, message is sent to DLQ
4. **Database** → DLQ listener persists the failed message

## Monitor the Flow

Check the application logs to see the retry mechanism and DLQ in action:
- Initial processing attempt
- Retry attempts (up to 5 times)
- Final move to Dead Letter Queue
- Database insertion of failed message
