# Data Plane Selector Configuration Exception

This control-plane extension makes it possible configure one or more data planes. After a data transfer is
triggered at the control plane will look for a data plane with matching capabilities.

## Configuration

Per data plane instance the following settings must be configured. As `<data-plane-id>` any unique string is valid.

| Key                                                     | Description                                  | Mandatory |
|:--------------------------------------------------------|:---------------------------------------------|-----------|
| edc.dataplane.selector.<data-plane-id>.url              | URL to connect to the Data Plane Instance.   | X         |
| edc.dataplane.selector.<data-plane-id>.sourcetypes      | Source Types in a comma separated List.      | X         |
| edc.dataplane.selector.<data-plane-id>.destinationtypes | Destination Types in a comma separated List. | X         |

