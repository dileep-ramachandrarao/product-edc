package net.catenax.edc.xsuaa.authorize;

import java.util.*;

public class XsuaaScopesMap {

  private static final Map<String, List<String>> SCOPES_MAP = createScopes();

  private static Map<String, List<String>> createScopes() {
    return Map.ofEntries(
        new AbstractMap.SimpleEntry<>("assets:GET", Arrays.asList("assetRead")),
        new AbstractMap.SimpleEntry<>("assets:POST", Arrays.asList("assetWrite")),
        new AbstractMap.SimpleEntry<>("assets:DELETE", Arrays.asList("assetWrite")),
        new AbstractMap.SimpleEntry<>("catalog:GET", Arrays.asList("catalogRead")),
        new AbstractMap.SimpleEntry<>(
            "contractagreements:GET", Arrays.asList("contractAgreementRead")),
        new AbstractMap.SimpleEntry<>(
            "contractdefinitions:GET", Arrays.asList("contractDefinitionRead")),
        new AbstractMap.SimpleEntry<>(
            "contractdefinitions:POST", Arrays.asList("contractDefinitionWrite")),
        new AbstractMap.SimpleEntry<>(
            "contractdefinitions:DELETE", Arrays.asList("contractDefinitionWrite")),
        new AbstractMap.SimpleEntry<>(
            "contractnegotiations:GET", Arrays.asList("contractNegotiationRead")),
        new AbstractMap.SimpleEntry<>(
            "contractnegotiations:POST", Arrays.asList("contractNegotiationWrite")),
        new AbstractMap.SimpleEntry<>(
            "policydefinitions:GET", Arrays.asList("policyDefinitionRead")),
        new AbstractMap.SimpleEntry<>(
            "policydefinitions:POST", Arrays.asList("policyDefinitionWrite")),
        new AbstractMap.SimpleEntry<>(
            "policydefinitions:DELETE", Arrays.asList("policyDefinitionWrite")),
        new AbstractMap.SimpleEntry<>("transferprocess:GET", Arrays.asList("transferProcessRead")),
        new AbstractMap.SimpleEntry<>(
            "transferprocess:POST", Arrays.asList("transferProcessWrite")));
  }

  public static Map<String, List<String>> getScopes() {
    return SCOPES_MAP;
  }
}
