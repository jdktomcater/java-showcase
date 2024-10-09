package com.jdktomcat.showcase.common.disruptor.event;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class LongEventProducerWithTranslator {

    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(final RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<LongEvent, Long> TRANSLATOR = (event, sequence, arg0) -> event.setValue(arg0);

    public void onData(Long data) {
        ringBuffer.publishEvent(TRANSLATOR, data);
    }
}
