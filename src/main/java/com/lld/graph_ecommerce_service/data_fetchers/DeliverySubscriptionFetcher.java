package com.lld.graph_ecommerce_service.data_fetchers;

import com.lld.graph_ecommerce_service.generated.types.DeliveryTelemetry;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsSubscription;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import java.time.Duration;

@DgsComponent
public class DeliverySubscriptionFetcher {

    @DgsSubscription
    public Publisher<DeliveryTelemetry> monitorOrderDelivery(String orderId) {
        // Emit a new telemetry object every 5 seconds to simulate a moving delivery
        return Flux.interval(Duration.ofSeconds(5))
                .map(tick -> {
                    DeliveryTelemetry telemetry = new DeliveryTelemetry();
                    telemetry.setOrderId(orderId);
                    telemetry.setCurrentLatitude(40.7128 + (tick * 0.001)); // Simulating movement
                    telemetry.setCurrentLongitude(-74.0060 + (tick * 0.001));
                    telemetry.setEstimatedMinutesToArrival(Math.max(0, 30 - tick.intValue()));
                    return telemetry;
                });
    }
}
