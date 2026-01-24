package com.ecommerce.checkout.commandhandler.consumers;

import com.ecommerce.checkout.application.commands.PlaceOrderCommand;
import com.ecommerce.checkout.application.handlers.PlaceOrderCommandHandler;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.common.commands.CommandEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CheckoutCommandConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutCommandConsumer.class);
    private final PlaceOrderCommandHandler placeOrderCommandHandler;
    private final ObjectMapper objectMapper;

    public CheckoutCommandConsumer(PlaceOrderCommandHandler placeOrderCommandHandler, ObjectMapper objectMapper) {
        this.placeOrderCommandHandler = placeOrderCommandHandler;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "checkout.commands.queue", durable = "true"), exchange = @Exchange(value = MessagingConstants.COMMANDS_EXCHANGE, type = "topic"), key = "checkout.commandsQueue"))
    public void onCommand(Message message) {
        try {
            // Check message type via header
            String type = (String) message.getMessageProperties().getHeaders().get("commandType");

            // For MVP, we assume all messages in this queue are PlaceOrderCommand or we
            // parse generically
            // Re-using CommandEnvelope logic
            CommandEnvelope<PlaceOrderCommand> envelope = objectMapper.readValue(message.getBody(),
                    objectMapper.getTypeFactory().constructParametricType(CommandEnvelope.class,
                            PlaceOrderCommand.class));

            logger.info("Received PlaceOrderCommand for guestToken={}", envelope.getPayload().getGuestToken());
            placeOrderCommandHandler.handle(envelope.getPayload()).join();

        } catch (Exception e) {
            logger.error("Error processing checkout command", e);
        }
    }
}
