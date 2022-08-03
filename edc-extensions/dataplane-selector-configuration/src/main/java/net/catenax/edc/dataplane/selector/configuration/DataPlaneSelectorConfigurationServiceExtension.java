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
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;

@Requires({DataPlaneSelectorService.class})
public class DataPlaneSelectorConfigurationServiceExtension implements ServiceExtension {

  @EdcSetting public static final String CONFIG_PREFIX = "edc.dataplane.selector";
  @EdcSetting public static final String URL_SUFFIX = "url";
  @EdcSetting public static final String DESTINATION_TYPES_SUFFIX = "destinationtypes";
  @EdcSetting public static final String SOURCE_TYPES_SUFFIX = "sourcetypes";

  private static final String COMMA = ",";

  // edc.dataplane.selector.<dataplane-id>.url=  // mandatory
  // edc.dataplane.selector.<dataplane-id>.sourceTypes= // warning
  // edc.dataplane.selector.<dataplane-id>.destinationTypes= // warning

  // @Inject private DataPlaneSelectorService dataPlaneSelectorService;

  @Override
  public String name() {
    return "Data Plane Selector Configuration Extension";
  }

  @Override
  public void initialize(ServiceExtensionContext context) {

    final DataPlaneSelectorService dataPlaneSelectorService =
        context.getService(DataPlaneSelectorService.class);

    final Config config = context.getConfig(CONFIG_PREFIX);
    config
        .partition()
        .forEach(
            c -> {
              final String id = c.currentNode();

               // TODO Add Warnings Logs and Null Checks

              final String url = c.getString(URL_SUFFIX);
              final List<String> sourceTypes =
                  Arrays.stream(c.getString(SOURCE_TYPES_SUFFIX, "").split(COMMA))
                      .map(String::trim)
                      .filter(Predicate.not(String::isEmpty))
                      .distinct()
                      .collect(Collectors.toList());
              final List<String> destinationTypes =
                  Arrays.stream(c.getString(DESTINATION_TYPES_SUFFIX, "").split(COMMA))
                      .map(String::trim)
                      .filter(Predicate.not(String::isEmpty))
                      .distinct()
                      .collect(Collectors.toList());

              final DataPlaneInstanceImpl.Builder builder =
                  DataPlaneInstanceImpl.Builder.newInstance();
              builder.id(id);
              builder.url(url);
              sourceTypes.forEach(builder::allowedSourceType);
              destinationTypes.forEach(builder::allowedDestType);

              final DataPlaneInstance dataPlaneInstance = builder.build();
              dataPlaneSelectorService.addInstance(dataPlaneInstance);
            });
  }
}
