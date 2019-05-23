package com.optimiza.ehope.lis.onboarding.helper;

public enum WebhookEvent {
	BILLING_SUBSCRIPTION_CREATED("BILLING.SUBSCRIPTION.CREATED"),
	BILLING_SUBSCRIPTION_UPDATED("BILLING.SUBSCRIPTION.UPDATED"),
	BILLING_SUBSCRIPTION_CANCELLED("BILLING.SUBSCRIPTION.CANCELLED"),
	BILLING_SUBSCRIPTION_REACTIVATED("BILLING.SUBSCRIPTION.RE-ACTIVATED"),
	BILLING_SUBSCRIPTION_SUSPENDED("BILLING.SUBSCRIPTION.SUSPENDED"),
	PAYMENT_SALE_COMPLETED("PAYMENT.SALE.COMPLETED"),
	PAYMENT_SALE_DENIED("PAYMENT.SALE.DENIED");

	private String value;

	private WebhookEvent(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static WebhookEvent getWebhookByValue(String eventValue) {
		for (WebhookEvent webhookEvent : WebhookEvent.values()) {
			if (webhookEvent.getValue().equals(eventValue))
				return webhookEvent;
		}
		return null;
	}
}
