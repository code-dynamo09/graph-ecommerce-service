package com.lld.graph_ecommerce_service.data_fetchers;

import com.example.generated.types.CreditCardPayment;
import com.example.generated.types.WalletPayment;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsTypeResolver;

@DgsComponent
public class PaymentTypeResolver {

    @DgsTypeResolver(name = "PaymentInstrument")
    public String resolvePaymentInstrument(Object payment) {
        if (payment instanceof CreditCardPayment) {
            return "CreditCardPayment"; // Must match the GraphQL type name exactly
        } else if (payment instanceof WalletPayment) {
            return "WalletPayment";
        }
        throw new IllegalArgumentException("Unknown payment type");
    }
}