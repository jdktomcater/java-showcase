package com.jdktomcat.showcase.project.exchange.match.engine.common.api.binary;

import net.openhft.chronicle.bytes.WriteBytesMarshallable;

public interface BinaryDataCommand extends WriteBytesMarshallable {

    int getBinaryCommandTypeCode();

}
