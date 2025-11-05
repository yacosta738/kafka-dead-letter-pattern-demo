# Grafana Monitoring Setup

This project includes Grafana and Prometheus for monitoring Kafka Dead Letter Queue metrics.

## Services Running

- **Kafka UI**: http://localhost:8081
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
- **Spring Boot Actuator**: http://localhost:8080/actuator/prometheus
- **Kafka**: localhost:9092 (external), kafka:9093 (internal Docker network)

## Kafka Configuration

The Kafka broker is configured with two listeners to support both:
- **External connections** (from your Spring Boot app running on localhost): `localhost:9092`
- **Internal connections** (from other Docker containers like kafka-ui): `kafka:9093`

This dual-listener setup ensures that:
1. Your Spring Boot application can connect to Kafka at `localhost:9092`
2. Kafka UI and other Docker services can connect at `kafka:9093`
3. No hostname resolution issues occur

## Accessing Grafana

1. Open your browser and navigate to: http://localhost:3000
2. Login credentials:
   - **Username**: admin
   - **Password**: admin

## Dashboard Overview

The pre-configured dashboard "Kafka Dead Letter Queue Monitoring" includes:

### Metrics Tracked

1. **Create Order Messages Rate** - Shows the rate of messages arriving at the `createOrder` topic
2. **Total Dead Letter Queue Messages** - Counter showing total messages that ended up in the DLQ
3. **Retry Messages Rate** - Rate of messages being retried
4. **Total Successful Messages** - Counter of successfully processed messages

### Custom Metrics

The application exposes the following custom metrics via Micrometer:

- `kafka_messages_createOrder_total` - Total messages received in createOrder topic
- `kafka_messages_retry_total` - Total messages sent to retry topic
- `kafka_messages_deadletter_total` - Total messages sent to dead letter queue
- `kafka_messages_success_total` - Total messages processed successfully

## Testing the Dashboard

1. Start all services:
   ```bash
   docker-compose up -d
   ```

2. Start the Spring Boot application

3. Send a failing message to trigger DLQ:
   ```bash
   curl -X POST \
     http://localhost:8080/api/orders/create \
     -H "Content-Type: application/json" \
     -d '"Order #99999 - fail - Product: Broken Item"'
   ```

4. Check the Grafana dashboard to see:
   - Message arriving at createOrder topic
   - Retry attempts increasing
   - Dead letter queue counter incrementing after 5 retries

5. Send a successful message:
   ```bash
   curl -X POST \
     http://localhost:8080/api/orders/create \
     -H "Content-Type: application/json" \
     -d '"Order #12345 - Product: Laptop"'
   ```

6. Watch the "Total Successful Messages" counter increase

## Troubleshooting

### Prometheus not collecting metrics

1. Check Spring Boot application is running on port 8080
2. Verify actuator endpoint: http://localhost:8080/actuator/prometheus
3. Check Prometheus targets: http://localhost:9090/targets

### Dashboard not showing data

1. Verify Prometheus datasource is configured in Grafana
2. Check that metrics are being exposed by the application
3. Wait a few minutes for data to accumulate

## Customizing the Dashboard

You can customize the dashboard by:
1. Logging into Grafana
2. Finding the "Kafka Dead Letter Queue Monitoring" dashboard
3. Clicking the gear icon (⚙️) to edit
4. Adding new panels or modifying existing ones
5. Changes are automatically saved

## Additional Metrics

To add more custom metrics, inject `MeterRegistry` in your services and create counters, gauges, or timers as needed.
{
  "annotations": {
    "list": []
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 0,
  "id": null,
  "links": [],
  "panels": [
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisBorderShow": false,
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "insertNulls": false,
            "lineInterpolation": "linear",
            "lineWidth": 2,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "short"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "id": 1,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "single",
          "sort": "none"
        }
      },
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "rate(kafka_messages_createOrder_total[5m])",
          "refId": "A",
          "legendFormat": "Create Order Messages"
        }
      ],
      "title": "Create Order Messages Rate",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 1
              }
            ]
          },
          "unit": "short"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 12,
        "y": 0
      },
      "id": 2,
      "options": {
        "colorMode": "value",
        "graphMode": "area",
        "justifyMode": "auto",
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showPercentChange": false,
        "textMode": "auto",
        "wideLayout": true
      },
      "pluginVersion": "11.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "kafka_messages_deadletter_total",
          "refId": "A"
        }
      ],
      "title": "Total Dead Letter Queue Messages",
      "type": "stat"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisBorderShow": false,
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "insertNulls": false,
            "lineInterpolation": "linear",
            "lineWidth": 2,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "short"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 8
      },
      "id": 3,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "single",
          "sort": "none"
        }
      },
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "rate(kafka_messages_retry_total[5m])",
          "refId": "A",
          "legendFormat": "Retry Messages"
        }
      ],
      "title": "Retry Messages Rate",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "short"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 12,
        "y": 8
      },
      "id": 4,
      "options": {
        "colorMode": "value",
        "graphMode": "area",
        "justifyMode": "auto",
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showPercentChange": false,
        "textMode": "auto",
        "wideLayout": true
      },
      "pluginVersion": "11.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "kafka_messages_success_total",
          "refId": "A"
        }
      ],
      "title": "Total Successful Messages",
      "type": "stat"
    }
  ],
  "schemaVersion": 39,
  "tags": ["kafka", "dead-letter-queue"],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-6h",
    "to": "now"
  },
  "timepicker": {},
  "timezone": "browser",
  "title": "Kafka Dead Letter Queue Monitoring",
  "uid": "kafka-dlq-monitoring",
  "version": 1,
  "weekStart": ""
}

