/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial implementation
 *
 */

package net.catenax.edc.dataplane.selector.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.dataspaceconnector.dataplane.selector.DataPlaneSelectorService;
import org.eclipse.dataspaceconnector.dataplane.selector.instance.DataPlaneInstance;
import org.eclipse.dataspaceconnector.dataplane.selector.instance.DataPlaneInstanceImpl;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;

/**
 * This Extension provides functionality to read materialized DataPlane instances from the
 * configuration file and add those to the DataPlaneSelectorService during
 * configuration/initialization phase of the connector.
 *
 * <p>Following configuration keys are made available:
 *
 * <table>
 * <thead>
 * <tr>
 * <th style="text-align:left">Key</th>
 * <th style="text-align:left">Description</th>
 * <th>Mandatory</th>
 * <th>Default</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td style="text-align:left">edc.dataplane.selector.<dataplane-id>.url</td>
 * <td style="text-align:left">URL of the Data-Plane instance</td>
 * <td>X</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td style="text-align:left">edc.dataplane.selector.<dataplane-id>.destinationtypes</td>
 * <td style="text-align:left">Comma separated list of destination transfer types supported of that Data-Plane instance</td>
 * <td></td>
 * <td>&#39;&#39; (empty string)</td>
 * </tr>
 * <tr>
 * <td style="text-align:left">edc.dataplane.selector.<dataplane-id>.sourcetypes</td>
 * <td style="text-align:left">Comma separated list of source transfer types supported of that Data-Plane instance</td>
 * <td></td>
 * <td>&#39;&#39; (empty string)</td>
 * </tr>
 * </tbody>
 * </table>
 */
@Requires({DataPlaneSelectorService.class})
public class DataPlaneSelectorConfigurationServiceExtension implements ServiceExtension {
  private static final String NAME = "Data Plane Selector Configuration Extension";
  private static final String COMMA = ",";

  @EdcSetting public static final String CONFIG_PREFIX = "edc.dataplane.selector";
  @EdcSetting public static final String URL_SUFFIX = "url";
  @EdcSetting public static final String DESTINATION_TYPES_SUFFIX = "destinationtypes";
  @EdcSetting public static final String SOURCE_TYPES_SUFFIX = "sourcetypes";

  private Monitor monitor;
  private DataPlaneSelectorService dataPlaneSelectorService;

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    this.dataPlaneSelectorService = context.getService(DataPlaneSelectorService.class);
    this.monitor = context.getMonitor();

    context.getConfig(CONFIG_PREFIX).partition().forEach(this::configureDataPlaneInstance);
  }

  private void configureDataPlaneInstance(final Config config) {
    final String id = config.currentNode();

    final String url = config.getString(URL_SUFFIX);

    final List<String> sourceTypes =
        Arrays.stream(config.getString(SOURCE_TYPES_SUFFIX, "").split(COMMA))
            .map(String::trim)
            .filter(Predicate.not(String::isEmpty))
            .distinct()
            .collect(Collectors.toList());

    if (sourceTypes.isEmpty()) {
      monitor.warning(
          String.format(
              "Data Plane instance '%s' configured to not support any transfer source types", id));
    }

    final List<String> destinationTypes =
        Arrays.stream(config.getString(DESTINATION_TYPES_SUFFIX, "").split(COMMA))
            .map(String::trim)
            .filter(Predicate.not(String::isEmpty))
            .distinct()
            .collect(Collectors.toList());

    if (destinationTypes.isEmpty()) {
      monitor.warning(
          String.format(
              "Data Plane instance '%s' configured to not support any transfer destination types",
              id));
    }

    final DataPlaneInstanceImpl.Builder builder =
        DataPlaneInstanceImpl.Builder.newInstance().id(id).url(url);

    sourceTypes.forEach(builder::allowedSourceType);
    destinationTypes.forEach(builder::allowedDestType);

    final DataPlaneInstance dataPlaneInstance = builder.build();
    dataPlaneSelectorService.addInstance(dataPlaneInstance);
  }
}
