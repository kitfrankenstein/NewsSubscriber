package com.kit.newsSubscriber.notification;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCallbackBus implements MqttCallback {

	private Pusher pusher;

	public MqttCallbackBus() {

	}

	public MqttCallbackBus(Pusher pusher) {
		this.pusher = pusher;
	}

	@Override
	public void connectionLost(Throwable cause) {

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			String[] msg = message.toString().split("\n");
			pusher.notification(topic, msg[0], msg[1], msg[2]);
		} catch (Exception ignored) {
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}
}
