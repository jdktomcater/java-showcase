package com.jdktomcat.showcase.project.exchange.match.engine.processors;

import com.jdktomcat.showcase.project.exchange.match.engine.common.cmd.OrderCommand;
import com.jdktomcat.showcase.project.exchange.match.engine.common.cmd.OrderCommandType;
import com.lmax.disruptor.EventHandler;
import lombok.RequiredArgsConstructor;

import java.util.function.ObjLongConsumer;

@RequiredArgsConstructor
public final class ResultsHandler implements EventHandler<OrderCommand> {

    private final ObjLongConsumer<OrderCommand> resultsConsumer;

    private boolean processingEnabled = true;

    @Override
    public void onEvent(OrderCommand cmd, long sequence, boolean endOfBatch) {

        if (cmd.command == OrderCommandType.GROUPING_CONTROL) {
            processingEnabled = cmd.orderId == 1;
        }

        if (processingEnabled) {
            resultsConsumer.accept(cmd, sequence);
        }

    }
}
