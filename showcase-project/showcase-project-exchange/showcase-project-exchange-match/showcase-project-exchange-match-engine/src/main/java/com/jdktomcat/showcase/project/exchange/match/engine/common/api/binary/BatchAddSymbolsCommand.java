/*
 * Copyright 2019 Maksim Zheravin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jdktomcat.showcase.project.exchange.match.engine.common.api.binary;

import com.jdktomcat.showcase.project.exchange.match.engine.common.CoreSymbolSpecification;
import com.jdktomcat.showcase.project.exchange.match.engine.utils.SerializationUtils;
import net.openhft.chronicle.bytes.BytesOut;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public record BatchAddSymbolsCommand(IntObjectHashMap<CoreSymbolSpecification> symbols) implements BinaryDataCommand {

    @Override
    public void writeMarshallable(BytesOut bytes) {
        SerializationUtils.marshallIntHashMap(symbols, bytes);
    }

    @Override
    public int getBinaryCommandTypeCode() {
        return BinaryCommandType.ADD_SYMBOLS.getCode();
    }
}
