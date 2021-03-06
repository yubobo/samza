/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.samza.table;

import com.google.common.base.Preconditions;
import org.apache.samza.config.MetricsConfig;
import org.apache.samza.context.Context;
import org.apache.samza.table.utils.TableReadMetrics;
import org.apache.samza.table.utils.TableWriteMetrics;
import org.apache.samza.util.HighResolutionClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base class for all readable tables
 *
 * @param <K> the type of the key in this table
 * @param <V> the type of the value in this table
 */
abstract public class BaseReadableTable<K, V> implements ReadableTable<K, V> {

  protected final Logger logger;

  protected final String tableId;

  protected TableReadMetrics readMetrics;
  protected TableWriteMetrics writeMetrics;

  protected HighResolutionClock clock;

  /**
   * Construct an instance
   * @param tableId Id of the table
   */
  public BaseReadableTable(String tableId) {
    Preconditions.checkArgument(tableId != null & !tableId.isEmpty(),
        String.format("Invalid table Id: %s", tableId));
    this.tableId = tableId;
    this.logger = LoggerFactory.getLogger(getClass().getName() + "." + tableId);
  }

  @Override
  public void init(Context context) {
    MetricsConfig metricsConfig = new MetricsConfig(context.getJobContext().getConfig());
    clock = metricsConfig.getMetricsTimerEnabled()
        ? () -> System.nanoTime()
        : () -> 0L;

    readMetrics = new TableReadMetrics(context, this, tableId);
    if (this instanceof ReadWriteTable) {
      writeMetrics = new TableWriteMetrics(context, this, tableId);
    }
  }

  public String getTableId() {
    return tableId;
  }
}
